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

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.anynet.wifiworld.util.ConvertUtil;
import com.anynet.wifiworld.util.XLLog;

/**
 * 交易行情图
 * <p>
 */
public class CurveGraphView extends View
{
    
    private static final String TAG = CurveGraphView.class.getSimpleName();
    
    public static final int BLANK_SPACE = 4; // View后面空两格位置
    public static abstract class BaseCoordinateFormater
    {
        /** value值对应出现在界面的字符，该字符将替代value出现在坐标系上；如果不显示该value对应的字符，则返回null. */
        public String text;
        
        public int textSize = 24;//X轴下面的文字的像素大小
        
        public int textPadding = 8; //x轴坐标值的与坐标系的间距
        
        public int textColor = Color.GRAY;//x轴值得颜色
        
        /** 辅助线的长度 */
        public int assiteLine = ASSITE_LINE_NONE;
        
        /** 辅助线的颜色 */
        public int assiteLineColor = Color.GRAY;
        
        /** 辅助线的宽度 */
        public int assiteLineWidth = 1;
        
        public static final int ASSITE_LINE_NONE = 0;
        
        public static final int ASSITE_LINE_MATCH_COORDINATE = -1;
        
        public boolean assisteLineHint = false;
        
        public int assisteLineHintColor = Color.GRAY;
        
        public int assisteLineHintWidth = 1;
    }
    
    public static class CoordinateXFormater extends BaseCoordinateFormater
    {
        
    }
    
    public static class CoordinateYFormater extends BaseCoordinateFormater
    {
        public int textAlign = TEXT_ALIGN_LEFT_OF_Y_COORDINATE;
        
        public static final int TEXT_ALIGN_LEFT_OF_Y_COORDINATE = 0;
        
        public static final int TEXT_ALIGN_END_OF_ASSITELINE = 1;
    }
    
    public static class Point
    {
        public int x;
        
        public int y;
        
        public Point()
        {
            
        }
        
        public Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }
    
    public static class PointFormater
    {
        public boolean show = false;
        
        public Style style = Style.STROKE;
       
        /**点的颜色*/
        public int color = Color.GREEN;
        
        /**如果点的样式是空心的，可以设置点的填充点颜色*/
        public int colorOfStroke = Color.WHITE;
        
        /**点的半径*/
        public int radius = 5;
        
        /**文字的内容*/
        public String value;
        
        /**点旁边的文字的颜色*/
        public int valueColor = Color.BLUE;
        
        /**文字的字号*/
        public int valueSize = 15;
    }
    
    public static abstract class XYListener
    {
        /**
         * 获取Y轴的值，该值得范围应该在设置的最大和最小之间
         * 
         * @param valueX
         *            x轴的值
         * @return y轴的值。 如果返回Integer.MIN_VALUE，表示忽略该点以及后面的所有的点。
         */
        public int getValueY(int valueX)
        {
            return 0;
        }
        
        /**
         * 获取y轴的显示字符，该字符可以是任意字符，用来显示显示在y轴上
         * 
         * @param valueX
         * @return
         */
        //        public String getTextY(int valueY)
        //        {
        //            return null;
        //        }
        
        /**
         * 
         * @param formater
         *            X或者Y轴的数值, 该formater是可以用重的，不必每次都new个再返回。
         *            用法类似月AdapterView的getView方法的convertView参数。
         * @return CoordinateFormater
         */
        public CoordinateXFormater getFormaterX(CoordinateXFormater formater, int valueX)
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
        public CoordinateYFormater getFormaterY(CoordinateYFormater formater, int valueY)
        {
            return null;
        }
        
        /**
         * 获取坐标点的样式信息：大小、颜色、是否显示。
         * 
         * @return
         */
        public PointFormater getPointFormater(PointFormater formater, int valueX, Point location)
        {
            return null;
        }
    }
    
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    private Paint mPaintForShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    LinearGradient linearGradient;
    
    private int lineWidth = 2;
    
    private Path pathForLine = new Path();
    
    private Path pathForShadow = new Path();
    
    private Point controlUp = new Point();
    
    private Point controlDown = new Point();
    
    private Point highestPoint;
    
    private Point lowestPoint;
    
    private int backgroundColor = Color.WHITE;
    
    private Rect rectCoordinate;//坐标系的位置和大小
    
    private int measuredWidth;//视图的实际像素宽度
    
