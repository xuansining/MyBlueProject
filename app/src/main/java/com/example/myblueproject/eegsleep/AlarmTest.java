package com.example.myblueproject.eegsleep;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.myblueproject.R;

import java.util.Calendar;

public class AlarmTest extends Activity {
	Button mButton1;
	Button mButton2;
	Calendar calendar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clock);
		calendar = Calendar.getInstance();
        ////获取布局界面 中的组件
		mButton1 = (Button) findViewById(R.id.setTime);
		mButton2 = (Button) findViewById(R.id.cancelTime);
        //为“设置闹钟”按钮绑定监听器
		mButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int mHour = calendar.get(Calendar.HOUR_OF_DAY);
				int mMinute = calendar.get(Calendar.MINUTE);
				//创建一个TimePickerDialog实例，并把它显示出来
				new TimePickerDialog(AlarmTest.this,////绑定监听器
						new TimePickerDialog.OnTimeSetListener() {
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								//设置为当前的系统时间
								calendar.setTimeInMillis(System
										.currentTimeMillis());
								////根据用户选择时间来设置Calendar对象
								calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
								calendar.set(Calendar.MINUTE, minute);
							    //指定启动AlarmReceiver组件
								Intent intent = new Intent(AlarmTest.this,
										AlarmActivity.class);
								PendingIntent pendingIntent = PendingIntent
										.getBroadcast(AlarmTest.this, 0,
												intent, 0);
								AlarmManager am;
								/* 获取闹钟管理的实例 */
								am = (AlarmManager) getSystemService(ALARM_SERVICE);
								/* 设置闹钟 */
								am.set(AlarmManager.RTC_WAKEUP, calendar
										.getTimeInMillis(), pendingIntent);
								/* 设置周期闹 */
								am.setRepeating(AlarmManager.RTC_WAKEUP, System
										.currentTimeMillis()
										+ (10 * 1000), (24 * 60 * 60 * 1000),
										pendingIntent);
							}
						}, mHour, mMinute, true).show();
			}
		});

		mButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AlarmTest.this, AlarmActivity.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						AlarmTest.this, 0, intent, 0);
				AlarmManager am;
				/* 获取闹钟管理的实例 */
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				/* 取消 */
				am.cancel(pendingIntent);
			}
		});
	}
}
