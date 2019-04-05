package com.example.myblueproject.bluetooth;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myblueproject.R;
import com.mt.tools.Tools;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;



//import org.achartengine.ChartFactory;

@SuppressLint("NewApi") public class StartActivity extends Activity implements OnClickListener{
   
	private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
	private Button talking_read_btn;
	private BLE ble = new BLE();
	private BroadcastReceiver bluetoothReceiver;
	//// 存储脑电数据
		private ArrayList<Float> EegData = new ArrayList<Float>();
		private float[] Ynn = new float[150000];

	// 画图有关的变量
		private LinearLayout mLayout;
		private XYSeries series;//XY数据点r
		private XYMultipleSeriesDataset mDataset;// XY轴数据集
		private XYSeriesRenderer R_Renderer;
		private GraphicalView mViewChart;// // 用于显示现行统计图
		private XYMultipleSeriesRenderer mXYRenderer;// // 线性统计图主描绘器
		private int X = 1000;//X数据集大小
		private String title = "脑电";
		private int resp_num = 0; 
		private int k = 0, m = 1, l = 200;
		private Handler handler;////进程刷新
		private TimerTask task;
		private Timer timer = new Timer();//时间
		private static final String TAG = "message";
		private Handler myHandler;
		
	// 与SD卡数据存储相关的变量
		private boolean sdcardExit;
		private File dateDir;
		private String origDataAdress;
		private String origDataName;
		private String USER_NAME;// / 用户名
		private String date;// // 日期
		private String time;//// 时间
		private String DATE = "date";
		private String TIME = "time";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 设置不显示标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start);
		Intent intent = getIntent();
		mBluetoothGattCharacteristic = Tools.mBLEService.mBluetoothGatt
				.getServices().get(intent.getIntExtra("one", 0))
				.getCharacteristics().get(intent.getIntExtra("two", 0));
		ble.setBroadcastReceiver();  // 设置广播监听
		initView();
		// 获取用户名，作为数据文件的命名
		getUserName();
		initChart();//画图
		initTime();
	}
	
	private void initView() {
		//// 查看是有什么权限
		talking_read_btn = (Button) findViewById(R.id.talking_read_btn);
		talking_read_btn.setOnClickListener(this);
		int proper = mBluetoothGattCharacteristic.getProperties();
		if (0 != (proper & 0x02)) { // 可读
			talking_read_btn.setVisibility(View.VISIBLE);
		}
//		if (0 != (proper & 0x08)) { // 可写
//					writeable_Layout.setVisibility(View.VISIBLE);
//				}
		if (0 != (proper & 0x10)) { // 通知
			Tools.mBLEService.mBluetoothGatt.setCharacteristicNotification(
					mBluetoothGattCharacteristic, true);
			BluetoothGattDescriptor descriptor = mBluetoothGattCharacteristic
					.getDescriptor(UUID
							.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			Tools.mBLEService.mBluetoothGatt.writeDescriptor(descriptor);
		}
	}
	
	////画图initChart()方法
	private void initChart(){	
    	//// 这里获得xy_chart的布局，下面会把图表画在这个布局里面
		mLayout = (LinearLayout) findViewById(R.id.chart);
		// 这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
		series = new XYSeries(title);
		// 创建一个数据集的实例，这个数据集将被用来创建图表，可添加多个XYSeries对象，因为一个折线图中可能有多条线
		mDataset = new XYMultipleSeriesDataset();
		// 将点集添加到这个数据集中
		mDataset.addSeries(series);
			//用来定义一个图的整体风格
		mXYRenderer = new XYMultipleSeriesRenderer();
		////主要用来设置一条线条的风格，颜色、粗细等
		R_Renderer = new XYSeriesRenderer();
		//// 显示表格
		int color = Color.GREEN;
		PointStyle style = PointStyle.POINT;
		buildRenderer(R_Renderer,color, style, true);
		mXYRenderer.setApplyBackgroundColor(true);
		//设置背景表格颜色
		mXYRenderer.setShowGrid(true);
		// 设置X轴标签
		mXYRenderer.setGridColor(Color.GRAY);
		//设置背景表格颜色
		mXYRenderer.setXLabels(15);
		// 设置X轴标签
		mXYRenderer.setYLabels(10);
		// // 设置Y轴标签
		mXYRenderer.setYLabelsAlign(Align.RIGHT);
		//// 不显示图例
		mXYRenderer.setShowLegend(false);
		mXYRenderer.setZoomEnabled(false);
		mXYRenderer.setPanEnabled(true, false);
		mXYRenderer.setClickEnabled(false);
		mXYRenderer.setPointSize(2f);
		R_Renderer.setColor(Color.BLUE);
		R_Renderer.setPointStyle(PointStyle.X);
		R_Renderer.setFillPoints(true);
		mXYRenderer.addSeriesRenderer(0, R_Renderer);
		setChartSettings(mXYRenderer, " ", null, "幅度/μV", 0, X, -1000, 1000,
				Color.WHITE, Color.WHITE);// 这个是采用官方APIdemo提供给的方法
		// 通过ChartFactory生成图表
		mViewChart = ChartFactory.getLineChartView(this, mDataset,mXYRenderer);
		//  将图表添加到布局中去
		mLayout.addView(mViewChart, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
    }
	
	/**
	 * 画图有关的控件
	 * 
	 * @param color
	 * @param style
	 * @param fill
	 * @return
	 */
	protected XYSeriesRenderer buildRenderer(XYSeriesRenderer renderer, int color,
            PointStyle style, boolean fill) {// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		renderer.setColor(color);
		renderer.setPointStyle(style);
		renderer.setFillPoints(fill);
		renderer.setLineWidth(3);
		return renderer;
	}

	/**
	 * 画图软件
	 * 
	 */
	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {// 设置主描绘器的各项属性，详情可阅读官方API文档
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}
	
	
  private void initTime() {
	  myHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					/*使用定时器，每隔1000ms刷新视图*/
				case Constants.MESSAGE_UPDATE_CHART:
					updateChart();
					break;
					//// 存储数据至SD卡
//				case Constants.MESSAGE_WRITE_FINISHED:
//					requestFromSever();
//					break;
				default:
					break;	
				}
			}
		};
		
		task = new TimerTask(){  //?????
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = Constants.MESSAGE_UPDATE_CHART;  //设置标志
				myHandler.sendMessage(message);
			}	
		};
		timer.schedule(task, 1000,1000);//运行时间和间隔都是1000ms
  }
	
  public class BLE {
	  // 接收相关参数
	private int NUM = 0;
	String N1 = "C0";
	String N2 = "00";
	String eegStr_temp = null;
	//private readThread read_thread;

	  // 设置广播监听
	private void setBroadcastReceiver() {
		//// 创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND
		IntentFilter intentFilter = new IntentFilter(
				BLEService.ACTION_DATA_CHANGE);
		intentFilter.addAction(BLEService.ACTION_READ_OVER);
		intentFilter.addAction(BLEService.ACTION_RSSI_READ);
		intentFilter.addAction(BLEService.ACTION_STATE_CONNECTED);
		intentFilter.addAction(BLEService.ACTION_STATE_DISCONNECTED);
		intentFilter.addAction(BLEService.ACTION_WRITE_OVER);
		bluetoothReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();	
				int m;
				// 数据改变通知
				if (BLEService.ACTION_DATA_CHANGE.equals(action)) {
						dis_recive_msg(intent.getByteArrayExtra("value"));
//						read_thread = new readThread();
//						read_thread.start();
						}
				// 读取数据
				if (BLEService.ACTION_READ_OVER.equals(action)) {			
						dis_recive_msg(intent.getByteArrayExtra("value"));
					return;
				}
				// 连接状态改变
				if (BLEService.ACTION_STATE_CONNECTED.equals(action)) {
				//	talking_conect_flag_txt.setText("已连接");
				}
				if (BLEService.ACTION_STATE_DISCONNECTED.equals(action)) {
					//talking_conect_flag_txt.setText("已断开");
					Toast.makeText(getApplicationContext(), "已断开连接",Toast.LENGTH_LONG ).show();
				}
			}	
		};
		// // 注册广播接收器
		registerReceiver(bluetoothReceiver, intentFilter);
		//动态receiver是在运行期通过调用registerReceiver()注册的
	}
				
    private void dis_recive_msg(byte []tmp_byte)
    {
    	String tmp = "";
    	int read_fmt_int = 1;
    	if(0 == tmp_byte.length){
			return;
		}
    	switch (read_fmt_int) {
		case 0: // 字符串显示
			try {
				tmp = new String(tmp_byte, "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case 1: // // 16进制显示,字节数组转化为16进制字符串
			String[]tmp_array = new String[tmp_byte.length];
			for (int i = 0; i < tmp_byte.length; i++) {
				String hex = Integer.toHexString(tmp_byte[i] & 0xFF);//tmp_byte[i]&0xFF将一个byte和 0xFF进行与运算,使用Integer.toHexString取得了十六进制字符串
				//将byte[] tmp_byte转换成16进制的字符串
				if (hex.length() == 1) {
					hex = '0' + hex;//如果hex.length() == 1，前面补零
				}
//				tmp_array[i] = hex;
				tmp=hex.toUpperCase();//.toUpperCase的意思是将全部字符变为大写，返回新的字符串
				 tmp_array[i] = tmp;
				 parseData(tmp_array[i]);
//				 read_thread = new readThread();
//				 read_thread.start();
			}
			break;
		case 2: // // 10进制显示
			int count = 0;
			for (int i = 0; i < tmp_byte.length; i++) {
				count *= 256;
				count += (tmp_byte[tmp_byte.length-1-i] & 0xFF);
			}
			tmp = Integer.toString(count);
			break;
		case 3:
			//for(int i=0;i<tmp_byte.length; i++){
			byte[] packetDataChannel1 = Arrays.copyOfRange(tmp_byte, 0, 3);
			float channel = OpenBCIDataConversion
					.convertByteToMicroVolts(packetDataChannel1);
			EegData.add(channel);
			//}
			break;
		default:    //default语句
			break;
		}		   	
    }
    
    /**
  		 * 通知UI线程来刷新图表(此线程没有用到，改成定时器了)
  		 */
  	/*	public class readThread extends Thread {
  			@Override
  			public void run() {
  				
  				try {
  					Thread.sleep(100);
  				} catch (InterruptedException e) {
  					e.printStackTrace();
  				}
  				try {
  				myHandler.sendEmptyMessage(Constants.MESSAGE_UPDATE_CHART);
  				} catch(Exception e) {
  					e.printStackTrace();
  				}
  			}
  		}*/
  
    /**
	 * 判断分离:找到帧头为"C0 00 00"的数据帧(前三个字节为帧头)
	 * @param
	 */
  public void parseData(String r) {
	  
	  float ADS1299_Vref = 4.5f;  //reference voltage for ADC in ADS1299.  set by its hardware
      float ADS1299_gain = 24;  //assumed gain setting for ADS1299.  set by its Arduino code
      float scale_fac_uVolts_per_count = (float) (ADS1299_Vref / (Math.pow(2,23)-1) / ADS1299_gain  * 1000000.f); 
  	  
		if (r.equals(N1) && NUM == 0) {
			NUM = 1;
			return;
		}
		if (NUM == 1) {
			if (r.equals(N2)) {
				NUM = 2;

			} else {
				NUM = 0;
			}
			return;
		}
		if (NUM == 2) {
			if (r.equals(N2)) {
				NUM = 3;

			} else {
				NUM = 0;
			}
			return;
		}
		if (NUM == 3) {
			eegStr_temp = r;
			NUM = 4;
			return;
		}
		if (NUM == 4) {
			eegStr_temp += r;
			NUM = 5;
			return;
		}
		if (NUM == 5) {
			eegStr_temp += r;
			
			//little endian
			int data = Integer.parseInt(eegStr_temp, 16);////十六进制转换成十进制
			 if((data & 0x00800000) > 0){
				 data |= 0xFF000000;
			    }else{
			    	data &= 0x00FFFFFF;
			    }
			float newData = scale_fac_uVolts_per_count *data;
			
			// 将原始脑电数据存储至EegData集合中
			EegData.add(newData);
			resp_num++;
			//new readThread().start();
			NUM = 0;
			return;
		}
  }
  }
  
	@Override
	public void onResume() {
	    super.onResume();
	}

	// 与Activity生命周期相关的函数
		@Override
		protected void onPause() {
			super.onPause();
		}
		
	@Override
	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
		unregisterReceiver(bluetoothReceiver);
		//finish();
	}

	/*此Handler换成定时器了，封装在initTime()中*/
	/*Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 脑电波形更新
			case Constants.MESSAGE_UPDATE_CHART:
				updateChart();
				break;
			default:
				break;
			}
		}
	};*/

	// 脑电波形的实时显
	public void updateChart() {
		
		if(EegData.size()>0){
			for(int i=0;i<EegData.size();i++){
				Ynn[k] = EegData.get(i);
				//System.out.println(Ynn[k]);
				EegData.remove(i);
				series.add(k, Ynn[k]);
				k++;
				
			/////均值滤波去除基线漂移
			/*	if (EegData.size() > 300) {
					for (int i = 0; i < 50; i++) {
						float sumYnn = 0;
						float meanYnn = 0;
						for (int j = l - 100; j < l; j++) {
							sumYnn = sumYnn + EegData.get(j);
						}
						meanYnn = (float) (sumYnn / 100.0);
						if (l < EegData.size()) {
							Ynn[k] = EegData.get(l - 50);
							series.add(k, (Ynn[k] - meanYnn));
							l++;
							k++;
						} else {
							return;
						}*/
						
				if (k == m * 1000) { //// 屏幕自动向右滑动
					mXYRenderer.setXAxisMin(X * m);
					mXYRenderer.setXAxisMax(X * (m + 1));
					m++;	
				}	
			}
		}
		//图表更新
		mViewChart.invalidate();
	}
	
	// 将脑电数据存储至SD卡
	private void requestFromSever() {
		// 开启数据存储线程
		new Thread() {
			public void run() {
				// 获取时间和日期
				getDateTime();
				//存储数据至SD卡
				//writeDataToSD(Ynn);
				//myHandler.sendEmptyMessage(Constants.MESSAGE_WRITE_FINISHED);
			}
		}.start();
	}
  
  /**
	 * 以文件形式存储数据至手机SD卡
	 */
	public void writeDataToSD(float eegData) {
		
		String SUFFIX = ".txt";
		// 如果手机插入了SD卡，而且应用程序具有访问SD的权限
	sdcardExit = Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED);
	origDataName = time + SUFFIX;
