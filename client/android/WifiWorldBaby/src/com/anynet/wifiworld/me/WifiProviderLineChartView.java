package com.anynet.wifiworld.me;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import com.anynet.wifiworld.data.MultiDataCallback;
import com.anynet.wifiworld.data.UserDynamic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class WifiProviderLineChartView extends ChartView {
	
	private String TAG = "WifiProviderLineChartView";
	private LineChart chart = new LineChart();
	private boolean flag = false;

	//标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<LineData> chartData = new LinkedList<LineData>();
	
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
		if (flag)
			DisplayOneDay();
		else
			DisplayOneWeek();
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
			ltrb[0] = DensityUtil.dip2px(getContext(), 20); //left	
			ltrb[1] = DensityUtil.dip2px(getContext(), 35); //top	
			ltrb[2] = DensityUtil.dip2px(getContext(), 20); //right		
			ltrb[3] = DensityUtil.dip2px(getContext(), 20); //bottom	
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);	
			//限制Tickmarks可滑动偏移范围
			chart.setXTickMarksOffsetMargin(ltrb[2] - 20.f);
			chart.setYTickMarksOffsetMargin(ltrb[3] - 20.f);
			chart.showRoundBorder(); //显示边框
			chart.getDataAxis().setAxisMax(100); //数据轴最大值
			chart.getDataAxis().setAxisSteps(10); //数据轴刻度间隔
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
		chart.setTitle("最近一周WiFi使用情况");
		chart.getAxisTitle().setLowerTitle("日期");
		
		LinkedList<Double> dataSeries1= new LinkedList<Double>();	
		dataSeries1.add(20d); 
		dataSeries1.add(10d); 
		dataSeries1.add(31d); 
		dataSeries1.add(40d);
		dataSeries1.add(0d);
		dataSeries1.add(80d);
		dataSeries1.add(70d);
		LineData lineData1 = new LineData("网络使用次数",dataSeries1,Color.rgb(234, 83, 71));
		lineData1.setLabelVisible(true);		
		lineData1.setDotStyle(XEnum.DotStyle.RECT);				
		lineData1.getDotLabelPaint().setColor(Color.BLUE);
		lineData1.getDotLabelPaint().setTextSize(22);
		lineData1.getDotLabelPaint().setTextAlign(Align.LEFT);	
		lineData1.setItemLabelRotateAngle(45.f);
		lineData1.getLabelOptions().setLabelBoxStyle(XEnum.LabelBoxStyle.TEXT);
		chartData.add(lineData1);
		chart.setDataSource(chartData);	//设定数据源
		
		labels.add("周一");
		labels.add("周二");
		labels.add("周三");
		labels.add("周四");
		labels.add("周五");
		labels.add("周六");
		labels.add("周日");
		chart.setCategories(labels);
		flag = true;
	}
	
	private void DisplayOneDay() {
		final UserDynamic data = new UserDynamic();
		final long time = System.currentTimeMillis();
		data.MacAddr = "12:34:56:78"; // just for test
		data.QueryWiFiInOneDay(getContext(), time, new MultiDataCallback<UserDynamic>() {

			@Override
            public void onSuccess(List<UserDynamic> objects) {
				int day_size = 24;
				int array_count[] = new int[day_size];
				//统计数据
				for (UserDynamic object : objects) {
					switch (object.LoginTime)
				}
				//展示数据
				chartData.clear();
				labels.clear();
				chart.setTitle("最近24小时WiFi使用情况");
				chart.getAxisTitle().setLowerTitle("时段");
				
				LinkedList<Double> dataSeries1= new LinkedList<Double>();
				Random rand = new Random();
				for (int i = 0; i < 24; ++i) {
					dataSeries1.add((double) rand.nextInt(100));
				}
				LineData lineData1 = new LineData("网络使用次数",dataSeries1, Color.rgb(234, 83, 71));
				lineData1.setLabelVisible(true);		
				lineData1.setDotStyle(XEnum.DotStyle.RECT);				
				lineData1.getDotLabelPaint().setColor(Color.BLUE);
				lineData1.getDotLabelPaint().setTextSize(22);
				lineData1.getDotLabelPaint().setTextAlign(Align.LEFT);	
				lineData1.setItemLabelRotateAngle(45.f);
				lineData1.getLabelOptions().setLabelBoxStyle(XEnum.LabelBoxStyle.TEXT);
				chartData.add(lineData1);
				chart.setDataSource(chartData);	//设定数据源
				
				for (int i = 1; i <= 24; ++i) {
					labels.add(i + "");
				}
				chart.setCategories(labels);
				flag = false;
            }

			@Override
            public void onFailed(String msg) {
                Toast.makeText(getContext(), "wifi使用数据失败。", 0).show();
            }
			
		});
	}
}