    private int measuredHeigth;//视图的实际像素高度
    
    //    private int xCoordinateValueHeight = 50; //X轴下面的文字区域的像素高度
    
    private int lineWidthOfCoordinateSystem = 1;//坐标系线条的粗细（pix）
    
    private int lineStartColor = Color.GRAY; //坐标线的起始颜色
    
    private int lineEndColor = Color.GRAY; //坐标线的结束颜色
    
    private int colorOfCoordinateSystem = Color.GRAY;//坐标系线条的颜色
    
    private int sizeOfAssistLine = 1;//坐标系中辅助线的粗细(pix)
    
    private int xCoordinateMinValue = 0; //x轴的最小值
    
    private int xCoordinateMaxValue = 10; //x轴的最大值
    
    private int yCoordinateMinValue = 0; //y轴最小值
    
    private int yCoordinateMaxValue = 10;//y轴最大值
    
    private int yCoordinateValueTextSize = 24;//X轴下面的文字的像素大小
    
    private int xCoordinateOffset = 1;
    
    private int yCoordinateOffset = 0;
    
    private int shadowStartColor = Color.GRAY | 0x50000000;//投影的起始颜色
    
    private int shadowEndColor = Color.GRAY | 0x10000000;//投影的结速颜色
    
    private XYListener xyListener;
    
    /**
     * 是否绘制成贝塞尔曲线，否则绘制为直线
     */
    private boolean drawLineAsBezier = true;
    