//获取SD卡的目录
	origDataAdress = Environment.getExternalStorageDirectory().getPath()
			+ "/" + USER_NAME + "/" + "/" + date + "/";
	if (sdcardExit) {
		dateDir = new File(origDataAdress);
		if (!dateDir.exists()) {
			// mkdirs()可以创建多级目录
			dateDir.mkdirs();
		}
	}
	File eegFile = new File(dateDir.getAbsolutePath(), "eeg_"
			+ origDataName);
	if (!eegFile.exists()) {
		try {
			/* 以指定文件创建RandomAccessFile对象 */
			RandomAccessFile raf = new RandomAccessFile(eegFile, "rw");
			//// 将文件记录指针移动到最后
			raf.seek(eegFile.length());
			//输出文件内容
			raf.writeFloat(eegData);
			/*for (int i = 0; i < resp_num; i++) {
				// 开始写入数据
				raf.writeFloat(eegData[i]);
			}*/
			// ???RandomAccessFile
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	}
	
      /**
		 * 获取用户名，作为数据文件的命名
		 */
	private void getUserName() {
		Intent intent = getIntent();
		USER_NAME = intent.getStringExtra("name");
		}
		
	/**
	 * 获取系统日期与时间
	 */
	private void getDateTime() {
		Calendar c = Calendar.getInstance();
		date = c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月"
				+ c.get(Calendar.DAY_OF_MONTH) + "日";
		int min = c.get(Calendar.MINUTE);
		if (min < 10) {
			time = c.get(Calendar.HOUR_OF_DAY) + "时" + "0"
					+ c.get(Calendar.MINUTE) + "分";
		} else {
			time = c.get(Calendar.HOUR_OF_DAY) + "时" + c.get(Calendar.MINUTE)
					+ "分";
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == talking_read_btn) {
			Tools.mBLEService.mBluetoothGatt
					.readCharacteristic(mBluetoothGattCharacteristic);
			return;
		}	
	}
}
	
