package com.sjtu.icarer.common.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
  
public class IconNumberView extends View {  

    String number="0"; 
    Paint paint=new Paint();  
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
  
        int x=getWidth()/2;  
//        int y=getHeight()/2;  
  
          
        //数字为0直接返回  
        if("0".equals(number)){  
            return;  
        }  
          
        //设置画笔为红色  
        paint.setColor(Color.BLUE);  
        //计算小圆形的圆心图标，半径取大图标半径的四分之一  
        canvas.drawCircle(x,x,(float)(x/1.2), paint);  
        paint.setColor(Color.WHITE);  
        //为适应各种屏幕分辨率，字体大小取半径的3.5分之一，具体根据项目需要调节  
        paint.setTextSize((float)(x/1.2));  
        //去除锯齿效果  
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);  
        paint.setAntiAlias(true);  
        //字体加粗  
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));  
        //字体位置设置为以圆心为中心  
        paint.setTextAlign(Paint.Align.CENTER);  
        canvas.drawText(number,x,(float)(1.2*x), paint);  
    }  
      
    //设置数字  
    public void setNumber(String number){  
        this.number=number;  
    }  
  
    public IconNumberView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
}