    public CurveGraphView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
        
    }
    
    public CurveGraphView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    
    public CurveGraphView(Context context)
    {
        super(context);
        init();
    }
    
    private void init()
    {
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        XLLog.log(TAG, "onMeasure " + getMeasuredWidth() + " " + getMeasuredHeight());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        //清屏, clear canvas
        Drawable drawable = getBackground();
        if (drawable instanceof ColorDrawable)
        {
        	/** 向下兼容android 2.3, color值直接用变量保存 */
            //ColorDrawable bg = (ColorDrawable) drawable;
            //int color = bg.getColor();
            int color = backgroundColor;
            canvas.drawColor(color, Mode.SRC);
        }
        else if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bg = (BitmapDrawable) drawable;
            Bitmap bgBitmap = bg.getBitmap();
            canvas.drawBitmap(bgBitmap, 0, 0, mPaint);
        } // end of clear canvas
        
        prepereForDraw();
        
        long t1 = System.currentTimeMillis();
        
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        System.out.println("width:" + width + " height:" + height);
        
        drawCoordinate(canvas);
        drawXAssitLines(canvas);
        drawYAssitLines(canvas);
        drawLineAndShadow(canvas);
        
        super.onDraw(canvas);
        XLLog.log(TAG, "ondraw time:" + (System.currentTimeMillis() - t1));
    }
    
    private void drawCoordinate(Canvas canvas)
    {
        XLLog.log(TAG, "drawCoordinate");
        mPaint.reset();
        mPaint.setStyle(Style.STROKE);
        mPaint.setShader(null);
        
        mPaint.setStrokeWidth(lineWidthOfCoordinateSystem);
        mPaint.setColor(colorOfCoordinateSystem);
        
        canvas.drawRect(rectCoordinate, mPaint);
    }
    
    private CoordinateYFormater getFormaterY()
    {
        CoordinateYFormater formater = new CoordinateYFormater();
        if (null != xyListener)
        {
            formater = xyListener.getFormaterY(formater, 0);
        }
        formater = null == formater ? new CoordinateYFormater() : formater;
        return formater;
    }
    
    private void drawXAssitLines(Canvas canvas)
    {
        long t1 = System.currentTimeMillis();
        int yCoordinateHigth = rectCoordinate.bottom - rectCoordinate.top;//Y轴的像素高度
        int xCoordinateLength = rectCoordinate.right - rectCoordinate.left; //X轴的像素长度
        CoordinateXFormater formater = null;
        CoordinateXFormater defaultFormater = new CoordinateXFormater();
        for (int i = xCoordinateMinValue + 2; i <= xCoordinateMaxValue - BLANK_SPACE; i++)
        {
            if (null != xyListener)
            {
                formater = xyListener.getFormaterX(formater, i - 2);
            }
            defaultFormater.text = String.valueOf(i);
            defaultFormater.assiteLine = 5;
            formater = (formater == null) ? defaultFormater : formater;
            int assitLineLength = 0;
            switch (formater.assiteLine)
            {
                case CoordinateXFormater.ASSITE_LINE_NONE:
                    break;
                case CoordinateXFormater.ASSITE_LINE_MATCH_COORDINATE:
                    assitLineLength = yCoordinateHigth;
                    break;
                default:
                    assitLineLength = formater.assiteLine;
                    break;
            }
            
            float x = (xCoordinateLength - xCoordinateOffset) / ((xCoordinateMaxValue - xCoordinateMinValue) * 1.0f) * (i - xCoordinateMinValue)
                    + getPaddingLeft() + xCoordinateOffset;
            float startY = rectCoordinate.bottom - assitLineLength;
            float stopY = rectCoordinate.bottom;
            
            if (formater.assiteLine != CoordinateXFormater.ASSITE_LINE_NONE)
            {
                mPaint.setColor(formater.assiteLineColor);
                mPaint.setStrokeWidth(formater.assiteLineWidth);
                mPaint.setStyle(Style.FILL);
                mPaint.setShader(null);
                canvas.drawLine(x, startY, x, stopY, mPaint);
            }
            
            if (formater.assisteLineHint)
            {
                startY = rectCoordinate.top;
                stopY = rectCoordinate.bottom - assitLineLength;
                mPaint.setColor(formater.assisteLineHintColor);
                mPaint.setStrokeWidth(formater.assisteLineHintWidth);
                mPaint.setStyle(Style.FILL);
                mPaint.setShader(null);
                canvas.drawLine(x, startY, x, stopY, mPaint);
            }
            
            if (formater.text != null)
            {
                mPaint.setTextAlign(Align.LEFT);
                mPaint.setTextSize(formater.textSize);
                mPaint.setAntiAlias(true);
                mPaint.setColor(formater.textColor);
                mPaint.setShader(null);
                
                float[] widths = new float[formater.text.length()];
                mPaint.getTextWidths(formater.text, widths);
                float width = 0;
                for (float f : widths)
                {
                    width += f;
                }
                
                Path path = new Path();
                path.moveTo(x - width / 2, rectCoordinate.bottom + formater.textPadding + formater.textSize);
                path.lineTo(x + width / 2, rectCoordinate.bottom + formater.textPadding + formater.textSize);
                canvas.drawTextOnPath(formater.text, path, 0, 0, mPaint);
            }
        }
        long t2 = System.currentTimeMillis();
        XLLog.log(TAG, "drawXAssitLines time:" + (t1 - t1));
        
    }
    
    private void drawYAssitLines(Canvas canvas)
    {
        
        float yCoordinateHigth = rectCoordinate.bottom - rectCoordinate.top;//Y轴的像素高度
        float xCoordinateLength = rectCoordinate.right - rectCoordinate.left; //X轴的像素长度
        CoordinateYFormater formater = null;
        CoordinateYFormater defaultFormater = new CoordinateYFormater();
        for (int i = yCoordinateMaxValue; i >= yCoordinateMinValue; i--)
        {
            if (null != xyListener)
            {
                formater = xyListener.getFormaterY(formater, i);
                //                formater.text = xyListener.getTextY(i);
            }
            formater = (formater == null) ? defaultFormater : formater;
            
            float y = rectCoordinate.bottom - yCoordinateOffset - (yCoordinateHigth - yCoordinateOffset) / (yCoordinateMaxValue - yCoordinateMinValue)
                    * (i - yCoordinateMinValue);
            
            float widthOfText = 0; //Y轴值的宽度
            float maxAssistLineLength = xCoordinateLength;
            
            if (formater.text != null)
            {
                mPaint.setTextAlign(Align.LEFT);
                mPaint.setTextSize(formater.textSize);
                mPaint.setAntiAlias(true);
                mPaint.setColor(formater.textColor);
                mPaint.setShader(null);
                
                widthOfText = ConvertUtil.getTextWidth(mPaint, formater.text);
                
                Path path = new Path();
                
                switch (formater.textAlign)
                {
                    case CoordinateYFormater.TEXT_ALIGN_END_OF_ASSITELINE:
                        path.moveTo(rectCoordinate.right - widthOfText - formater.textPadding, y + formater.textSize / 2f);
                        path.lineTo(rectCoordinate.right - formater.textPadding, y + formater.textSize / 2f);
                        maxAssistLineLength = xCoordinateLength - widthOfText - formater.textPadding * 2;
                        //                        if (assitLineLength > xCoordinateLength - widthOfText - formater.textPadding)
                        //                        {
                        //                            assitLineLength = xCoordinateLength - widthOfText - formater.textPadding;
                        //                            endX = rectCoordinate.left + assitLineLength;
                        //                        }
                        break;
                    
                    default:
                        path.moveTo(rectCoordinate.left - widthOfText - formater.textPadding, y + formater.textSize / 2f);
                        path.lineTo(rectCoordinate.left - formater.textPadding, y + formater.textSize / 2f);
                        
                        break;
                }
                canvas.drawTextOnPath(formater.text, path, 0, 0, mPaint);
            }
            
            //使计算辅助线的长度
            float assitLineLength = 0;
            switch (formater.assiteLine)
            {
                case CoordinateXFormater.ASSITE_LINE_NONE:
                    break;
                case CoordinateXFormater.ASSITE_LINE_MATCH_COORDINATE:
                    assitLineLength = maxAssistLineLength;
                    break;
                default:
                    assitLineLength = formater.assiteLine;
                    assitLineLength = assitLineLength > maxAssistLineLength ? maxAssistLineLength : assitLineLength;
                    break;
            }
            
            //辅助线的起始X坐标和结束X坐标
            if (assitLineLength > 0)
            {
                float startX = rectCoordinate.left;
                float endX = rectCoordinate.left + assitLineLength;
                
                mPaint.setColor(formater.assiteLineColor);
                mPaint.setStrokeWidth(formater.assiteLineWidth);
                mPaint.setStyle(Style.FILL);
                canvas.drawLine(startX, y, endX, y, mPaint);
            }
        }
        
    }
    
    //    private static class XY {
    //        public int x;
    //        public int y;
    //        public XY(int x, int y) {
    //            this.x = x;
    //            this.y = y;
    //        }
    //    }
    private void drawLineAndShadow(Canvas canvas)
    {
        XLLog.log(TAG, "drawLineAndShadow");
        mPaint.reset();
        
        pathForLine.reset();
        pathForShadow.reset();
        
        int yCoordinateHigth = rectCoordinate.bottom - rectCoordinate.top;//Y轴的像素高度
        int xCoordinateLength = rectCoordinate.right - rectCoordinate.left; //X轴的像素长度
        
        float baseX = (xCoordinateLength - xCoordinateOffset) * 1.0f / (xCoordinateMaxValue - xCoordinateMinValue);
        float baseY = (yCoordinateHigth - yCoordinateOffset) * 1.0f / (yCoordinateMaxValue - yCoordinateMinValue);
        ArrayList<Point> xyArray = new ArrayList<Point>();
        
        for (int valueX = xCoordinateMinValue; valueX <= xCoordinateMaxValue - BLANK_SPACE; valueX++)
        {
            int valueY = 0;
            if (null != xyListener)
            {
                valueY = xyListener.getValueY(valueX);
            }
            
            int x1 = (int) (baseX * (valueX - xCoordinateMinValue) + getPaddingLeft()) + xCoordinateOffset; //在该View中的相对x
            
            int yOfCoordinate = (int) (baseY * (valueY - yCoordinateMinValue)) + yCoordinateOffset; //在数学直角坐标系中的y坐标
            int y1 = (yCoordinateHigth - yOfCoordinate) + getPaddingTop(); //坐标系转换，算出在View中的相对y坐标
            
            //            System.out.println("(" + x1 + ", " + y1 + ")  on x=" + x + " y=" + y);
            
            Point point = new Point(x1, y1);
            if (valueX == xCoordinateMinValue)
            {
                highestPoint = point; //初始化为第一个点
                lowestPoint = point;//初始化为第一个点 
            }
            
            if (point.y > highestPoint.y)
            {
                highestPoint = point;
            }
            if (point.y < lowestPoint.y)
            {
                lowestPoint = point;
            }
            
            //当y值等于Integer.MIN_VALUE时，表示绘制曲线点结束，后面的点都不要继续绘制了。
            if (valueY == Integer.MIN_VALUE)
            {
                break;
            }
            //当y值等于Integer.MIN_VALUE + 1时，不绘制该点。
            if (valueY != Integer.MIN_VALUE + 1)
            {
                xyArray.add(point);
            }
            
        }
        
        System.out.println("highestPoint x:" + highestPoint.x + " y:" + highestPoint.y);
        System.out.println("lowestPoint x:" + lowestPoint.x + " y:" + lowestPoint.y);
        //        if (drawLimitLine)
        //        {
        //            String textOnHighest = (null == xyListener) ? null : xyListener.getTextY(highestPoint.x);
        //            String textOnLowest = (null == xyListener) ? null : xyListener.getTextY(lowestPoint.x);
        //            drawYAssitLineOnPoint(canvas, highestPoint, null, textOnHighest);
        //            drawYAssitLineOnPoint(canvas, lowestPoint, null, textOnLowest);
        //        }
        
        if (xyArray.size() <= 0)
        {
            return;
        }
        
        Point firstPoint = xyArray.get(0);
        Point lastPoint = xyArray.get(xyArray.size() - 1);
        
        Point start = new Point(0, 0);
        Point end = new Point(0, 0);
        
        pathForLine.moveTo(firstPoint.x, firstPoint.y);
        pathForShadow.moveTo(firstPoint.x, firstPoint.y);
        PointFormater pointFormater = new PointFormater();
        for (int i = 0; i < xyArray.size() - 1; i++)
        {
            start = xyArray.get(i);
            end = xyArray.get(i + 1);
            //绘制0~~i 个点
            //            drawPoint(canvas, start, xCoordinateMinValue + i, pointFormater);
            
            if (drawLineAsBezier)
            {
                controlUp.x = (start.x + end.x) / 2;
                controlUp.y = start.y;
                controlDown.x = controlUp.x;
                controlDown.y = end.y;
                pathForLine.cubicTo(controlUp.x, controlUp.y, controlDown.x, controlDown.y, end.x, end.y);
                
                start.y += lineWidth;
                end.y += lineWidth;
                controlUp.x = (start.x + end.x) / 2;
                controlUp.y = start.y;
                controlDown.x = controlUp.x;
                controlDown.y = end.y;
                pathForShadow.cubicTo(controlUp.x, controlUp.y, controlDown.x, controlDown.y, end.x, end.y);
            }
            else
            {
                pathForLine.lineTo(end.x, end.y);
                pathForShadow.lineTo(end.x, end.y + lineWidth);
            }
        }
        
        //绘制最后一个点
        //        drawPoint(canvas, xyArray.get(xyArray.size() - 2), xCoordinateMinValue + xyArray.size() - 2, pointFormater);
        //        drawPoint(canvas, xyArray.get(xyArray.size() - 1), xCoordinateMinValue + xyArray.size() - 1, pointFormater);
        
        pathForShadow.lineTo(lastPoint.x, rectCoordinate.bottom);
        pathForShadow.lineTo(firstPoint.x, rectCoordinate.bottom);
        pathForShadow.lineTo(firstPoint.x, firstPoint.y);
        
        mPaint.setColor(lineStartColor);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStyle(Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        int gradientStartX = firstPoint.x;
        int gradientStartY = firstPoint.y;
        int gradientEndX = lastPoint.x;
        int gradientEndY = lastPoint.y;
        linearGradient = new LinearGradient(gradientStartX, gradientStartY, gradientEndX, gradientEndY, lineStartColor, lineEndColor, TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        
        canvas.drawPath(pathForLine, mPaint);
        
        linearGradient = new LinearGradient(start.x, 0, start.x, measuredHeigth, shadowStartColor, shadowEndColor, TileMode.CLAMP);
        mPaintForShadow.setColor(shadowStartColor);
        mPaintForShadow.setShader(linearGradient);
        mPaintForShadow.setStyle(Style.FILL);
        
        canvas.drawPath(pathForShadow, mPaintForShadow);
        
        for (int i = 0; i < xyArray.size(); i++)
        {
            //绘制0~~i 个点
            drawPoint(canvas, xyArray.get(i), xCoordinateMinValue + i, pointFormater);
            
        }
        
        //        mPaint.setStyle(Style.STROKE);
        //        mPaint.setStrokeWidth(10);
        //        canvas.drawCircle(80, 80, 50, mPaint);
        //        
        //        mPaint.setStyle(Style.FILL);
        //        mPaint.setColor(Color.RED);
        //        mPaint.setShader(null);
        //        mPaint.setStrokeWidth(10);
        //        canvas.drawCircle(80, 80, 50, mPaint);
    }
    
    private void drawPoint(Canvas canvas, Point point, int xValue, PointFormater pointFormater)
    {
        if (null != xyListener)
        {
            mPaint.setShader(null);
            pointFormater = xyListener.getPointFormater(pointFormater, xValue, point);
            if (pointFormater != null && pointFormater.show)
            {
//                XLLog.log(TAG, "drawPont at (", point.x, ",", point.y, ") radius:", pointFormater.radius, " color:", pointFormater.color);
                mPaint.setColor(pointFormater.color);
                mPaint.setStrokeWidth(lineWidth);
                mPaint.setStyle(pointFormater.style);
                canvas.drawCircle(point.x, point.y, pointFormater.radius, mPaint);
                
                if (pointFormater.style == Style.STROKE)
                {
                    mPaint.setShader(null);
                    mPaint.setStyle(Style.FILL_AND_STROKE);
                    mPaint.setColor(pointFormater.colorOfStroke);
                    canvas.drawCircle(point.x, point.y, pointFormater.radius - lineWidth, mPaint);
                }
                
                if (pointFormater.value != null)
                {
                    
                    float[] widths = new float[pointFormater.value.length()];
                    mPaint.getTextWidths(pointFormater.value, widths);
                    float width = 0;
                    for (float f : widths)
                    {
                        width += f;
                    }
                    
                    mPaint.setStrokeWidth(1);
                    mPaint.setTextSize(pointFormater.valueSize);
                    mPaint.setColor(pointFormater.valueColor);
                    
                    canvas.drawText(pointFormater.value, point.x - width, point.y - pointFormater.valueSize - 2, mPaint);
                    
                }
            }
        }
    }
    
    private void prepereForDraw()
    {
        measuredWidth = getMeasuredWidth();
        measuredHeigth = getMeasuredHeight();
        //        rectCoordinate = new Rect(0, 0, measuredWidth, measuredHeigth - xCoordinateValueHeight);
        
        int bottom = getMeasuredHeight() - getPaddingBottom() - 1; //bottom： 视图的高度 - 底部的Padding - x坐标值区域的高度 - 1
        int right = getMeasuredWidth() - getPaddingRight(); //right: 
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
    
    public int getxCoordinateMinValue()
    {
        return xCoordinateMinValue;
    }
    
    public void setxCoordinateMinValue(int xCoordinateMinValue)
    {
        this.xCoordinateMinValue = xCoordinateMinValue;
    }
    
    public int getxCoordinateMaxValue()
    {
        return xCoordinateMaxValue;
    }
    
    public void setxCoordinateMaxValue(int xCoordinateMaxValue)
    {
        this.xCoordinateMaxValue = xCoordinateMaxValue;
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
    
    public int getxCoordinateOffset()
    {
        return xCoordinateOffset;
    }
    
    public void setxCoordinateOffset(int xCoordinateOffset)
    {
        this.xCoordinateOffset = xCoordinateOffset;
    }
    
    public int getyCoordinateOffset()
    {
        return yCoordinateOffset;
    }
    
    public void setyCoordinateOffset(int yCoordinateOffset)
    {
        this.yCoordinateOffset = yCoordinateOffset;
    }
    
    public XYListener getXyListener()
    {
        return xyListener;
    }
    
    public void setXyListener(XYListener xyListener)
    {
        this.xyListener = xyListener;
    }
    
    public boolean isDrawLineAsBezier()
    {
        return drawLineAsBezier;
    }
    
    public void setDrawLineAsBezier(boolean drawLineAsBezier)
    {
        this.drawLineAsBezier = drawLineAsBezier;
    }
    
    public int getShadowStartColor()
    {
        return shadowStartColor;
    }
    
    public void setShadowStartColor(int shadowStartColor)
    {
        this.shadowStartColor = shadowStartColor;
    }
    
    public int getShadowEndColor()
    {
        return shadowEndColor;
    }
    
    public void setShadowEndColor(int shadowEndColor)
    {
        this.shadowEndColor = shadowEndColor;
    }
    
    public int getLineStartColor()
    {
        return lineStartColor;
    }
    
    public void setLineStartColor(int lineStartColor)
    {
        this.lineStartColor = lineStartColor;
    }
    
    public int getLineEndColor()
    {
        return lineEndColor;
    }
    
    public void setLineEndColor(int lineEndColor)
    {
        this.lineEndColor = lineEndColor;
    }
    
}
