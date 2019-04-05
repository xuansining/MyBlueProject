package com.example.myblueproject.historyActivity;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.myblueproject.R;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

public class HistoryActivity extends Activity {
	// // 画图有关的变量
	    private LinearLayout mLayout;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		initChart();
	}
	
	  private void initChart(){
	    	// 这里获得xy_chart的布局，下面会把图表画在这个布局里面
			mLayout = (LinearLayout) findViewById(R.id.chart);
			View mchartView =ChartFactory.getLineChartView(this,getDataSet(),getRenderer());
					 mLayout.addView(mchartView,new LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
	    }
	  
		  /**
		    * 构造数据
		    * @return
		    */
		    public XYMultipleSeriesDataset getDataSet() {
		    // 构造数据
		    XYMultipleSeriesDataset barDataset = new XYMultipleSeriesDataset();
		    CategorySeries barSeries = new CategorySeries("分数");
		    barSeries.add(93.4);
		    barSeries.add(81.5);
		    barSeries.add(67.2);
		    barSeries.add(79.7);
		    barSeries.add(96.1);
		    barSeries.add(83.5);
		    barSeries.add(76.8);
		    barDataset.addSeries(barSeries.toXYSeries());
		    return barDataset;
		    }		    
			
		    /**
		     * 构造渲染器
		     * @return
		     */
		     public XYMultipleSeriesRenderer getRenderer() {
		     XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		     //renderer.setXTitle("睡眠分析");
		     renderer.setAxesColor(Color.WHITE);
		     renderer.setLabelsColor(Color.WHITE);
		     // 设置X轴的最小数字和最大数字
		     renderer.setXAxisMin(0.5);
		     renderer.setXAxisMax(7.5);
		     //  设置Y轴的最小数字和最大数字
		     renderer.setYAxisMin(0);
		     renderer.setYAxisMax(100);
		     renderer.addXTextLabel(1, "周一");
		     renderer.addXTextLabel(2, "周二");
		     renderer.addXTextLabel(3, "周三");
		     renderer.addXTextLabel(4, "周四");
		     renderer.addXTextLabel(5, "周五");
		     renderer.addXTextLabel(6, "周六");
		     renderer.addXTextLabel(7, "周日");
				 // 设置背景表格颜色
		     renderer.setShowGrid(true);
			// 设置背景表格颜色
		     renderer.setGridColor(Color.WHITE);
		  // 设置X轴标签
		     renderer.setXLabels(15);
				// 右对齐
		     renderer.setYLabels(10);
				// 右对齐
		     renderer.setYLabelsAlign(Align.RIGHT);
		     renderer.setApplyBackgroundColor(true);
		     renderer.setBackgroundColor(Color.GRAY);
		     // 设置每条柱子的颜色
		     XYSeriesRenderer sr = new XYSeriesRenderer();
		     sr.setColor(Color.YELLOW);
		     sr.setPointStyle(PointStyle.TRIANGLE);
			 sr.setFillPoints(true);
			 sr.setLineWidth(2);
			 sr.setDisplayChartValues(true);
			 sr.setDisplayChartValuesDistance(30);
		     renderer.addSeriesRenderer(sr);
		     
		     // X轴的近似坐标数 (这样不显示横坐标)
		     renderer.setXLabels(0);
				 // Y轴的近似坐标数
		     renderer.setYLabels(10);//设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔
				 // 刻度线与X轴坐标文字中间对齐
		     renderer.setXLabelsAlign(Align.CENTER);
				 // Y轴与Y轴坐标文字中间对齐
		     renderer.setYLabelsAlign(Align.CENTER);
		     // // 允许左右拖动,但不允许上下拖动.
		     renderer.setPanEnabled(true, false);
		     // 设置X,Y轴单位的字体大小
		     renderer.setAxisTitleTextSize(30);
		     return renderer;
		     }
}
