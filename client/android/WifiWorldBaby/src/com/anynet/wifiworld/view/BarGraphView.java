/*
 * 文件名称 : CurveGraphView.java
 * <p>
 * 作者信息 : admin
 * <p>
 * 创建时间 : 2014年9月9日, 下午4:08:12
 * <p>
 * 版权声明 : Copyright (c) 2012-2013 Xunlei Network Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.anynet.wifiworld.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.anynet.wifiworld.util.XLLog;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class BarGraphView extends View
{
    
    private static final String TAG = BarGraphView.class.getSimpleName();
    
    public BarGraphView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
        
    }
    
    private void init()
    {
    }
    
    public BarGraphView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    
    public BarGraphView(Context context)
    {
        super(context);
        init();
    }
    
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    LinearGradient linearGradient;
    
    private int lineWidth = 2;
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        System.out.println("onMeasure " + getMeasuredWidth() + " " + getMeasuredHeight());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        prepereForDraw();
        super.onDraw(canvas);
        long t1 = System.currentTimeMillis();
        
        drawCoordinate(canvas);
        drawLineAndShadow(canvas);
        super.onDraw(canvas);
        XLLog.log(TAG, "ondraw time:" + (System.currentTimeMillis() - t1));
    }
    
    public static class CoordinateFormater
    {
        /** value值对应出现在界面的字符，该字符将替代value出现在坐标系上；如果不显示该value对应的字符，则返回null. */
        public String text;
        
        /** 辅助线的长度 */
        public int assiteLine = ASSITE_LINE_NONE;
        
        public static final int ASSITE_LINE_NONE = 0;
        
        public static final int ASSITE_LINE_MATCH_COORDINATE = -1;
    }
    
    public static abstract class XYListener
    {
        public int getValueY(int valueX)
        {
            return 0;
        }
        
        public String getTextY(int valueX)
        {
            return null;
        }
        
        /**
         * 
         * @param formater
         *            X或者Y轴的数值, 该formater是可以用重的，不必每次都new个再返回。
         *            用法类似月AdapterView的getView方法的convertView参数。
         * @return CoordinateFormater
         */
        public CoordinateFormater getFormaterX(CoordinateFormater formater, int valueX)
        {
            return null;
        }
        
        /**
         * 
         * @param formater
         *            X或者Y轴的数值, 该formater是可以用重的，不必每次都new个再返回。
         *            用法类似月AdapterView的getView方法的convertView参数。
         * @return CoordinateFormater
         */
        public CoordinateFormater getFormaterY(CoordinateFormater formater, int valueX)
        {
            return null;
        }
        
        public int getBarColor(int valueX)
        {
            return Color.GRAY;
        }
        
        public int getBarValueColor(int valueX)
        {
            return Color.GRAY;
        }
    }
    
    private Rect rectCoordinate;//坐标系的位置和大小
    
    private int xCoordinateValueSize = 24;//X轴下面的文字的像素大小
    
    private int xCoordinateValueColor = Color.GRAY;//x轴值得颜色
    
    private int lineWidthOfCoordinateSystem = 1;//坐标系线条的粗细（pix）
    
    private int colorOfCoordinateSystem = Color.GRAY;//坐标系线条的颜色
    
    private int yCoordinateMinValue = 0; //y轴最小值
    
    private int yCoordinateMaxValue = 10;//y轴最大值
    

    private int yCoordinateValueSize = 26; //y值得大小
    
    //    private int barColor = Color.GRAY; //柱子的颜色
    
    private int barCount = 7; //绘制多少个柱子
    
    private int xOffset = 28;//pix, x方向上起始和介绍的空白区域。
    
    private int xValuePadding = 5; //x轴坐标值与x轴的间距。
    
    private int yValuePadding = 10; //y值与柱子的间距
    
    private XYListener xyListener;
    
    private void drawCoordinate(Canvas canvas)
    {
        XLLog.log(TAG, "drawCoordinate");
        mPaint.reset();
        mPaint.setStyle(Style.STROKE);
        mPaint.setShader(null);
        
        mPaint.setStrokeWidth(lineWidthOfCoordinateSystem);
        mPaint.setColor(colorOfCoordinateSystem);
        
        canvas.drawLine(rectCoordinate.left, rectCoordinate.bottom, rectCoordinate.right, rectCoordinate.bottom, mPaint);
    }
    
    private float getTextWidth(Paint paint, String text)
    {
        float width = 0;
        if (text != null)
        {
            float[] widths = new float[text.length()];
            paint.getTextWidths(text, widths);
            for (float f : widths)
            {
                width += f;
            }
        }
        return width;
    }
    
    private void drawLineAndShadow(Canvas canvas)
    {
        XLLog.log(TAG, "drawLineAndShadow");
        mPaint.reset();
        
        int yCoordinateHigth = rectCoordinate.bottom - rectCoordinate.top;//Y轴的像素高度
        int xCoordinateLength = rectCoordinate.right - rectCoordinate.left; //X轴的像素长度
        int barWidth = (xCoordinateLength - 2 * xOffset) / (barCount * 2 - 1); //柱子的宽度
        
        for (int x = 0; x < barCount; x++)
        {
            int valueY = 0;
            String textY = null;
            CoordinateFormater formater = null;
            int barColor = 0;
            int barValueColor = 0;
            if (null != xyListener)
            {
                valueY = xyListener.getValueY(x);
                textY = xyListener.getTextY(x);
                formater = xyListener.getFormaterX(formater, x);
                barColor = xyListener.getBarColor(x);
                barValueColor = xyListener.getBarValueColor(x);
            }
            
            //绘制柱子
            int left = x * 2 * barWidth + xOffset;
            int right = left + barWidth;
            int bottom = rectCoordinate.bottom;
            int barHeight = (int) (yCoordinateHigth * 1.0 * valueY / yCoordinateMaxValue);
            int top = bottom - barHeight;
            
            mPaint.setStyle(Style.FILL);
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(barColor);
            canvas.drawRect(left, top, right, bottom, mPaint);
            
            //绘制x坐标值
            int textX = left + barWidth / 2; //坐标值得中点坐标
            if (formater != null && formater.text != null)
            {
                mPaint.setTextSize(xCoordinateValueSize);
                mPaint.setColor(xCoordinateValueColor);
                float textWidth = getTextWidth(mPaint, formater.text);
                canvas.drawText(formater.text, textX - textWidth / 2, rectCoordinate.bottom + xValuePadding + xCoordinateValueSize, mPaint);
            }
            
            //绘制柱子的值
            if (null != textY)
            {
                mPaint.setTextSize(yCoordinateValueSize);
                mPaint.setColor(barValueColor);
                float textWidth = getTextWidth(mPaint, textY);
                canvas.drawText(textY, textX - textWidth / 2, top - yValuePadding, mPaint);
            }
        }
        
    }
    
    private void prepereForDraw()
    {
        
        int measuredHeight = getMeasuredHeight();
        int bottom = measuredHeight - getPaddingBottom() - xCoordinateValueSize - xOffset; //bottom： 视图的高度 - 底部的Padding - x坐标值的高度 - x坐标值与x轴的间距
        
        int measuredWidth = getMeasuredWidth();
        int right = measuredWidth - getPaddingRight(); //right: 
        rectCoordinate = new Rect(getPaddingLeft(), getPaddingTop() + 1, right, bottom);
    }
    
    public int getLineWidth()
    {
        return lineWidth;
    }
    
    public void setLineWidth(int lineWidth)
    {
        this.lineWidth = lineWidth;
    }
    
    public int getxCoordinateValueTextSize()
    {
        return xCoordinateValueSize;
    }
    
    public void setxCoordinateValueTextSize(int xCoordinateValueTextSize)
    {
        this.xCoordinateValueSize = xCoordinateValueTextSize;
    }
    
    public int getxCoordinateValueColor()
    {
        return xCoordinateValueColor;
    }
    
    public void setxCoordinateValueColor(int xCoordinateValueColor)
    {
        this.xCoordinateValueColor = xCoordinateValueColor;
    }
    
    public int getLineWidthOfCoordinateSystem()
    {
        return lineWidthOfCoordinateSystem;
    }
    
    public void setLineWidthOfCoordinateSystem(int lineWidthOfCoordinateSystem)
    {
        this.lineWidthOfCoordinateSystem = lineWidthOfCoordinateSystem;
    }
    
    public int getColorOfCoordinateSystem()
    {
        return colorOfCoordinateSystem;
    }
    
    public void setColorOfCoordinateSystem(int colorOfCoordinateSystem)
    {
        this.colorOfCoordinateSystem = colorOfCoordinateSystem;
    }
    
    public int getyCoordinateMinValue()
    {
        return yCoordinateMinValue;
    }
    
    public void setyCoordinateMinValue(int yCoordinateMinValue)
    {
        this.yCoordinateMinValue = yCoordinateMinValue;
    }
    
    public int getyCoordinateMaxValue()
    {
        return yCoordinateMaxValue;
    }
    
    public void setyCoordinateMaxValue(int yCoordinateMaxValue)
    {
        this.yCoordinateMaxValue = yCoordinateMaxValue;
    }
    
    public XYListener getXyListener()
    {
        return xyListener;
    }
    
    public void setXyListener(XYListener xyListener)
    {
        this.xyListener = xyListener;
    }
    
    public int getxCoordinateValueSize()
    {
        return xCoordinateValueSize;
    }
    
    public void setxCoordinateValueSize(int xCoordinateValueSize)
    {
        this.xCoordinateValueSize = xCoordinateValueSize;
    }

    
    public int getyCoordinateValueSize()
    {
        return yCoordinateValueSize;
    }
    
    public void setyCoordinateValueSize(int yCoordinateValueSize)
    {
        this.yCoordinateValueSize = yCoordinateValueSize;
    }

    
    public int getBarCount()
    {
        return barCount;
    }
    
    public void setBarCount(int barCount)
    {
        this.barCount = barCount;
    }
    
    public int getxOffset()
    {
        return xOffset;
    }
    
    public void setxOffset(int xOffset)
    {
        this.xOffset = xOffset;
    }
    
    public int getxValuePadding()
    {
        return xValuePadding;
    }
    
    public void setxValuePadding(int xValuePadding)
    {
        this.xValuePadding = xValuePadding;
    }
    
    public int getyValuePadding()
    {
        return yValuePadding;
    }
    
    public void setyValuePadding(int yValuePadding)
    {
        this.yValuePadding = yValuePadding;
    }
    
}
