package com.example.myblueproject.eegsleep;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.myblueproject.R;
import com.example.myblueproject.bluetooth.DeviceScanActivity;
import com.example.myblueproject.historyActivity.HelpActivity;
import com.example.myblueproject.historyActivity.HistoryActivity;
import com.example.myblueproject.userInfoActivity.UserInfoActivity;

public class HomePage extends Activity implements OnClickListener   {
	
	private Button btnSub; //主观评价按钮
	private Button btnAna; ////数据分析按钮
	private Button btnHis; //˯睡眠记录按钮
	private Button btnSet; //设置按钮
	private Button btnAdi; //建议按钮
	private Button btnMore;//更多按钮
	private String USER_NAME;// 用户名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homelayout);
		initView();
		// 获取用户名，作为数据文件的命名
		getUserName();
	}

	////按钮控件初始化
  private void initView() {
	    btnSub = (Button) findViewById(R.id.subjective);
	    btnAna = (Button) findViewById(R.id.analysis);
	    btnHis = (Button) findViewById(R.id.history);
	    btnSet = (Button) findViewById(R.id.set);
	    btnAdi = (Button) findViewById(R.id.advise);
	    btnMore= (Button) findViewById(R.id.more);
	    btnSub.setOnClickListener(this);
	    btnAna.setOnClickListener(this);
	    btnHis.setOnClickListener(this);
	    btnSet.setOnClickListener(this);
	    btnAdi.setOnClickListener(this);
	    btnMore.setOnClickListener(this);
	}

  /**
	 * 获取用户名，作为数据文件的命名
	 */
	private void getUserName() {
		Intent intent = getIntent();
		USER_NAME = intent.getStringExtra("user_name");
	}
	
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.subjective: // 主观评价
		Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri
				.parse("https://www.sojump.com/jq/3253577.aspx")); //访问PSQI网站
		break;

	case R.id.analysis: // // 跳转到蓝牙配对
		Intent intent = new Intent(HomePage.this, DeviceScanActivity.class);
		intent.putExtra("name", USER_NAME);
		startActivity(intent);
		break;
		
	case R.id.history: // ˯// 睡眠记录
		Intent historyIntent = new Intent(HomePage.this, HistoryActivity.class);
		startActivity(historyIntent);
		break;
	
	case R.id.set: // //设置
		Intent clockIntent = new Intent(HomePage.this, AlarmTest.class);
		startActivity(clockIntent);
		break;
		
	case R.id.advise://建议
		Intent helpIntent = new Intent(HomePage.this, HelpActivity.class);
		startActivity(helpIntent);
		break;
		
	case R.id.more: //更多
		Intent moreIntent = new Intent(HomePage.this, UserInfoActivity.class);
		startActivity(moreIntent);
		break;
	}
}	
}
