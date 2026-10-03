package co.za.rhythmandflow

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import java.time.LocalDate
import java.util.ArrayDeque

/** Offline programme, course and tracker flows sharing the existing brand. */
class FeaturesActivity:Activity() {
    private val teal=Color.rgb(8,119,115)
    private val ink=Color.rgb(30,39,37)
    private val muted=Color.rgb(100,115,120)
    private val cream=Color.rgb(250,248,245)
    private lateinit var repo:DeviceWellnessRepository
    private lateinit var body:LinearLayout
    private val history=ArrayDeque<String>()
    private var page="programmes"
    private var topic="All"
    private var courseId=LearningContent.courses.first().id
    private var lessonId=LearningContent.lessons.first().id
    private var mediaCourse:String?=null
    private var pendingMediaLesson:String?=null
    private var video:VideoView?=null
    private var videoPosition=0
    private var resumePlaying=false
    private var trackerTab="Weight"
    private var workoutDate=LocalDate.now().toString()
    private var weightDate=LocalDate.now().toString()
    private var workoutChoice=0
    override fun onCreate(saved:Bundle?){super.onCreate(saved);repo=DeviceWellnessRepository(this);window.statusBarColor=cream;window.navigationBarColor=Color.WHITE;window.decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        courseId=saved?.getString("course")?:courseId;lessonId=saved?.getString("lesson")?:lessonId;mediaCourse=saved?.getString("mediaCourse");pendingMediaLesson=saved?.getString("pending");videoPosition=saved?.getInt("position")?:0;resumePlaying=saved?.getBoolean("playing")?:false
        show(saved?.getString("page")?:intent.getStringExtra("page")?:"programmes",false)
    }
    override fun onSaveInstanceState(out:Bundle){out.putString("page",page);out.putString("course",courseId);out.putString("lesson",lessonId);out.putString("mediaCourse",mediaCourse);out.putString("pending",pendingMediaLesson);out.putInt("position",video?.currentPosition?:videoPosition);out.putBoolean("playing",video?.isPlaying?:resumePlaying);super.onSaveInstanceState(out)}
    override fun onPause(){videoPosition=video?.currentPosition?:videoPosition;resumePlaying=video?.isPlaying?:false;video?.pause();super.onPause()}
    override fun onResume(){super.onResume();if(resumePlaying)video?.start()}
    override fun onDestroy(){video?.stopPlayback();repo.close();super.onDestroy()}
    @Deprecated("Native Activity back dispatch") override fun onBackPressed(){if(history.isEmpty())finish()else show(history.removeLast(),false)}
    private fun dp(n:Int)=(n*resources.displayMetrics.density).toInt()
    private fun rounded(color:Int=Color.WHITE)=GradientDrawable().apply{setColor(color);cornerRadius=dp(14).toFloat();setStroke(dp(1),Color.rgb(228,233,230))}
    private fun col(pad:Int=0)=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(pad),dp(pad),dp(pad),dp(pad))}
    private fun text(value:String,size:Float=14f,serif:Boolean=false)=TextView(this).apply{text=value;textSize=size;setTextColor(if(serif)ink else muted);typeface=Typeface.create(if(serif)"serif" else "sans-serif",Typeface.NORMAL);setLineSpacing(dp(3).toFloat(),1.05f)}
    private fun add(view:View,parent:LinearLayout=body){parent.addView(view,LinearLayout.LayoutParams(-1,-2).apply{topMargin=dp(12)})}
    private fun button(label:String,primary:Boolean=true,fn:()->Unit)=Button(this).apply{text=label;isAllCaps=false;textSize=14f;setTextColor(if(primary)Color.WHITE else teal);background=rounded(if(primary)teal else Color.WHITE);minHeight=dp(48);stateListAnimator=null;setOnClickListener{fn()}}
    private fun card(title:String,sub:String,image:Int=0,fn:()->Unit){val c=col(14);c.background=rounded();if(image!=0){val photo=ImageView(this);photo.setImageResource(image);photo.scaleType=ImageView.ScaleType.CENTER_CROP;photo.importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_NO;c.addView(photo,LinearLayout.LayoutParams(-1,dp(130)))};add(text(title,21f,true),c);add(text(sub),c);add(button("Open   →",false,fn),c);add(c)}
    private fun input(hint:String)=EditText(this).apply{this.hint=hint;textSize=14f;background=rounded();setTextColor(ink);setPadding(dp(12),dp(10),dp(12),dp(10));minHeight=dp(48);setSingleLine(true)}
    private fun toast(value:String)=Toast.makeText(this,value,Toast.LENGTH_SHORT).show()
    private fun safely(fn:()->Unit){try{fn()}catch(_:Exception){toast("Could not save. Please try again.")}}
    private fun show(next:String,push:Boolean=true){video?.stopPlayback();video=null;if(push&&next!=page)history.addLast(page);if(next!="video"){videoPosition=0;resumePlaying=false};page=next
        val root=col();root.setBackgroundColor(cream);setContentView(root);val scroll=ScrollView(this).apply{isFillViewport=true};val outer=col();outer.gravity=Gravity.CENTER_HORIZONTAL;body=col(20);outer.addView(body,LinearLayout.LayoutParams(if(resources.configuration.screenWidthDp>600)dp(560)else -1,-2));scroll.addView(outer);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f))
        add(button("‹  Back",false){onBackPressed()});when(page){"programmes"->programmes();"programme"->programme();"videos"->videos();"video"->videoScreen();"courses"->courses();"course"->course();"checkout"->checkout();"purchased"->purchased();"tracker"->tracker();else->programmes()}
        val nav=LinearLayout(this);listOf("programmes" to "Plans","videos" to "Videos","courses" to "Courses","purchased" to "My Courses","tracker" to "Tracker").forEach{(p,l)->val t=text(l,10f).apply{gravity=Gravity.CENTER;minHeight=dp(58);setTextColor(if(page==p)teal else muted);isFocusable=true;setOnClickListener{show(p)}};nav.addView(t,LinearLayout.LayoutParams(0,dp(58),1f))};nav.setBackgroundColor(Color.WHITE);root.addView(nav)
    }
    private fun title(value:String,sub:String){add(text(value,29f,true));add(text(sub))}
    private fun programmes(){title("Programmes","Build a routine, one week at a time.");card("8-Week Find Your Rhythm","A sample programme of movement, stretching and mindful pauses. Free preview programme.",R.drawable.flow){show("programme")};add(text("Programme descriptions and schedules are sample content pending client approval.",12f))}
    private fun programme(){title("8-Week Find Your Rhythm","A gentle introduction to creating a regular wellness routine.");add(text("How to follow\nChoose two or three sessions each week, leaving time for rest. Repeat lessons as you prefer. The schedule is a sample and can be adjusted when the client's programme content is available."));LearningContent.weeks.forEach{w->val c=col(14);c.background=rounded();add(text("Week ${w.number} · ${w.title}",21f,true),c);add(text(w.description),c);w.lessons.forEach{id->val lesson=LearningContent.lesson(id);add(button("${lesson.title} · ${lesson.minutes} mins",false){openLesson(id,null)},c)};add(c)}}
    private fun videos(){title("Workout Videos","Browse by topic or theme.");val topics=listOf("All")+LearningContent.lessons.map{it.topic}.distinct();val spinner=Spinner(this);spinner.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,topics);spinner.setSelection(topics.indexOf(topic).coerceAtLeast(0));add(spinner);val list=col();add(list);val search=input("Search workout videos…");body.addView(search,body.indexOfChild(list));var query=""
        fun render(){list.removeAllViews();val found=LearningContent.lessons.filter{(topic=="All"||it.topic==topic)&&it.title.contains(query,true)};found.forEach{l->add(button("${l.title}\n${l.topic} · ${l.minutes} mins · ${if(repo.media(l.id)==null)"Coming soon" else "Local video attached"}",false){openLesson(l.id,null)},list)};if(found.isEmpty())add(text("No matching videos."),list)}
        spinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{override fun onNothingSelected(p:AdapterView<*>?){};override fun onItemSelected(p:AdapterView<*>?,v:View?,position:Int,id:Long){topic=topics[position];render()}}
        search.addTextChangedListener(object:android.text.TextWatcher{override fun beforeTextChanged(s:CharSequence?,a:Int,b:Int,c:Int){};override fun afterTextChanged(e:android.text.Editable?){};override fun onTextChanged(s:CharSequence?,a:Int,b:Int,c:Int){query=s.toString();render()}});render()
    }
    private fun openLesson(id:String,course:String?){lessonId=id;mediaCourse=course;videoPosition=0;resumePlaying=false;show("video")}
    private fun videoScreen(){val lesson=LearningContent.lesson(lessonId);title(lesson.title,"${lesson.topic} · ${lesson.minutes} mins");if(mediaCourse!=null&&!repo.purchased(mediaCourse!!)){add(text("This course is locked. Complete a simulated purchase to access its lessons."));return};val uri=repo.media(lesson.id)
        if(uri==null){val image=ImageView(this);image.setImageResource(lesson.image);image.scaleType=ImageView.ScaleType.CENTER_CROP;body.addView(image,LinearLayout.LayoutParams(-1,dp(180)));add(text("Video coming soon",23f,true));add(text("No video has been supplied yet. Attach a local video to preview playback on this device."))}
        else {val player=VideoView(this);video=player;body.addView(player,LinearLayout.LayoutParams(-1,dp(230)));val controls=MediaController(this);controls.setAnchorView(player);player.setMediaController(controls);player.setOnPreparedListener{player.seekTo(videoPosition);if(resumePlaying)player.start()};player.setOnErrorListener{_,_,_->toast("This video cannot be opened. Attach the file again or try a supported MP4.");true};player.setOnCompletionListener{resumePlaying=false;videoPosition=0;toast("Video finished. You can log the workout below.")};try{player.setVideoURI(Uri.parse(uri))}catch(_:Exception){toast("Video unavailable. Attach it again.")};add(button("Play Video"){player.start()})}
        add(button(if(uri==null)"Attach Local Video" else "Replace Local Video",false){pendingMediaLesson=lesson.id;startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="video/*";addCategory(Intent.CATEGORY_OPENABLE);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)},30)})
        add(button("Log Workout Done",false){AlertDialog.Builder(this).setTitle("Record completed workout?").setMessage("Record ${lesson.title} for today? Only confirm after you have completed it.").setPositiveButton("Log Workout"){_,_->safely{repo.logWorkout(lesson,LocalDate.now().toString(),"video library");toast("Workout recorded");trackerTab="Workouts";show("tracker")}}.setNegativeButton("Cancel",null).show()})
    }
    @Deprecated("System document picker") override fun onActivityResult(request:Int,result:Int,data:Intent?){super.onActivityResult(request,result,data);if(request==30&&result==RESULT_OK){val id=pendingMediaLesson?:return;val uri=data?.data?:return;try{contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);repo.setMedia(id,uri.toString());toast("Local video attached");show("video",false)}catch(_:Exception){toast("Unable to retain video access. Please select another file.")};pendingMediaLesson=null}}
    private fun courses(){title("Short Courses","Learn at your own pace.");LearningContent.courses.forEach{c->card(c.title,"${c.lessonIds.size} lessons · R${c.price} · ${if(repo.purchased(c.id))"Owned (demo)" else "Sample course"}",c.image){courseId=c.id;show("course")}}}
    private fun course(){val c=LearningContent.course(courseId);title(c.title,"${c.lessonIds.size} lessons · R${c.price}");add(text(c.description));val owned=repo.purchased(c.id);add(text(if(owned)"Purchased in demo mode. Your lessons are unlocked on this device." else "Lesson previews are shown below. Access through this course unlocks after a simulated purchase."));c.lessonIds.forEach{id->val l=LearningContent.lesson(id);add(button("${if(owned)"Open" else "Locked"}: ${l.title}",false){if(owned)openLesson(id,c.id)else toast("Complete the dummy checkout to unlock this course.")})};add(button(if(owned)"View My Courses" else "Buy Course · R${c.price}"){show(if(owned)"purchased" else "checkout")});add(text("Sample pricing and lessons. Videos are coming soon. This demo does not provide secure paid-content protection; cloud entitlements will be required before real sales.",12f))}
    private fun checkout(){val c=LearningContent.course(courseId);title("PayFast · Dummy Checkout","Simulation only — no payment is collected.");add(text(c.title,24f,true));add(text("Total: R${c.price}\nNo card details or PayFast credentials are requested. This is a local simulation, not a PayFast transaction."));add(button("Simulate Successful Payment"){safely{if(repo.simulatePurchase(c))toast("Demo purchase saved. Course unlocked.")else toast("You already own this demo course.");show("purchased")}});add(button("Simulate Failed Payment",false){toast("Simulated failure: course remains locked.")});add(button("Cancel Checkout",false){toast("Checkout cancelled. No purchase created.");show("course")})}
    private fun purchased(){title("My Courses","Courses purchased in local demo mode.");val rows=repo.purchases();if(rows.isEmpty()){add(text("You haven't purchased any courses yet."));add(button("Browse Courses"){show("courses")})};rows.forEach{p->val c=LearningContent.courses.find{it.id==p.course}?:return@forEach;card(c.title,"Demo purchase · R${p.amount}\n${java.text.SimpleDateFormat("dd MMM yyyy",java.util.Locale.getDefault()).format(java.util.Date(p.time))}",c.image){courseId=c.id;show("course")}};add(text("Purchases are saved on this device only and remain after logout. This is a single-device preview, not a multi-user account system.",12f))}
    private fun dateButton(value:String,changed:(String)->Unit)=button("Date: $value",false){val d=LocalDate.parse(value);val picker=DatePickerDialog(this,{_,y,m,day->changed(LocalDate.of(y,m+1,day).toString())},d.year,d.monthValue-1,d.dayOfMonth);picker.datePicker.maxDate=System.currentTimeMillis();picker.show()}
    private fun tracker(){title("Tracker","Record weight and completed workouts.");val r=LinearLayout(this);listOf("Weight","Workouts").forEach{t->r.addView(button(t,trackerTab==t){trackerTab=t;show("tracker",false)},LinearLayout.LayoutParams(0,-2,1f))};add(r)
        if(trackerTab=="Weight")weightTracker()else workoutTracker()
    }
    private fun weightTracker(){val rows=repo.weights();if(rows.isNotEmpty())add(text("Latest: ${rows.first().kg} kg",23f,true));val input=input("Weight in kg (e.g. 65.5)");input.inputType=android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;add(input);lateinit var date:Button;date=dateButton(weightDate){weightDate=it;date.text="Date: $it"};add(date);add(button("Save Weight"){val kg=RecordValidation.weight(input.text.toString());if(kg==null)input.error="Enter a valid positive weight in kg" else safely{repo.addWeight(kg,weightDate);toast("Weight recorded");show("tracker",false)}});add(text("Weight History",22f,true));if(rows.isEmpty())add(text("No weight records yet."));rows.forEach{w->add(button("${w.date} · ${w.kg} kg",false){AlertDialog.Builder(this).setTitle("Delete weight record?").setMessage("${w.date} · ${w.kg} kg").setPositiveButton("Delete"){_,_->safely{repo.deleteWeight(w.id);show("tracker",false)}}.setNegativeButton("Cancel",null).show()})}}
    private fun workoutTracker(){val rows=repo.workouts();add(text("${rows.size} workouts recorded · ${rows.sumOf{it.minutes}} minutes",19f,true));val select=Spinner(this);select.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,LearningContent.lessons.map{it.title});select.setSelection(workoutChoice);select.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{override fun onNothingSelected(p:AdapterView<*>?){};override fun onItemSelected(p:AdapterView<*>?,v:View?,position:Int,id:Long){workoutChoice=position}};add(select);lateinit var date:Button;date=dateButton(workoutDate){workoutDate=it;date.text="Date: $it"};add(date);add(button("Record Workout Done"){safely{repo.logWorkout(LearningContent.lessons[workoutChoice],workoutDate);toast("Completed workout recorded");show("tracker",false)}});add(text("Workout History",22f,true));if(rows.isEmpty())add(text("No completed workouts yet."));rows.forEach{w->add(button("${w.date} · ${w.title}\n${w.minutes} mins · ${w.source}",false){AlertDialog.Builder(this).setTitle("Delete workout record?").setMessage(w.title).setPositiveButton("Delete"){_,_->safely{repo.deleteWorkout(w.id);show("tracker",false)}}.setNegativeButton("Cancel",null).show()})}}
}
