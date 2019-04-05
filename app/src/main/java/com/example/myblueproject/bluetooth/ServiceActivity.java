package com.example.myblueproject.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;

import com.example.myblueproject.R;
import com.mt.tools.SampleGattAttributes;
import com.mt.tools.Tools;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServiceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_page);
		setBroadcastReceiver();
		initView();
		getDefaultName();
		getUserName();
	}

	/// 设置广播监听
	private BluetoothReceiver bluetoothReceiver = null;

	private void setBroadcastReceiver() {
		//创建一个IntentFilter对象，将其action指定为BluetoothDevice.ACTION_FOUND
		IntentFilter intentFilter = new IntentFilter(
				BLEService.ACTION_READ_Descriptor_OVER);
		intentFilter.addAction(BLEService.ACTION_ServicesDiscovered_OVER);
		intentFilter.addAction(BLEService.ACTION_STATE_CONNECTED);
		bluetoothReceiver = new BluetoothReceiver();
		// 注册广播接收器
		registerReceiver(bluetoothReceiver, intentFilter);
	}

	private class BluetoothReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();//接收到的Intent的Action
			//intent.getIntExtra消息内容
			if (BLEService.ACTION_READ_Descriptor_OVER.equals(action)) {
				if (BluetoothGatt.GATT_SUCCESS == intent.getIntExtra("value",
						-1)) {
					read_name_flag = true;
				}
				return;
			}
			if (BLEService.ACTION_ServicesDiscovered_OVER.equals(action)) {
				servicesdiscovered_flag = true;
				return;
			}
			if (BLEService.ACTION_STATE_CONNECTED.equals(action)) {
				connect_flag = true;
				return;
			}
		}
	}

	// // 初始化控件
	private List<List<BluetoothGattCharacteristic>> mBluetoothGattCharacteristic; // 记录所有特征
	private ExpandableListView service_list_view;
	private SimpleExpandableListAdapter service_list_adapter;
	private List<Map<String, String>> grounps;/// 一级条目
	private List<List<Map<String, String>>> childs;/// 二级条目

	private void initView() {
		service_list_view = (ExpandableListView) findViewById(R.id.service_list_view);
		grounps = new ArrayList<Map<String, String>>();
		childs = new ArrayList<List<Map<String, String>>>();
		mBluetoothGattCharacteristic = new ArrayList<List<BluetoothGattCharacteristic>>();
		service_list_adapter = new SimpleExpandableListAdapter(this, grounps,
				R.layout.service_grounp_fmt, new String[] { "name", "Uuid" },
				new int[] { R.id.grounpname_txt, R.id.grounp_uuid_txt },
				childs, R.layout.service_child_fmt, new String[] { "name",
						"prov", "uuid" }, new int[] { R.id.childname_txt,
						R.id.prov, R.id.child_uuid_txt });
		service_list_view.setAdapter(service_list_adapter);
		service_list_view.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(getApplicationContext(),
						StartActivity.class);
				intent.putExtra("one", groupPosition);
				intent.putExtra("two", childPosition);
				intent.putExtra("name", USER_NAME);
				startActivity(intent);
				return false;
			}
		});
	}
	//// 获取名字
	private boolean read_name_flag = false;
	private boolean servicesdiscovered_flag = false;
	private boolean connect_flag = false;
	private BluetoothDevice device;
	private String USER_NAME;//// 用户名
	private Handler dis_services_handl = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			service_list_adapter.notifyDataSetChanged();
		}
	};
	private Handler readNameFail = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			new readNameThread().start();
		}
	};
	private void getDefaultName() {
		device = (BluetoothDevice) getIntent()
				.getParcelableExtra("device");
		new readNameThread().start();
	}
	
	private void getUserName() {
		Intent intent = getIntent();
		USER_NAME = intent.getStringExtra("name");
	}
	// // 读取线程
	private class readNameThread extends Thread{
		@SuppressLint("NewApi") @Override
		public void run() {
			super.run();
			connect_flag = false; // 连接设备
			Tools.mBLEService.conectBle(device);
			while (!connect_flag) {
				for (int i = 0; i < 100; i++) {
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (connect_flag) {
						break;
					}
				}
				if (!connect_flag) {
					Tools.mBLEService.mBluetoothGatt.connect();
				}
				if (exit_activity) {
					return;
				}
			}
			servicesdiscovered_flag = false; // 读取设备名
			Tools.mBLEService.mBluetoothGatt.discoverServices();
			for(int i=0; i<200;i++){
				try {
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(servicesdiscovered_flag){
					break;
				}
				if(i>190){
					Tools.mBLEService.disConectBle();
					readNameFail.sendEmptyMessageDelayed(0, 200);
					return;
				}
				if (exit_activity) {
					return;
				}
			}
//			while (!servicesdiscovered_flag) {
//				if (exit_activity) {
//					return;
//				}
//			}
			read_name_flag = false; // 读取设备名
			List<BluetoothGattService> services = Tools.mBLEService.mBluetoothGatt
					.getServices();
			String uuid;
			for (BluetoothGattService service : services) {
				uuid = service.getUuid().toString();
				List<BluetoothGattCharacteristic> gattCharacteristics = service
						.getCharacteristics();
				//  添加一个一级目录
				Map<String, String> grounp = new HashMap<String, String>();
				grounp.put("name",
						SampleGattAttributes.lookup(uuid, "unknow"));
				grounp.put("Uuid", uuid);
				grounps.add(grounp);
				List<BluetoothGattCharacteristic> grounpCharacteristic = new ArrayList<BluetoothGattCharacteristic>();

				List<Map<String, String>> child = new ArrayList<Map<String, String>>();

				for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
					Map<String, String> child_data = new HashMap<String, String>(); //// 添加一个二级条目
					uuid = gattCharacteristic.getUuid().toString();
					BluetoothGattDescriptor descriptor = gattCharacteristic
							.getDescriptor(UUID
									.fromString("00002901-0000-1000-8000-00805f9b34fb"));
					if (null != descriptor) {
						read_name_flag = false;
						Tools.mBLEService.mBluetoothGatt
								.readDescriptor(descriptor);
						while (!read_name_flag) {//等待读取完成
							if (exit_activity)
								return; // 读取超时
						}
						try {
							child_data.put("name",
									new String(descriptor.getValue(),
											"GB2312"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else {
						child_data
								.put("name", SampleGattAttributes.lookup(
										uuid, "unknow"));
					}

					String pro = "";
					if (0 != (gattCharacteristic.getProperties() & 0x02)) { //
						pro += "可读,";
					}
					if (0 != (gattCharacteristic.getProperties() & 0x08)) { //
						pro += "可写,";
					}
					if (0 != (gattCharacteristic.getProperties() & 0x10)) { //
						pro += "可通知";
					}
					child_data.put("prov", pro);
					child_data.put("uuid", uuid);
					child.add(child_data);
					grounpCharacteristic.add(gattCharacteristic);
				}
				childs.add(child); //一个一级条目添加完成
				mBluetoothGattCharacteristic.add(grounpCharacteristic);
			}
			dis_services_handl.sendEmptyMessage(0);
		}
	}

	private boolean exit_activity = false;

	@Override
	protected void onDestroy() {
		Tools.mBLEService.disConectBle();
		exit_activity = true;
		unregisterReceiver(bluetoothReceiver);
		super.onDestroy();
	}
}
