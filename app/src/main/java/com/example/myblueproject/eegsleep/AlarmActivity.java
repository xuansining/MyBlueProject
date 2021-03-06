package com.example.myblueproject.eegsleep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.example.myblueproject.R;

public class AlarmActivity extends Activity
{
	MediaPlayer alarmMusic;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// // 加载指定音乐，并为之创建MediaPlayer对象
		alarmMusic = MediaPlayer.create(this, R.raw.promise);
		alarmMusic.setLooping(true);
		// 播放音乐
		alarmMusic.start();
		// 创建一个对话框
		new AlertDialog.Builder(AlarmActivity.this).setTitle("闹钟")
			.setMessage("设置的时间到")
			.setPositiveButton("确定", new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//停止音乐					/
					alarmMusic.stop();
					// 结束该Activity
					AlarmActivity.this.finish();
				}
			}).create().show();
	}
}
