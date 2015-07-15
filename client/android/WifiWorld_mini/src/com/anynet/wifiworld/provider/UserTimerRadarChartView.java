package com.anynet.wifiworld.provider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.PieChart;
import org.xclcharts.chart.PieData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotLegend;
import org.xclcharts.view.ChartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

public class UserTimerRadarChartView extends ChartView implements Runnable {
	
	private String TAG = "UserLocationRadarChartView";
	private PieChart chart = new PieChart();

	//标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<PieData> chartData = new LinkedList<PieData>();
	
	public UserTimerRadarChartView(Context context) {
		super(context);
		initView();
	}
	
	public UserTimerRadarChartView(Context context, AttributeSet attrs){   
	    super(context, attrs);   
	    initView();
	}
	 
	public UserTimerRadarChartView(Context context, AttributeSet attrs, int defStyle) {
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
		long timecount[] = new long[5];
		int size = 5;
		DisplayOneWeek(timecount, size); //default
	}
	
	private void setChartStyle() {
		try {
			chart.disableHighPrecision();
			chart.disablePanMode(); //禁止平移
			chart.disableScale();		
			int [] ltrb = new int[4];
			ltrb[0] = DensityUtil.dip2px(getContext(), 5); //left	
			ltrb[1] = DensityUtil.dip2px(getContext(), 35); //top	
			ltrb[2] = DensityUtil.dip2px(getContext(), 5); //right		
			ltrb[3] = DensityUtil.dip2px(getContext(), 5); //bottom	
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);
			chart.showRoundBorder(); //显示边框
			//显示图例
			PlotLegend legend = chart.getPlotLegend();	
			legend.show();
			legend.setType(XEnum.LegendType.COLUMN);
			legend.setHorizontalAlign(XEnum.HorizontalAlign.RIGHT);
			legend.setVerticalAlign(XEnum.VerticalAlign.MIDDLE);
			legend.showBox();
			
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}
	
	public void DisplayOneWeek(long timercount[], int size) {
		chartData.clear();
		labels.clear();
		chart.setTitle("一周WiFi使用者时间段统计");
		
		long total = 5;
		for (int i=0; i<size; ++i) {
			total += timercount[i];
		}
		
		DecimalFormat dcmFmt = new DecimalFormat("0.00");
		double per = (timercount[0]+1)*100 / (double)total;
		chartData.add(new PieData("0-8", dcmFmt.format(per) + "%", per, Color.rgb(155, 187, 90)));
		per = (timercount[1]+1)*100 / (double)total;
		chartData.add(new PieData("8-12", dcmFmt.format(per) + "%", per, Color.rgb(191, 79, 75)));
		per = (timercount[2]+1)*100 / (double)total;
		chartData.add(new PieData("12-16", dcmFmt.format(per) + "%", per, Color.rgb(242, 167, 69)));
		per = (timercount[3]+1)*100 / (double)total;
		chartData.add(new PieData("16-20", dcmFmt.format(per) + "%", per, Color.rgb(60, 173, 213)));
		per = (timercount[4]+1)*100 / (double)total;
		chartData.add(new PieData("20-24", dcmFmt.format(per) + "%", per, Color.rgb(90, 79, 88)));

		chart.setDataSource(chartData);	//设定数据源
		
		this.invalidate();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {          
         	chartAnimation();         	
         }
         catch(Exception e) {
             Thread.currentThread().interrupt();
         }  
	}
	
	private void chartAnimation()
	{
		  try {       
			 
			    chart.setDataSource(chartData);
			  	int count = 360 / 10;
			  	
	          	for(int i=1;i<count ;i++)
	          	{
	          		Thread.sleep(40);
	          		
	          		chart.setTotalAngle(10 * i);
	          		
	          		//激活点击监听
	    			if(count - 1 == i)
	    			{
	    				chart.setTotalAngle(360);
	    				
	    				chart.ActiveListenItemClick();
	    				//显示边框线，并设置其颜色
	    				chart.getArcBorderPaint().setColor(Color.YELLOW);
	    				chart.getArcBorderPaint().setStrokeWidth(3);
	    			}
	    			
	          		postInvalidate();            				          	          	
	          }
			  
          }
          catch(Exception e) {
              Thread.currentThread().interrupt();
          }       
		  
	}
}
