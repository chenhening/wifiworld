package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class ProviderIncomeChartView extends ChartView {

	private String TAG = "WifiProviderLineChartView";
	private LineChart chart = new LineChart();

	//标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<LineData> chartData = new LinkedList<LineData>();
	
	public ProviderIncomeChartView(Context context) {
		super(context);
		initView();
	}
	
	public ProviderIncomeChartView(Context context, AttributeSet attrs){   
	    super(context, attrs);   
	    initView();
	}
	 
	public ProviderIncomeChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	 
	@Override  
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
	    super.onSizeChanged(w, h, oldw, oldh);  
	   //图所占范围大小
	    chart.setChartRange(w,h);
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
		super.onTouchEvent(event);
		return true;
	}

// ------------------------------------------------------------------------------------------------
	private void initView() {
		setChartStyle();
		DisplayOneWeek(); //default
	}
	
	private void setChartStyle() {
		try {
			chart.disableHighPrecision();
			chart.disablePanMode(); //禁止平移
			//设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....		
			int [] ltrb = new int[4];
			ltrb[0] = DensityUtil.dip2px(getContext(), 10); //left	
			ltrb[1] = DensityUtil.dip2px(getContext(), 10); //top	
			ltrb[2] = DensityUtil.dip2px(getContext(), 10); //right		
			ltrb[3] = DensityUtil.dip2px(getContext(), 10); //bottom	
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);
			chart.showRoundBorder(); //显示边框
			chart.getDataAxis().setAxisMax(10); //数据轴最大值
			chart.getDataAxis().setAxisSteps(1); //数据轴刻度间隔
			chart.getPlotGrid().showHorizontalLines(); //背景网格
			//chart.getPlotGrid().showVerticalLines();
			chart.getPlotGrid().showEvenRowBgColor();
			chart.getPlotGrid().showOddRowBgColor();
			chart.getPlotGrid().getHorizontalLinePaint().setStrokeWidth(2);
			chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DASH);
			chart.getPlotGrid().setVerticalLineStyle(XEnum.LineStyle.DOT);
			chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.RED);
			chart.getPlotGrid().getVerticalLinePaint().setColor(Color.BLUE);						
			chart.showDyLine(); //绘制十字交叉线
			chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
			//chart.getPlotArea().extWidth(100.f);
			chart.setDataAxisLocation(XEnum.AxisLocation.RIGHT); //调整轴显示位置
			chart.setCategoryAxisLocation(XEnum.AxisLocation.TOP);
			chart.getClipExt().setExtRight(0.f); //收缩绘图区右边分割的范围，让绘图区的线不显示出来
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	private void DisplayOneWeek() { 
		chartData.clear();
		labels.clear();
		
		LinkedList<Double> dataSeries1= new LinkedList<Double>();	
		dataSeries1.add(2.2d); 
		dataSeries1.add(1.2d); 
		dataSeries1.add(3.1d); 
		dataSeries1.add(4.0d);
		dataSeries1.add(0d);
		dataSeries1.add(8.0d);
		dataSeries1.add(7.0d);
		LineData lineData1 = new LineData("单位(元)",dataSeries1,Color.rgb(234, 83, 71));
		lineData1.setLabelVisible(true);		
		lineData1.setDotStyle(XEnum.DotStyle.RECT);				
		lineData1.getDotLabelPaint().setColor(Color.BLUE);
		lineData1.getDotLabelPaint().setTextSize(22);
		lineData1.getDotLabelPaint().setTextAlign(Align.LEFT);	
		lineData1.setItemLabelRotateAngle(45.f);
		lineData1.getLabelOptions().setLabelBoxStyle(XEnum.LabelBoxStyle.TEXT);
		chartData.add(lineData1);
		chart.setDataSource(chartData);	//设定数据源
		
		labels.add("");
		labels.add("");
		labels.add("");
		labels.add("");
		labels.add("");
		labels.add("");
		labels.add("");
		chart.setCategories(labels);
	}
}
