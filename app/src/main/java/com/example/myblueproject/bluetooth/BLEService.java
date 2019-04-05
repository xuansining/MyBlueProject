package com.example.myblueproject.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BLEService extends Service {

	public final static String ACTION_DATA_CHANGE = "com.example.bluetooth.le.ACTION_DATA_CHANGE";


	public final static String ACTION_RSSI_READ = "com.example.bluetooth.le.ACTION_RSSI_READ";
	public final static String ACTION_STATE_CONNECTED = "com.example.bluetooth.le.ACTION_STATE_CONNECTED";
	public final static String ACTION_STATE_DISCONNECTED = "com.example.bluetooth.le.ACTION_STATE_DISCONNECTED";
	public final static String ACTION_WRITE_OVER = "com.example.bluetooth.le.ACTION_WRITE_OVER";
	public final static String ACTION_READ_OVER = "com.example.bluetooth.le.ACTION_READ_OVER";
	public final static String ACTION_READ_Descriptor_OVER = "com.example.bluetooth.le.ACTION_READ_Descriptor_OVER";
	public final static String ACTION_WRITE_Descriptor_OVER = "com.example.bluetooth.le.ACTION_WRITE_Descriptor_OVER";
	public final static String ACTION_ServicesDiscovered_OVER = "com.example.bluetooth.le.ACTION_ServicesDiscovered_OVER";

	public  BluetoothManager mBluetoothManager;
	public  BluetoothAdapter mBluetoothAdapter;
	public  BluetoothGatt mBluetoothGatt;

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			super.onConnectionStateChange(gatt, status, newState);
			if (newState == BluetoothProfile.STATE_CONNECTED) { // 衔接成功
				System.out.println("CONNECTED");

//				mBluetoothGatt.discoverServices();
				broadcastUpdate(ACTION_STATE_CONNECTED);

			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 断开连接
				System.out.println("UNCONNECTED");
				broadcastUpdate(ACTION_STATE_DISCONNECTED);
			}

		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			System.out.println("onServicesDiscovered");
			broadcastUpdate(ACTION_ServicesDiscovered_OVER, status);
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			super.onDescriptorRead(gatt, descriptor, status);

			broadcastUpdate(ACTION_READ_Descriptor_OVER, status);
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(ACTION_READ_OVER, characteristic.getValue());
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			super.onCharacteristicChanged(gatt, characteristic);
			broadcastUpdate(ACTION_DATA_CHANGE, characteristic.getValue());
		}
		

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			broadcastUpdate(ACTION_WRITE_OVER, status);
		}

	};
  ////通过继承Binder来实现IBinder类
	public class LocalBinder extends Binder {
		public BLEService getService() {
			return BLEService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();
   ////该方法是Service子类必须实现的方法，返回一个IBinder对象，应用程序可通过该对象与Service组件通信
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
		//返回IBinder对象
	}

	// 	// 初始化BLE
	public boolean initBle() {
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

		if (null == mBluetoothManager) {
			return false;
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (null == mBluetoothAdapter) {
			return false;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}

		return true;
	}

	// // 扫描

	public void scanBle(BluetoothAdapter.LeScanCallback callback) {
		mBluetoothAdapter.startLeScan(callback);

	}

	// 停止扫描
	@SuppressWarnings("deprecation")
	public void stopscanBle(BluetoothAdapter.LeScanCallback callback) {
		mBluetoothAdapter.stopLeScan(callback);
	}

	// // 发起连接
	public void conectBle(BluetoothDevice mBluetoothDevice) {
		disConectBle();
		mBluetoothGatt = mBluetoothDevice.connectGatt(getApplicationContext(),
				true, mGattCallback);
	}
	
	//  关闭连接
	public void disConectBle(){
		if(mBluetoothGatt != null){
			mBluetoothGatt.disconnect();
		}
	}
	
	// // 发送广播消息
	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	//// 发送广播消息
	private void broadcastUpdate(final String action, int value) {
		final Intent intent = new Intent(action);
		intent.putExtra("value", value);
		sendBroadcast(intent);
	}
	
	//// 发送广播消息
		@SuppressWarnings("unused")
		private void broadcastUpdate(final String action,String value) {
			final Intent intent = new Intent(action);
			intent.putExtra("value", value);
			sendBroadcast(intent);
		}
	// // 发送广播消息
	private void broadcastUpdate(final String action, byte value[]) {
		final Intent intent = new Intent(action);
		intent.putExtra("value", value);
		sendBroadcast(intent);
	}
}
