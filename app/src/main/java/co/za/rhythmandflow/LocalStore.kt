package co.za.rhythmandflow

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Device-only preview persistence. Replace with repositories when connecting the backend. */
class LocalStore(context:Context) {
    private val prefs=context.getSharedPreferences("rhythm_flow_frontend",Context.MODE_PRIVATE)
    private fun str(key:String,default:String)=prefs.getString(key,default)?:default
    private fun put(key:String,value:String){prefs.edit().putString(key,value).apply()}
    var name:String get()=str("name","Alex");set(v){put("name",v)}
    var email:String get()=str("email","alex@email.com");set(v){put("email",v)}
    var about:String get()=str("about","A work in progress, and that's enough.");set(v){put("about",v)}
    var photo:String? get()=prefs.getString("photo",null);set(v){prefs.edit().putString("photo",v).apply()}
    var demoSession:Boolean get()=prefs.getBoolean("demoSession",false);set(v){prefs.edit().putBoolean("demoSession",v).apply()}
    var reminders:Boolean get()=prefs.getBoolean("reminders",true);set(v){prefs.edit().putBoolean("reminders",v).apply()}
    var completed:Int get()=prefs.getInt("completed",0);set(v){prefs.edit().putInt("completed",v).apply()}
    val intentions:Set<String> get()=prefs.getStringSet("intentions",setOf("Build confidence"))?.toSet()?:emptySet()
    val favourites:Set<String> get()=prefs.getStringSet("favourites",setOf("Dance"))?.toSet()?:emptySet()
    val saved:Set<String> get()=prefs.getStringSet("saved",emptySet())?.toSet()?:emptySet()
    private fun toggle(key:String,value:String,initial:Set<String>){val set=initial.toMutableSet();if(!set.add(value))set.remove(value);prefs.edit().putStringSet(key,set).apply()}
    fun setIntention(v:String,checked:Boolean){val set=intentions.toMutableSet();if(checked)set.add(v)else set.remove(v);prefs.edit().putStringSet("intentions",set).apply()}
    fun toggleFavourite(v:String)=toggle("favourites",v,favourites)
    fun toggleSaved(v:String)=toggle("saved",v,saved)
    fun entries():JSONArray=try{JSONArray(str("entries","[]"))}catch(_:Exception){JSONArray()}
    fun addEntry(value:String,feeling:String=""){val array=entries();array.put(JSONObject().put("text",value).put("feeling",feeling).put("date",SimpleDateFormat("dd MMM yyyy · HH:mm",Locale.getDefault()).format(Date())));put("entries",array.toString())}
    fun deleteEntry(index:Int){val array=entries();array.remove(index);put("entries",array.toString())}
    fun checkIn(mood:String){val a=try{JSONArray(str("checkins","[]"))}catch(_:Exception){JSONArray()};a.put(JSONObject().put("mood",mood).put("time",System.currentTimeMillis()));put("checkins",a.toString())}
    fun monthlyCheckins():Int{val a=try{JSONArray(str("checkins","[]"))}catch(_:Exception){JSONArray()};val fmt=SimpleDateFormat("yyyy-MM",Locale.US);val month=fmt.format(Date());var count=0;for(i in 0 until a.length())if(fmt.format(Date(a.getJSONObject(i).getLong("time")))==month)count++;return count}
    fun cart():JSONObject=try{JSONObject(str("cart","{}"))}catch(_:Exception){JSONObject()}
    fun addCart(title:String,delta:Int=1){val obj=cart();val q=(obj.optInt(title)+delta).coerceAtLeast(0);if(q==0)obj.remove(title)else obj.put(title,q);put("cart",obj.toString())}
}
