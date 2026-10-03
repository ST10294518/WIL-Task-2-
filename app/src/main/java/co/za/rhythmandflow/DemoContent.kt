package co.za.rhythmandflow

data class Practice(val title:String,val description:String,val minutes:Int,val category:String,val image:Int)
data class Product(val title:String,val price:Int,val category:String,val image:Int)
object DemoContent {
    val practices=listOf(
        Practice("Full Body Flow","A gentle flow to move and reset.",20,"Yoga",R.drawable.flow),
        Practice("Move & Release","Release tension and feel lighter.",15,"Stretch",R.drawable.move_release),
        Practice("Dance with Joy","Feel-good movement for your mood.",25,"Dance",R.drawable.dance),
        Practice("Strength & Confidence","Build strength with gentle barre movement.",20,"Barre",R.drawable.yoga)
    )
    val products=listOf(
        Product("Yoga Mat",650,"Accessories",R.drawable.mat),
        Product("Stainless Steel Bottle",450,"Accessories",R.drawable.bottle),
        Product("Resistance Bands",320,"Accessories",R.drawable.bands),
        Product("Wellness Journal",280,"Self-Care",R.drawable.notebook)
    )
}
