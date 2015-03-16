package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class WifiProviderLineChartView extends ChartView {
	
private String TAG = "LineChart01View";
private LineChart chart = new LineChart();

	//标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<LineData> chartData = new LinkedList<LineData>();
	private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public WifiProviderLineChartView(Context context) {
		super(context);
		initView();
	}
	
	public WifiProviderLineChartView(Context context, AttributeSet attrs){   
	    super(context, attrs);   
	    initView();
	}
	 
	public WifiProviderLineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	 
	private void initView() {
		setChartLabels();
		setChartDataSet();	
		setChartStyle();
	}
	 
	@Override  
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
	    super.onSizeChanged(w, h, oldw, oldh);  
	   //图所占范围大小
	    chart.setChartRange(w,h);
	}  
	
	private void setChartStyle() {
		try {
			chart.disableHighPrecision();
			chart.disablePanMode(); //禁止平移
			//设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....		
			int [] ltrb = new int[4];
			ltrb[0] = DensityUtil.dip2px(getContext(), 20); //left	
			ltrb[1] = DensityUtil.dip2px(getContext(), 35); //top	
			ltrb[2] = DensityUtil.dip2px(getContext(), 20); //right		
			ltrb[3] = DensityUtil.dip2px(getContext(), 20); //bottom	
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);	
			//限制Tickmarks可滑动偏移范围
			//chart.setXTickMarksOffsetMargin(ltrb[2] - 20.f);
			//chart.setYTickMarksOffsetMargin(ltrb[3] - 20.f);
			//显示边框
			chart.showRoundBorder();
			//设定数据源
			chart.setCategories(labels);								
			chart.setDataSource(chartData);		
			//数据轴最大值
			chart.getDataAxis().setAxisMax(100); 
			//数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(10);	
			//背景网格
			chart.getPlotGrid().showHorizontalLines();
			//chart.getPlotGrid().showVerticalLines();
			chart.getPlotGrid().showEvenRowBgColor();
			chart.getPlotGrid().showOddRowBgColor();
			chart.getPlotGrid().getHorizontalLinePaint().setStrokeWidth(2);
			chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DASH);
			chart.getPlotGrid().setVerticalLineStyle(XEnum.LineStyle.DOT);
			chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.RED);
			chart.getPlotGrid().getVerticalLinePaint().setColor(Color.BLUE);
			chart.setTitle("最近一周WiFi使用情况");
			chart.getAxisTitle().setLowerTitle("日期");
			
			//激活点击监听
			//chart.ActiveListenItemClick();
			//为了让触发更灵敏，可以扩大5px的点击监听范围
			//chart.extPointClickRange(5);
			//chart.showClikedFocus();								
			//绘制十字交叉线
			chart.showDyLine();
			chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
			chart.getPlotArea().extWidth(100.f);
			//调整轴显示位置
			chart.setDataAxisLocation(XEnum.AxisLocation.RIGHT);
			chart.setCategoryAxisLocation(XEnum.AxisLocation.TOP);
			//收缩绘图区右边分割的范围，让绘图区的线不显示出来
			chart.getClipExt().setExtRight(0.f);
			
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	private void setChartDataSet() {
		LinkedList<Double> dataSeries1= new LinkedList<Double>();	
		dataSeries1.add(20d); 
		dataSeries1.add(10d); 
		dataSeries1.add(31d); 
		dataSeries1.add(40d);
		dataSeries1.add(0d);
		LineData lineData1 = new LineData("方块",dataSeries1,Color.rgb(234, 83, 71));
		lineData1.setLabelVisible(true);		
		lineData1.setDotStyle(XEnum.DotStyle.RECT);				
		lineData1.getDotLabelPaint().setColor(Color.BLUE);
		lineData1.getDotLabelPaint().setTextSize(22);
		lineData1.getDotLabelPaint().setTextAlign(Align.LEFT);	
		lineData1.setItemLabelRotateAngle(45.f);
		lineData1.getLabelOptions().setLabelBoxStyle(XEnum.LabelBoxStyle.TEXT);
		
		//Line 2
		LinkedList<Double> dataSeries2= new LinkedList<Double>();	
		dataSeries2.add((double)30); 
		dataSeries2.add((double)42); 
		dataSeries2.add((double)0); 	
		dataSeries2.add((double)60); 
		dataSeries2.add((double)40); 
		LineData lineData2 = new LineData("圆环",dataSeries2,Color.rgb(75, 166, 51));
		lineData2.setDotStyle(XEnum.DotStyle.RING);				
		lineData2.getPlotLine().getDotPaint().setColor(Color.RED);
		lineData2.setLabelVisible(true);		
		lineData2.getPlotLine().getPlotDot().setRingInnerColor(Color.GREEN);
		lineData2.setLineStyle(XEnum.LineStyle.DASH);
						
		//Line 3
		LinkedList<Double> dataSeries3= new LinkedList<Double>();	
		dataSeries3.add(65d);
		dataSeries3.add(75d);
		dataSeries3.add(55d);
		dataSeries3.add(65d);
		dataSeries3.add(95d);
		LineData lineData3 = new LineData("圆点",dataSeries3,Color.rgb(123, 89, 168));
		lineData3.setDotStyle(XEnum.DotStyle.DOT);				
		
		//Line 4
		LinkedList<Double> dataSeries4= new LinkedList<Double>();	
		dataSeries4.add(50d);
		dataSeries4.add(60d);
		dataSeries4.add(80d);
		dataSeries4.add(84d);
		dataSeries4.add(90d);
		LineData lineData4 = new LineData("棱形",dataSeries4,Color.rgb(84, 206, 231));		
		lineData4.setDotStyle(XEnum.DotStyle.PRISMATIC);
		//把线弄细点
		lineData4.getLinePaint().setStrokeWidth(2);
		
		lineData4.getLabelOptions().setLabelBoxStyle(XEnum.LabelBoxStyle.CIRCLE);
		lineData4.getLabelOptions().getBox().getBackgroundPaint().setColor(Color.GREEN);	
		lineData4.setLabelVisible(true);	
		
		//Line 5
		LinkedList<Double> valuesE= new LinkedList<Double>();	
		valuesE.add(0d);
		valuesE.add(80d);
		valuesE.add(85d);
		valuesE.add(90d);
		LineData lineData5 = new LineData("定制",valuesE,Color.rgb(234, 142, 43));
		lineData5.setDotRadius(15);
		lineData5.setDotStyle(XEnum.DotStyle.TRIANGLE);
		
		chartData.add(lineData1);
		chartData.add(lineData2);
		chartData.add(lineData3);
		chartData.add(lineData4);
		chartData.add(lineData5);
		
	}
	
	private void setChartLabels() {
		labels.add("周一");
		labels.add("周二");
		labels.add("周三");
		labels.add("周四");
		labels.add("周五");
		labels.add("周六");
		labels.add("周日");
		labels.add("占位");
	}
	
	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }

	@Override
	public List<XChart> bindChart() {		
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);		
		return lst;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {	
		if(event.getAction() == MotionEvent.ACTION_UP) 
		{			
			triggerClick(event.getX(),event.getY());
		}
		super.onTouchEvent(event);
		return true;
	}
	
	//触发监听
	private void triggerClick(float x,float y) {		
		
		//交叉线
		if(chart.getDyLineVisible())chart.getDyLine().setCurrentXY(x,y);		
		if(!chart.getListenItemClickStatus())
		{
			//交叉线
			if(chart.getDyLineVisible())this.invalidate();
		}else{			
			PointPosition record = chart.getPositionRecord(x,y);			
			if( null == record)
			{
				if(chart.getDyLineVisible())this.invalidate();
				return;
			}
	
			LineData lData = chartData.get(record.getDataID());
			Double lValue = lData.getLinePoint().get(record.getDataChildID());
		
			float r = record.getRadius();
			chart.showFocusPointF(record.getPosition(),r + r*0.5f);		
			chart.getFocusPaint().setStyle(Style.STROKE);
			chart.getFocusPaint().setStrokeWidth(3);		
			if(record.getDataID() >= 3)
			{
				chart.getFocusPaint().setColor(Color.BLUE);
			}else{
				chart.getFocusPaint().setColor(Color.RED);
			}		
			
			//在点击处显示tooltip
			mPaintTooltips.setColor(Color.RED);				
			chart.getToolTip().setCurrentXY(x,y);
			chart.getToolTip().addToolTip(" Key:"+lData.getLineKey(),mPaintTooltips);
			chart.getToolTip().addToolTip(" Label:"+lData.getLabel(),mPaintTooltips);		
			chart.getToolTip().addToolTip(" Current Value:" +Double.toString(lValue),mPaintTooltips);
				
			
			//当前标签对应的其它点的值
			int cid = record.getDataChildID();
			String xLabels = "";			
			for(LineData data : chartData)
			{
				if(cid < data.getLinePoint().size())
				{
					xLabels = Double.toString(data.getLinePoint().get(cid));					
					chart.getToolTip().addToolTip("Line:"+data.getLabel()+","+ xLabels,mPaintTooltips);					
				}
			}
			
			
			this.invalidate();
		}	
	}
}
