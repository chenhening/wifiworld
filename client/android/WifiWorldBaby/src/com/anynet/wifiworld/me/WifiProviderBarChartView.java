package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.BarChart3D;
import org.xclcharts.chart.BarData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

public class WifiProviderBarChartView extends ChartView {
	
	private String TAG = "WifiProviderBarChartView";
	private BarChart3D chart = new BarChart3D();

	//标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<BarData> chartData = new LinkedList<BarData>();
	private LinkedList<Double> mdataSeries = new LinkedList<Double>();
	
	public WifiProviderBarChartView(Context context) {
		super(context);
		initView();
	}
	
	public WifiProviderBarChartView(Context context, AttributeSet attrs){   
	    super(context, attrs);   
	    initView();
	}
	 
	public WifiProviderBarChartView(Context context, AttributeSet attrs, int defStyle) {
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

// ------------------------------------------------------------------------------------------------
	private void initView() {
		setChartStyle();
		long headcount[] = new long[7];
		int size = 7;
		DisplayOneWeek(headcount, size); //default
	}
	
	private void setChartStyle() {
		try {
			chart.disableHighPrecision();
			chart.disablePanMode(); //禁止平移
			chart.disableScale();
			//设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....		
			int [] ltrb = new int[4];
			ltrb[0] = DensityUtil.dip2px(getContext(), 25); //left	
			ltrb[1] = DensityUtil.dip2px(getContext(), 35); //top	
			ltrb[2] = DensityUtil.dip2px(getContext(), 25); //right		
			ltrb[3] = DensityUtil.dip2px(getContext(), 30); //bottom	
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);	
			//限制Tickmarks可滑动偏移范围
			chart.setXTickMarksOffsetMargin(ltrb[2] - 20.f);
			chart.setYTickMarksOffsetMargin(ltrb[3] - 20.f);
			chart.showRoundBorder(); //显示边框
			chart.getDataAxis().setAxisMax(16); //数据轴最大值
			chart.getDataAxis().setAxisSteps(10); //数据轴刻度间隔
			chart.getPlotGrid().showHorizontalLines(); //背景网格
			chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
			chart.getClipExt().setExtRight(0.f); //收缩绘图区右边分割的范围，让绘图区的线不显示出来
			
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	public void DisplayOneWeek(long headcount[], int size) {
		mdataSeries.clear();
		chartData.clear();
		labels.clear();
		chart.setTitle("最近一周WiFi使用情况");
		
		for (int i=0; i<size; ++i) {
			mdataSeries.add((double) headcount[i]);
		}
		BarData lineData1 = new BarData("网络使用次数", mdataSeries, Color.rgb(234, 83, 71));
		chartData.add(lineData1);
		chart.setDataSource(chartData);	//设定数据源
		
		labels.add("6天前");
		labels.add("5天前");
		labels.add("4天前");
		labels.add("3天前");
		labels.add("前天");
		labels.add("昨天");
		labels.add("今天");
		chart.setCategories(labels);
		this.invalidate();
	}
}
