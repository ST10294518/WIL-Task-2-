package co.za.rhythmandflow

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.util.ArrayDeque

/** Native frontend rebuilt from the supplied 16-screen wireframe. No cloud login is simulated. */
class MainActivity : Activity() {
    private val cream = Color.rgb(250,248,245)
    private val ink = Color.rgb(30,39,37)
    private val muted = Color.rgb(100,115,120)
    private val teal = Color.rgb(8,119,115)
    private val line = Color.rgb(228,233,230)
    private val pale = Color.rgb(234,243,239)
    private val blush = Color.rgb(249,239,233)
    private lateinit var store: LocalStore
    private lateinit var root: LinearLayout
    private lateinit var body: LinearLayout
    private var page = "welcome"
    private val history = ArrayDeque<String>()
    private var moveFilter = "All"
    private var shopFilter = "All"
    private var journalTab = "Check-in"
    private var youTab = "Overview"
    private var selectedPractice = DemoContent.practices[0]
    private var selectedArticle = "Finding Balance in Everyday Life"
    private var selectedFeeling = ""
    private var timer: CountDownTimer? = null
    private var profilePhoto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = LocalStore(this)
        profilePhoto = store.photo
        window.statusBarColor = cream
        window.navigationBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        show(savedInstanceState?.getString("page") ?: if(store.demoSession) "home" else "welcome", false)
    }
    override fun onSaveInstanceState(out: Bundle) { out.putString("page",page); super.onSaveInstanceState(out) }
    override fun onDestroy() { timer?.cancel(); super.onDestroy() }
    @Deprecated("Legacy back dispatch for native Activity")
    override fun onBackPressed() { if(history.isNotEmpty()) show(history.removeLast(),false) else if(page != "home" && page !in listOf("welcome","login")) show("home",false) else super.onBackPressed() }
    private fun dp(n:Int) = (n * resources.displayMetrics.density).toInt()
    private fun bg(color:Int=Color.WHITE, radius:Int=14, stroke:Boolean=false) = GradientDrawable().apply { setColor(color); cornerRadius=dp(radius).toFloat(); if(stroke)setStroke(dp(1),line) }
    private fun col(padding:Int=0) = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(dp(padding),dp(padding),dp(padding),dp(padding)) }
    private fun row() = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
    private fun text(s:String,size:Float=14f,color:Int=ink,serif:Boolean=false,bold:Boolean=false) = TextView(this).apply {
        text=s; textSize=size; setTextColor(color); typeface=Typeface.create(if(serif)"serif" else "sans-serif",if(bold)Typeface.BOLD else Typeface.NORMAL)
        setLineSpacing(dp(2).toFloat(),1.05f)
    }
    private fun add(parent:LinearLayout,v:View,height:Int=-2,top:Int=10) { parent.addView(v,LinearLayout.LayoutParams(-1,if(height<0)height else dp(height)).apply { topMargin=dp(top) }) }
    private fun heading(s:String,size:Float=29f) = text(s,size,ink,true)
    private fun action(label:String,primary:Boolean=true,fn:()->Unit):Button = Button(this).apply {
        text=label; isAllCaps=false; textSize=14f; setTextColor(if(primary) Color.WHITE else teal); background=bg(if(primary)teal else Color.WHITE,12,!primary)
        minHeight=dp(48); minimumHeight=dp(48); stateListAnimator=null; setPadding(dp(12),dp(4),dp(12),dp(4)); setOnClickListener{hideKeyboard();fn()}
    }
    private fun icon(name:String,label:String,color:Int=muted,fn:()->Unit):View = FrameLayout(this).apply {
        contentDescription=label; isFocusable=true; background=bg(Color.TRANSPARENT,10); addView(SymbolView(this@MainActivity,name,color),FrameLayout.LayoutParams(dp(24),dp(24),Gravity.CENTER));setOnClickListener{fn()}
        layoutParams=LinearLayout.LayoutParams(dp(48),dp(48))
    }
    private fun field(hint:String,password:Boolean=false,multi:Boolean=false):EditText = EditText(this).apply {
        this.hint=hint; textSize=14f;setTextColor(ink);setHintTextColor(muted);background=bg(Color.WHITE,10,true);setPadding(dp(13),dp(12),dp(13),dp(12));minHeight=dp(48)
        inputType=if(password)129 else if(multi) android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE else android.text.InputType.TYPE_CLASS_TEXT
        if(multi) { gravity=Gravity.TOP;minLines=4 } else setSingleLine(true)
        contentDescription=hint
    }
    private fun password(parent:LinearLayout):EditText {
        val r=row();r.background=bg(Color.WHITE,10,true);val e=field("Password",true);e.background=null;r.addView(e,LinearLayout.LayoutParams(0,-2,1f))
        var visible=false;r.addView(icon("eye","Show or hide password"){visible=!visible;e.inputType=if(visible)145 else 129;e.setSelection(e.text.length)})
        add(parent,r);return e
    }
    private fun photo(id:Int,height:Int=140):ImageView = ImageView(this).apply { setImageResource(id);scaleType=ImageView.ScaleType.CENTER_CROP;background=bg();clipToOutline=true;layoutParams=LinearLayout.LayoutParams(-1,dp(height));importantForAccessibility=View.IMPORTANT_FOR_ACCESSIBILITY_NO }
    private fun card(title:String,sub:String="",image:Int=0,fn:(()->Unit)?=null):View {
        val r=row();r.background=bg(Color.WHITE,13,true)
        if(image!=0)r.addView(photo(image,86),LinearLayout.LayoutParams(dp(94),dp(86)))
        val c=col(12);add(c,text(title,17f,ink,true,true),top=0);if(sub.isNotEmpty())add(c,text(sub,12f,muted),top=4)
        r.addView(c,LinearLayout.LayoutParams(0,-2,1f));if(fn!=null){r.addView(icon("chevron","Open $title",teal){fn()});r.setOnClickListener{fn()};r.isFocusable=true}
        return r
    }
    private fun tabs(parent:LinearLayout,labels:List<String>,selected:String,fn:(String)->Unit) {
        val scroll=HorizontalScrollView(this);scroll.isHorizontalScrollBarEnabled=false;val r=row()
        labels.forEach { label -> val t=text(label,12f,if(label==selected)Color.WHITE else muted,bold=true);t.gravity=Gravity.CENTER;t.setPadding(dp(16),dp(11),dp(16),dp(11));t.minHeight=dp(48);t.background=bg(if(label==selected)teal else Color.WHITE,24,true);t.isFocusable=true;t.isSelected=label==selected;t.setOnClickListener{fn(label)};r.addView(t,LinearLayout.LayoutParams(-2,-2).apply{rightMargin=dp(6)}) }
        scroll.addView(r);add(parent,scroll,top=12)
    }
    private fun header(title:String,sub:String="",back:Boolean=false,cart:Boolean=false) {
        val r=row();if(back)r.addView(icon("back","Back"){onBackPressed()})
        val c=col();add(c,heading(title),top=0);if(sub.isNotEmpty())add(c,text(sub,14f,muted),top=4);r.addView(c,LinearLayout.LayoutParams(0,-2,1f))
        r.addView(if(cart)icon("cart","Shopping cart",teal){go("cart")} else icon("bell","Notifications"){go("notifications")});add(body,r,top=4)
    }
    private fun show(next:String,remember:Boolean=true) {
        timer?.cancel();timer=null
        if(remember && page!=next)history.addLast(page)
        page=next;root=col();root.setBackgroundColor(cream);setContentView(root)
        val frame=FrameLayout(this);root.addView(frame,LinearLayout.LayoutParams(-1,0,1f))
        val sc=ScrollView(this).apply{isFillViewport=true;isVerticalScrollBarEnabled=false}
        val container=col();container.gravity=Gravity.CENTER_HORIZONTAL;sc.addView(container)
        body=col(20);container.addView(body,LinearLayout.LayoutParams(if(resources.configuration.screenWidthDp>600)dp(560) else -1,-2));frame.addView(sc)
        when(next) {
            "welcome"->welcome(frame);"login"->login();"signup"->signup();"home"->home();"rhythm"->rhythm();"move"->move();"practice"->practice();"player"->player(false);"complete"->complete();"explore"->explore();"article"->article();"journal"->journal();"meditation"->meditation();"meditationPlayer"->player(true);"you"->you();"editProfile"->editProfile();"shop"->shop();"settings"->settings();"cart"->cart();"notifications"->notifications();"journeys"->journeys();else->home()
        }
        if(next !in listOf("welcome","login","signup","player","meditationPlayer","complete","practice"))bottom()
    }
    private fun go(next:String)=show(next)
    private fun hideKeyboard(){(getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(root.windowToken,0);root.clearFocus()}
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    private fun dialog(title:String,message:String)=AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("OK",null).show()
    private fun logo(height:Int=80)=ImageView(this).apply{setImageResource(R.drawable.rhythm_flow_logo);scaleType=ImageView.ScaleType.FIT_CENTER;contentDescription="Rhythm and Flow logo";layoutParams=LinearLayout.LayoutParams(-1,dp(height))}
    private fun welcome(frame:FrameLayout) {
        frame.removeAllViews();val image=ImageView(this);image.setImageResource(R.drawable.welcome_photo);image.scaleType=ImageView.ScaleType.CENTER_CROP;frame.addView(image,FrameLayout.LayoutParams(-1,-1))
        val shade=View(this);shade.background=GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,intArrayOf(0xBFFFFFFF.toInt(),0x22FFFFFF,0xBBFFFFFF.toInt()));frame.addView(shade,FrameLayout.LayoutParams(-1,-1))
        val sc=ScrollView(this).apply{isFillViewport=true};val c=col(28);c.gravity=Gravity.CENTER_HORIZONTAL;sc.addView(c);frame.addView(sc)
        add(c,logo(145),top=30);add(c,text("A kinder\nstronger\nhappier you",38f,ink,true).apply{typeface=Typeface.create("serif",Typeface.ITALIC)},top=18)
        add(c,text("MOVE  ·  NOURISH  ·  REFLECT\nRECONNECT  ·  GROW",12f,ink,bold=true).apply{gravity=Gravity.CENTER},top=30)
        c.addView(Space(this),LinearLayout.LayoutParams(-1,0,1f));add(c,action("Get Started   →"){go("login")},top=45);add(c,text("Your personal Rhythm & Flow space",12f).apply{gravity=Gravity.CENTER},top=12)
    }
    private fun login() {
        add(body,logo(90),top=25);add(body,heading("Welcome Back!").apply{gravity=Gravity.CENTER},top=16);add(body,text("Log in to continue your wellness journey.",13f,muted).apply{gravity=Gravity.CENTER},top=6)
        val email=field("Username or Email");add(body,email,top=24);val pass=password(body)
        add(body,text("Forgot Password?",12f,teal,bold=true).apply{gravity=Gravity.RIGHT;minHeight=dp(48);setOnClickListener{dialog("Password reset","Password reset will be available when cloud authentication is connected.")}})
        add(body,action("Log In   →"){if(email.text.isBlank()){email.error="Enter your username or email"}else if(pass.text.length<6){pass.error="Enter at least 6 characters"}else demo(email.text.toString().substringBefore('@'))})
        add(body,text("OR",12f,muted).apply{gravity=Gravity.CENTER},top=16)
        add(body,action("G   Continue with Google",false){dialog("Google sign-in","Google sign-in needs the backend configuration. You can use Preview App to explore the frontend.")})
        add(body,action("Don't have an account?  Sign up",false){go("signup")})
        add(body,action("Preview App",false){demo(store.name)})
        add(body,text("Frontend preview: cloud sign-in is not connected yet.",11f,muted).apply{gravity=Gravity.CENTER},top=16);add(body,WaveView(this),50,20)
    }
    private fun demo(name:String) { store.name=name.ifBlank{"Alex"};store.demoSession=true;history.clear();show("home",false);toast("Frontend preview opened") }
    private fun signup() {
        header("Create Your Account","Join Rhythm and Flow today.",true);val name=field("Full Name");val user=field("Username");val email=field("Email").apply{inputType=33}
        add(body,name,top=22);add(body,user);add(body,email);val pass=password(body)
        add(body,action("Sign Up   →"){
            when{ name.text.isBlank()->name.error="Enter your name";user.text.isBlank()->user.error="Enter a username";!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()->email.error="Enter a valid email";pass.text.length<6->pass.error="Use at least 6 characters";else->{store.email=email.text.toString();demo(name.text.toString());toast("Profile saved locally for preview")}}
        },top=18)
        add(body,action("Already have an account?  Log in",false){go("login")});add(body,text("This creates a local preview profile. Cloud registration will be connected next.",12f,muted),top=18);add(body,WaveView(this),65,24)
    }
    private fun home() {
        val r=row();r.addView(icon("menu","Menu",teal){go("settings")});r.addView(logo(64),LinearLayout.LayoutParams(0,dp(64),1f));r.addView(icon("bell","Notifications"){go("notifications")});add(body,r,top=0)
        add(body,heading("Good morning, ${store.name.substringBefore(' ')}",27f));add(body,text("Take a moment to check in with yourself.\nHow are you feeling today?",14f,muted),top=6)
        val moods=listOf("Energised","Tired","Stressed","Grounded","Overwhelmed","Happy","Low","Need some space")
        val symbols=listOf("sun","battery","cloud","leaf","spiral","smile","moon","heart")
        val colors=listOf(0xFFFBF0E5.toInt(),0xFFECF2F6.toInt(),0xFFF4E8E8.toInt(),pale,blush,0xFFFBF0E5.toInt(),0xFFECF2F6.toInt(),blush)
        moods.chunked(3).forEachIndexed{ri,items->val rr=row();rr.gravity=Gravity.CENTER;items.forEachIndexed{ci,m->val index=ri*3+ci;val c=col(7);c.gravity=Gravity.CENTER;c.background=bg(colors[index],70);c.addView(SymbolView(this,symbols[index],if(index==1||index==6)0xFF527BBD.toInt() else teal),LinearLayout.LayoutParams(dp(32),dp(32)));add(c,text(m,11f,ink,bold=true).apply{gravity=Gravity.CENTER},top=8);c.contentDescription="Check in: $m";c.isFocusable=true;c.setOnClickListener{store.checkIn(m);toast("Check-in saved: $m");go("rhythm")};rr.addView(c,LinearLayout.LayoutParams(0,dp(102),1f).apply{setMargins(dp(4),dp(4),dp(4),dp(4))})};if(items.size==2){rr.addView(Space(this),LinearLayout.LayoutParams(0,1,.5f))};add(body,rr,top=4)}
        quote("A small check-in can\nlead to a big shift.");add(body,action("Your Rhythm Today   →",false){go("rhythm")})
    }
    private fun quote(s:String) {add(body,text("“$s”",19f,ink,true).apply{gravity=Gravity.CENTER;typeface=Typeface.create("serif",Typeface.ITALIC);setPadding(dp(16),dp(18),dp(16),dp(18));background=bg(blush)},top=16)}
    private fun rhythm() {
        header("Your Rhythm Today",back=true);val r=row()
        listOf(Triple("Morning Reset","10 mins",R.drawable.stones),Triple("Nourish Your Body","Recipes",R.drawable.food),Triple("Move & Release","15 mins",R.drawable.move_release)).forEachIndexed{i,item->val c=col();c.background=bg(blush);c.clipToOutline=true;c.addView(photo(item.third,82));add(c,text(item.first,16f,ink,true).apply{gravity=Gravity.CENTER},top=9);add(c,text(item.second,12f,muted).apply{gravity=Gravity.CENTER},top=6);c.setPadding(0,0,0,dp(12));c.setOnClickListener{when(i){0->go("meditation");1->{selectedArticle="Nourish Your Body";go("article")};else->{selectedPractice=DemoContent.practices[1];go("practice")}}};r.addView(c,LinearLayout.LayoutParams(0,-2,1f).apply{setMargins(dp(3),0,dp(3),0)})};add(body,r,top=18)
        add(body,photo(R.drawable.welcome_photo,190),top=18);add(body,heading("Build a life\nthat feels good.",28f),top=12);add(body,action("Explore Today   →"){go("explore")});quote("Progress isn't about doing more,\nit's about coming back to yourself.")
    }
    private fun search(parent:LinearLayout,hint:String,changed:(String)->Unit) {val e=field(hint);add(parent,e,top=14);e.addTextChangedListener(object:TextWatcher{override fun beforeTextChanged(s:CharSequence?,start:Int,count:Int,after:Int){};override fun onTextChanged(s:CharSequence?,start:Int,before:Int,count:Int){changed(s.toString())};override fun afterTextChanged(s:Editable?){} })}
    private fun move() {
        header("Move","Practices to help you feel good.");tabs(body,listOf("All","Yoga","Barre","Dance","Stretch"),moveFilter){moveFilter=it;show("move",false)}
        val list=col();var query="";fun render(){list.removeAllViews();val found=DemoContent.practices.filter{(moveFilter=="All"||it.category==moveFilter)&&it.title.contains(query,true)};found.forEach{p->add(list,card(p.title,"${p.description}\n◷ ${p.minutes} mins  ·  All levels",p.image){selectedPractice=p;go("practice")},top=10)};if(found.isEmpty())add(list,text("No practices found. Try another search.",14f,muted))}
        search(body,"Search practices…"){query=it;render()};add(body,list,top=0);render()
    }
    private fun detailHero(image:Int,savedKey:String) {val r=row();r.addView(icon("back","Back"){onBackPressed()});r.addView(Space(this),LinearLayout.LayoutParams(0,1,1f));r.addView(icon("heart","Save or unsave",teal){store.toggleSaved(savedKey);toast(if(store.saved.contains(savedKey))"Saved to your favourites" else "Removed from favourites")});add(body,r,top=0);add(body,photo(image,210),top=4)}
    private fun practice() {
        val p=selectedPractice;detailHero(p.image,p.title);add(body,heading(p.title),top=15);add(body,text("◷ ${p.minutes} mins   ·   All levels   ·   ${p.category}",12f,teal));add(body,text(p.description+" Breathe and reconnect with your body.",14f,muted))
        add(body,text("What you'll need",17f,ink,true,true),top=18);val r=row();listOf("Yoga Mat","Water","Open Space").forEach{v->val c=text(v,12f,muted).apply{gravity=Gravity.CENTER;setPadding(dp(5),dp(22),dp(5),dp(22));background=bg(pale)};r.addView(c,LinearLayout.LayoutParams(0,-2,1f).apply{setMargins(dp(3),0,dp(3),0)})};add(body,r)
        add(body,action("Begin Practice   →"){go("player")},top=18);detailTabs(false)
    }
    private fun detailTabs(meditation:Boolean) {
        val area=col();tabs(body,listOf("About","Guide","Related"),"About"){tab->area.removeAllViews();when(tab){"About"->add(area,text(if(meditation)"A quiet moment to pause and return to yourself." else "Move at your own pace. Choose comfortable movements and rest whenever you need.",14f,muted));"Guide"->add(area,text(if(meditation)"1. Sit comfortably.\n2. Relax your shoulders.\n3. Breathe slowly and naturally.\n4. Notice each breath without judgement." else "1. Prepare your space.\n2. Warm up gently.\n3. Follow comfortable movements.\n4. Finish with a quiet breath.",14f,muted));else->add(area,card("Breathwork for Calm","6 mins · Meditation",R.drawable.yoga){go("meditation")})}}
        add(body,area);add(area,text(if(meditation)"A quiet moment to pause and return to yourself." else "Move at your own pace. Choose comfortable movements and rest whenever you need.",14f,muted))
    }
    private fun player(meditation:Boolean) {
        header(if(meditation)"6-Minute Reset" else selectedPractice.title,"Practice timer",true);add(body,photo(if(meditation)R.drawable.stones else selectedPractice.image,220));val remaining=text("",48f,teal,true).apply{gravity=Gravity.CENTER};add(body,remaining,top=24)
        add(body,text(if(meditation)"Breathe in gently. Breathe out slowly.\nReturn your attention to this moment." else "Follow your own comfortable movement routine.\nGuided media will be connected in the backend stage.",14f,muted).apply{gravity=Gravity.CENTER})
        var millis=(if(meditation)6 else selectedPractice.minutes)*60_000L;var running=false;lateinit var control:Button
        fun update(){remaining.text="%02d:%02d".format(millis/60000,(millis/1000)%60)}
        fun start(){running=true;control.text="Pause";timer=object:CountDownTimer(millis,1000){override fun onTick(left:Long){millis=left;update()};override fun onFinish(){millis=0;update();store.completed++;go("complete")}}.start()}
        control=action("Start"){if(running){timer?.cancel();running=false;control.text="Resume"}else start()};add(body,control,top=24);add(body,action("Finish Practice",false){store.completed++;go("complete")});update()
    }
    private fun complete() {
        add(body,text("✓",56f,teal).apply{gravity=Gravity.CENTER},top=25);add(body,heading("Practice Complete!").apply{gravity=Gravity.CENTER});add(body,text("You showed up for yourself.\nHow do you feel now?",14f,muted).apply{gravity=Gravity.CENTER})
        tabs(body,listOf("Calmer","More energised","Lighter","Stronger","More present","Other"),selectedFeeling){selectedFeeling=it;show("complete",false)}
        val note=field("What did your body need today?",multi=true);add(body,note,top=20);add(body,action("Save to Journal   →"){if(note.text.isBlank()&&selectedFeeling.isBlank())note.error="Add a reflection or choose a feeling" else {store.addEntry(note.text.toString().ifBlank{selectedFeeling},selectedFeeling);toast("Reflection saved");journalTab="Journal";go("journal")}});add(body,action("Back to Home",false){go("home")})
    }
    private fun explore() {
        header("Explore","A holistic wellness library.");val list=col();val data=listOf(Triple("Feed Your Soul","Mindfulness, meditation, emotional wellbeing.",R.drawable.yoga),Triple("Nourish Your Body","Nutrition, food, self-care.",R.drawable.food),Triple("Open Your Mind","Personal development, mindset, coaching.",R.drawable.mind),Triple("Discover New Journeys","Travel, experiences, trying something new.",R.drawable.travel),Triple("Coaching & Programmes","Guided support for your next chapter.",R.drawable.coach),Triple("Shop","Wellness products for everyday living.",R.drawable.shop_photo))
        fun render(q:String){list.removeAllViews();val found=data.filter{(it.first+it.second).contains(q,true)};found.forEach{d->add(list,card(d.first,d.second,d.third){when(d.first){"Shop"->go("shop");"Coaching & Programmes"->go("journeys");"Feed Your Soul"->go("meditation");else->{selectedArticle=if(d.first=="Open Your Mind")"Finding Balance in Everyday Life" else d.first;go("article")}}})};if(found.isEmpty())add(list,text("No results found.",14f,muted))}
        search(body,"Search articles, practices and more…"){render(it)};add(body,list,top=0);render("")
    }
    private fun article() {
        detailHero(if(selectedArticle=="Nourish Your Body")R.drawable.food else R.drawable.coast,selectedArticle);add(body,heading(selectedArticle,29f),top=18);add(body,text("MINDSET  ·  5 MIN READ",11f,muted,bold=true));add(body,text("Balance isn't about doing it all; it's about creating space for what matters most. Here are a few simple ways to bring more intention into your everyday life.",15f,muted))
        val full=text("Make room for small moments\n\nBegin with one manageable intention. Pause during your day and notice what you need: rest, movement, nourishment or connection.\n\nA gentler rhythm\n\nYou don't need to do everything at once. Choose one thing that matters to you, give it your attention, and allow space for rest.\n\nReflect without judgement\n\nWrite down what felt good today and what you would like to change tomorrow. Small, consistent choices can help you find your rhythm.\n\nSample frontend article content.",14f,muted).apply{visibility=View.GONE}
        add(body,action("Read Full Article   →"){full.visibility=if(full.visibility==View.VISIBLE)View.GONE else View.VISIBLE});add(body,full);add(body,text("Related Articles",19f,ink,true,true),top=22);add(body,card("The Power of Self-Compassion","5 min read",R.drawable.flow){selectedArticle="The Power of Self-Compassion";show("article",false)})
    }
    private fun journal() {
        header("Journal","Reconnect with yourself.");tabs(body,listOf("Check-in","Journal","Meditation","Affirmations"),journalTab){journalTab=it;show("journal",false)}
        when(journalTab){
            "Meditation"->add(body,card("6-Minute Reset","Breathe, reset and be present.",R.drawable.stones){go("meditation")})
            "Affirmations"->{quote("I am enough, exactly as I am,\nand I am always growing.");quote("I can give myself permission to rest.");add(body,action("Save affirmation",false){store.toggleSaved("Daily Affirmation");toast("Affirmation preference updated")})}
            else->{val c=col(14);c.background=bg(blush,14,true);add(c,text("Today's Reflection",20f,ink,true,true),top=0);add(c,text("What's on your mind?",13f,muted),top=4);val e=field("Write freely…",multi=true);add(c,e);add(c,action("Save Entry   →"){if(e.text.isBlank())e.error="Write something before saving" else{store.addEntry(e.text.toString());e.setText("");toast("Journal entry saved");journalTab="Journal";show("journal",false)}});add(body,c,top=16)
                if(journalTab=="Journal"){add(body,text("Your Entries",20f,ink,true,true),top=20);val entries=store.entries();if(entries.length()==0)add(body,text("Your reflections will appear here.",14f,muted));for(i in entries.length()-1 downTo 0){val item=entries.getJSONObject(i);add(body,card(item.getString("date"),item.getString("text")){AlertDialog.Builder(this).setTitle("Journal entry").setMessage(item.getString("text")).setPositiveButton("Close",null).setNegativeButton("Delete"){_,_->store.deleteEntry(i);show("journal",false)}.show()})}}
                else {val a=col(14);a.background=bg(blush,14,true);add(a,text("Daily Affirmation",20f,ink,true,true),top=0);add(a,text("“I am enough, exactly as I am, and I am always growing.”",18f,ink,true).apply{typeface=Typeface.create("serif",Typeface.ITALIC)});add(body,a);add(body,card("Gratitude","What are you grateful for today?"){val input=field("Today I'm grateful for…",multi=true);AlertDialog.Builder(this).setTitle("Gratitude").setView(input).setPositiveButton("Save"){_,_->if(input.text.isNotBlank()){store.addEntry("Gratitude: "+input.text);toast("Gratitude saved")}else toast("Nothing saved: entry was empty")}.setNegativeButton("Cancel",null).show()})}
            }
        }
    }
    private fun meditation() {detailHero(R.drawable.stones,"6-Minute Reset");add(body,heading("6-Minute Reset"),top=16);add(body,text("◷ 6 mins   ·   All levels   ·   Meditation",12f,teal));add(body,text("A short guided meditation to help you breathe, reset and be present.",14f,muted));add(body,action("Start Meditation   →"){go("meditationPlayer")},top=20);detailTabs(true);add(body,text("You might also like",20f,ink,true,true),top=20);add(body,card("Breathwork for Calm","6 mins",R.drawable.yoga){go("meditationPlayer")})}
    private fun you() {
        header("You","Your journey, your way.");tabs(body,listOf("Overview","Intentions","Preferences","Saved"),youTab){youTab=it;show("you",false)}
        if(youTab=="Saved"){if(store.saved.isEmpty())add(body,text("Save practices or articles using the heart icon.",14f,muted),top=20);store.saved.sorted().forEach{s->add(body,card(s,"Saved to your collection"){val p=DemoContent.practices.find{it.title==s};when{p!=null->{selectedPractice=p;go("practice")};s=="6-Minute Reset"->go("meditation");s=="Daily Affirmation"->{journalTab="Affirmations";go("journal")};else->{selectedArticle=s;go("article")}}})};return}
        if(youTab=="Overview"){val c=col(16);c.background=bg(pale);add(c,text("Your Journey",22f,ink,true,true),top=0);add(c,text("You've shown up for yourself ${store.monthlyCheckins()} times this month.\n${store.completed} practices completed.",14f,muted));c.setOnClickListener{go("journeys")};add(body,c,top=16)}
        if(youTab!="Preferences"){add(body,text("How do you want to feel?",20f,ink,true,true),top=20);add(body,text("Select your wellness intentions:",13f,muted),top=4)
            val intents=listOf("Move more","Create balance","Reduce stress","Nourish my body","Improve sleep","Connect with myself","Build confidence","Make time for myself")
            intents.chunked(2).forEach{pair->val r=row();pair.forEach{v->val cb=CheckBox(this).apply{text=v;textSize=12f;setTextColor(muted);buttonTintList=android.content.res.ColorStateList.valueOf(teal);isChecked=store.intentions.contains(v);minHeight=dp(48);setOnCheckedChangeListener{_,checked->store.setIntention(v,checked)}};r.addView(cb,LinearLayout.LayoutParams(0,-2,1f))};add(body,r,top=0)}}
        if(youTab!="Intentions"){add(body,text("Favourite practices:",19f,ink,true,true),top=16);listOf("Yoga","Dance","Barre","Meditation","Breathwork","Stretch","Walking","Strength").chunked(4).forEach{items->val r=row();items.forEach{v->val t=text(v,11f,if(store.favourites.contains(v))Color.WHITE else muted).apply{gravity=Gravity.CENTER;background=bg(if(store.favourites.contains(v))teal else pale,24);minHeight=dp(48);setOnClickListener{store.toggleFavourite(v);show("you",false)}};r.addView(t,LinearLayout.LayoutParams(0,dp(48),1f).apply{setMargins(dp(2),dp(3),dp(2),dp(3))})};add(body,r,top=0)}}
        add(body,action("Edit Profile",false){go("editProfile")},top=18);add(body,action("Settings",false){go("settings")})
    }
    private fun editProfile() {
        header("Edit Profile",back=true);val c=col(14);c.background=bg(Color.WHITE,14,true);add(c,text("Profile Photo",16f,ink,true,true),top=0)
        val avatar=photo(R.drawable.avatar,115);avatar.scaleType=ImageView.ScaleType.CENTER_CROP;if(profilePhoto!=null)try{avatar.setImageURI(android.net.Uri.parse(profilePhoto))}catch(_:Exception){}
        avatar.background=bg(pale,100);avatar.contentDescription="Choose profile photo";avatar.setOnClickListener{val intent=Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="image/*";addCategory(Intent.CATEGORY_OPENABLE)};startActivityForResult(intent,20)};c.addView(avatar,LinearLayout.LayoutParams(dp(115),dp(115)).apply{gravity=Gravity.CENTER_HORIZONTAL;topMargin=dp(12)})
        add(c,text("Tap photo to change",11f,muted).apply{gravity=Gravity.CENTER});add(c,text("Full Name",13f,ink,bold=true));val n=field("Full Name");n.setText(store.name);add(c,n,top=5);add(c,text("Email",13f,ink,bold=true));val e=field("Email").apply{inputType=33};e.setText(store.email);add(c,e,top=5);add(c,text("About Me",13f,ink,bold=true));val about=field("About Me",multi=true);about.setText(store.about);add(c,about,top=5);add(body,c,top=16)
        add(body,action("Save Changes   →"){if(n.text.isBlank())n.error="Enter your name" else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(e.text).matches())e.error="Enter a valid email" else{store.name=n.text.toString();store.email=e.text.toString();store.about=about.text.toString();store.photo=profilePhoto;toast("Profile saved");go("you")}},top=18)
    }
    @Deprecated("Native document picker result")
    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){super.onActivityResult(requestCode,resultCode,data);if(requestCode==20&&resultCode==RESULT_OK){data?.data?.let{uri->try{contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)}catch(_:SecurityException){};profilePhoto=uri.toString();store.photo=profilePhoto;show("editProfile",false)}}}
    private fun shop() {
        header("Shop","Wellness products for everyday living.",cart=true);tabs(body,listOf("All","Activewear","Accessories","Self-Care"),shopFilter){shopFilter=it;show("shop",false)}
        val list=col();var query="";fun render(){list.removeAllViews();val found=DemoContent.products.filter{(shopFilter=="All"||shopFilter==it.category)&&it.title.contains(query,true)};found.chunked(2).forEach{pair->val r=row();pair.forEach{p->val c=col();c.background=bg(Color.WHITE);c.clipToOutline=true;c.addView(photo(p.image,115));val info=col(10);add(info,text(p.title,14f,ink,bold=true),top=0);add(info,text("R${p.price}",14f,muted),top=4);add(info,action("Add to Cart",false){store.addCart(p.title);toast("${p.title} added to cart")},top=8);c.addView(info);r.addView(c,LinearLayout.LayoutParams(0,-2,1f).apply{setMargins(dp(4),dp(6),dp(4),dp(6))})};add(list,r,top=0)};if(found.isEmpty())add(list,text("No products in this category yet.",14f,muted))}
        search(body,"Search products…"){query=it;render()};add(body,list,top=8);render()
    }
    private fun cart() {header("Your Cart",back=true);val cart=store.cart();var total=0;DemoContent.products.forEach{p->val q=cart.optInt(p.title);if(q>0){total+=q*p.price;add(body,card(p.title,"$q × R${p.price} = R${q*p.price}",p.image){AlertDialog.Builder(this).setTitle(p.title).setItems(arrayOf("Add one","Remove one")){_,which->store.addCart(p.title,if(which==0)1 else -1);show("cart",false)}.show()})}};if(total==0)add(body,text("Your cart is empty. Explore the shop to add products.",14f,muted),top=20);else{add(body,heading("Total: R$total",24f),top=24);add(body,action("Checkout   →"){dialog("Checkout","This is a frontend demo cart. Payment processing and order submission require the backend and payment provider.")})};add(body,action("Continue Shopping",false){go("shop")})}
    private fun settings() {
        header("Settings",back=true)
        add(body,card("Account","Personal information"){go("editProfile")},top=20)
        add(body,card("Notifications","Manage your preferences"){go("notifications")})
        add(body,card("Privacy & Security","Keep your data safe"){dialog("Privacy & Security","Preview data is stored on this device. Cloud authentication, account security and data synchronisation will be added in the backend stage.")})
        add(body,card("Help & Support","Get answers and contact us"){dialog("Help & Support","Use the bottom tabs to move around the app. Save reflections in Journal, choose your intentions under You, and explore the practice library. This build is an offline frontend preview.")})
        add(body,card("Terms & Conditions","Read our policies"){dialog("Terms & Conditions","Preview build for the Rhythm & Flow project. Final client-approved terms and privacy policy must be supplied before release. No purchases or cloud accounts are created in this preview.")})
        add(body,action("Log Out",false){AlertDialog.Builder(this).setTitle("Log out?").setMessage("Your local entries will stay on this device.").setPositiveButton("Log Out"){_,_->store.demoSession=false;history.clear();show("welcome",false)}.setNegativeButton("Cancel",null).show()}.apply{setTextColor(0xFFC65C49.toInt());background=bg(blush,12,true)},top=28)
    }
    private fun notifications() {header("Notifications",back=true);val sw=Switch(this).apply{text="Wellness reminders";textSize=14f;isChecked=store.reminders;minHeight=dp(56);setOnCheckedChangeListener{_,v->store.reminders=v;toast("Preference saved")}};add(body,sw,top=20);add(body,text("Reminder preference is saved locally. Push delivery will be connected with the backend.",12f,muted));add(body,card("Make time for yourself","A small check-in can lead to a big shift."){go("home")});add(body,card("Your reflections","${store.entries().length()} journal entries saved on this device."){journalTab="Journal";go("journal")})}
    private fun journeys() {header("Rhythm & Flow Journeys","Curated experiences for your next chapter.",true);listOf("7-Day Nervous System Reset","21 Days of Mindful Movement","Confidence & Self-Belief","Better Sleep").forEach{title->add(body,card(title,"Move, breathe and reconnect.",R.drawable.coach){dialog(title,"Start with a short reset, then reflect on how you feel. Programme content will be supplied by the client.")})};add(body,action("Start a 6-Minute Reset"){go("meditation")})}
    private fun bottom() {
        val nav=row();nav.background=bg(Color.WHITE,0,true);val active=when(page){"rhythm"->"home";"practice","player"->"move";"article","shop","cart","journeys"->"explore";"meditation","meditationPlayer"->"journal";"settings","editProfile","notifications"->"you";else->page}
        listOf("home" to "Home","move" to "Move","explore" to "Explore","journal" to "Journal","you" to "You").forEach{(p,label)->val c=col(5);c.gravity=Gravity.CENTER;val color=if(active==p)teal else muted;c.addView(SymbolView(this,p,color),LinearLayout.LayoutParams(dp(24),dp(24)));add(c,text(label,10f,color,bold=active==p).apply{gravity=Gravity.CENTER},top=3);c.isSelected=active==p;c.isFocusable=true;c.contentDescription=label;c.setOnClickListener{history.clear();show(p,false)};nav.addView(c,LinearLayout.LayoutParams(0,dp(64),1f))};root.addView(nav)
    }
}
