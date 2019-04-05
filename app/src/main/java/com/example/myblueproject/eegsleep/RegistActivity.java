package com.example.myblueproject.eegsleep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.myblueproject.R;

public class RegistActivity extends Activity {

	private EditText edit_username;
	private EditText edit_passwd;
	private EditText edit_passwd_again;
	private EditText edit_age;
	private RadioButton man;
	private RadioButton women;
	private Button regist;
	private Button exit;
	private String ageStr;
	private String genderStr;

	private boolean regFlag = false;//注册成功标识

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.regist);

		/*获取布局中的控件*/
		edit_username = (EditText) findViewById(R.id.edit_username);
		edit_passwd = (EditText) findViewById(R.id.edit_passwd);
		edit_passwd_again = (EditText) findViewById(R.id.edit_passwd_again);
		edit_age = (EditText) findViewById(R.id.edit_age);
		man = (RadioButton) findViewById(R.id.radio_man);
		women = (RadioButton) findViewById(R.id.radio_women);
		regist = (Button) findViewById(R.id.btn_regist);
		exit = (Button) findViewById(R.id.exit_regist);

		/*注册事件响应*/
		regist.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				registBefore();
				if (regFlag == true) {
				Toast.makeText(RegistActivity.this, "恭喜您，注册成功", Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent();
				intent.setClass(RegistActivity.this, LoginActivity.class);
				startActivity(intent);
			}
			}
		});

		/*取消注册*/
       exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RegistActivity.this.finish();
			}
		});
	}

	/* 注册前判断注册信息是否合理*/
	protected void registBefore() {
		String name = edit_username.getText().toString();
		String password = edit_passwd.getText().toString();
		String repassword = edit_passwd_again.getText().toString();
		String age = edit_age.getText().toString();
		regFlag = false;
		/*获得一个sharedPreferences对象（轻量级）*/
		SharedPreferences ref = RegistActivity.this.getSharedPreferences(
				"datafile", Context.MODE_PRIVATE);
		//Context.MODE_WORLD_READABLE:指定该SharedPreferences数据能被其他应用程序读，但不能写
		Editor ed = ref.edit();

		/*判断用户名是否为空*/
		if (name.trim().equals("")) {
			Toast.makeText(RegistActivity.this, "用户名不能为空！",
					Toast.LENGTH_LONG).show();
			return;
		}

		/*判断密码是否为空*/
		if (password.trim().equals("")
				|| repassword.trim().equals("")) {
			Toast.makeText(RegistActivity.this, "密码不能为空！",
					Toast.LENGTH_LONG).show();
			return;
		}

		/*判断两次输入密码是否一致*/
		if (!(password.trim().equals(repassword.trim()))) {
			Toast.makeText(RegistActivity.this, "两次密码输入不一致！",
					Toast.LENGTH_LONG).show();
			return;
		}


		/*将usrname、password传递到LoginActivity中*/
		ed.putString("usrname", name);
		ed.putString("password", password);
		ed.commit();

		/*选择性别*/
		if (man.isChecked()) {
			genderStr = "男";
		} else if (women.isChecked()) {
			genderStr = "女";
		} else {
			showToast("请选择您的性别");
			return;
		}
        /*选择年龄*/
		if (age.length() != 0) {
			ageStr = age;
		} else {
			showToast("请输入您的年龄");
			return;
		}
		regFlag = true;
	}

	private void showToast(String info) {
		Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
	}
}
