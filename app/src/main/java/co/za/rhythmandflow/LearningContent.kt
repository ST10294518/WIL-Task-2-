package co.za.rhythmandflow

data class Lesson(val id:String,val title:String,val topic:String,val minutes:Int,val image:Int)
data class ShortCourse(val id:String,val title:String,val description:String,val price:Int,val lessonIds:List<String>,val image:Int)
data class ProgrammeWeek(val number:Int,val title:String,val description:String,val lessons:List<String>)

/** Sample catalogue; replace text and media with client-approved content when available. */
object LearningContent {
    val lessons=listOf(
        Lesson("full_body_flow","Full Body Flow","Yoga",20,R.drawable.flow),
        Lesson("move_release","Move & Release","Stretch",15,R.drawable.move_release),
        Lesson("dance_joy","Dance with Joy","Dance",25,R.drawable.dance),
        Lesson("barre_strength","Strength & Confidence","Barre",20,R.drawable.yoga),
        Lesson("breathing_reset","6-Minute Reset","Mindfulness",6,R.drawable.stones),
        Lesson("gentle_flow","Gentle Morning Flow","Yoga",12,R.drawable.yoga)
    )
    val courses=listOf(
        ShortCourse("mindful_movement","Mindful Movement Essentials","A short introduction to a gentler movement routine, with yoga, stretching and mindful breathing. Includes three lessons you can revisit at your own pace.",199,listOf("full_body_flow","move_release","breathing_reset"),R.drawable.flow),
        ShortCourse("dance_confidence","Dance & Confidence","Find your rhythm through dance and gentle strength practice. Two short lessons to explore movement at your own pace.",149,listOf("dance_joy","barre_strength"),R.drawable.dance)
    )
    val weeks=listOf(
        ProgrammeWeek(1,"Find your starting point","Explore a gentle flow and note how you feel. Sample schedule: two sessions, with rest between them.",listOf("gentle_flow","breathing_reset")),
        ProgrammeWeek(2,"Build a routine","Make room for two or three short sessions that fit your week.",listOf("full_body_flow","move_release")),
        ProgrammeWeek(3,"Move with intention","Notice your breathing and choose comfortable movements.",listOf("full_body_flow","breathing_reset")),
        ProgrammeWeek(4,"Explore your rhythm","Try dance alongside a stretch session and reflect on the experience.",listOf("dance_joy","move_release")),
        ProgrammeWeek(5,"Strength and confidence","Explore gentle barre and balance it with a reset.",listOf("barre_strength","breathing_reset")),
        ProgrammeWeek(6,"Make room for recovery","Return to a gentle practice and take time to rest.",listOf("gentle_flow","move_release")),
        ProgrammeWeek(7,"Choose what feels good","Revisit your favourite sessions and keep your routine manageable.",listOf("dance_joy","full_body_flow")),
        ProgrammeWeek(8,"Reflect and continue","Notice what you enjoyed and choose a routine to carry forward.",listOf("gentle_flow","breathing_reset"))
    )
    fun lesson(id:String)=lessons.first{it.id==id}
    fun course(id:String)=courses.first{it.id==id}
}
