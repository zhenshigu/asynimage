package com.example.asynimg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class P1 extends Activity {
	private String username;
	private String tag;
	private SharedPreferences sharedPreferences;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//====20150410=======================
		
		handler= new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					LinearLayout layout=(LinearLayout)findViewById(R.id.logintip);
					layout.setVisibility(View.GONE);
					RelativeLayout layout2=(RelativeLayout)findViewById(R.id.myaddress);
					layout2.setVisibility(View.VISIBLE);
					
					
					ListView personinfo=(ListView)findViewById(R.id.personinfo);
					personinfo.setVisibility(View.VISIBLE);
					ArrayAdapter<String> infoAdapter=new ArrayAdapter<String>(P1.this, android.R.layout.simple_list_item_1);
					infoAdapter.add(sharedPreferences.getString("name", "null"));
					infoAdapter.add(sharedPreferences.getString("email", "null"));
					infoAdapter.add(sharedPreferences.getString("phone", "null"));
					personinfo.setAdapter(infoAdapter);
					Button manageAddr=(Button)findViewById(R.id.button1);
					manageAddr.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View view) {
							// TODO Auto-generated method stub
							
						}
					});
					break;
				case 1:
					Toast.makeText(getApplicationContext(), "自动登录失败",
							Toast.LENGTH_SHORT).show();
					sharedPreferences.edit().remove("autologin").commit();
					break;
				}

			}
		};
		ExitUtil.activityList.add(this);
		//=========================================
		tag="P1";
		Log.i(tag, "in oncreate");
		setContentView(R.layout.activity_p1);
		TextView unlogin=(TextView)findViewById(R.id.unlogin);
		Button login=(Button)findViewById(R.id.login);
		
		sharedPreferences=getSharedPreferences("userinfo", 0);
		boolean isLogin=sharedPreferences.getBoolean("isLogin", false);
		 boolean isAutologin=sharedPreferences.getBoolean("autologin", false);
		String email=sharedPreferences.getString("email", null);
		String password=sharedPreferences.getString("password", null);
		login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setComponent(new ComponentName("com.example.asynimg", "com.example.asynimg.Login"));
//				startActivity(intent);
				startActivityForResult(intent, 0);
			}
		});
		if (!isLogin && !isAutologin) {
			
			unlogin.setText("你还没登录");

		}
		else if (!isLogin && isAutologin && email!=null && password!=null) {
			Log.i("p1 email", email);
			Log.i("p1 pwd", password);
			new Thread(new LoginThread(email, password, handler, sharedPreferences)).start();
		}
		else {
			LinearLayout layout=(LinearLayout)findViewById(R.id.logintip);
			layout.setVisibility(View.GONE);
			RelativeLayout layout2=(RelativeLayout)findViewById(R.id.myaddress);
			layout2.setVisibility(View.VISIBLE);
			Button addButton=(Button)findViewById(R.id.button1);
			addButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					intent.setComponent(new ComponentName(getPackageName(), "com.example.asynimg.ManageAddress"));
					startActivity(intent);
				}
			});
			ListView personinfo=(ListView)findViewById(R.id.personinfo);
			personinfo.setVisibility(View.VISIBLE);
			ArrayAdapter<String> infoAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
			infoAdapter.add("用户名:"+sharedPreferences.getString("name", "null"));
			infoAdapter.add("邮箱:"+sharedPreferences.getString("email", "null"));
			infoAdapter.add("手机:"+sharedPreferences.getString("phone", "null"));
			personinfo.setAdapter(infoAdapter);
		}
	}
	@Override
	protected void onActivityResult(int requestcode, int resultcode, Intent intent) {
		// TODO Auto-generated method stub
		switch (resultcode) {
		case 1:
			LinearLayout layout=(LinearLayout)findViewById(R.id.logintip);
			layout.setVisibility(View.GONE);
			ListView personinfo=(ListView)findViewById(R.id.personinfo);
			personinfo.setVisibility(View.VISIBLE);
			ArrayAdapter<String> infoAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
			infoAdapter.add(sharedPreferences.getString("name", "null"));
			infoAdapter.add(sharedPreferences.getString("email", "null"));
			infoAdapter.add(sharedPreferences.getString("phone", "null"));
			personinfo.setAdapter(infoAdapter);
			break;

		default:
			break;
		}
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Log.i(tag, "in restoreinstancestate");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuItem item=menu.add(1, 1, 1, "帐号切换");
		MenuItem item2=menu.add(1,2,2,"退出");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == 2) {
			Editor editor=sharedPreferences.edit();
			editor.remove("isLogin");
			editor.commit();
			ExitUtil.exit();
			return true;
		}
		if (id==1) {
			Intent intent=new Intent();
			intent.setComponent(new ComponentName("com.example.asynimg", "com.example.asynimg.Login"));
//			startActivity(intent);
			startActivityForResult(intent, 0);
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(tag, "in onstart");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(tag, "in onresume");
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(tag, "in onpause");
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i(tag, "in onstop");
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		Log.i(tag, "in destroy");
		super.onDestroy();
	}
}
