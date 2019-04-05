package com.example.myblueproject.eegsleep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.example.myblueproject.R;

public class LoginActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnDismissListener {

	private EditText usr;// 用户名
	private EditText pwd; // 密码
	private CheckBox rem_password;// 记住密码
	private Button btn_login;
	private Button btn_regist;
	private Button btn_exit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginlayout);
		initView();
	}


	private void initView() {
		usr = (EditText) findViewById(R.id.edit_username);
		pwd = (EditText) findViewById(R.id.edit_password);
		rem_password = (CheckBox) findViewById(R.id.cb_mima);
		btn_regist = (Button) findViewById(R.id.regist);
		btn_login = (Button) findViewById(R.id.login);
		btn_exit = (Button) findViewById(R.id.exit);
		btn_regist.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
	}

	//获取注册时的用户名和密码
	private void loginBefore() {
		String name = usr.getText().toString();
		String passd = pwd.getText().toString();
		//获取注册时的用户名和密码
		SharedPreferences ref = LoginActivity.this.getSharedPreferences(
				"datafile", Context.MODE_PRIVATE);
		String name1 = ref.getString("usrname", "shang");
		String pwd1 = ref.getString("password", "1");

        ////判断用户名和密码是否匹配成功
		if (name.equalsIgnoreCase(name1)) {
			if (passd.equalsIgnoreCase(pwd1)) {
				showToastInfo("正确匹配，正在登陆");
				Intent inlog = new Intent();
				inlog.setClass(LoginActivity.this, HomePage.class);
				startActivity(inlog);
			} else {
				showToastInfo("输入密码错误");
			}
		} else {
			showToastInfo("输入用户名不正确");
		}
	}

	/*Toast信息提示*/
	private void showToastInfo(String s) {
		Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
	}

	/* 按钮点击事件 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.regist: //
			Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
			startActivity(intent);
			break;

		case R.id.login: //
			loginBefore();
			break;

		case R.id.exit: //
			LoginActivity.this.finish();
			break;
		}
	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
	}
}
