package co.za.rhythmandflow

import android.content.Context
import android.graphics.*
import android.view.View

/** Resolution-independent line icons with no font or external dependency. */
class SymbolView(context:Context,private val symbol:String,private val tint:Int):View(context) {
    private val pen=Paint(Paint.ANTI_ALIAS_FLAG).apply{color=tint;style=Paint.Style.STROKE;strokeWidth=1.7f;strokeCap=Paint.Cap.ROUND;strokeJoin=Paint.Join.ROUND}
    private fun path(canvas:Canvas,vararg xy:Float){val p=Path();p.moveTo(xy[0],xy[1]);var i=2;while(i<xy.size){p.lineTo(xy[i],xy[i+1]);i+=2};canvas.drawPath(p,pen)}
    override fun onDraw(c:Canvas){super.onDraw(c);c.save();c.scale(width/24f,height/24f)
        when(symbol){
            "home"->{path(c,2f,11f,12f,3f,22f,11f);path(c,5f,10f,5f,21f,10f,21f,10f,14f,14f,14f,14f,21f,19f,21f,19f,10f)}
            "you"->{c.drawCircle(12f,7f,3.5f,pen);c.drawArc(RectF(4f,13f,20f,25f),180f,180f,false,pen);path(c,4f,19f,4f,21f,20f,21f,20f,19f)}
            "move"->{c.drawCircle(13f,4f,2f,pen);path(c,4f,11f,9f,7f,15f,9f,19f,6f);path(c,11f,8f,10f,14f,6f,21f);path(c,10f,14f,15f,15f,18f,21f)}
            "explore"->{c.drawCircle(12f,12f,9f,pen);path(c,16f,7f,13f,14f,7f,17f,10f,10f,16f,7f)}
            "journal"->{path(c,12f,5f,12f,21f);path(c,12f,6f,9f,4f,3f,4f,3f,19f,9f,19f,12f,21f,15f,19f,21f,19f,21f,4f,15f,4f,12f,6f)}
            "back"->path(c,15f,4f,7f,12f,15f,20f)
            "chevron"->path(c,9f,5f,16f,12f,9f,19f)
            "menu"->{for(y in listOf(6f,12f,18f))path(c,4f,y,20f,y)}
            "bell"->{val p=Path();p.moveTo(5f,17f);p.lineTo(7f,14f);p.lineTo(7f,9f);p.cubicTo(7f,2f,17f,2f,17f,9f);p.lineTo(17f,14f);p.lineTo(19f,17f);p.close();c.drawPath(p,pen);c.drawArc(RectF(9f,17f,15f,22f),0f,180f,false,pen)}
            "heart"->{val p=Path();p.moveTo(12f,21f);p.cubicTo(10f,18f,2f,12f,3f,7f);p.cubicTo(4f,2f,10f,2f,12f,7f);p.cubicTo(14f,2f,20f,2f,21f,7f);p.cubicTo(22f,12f,14f,18f,12f,21f);c.drawPath(p,pen)}
            "cart"->{path(c,2f,3f,5f,3f,8f,16f,20f,16f);path(c,6f,6f,22f,6f,20f,13f,8f,13f);c.drawCircle(9f,20f,1.3f,pen);c.drawCircle(19f,20f,1.3f,pen)}
            "sun"->{c.drawCircle(12f,12f,5f,pen);for(i in 0..7){val a=i*Math.PI/4;path(c,(12+8*Math.cos(a)).toFloat(),(12+8*Math.sin(a)).toFloat(),(12+10*Math.cos(a)).toFloat(),(12+10*Math.sin(a)).toFloat())}}
            "battery"->{c.drawRoundRect(RectF(3f,7f,20f,17f),2f,2f,pen);path(c,22f,10f,22f,14f);path(c,7f,10f,7f,14f);path(c,10f,10f,10f,14f)}
            "cloud"->{val p=Path();p.moveTo(5f,19f);p.cubicTo(-1f,19f,0f,10f,6f,10f);p.cubicTo(5f,1f,17f,1f,18f,10f);p.cubicTo(25f,10f,25f,19f,19f,19f);p.close();c.drawPath(p,pen)}
            "leaf"->{val p=Path();p.moveTo(6f,18f);p.cubicTo(0f,10f,10f,2f,21f,3f);p.cubicTo(20f,16f,12f,21f,6f,18f);c.drawPath(p,pen);path(c,3f,22f,16f,8f)}
            "spiral"->{val p=Path();for(i in 0..90){val a=i*.2;val r=i*.1;val x=(12+r*Math.cos(a)).toFloat();val y=(12+r*Math.sin(a)).toFloat();if(i==0)p.moveTo(x,y)else p.lineTo(x,y)};c.drawPath(p,pen)}
            "smile"->{c.drawCircle(12f,12f,9f,pen);c.drawPoint(9f,9f,pen);c.drawPoint(15f,9f,pen);c.drawArc(RectF(7f,9f,17f,17f),0f,180f,false,pen)}
            "moon"->{val p=Path();p.moveTo(16f,3f);p.cubicTo(3f,1f,0f,20f,13f,21f);p.cubicTo(19f,22f,22f,18f,22f,16f);p.cubicTo(9f,20f,7f,8f,16f,3f);c.drawPath(p,pen)}
            "eye"->{val p=Path();p.moveTo(2f,12f);p.quadTo(12f,0f,22f,12f);p.quadTo(12f,24f,2f,12f);c.drawPath(p,pen);c.drawCircle(12f,12f,3f,pen)}
        };c.restore()
    }
}
class WaveView(context:Context):View(context){override fun onDraw(c:Canvas){val p=Paint(Paint.ANTI_ALIAS_FLAG);p.color=Color.rgb(220,234,228);val wave=Path();wave.moveTo(0f,height*.35f);wave.cubicTo(width*.3f,-height*.4f,width*.6f,height*1.5f,width.toFloat(),height*.3f);wave.lineTo(width.toFloat(),height.toFloat());wave.lineTo(0f,height.toFloat());wave.close();c.drawPath(wave,p)}}
