package co.za.rhythmandflow

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

interface WellnessRepository {
    fun purchased(course:String):Boolean
    fun purchases():List<Purchase>
    fun simulatePurchase(course:ShortCourse):Boolean
    fun weights():List<WeightRecord>
    fun addWeight(kg:Double,date:String)
    fun deleteWeight(id:Long)
    fun workouts():List<WorkoutRecord>
    fun logWorkout(lesson:Lesson,date:String,source:String="manual")
    fun deleteWorkout(id:Long)
    fun media(lesson:String):String?
    fun setMedia(lesson:String,uri:String)
}
data class Purchase(val course:String,val amount:Int,val time:Long)
data class WeightRecord(val id:Long,val kg:Double,val date:String)
data class WorkoutRecord(val id:Long,val title:String,val minutes:Int,val date:String,val source:String)

/** Single-device preview database. Purchases are simulated entitlements, not payment receipts. */
class DeviceWellnessRepository(context:Context):SQLiteOpenHelper(context,"wellness.db",null,1),WellnessRepository {
    override fun onCreate(db:SQLiteDatabase){
        db.execSQL("CREATE TABLE purchases (course_id TEXT PRIMARY KEY, amount INTEGER NOT NULL CHECK(amount >= 0), purchased_at INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE weights (id INTEGER PRIMARY KEY AUTOINCREMENT, kg REAL NOT NULL CHECK(kg > 0), recorded_date TEXT NOT NULL)")
        db.execSQL("CREATE TABLE workouts (id INTEGER PRIMARY KEY AUTOINCREMENT, lesson_id TEXT NOT NULL, title TEXT NOT NULL, minutes INTEGER NOT NULL CHECK(minutes > 0), recorded_date TEXT NOT NULL, source TEXT NOT NULL)")
        db.execSQL("CREATE INDEX workouts_date ON workouts(recorded_date)")
        db.execSQL("CREATE INDEX weights_date ON weights(recorded_date)")
        db.execSQL("CREATE TABLE media (lesson_id TEXT PRIMARY KEY, uri TEXT NOT NULL)")
    }
    override fun onUpgrade(db:SQLiteDatabase,old:Int,new:Int) { /* Version 1; future upgrades must preserve data. */ }
    override fun purchased(course:String):Boolean=readableDatabase.rawQuery("SELECT 1 FROM purchases WHERE course_id=?",arrayOf(course)).use{it.moveToFirst()}
    override fun purchases():List<Purchase>{val rows=mutableListOf<Purchase>();readableDatabase.rawQuery("SELECT course_id, amount, purchased_at FROM purchases ORDER BY purchased_at DESC",null).use{c->while(c.moveToNext())rows.add(Purchase(c.getString(0),c.getInt(1),c.getLong(2)))};return rows}
    override fun simulatePurchase(course:ShortCourse):Boolean {val values=ContentValues().apply{put("course_id",course.id);put("amount",course.price);put("purchased_at",System.currentTimeMillis())};return writableDatabase.insertWithOnConflict("purchases",null,values,SQLiteDatabase.CONFLICT_IGNORE)!=-1L}
    override fun weights():List<WeightRecord>{val rows=mutableListOf<WeightRecord>();readableDatabase.rawQuery("SELECT id,kg,recorded_date FROM weights ORDER BY recorded_date DESC,id DESC",null).use{c->while(c.moveToNext())rows.add(WeightRecord(c.getLong(0),c.getDouble(1),c.getString(2)))};return rows}
    override fun addWeight(kg:Double,date:String){require(kg.isFinite()&&kg>0&&kg<=1000);require(RecordValidation.validDate(date));val v=ContentValues().apply{put("kg",kg);put("recorded_date",date)};check(writableDatabase.insertOrThrow("weights",null,v)>0)}
    override fun deleteWeight(id:Long){writableDatabase.delete("weights","id=?",arrayOf(id.toString()))}
    override fun workouts():List<WorkoutRecord>{val rows=mutableListOf<WorkoutRecord>();readableDatabase.rawQuery("SELECT id,title,minutes,recorded_date,source FROM workouts ORDER BY recorded_date DESC,id DESC",null).use{c->while(c.moveToNext())rows.add(WorkoutRecord(c.getLong(0),c.getString(1),c.getInt(2),c.getString(3),c.getString(4)))};return rows}
    override fun logWorkout(lesson:Lesson,date:String,source:String){require(RecordValidation.validDate(date));val v=ContentValues().apply{put("lesson_id",lesson.id);put("title",lesson.title);put("minutes",lesson.minutes);put("recorded_date",date);put("source",source)};check(writableDatabase.insertOrThrow("workouts",null,v)>0)}
    override fun deleteWorkout(id:Long){writableDatabase.delete("workouts","id=?",arrayOf(id.toString()))}
    override fun media(lesson:String):String?=readableDatabase.rawQuery("SELECT uri FROM media WHERE lesson_id=?",arrayOf(lesson)).use{if(it.moveToFirst())it.getString(0)else null}
    override fun setMedia(lesson:String,uri:String){require(uri.startsWith("content://"));val v=ContentValues().apply{put("lesson_id",lesson);put("uri",uri)};writableDatabase.insertWithOnConflict("media",null,v,SQLiteDatabase.CONFLICT_REPLACE)}
}
