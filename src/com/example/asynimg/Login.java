package com.example.asynimg;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {
	private TextView registertextview, login_cancle, recode_textview;
	private TextView usernameerrorid, passworderrorid;
	private EditText LoginName, Password;
	private ImageView login_name_clear_btn, clear_btn2;
	private CheckBox autologin;
	private Button BtnMenulogin;
	private String responseMsg = "";
	private static Handler handler;
	private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	private static final int LOGIN_OK = 1;
	private SharedPreferences sp;
	private JSONObject jsonObject;
	int REQUEST_CODE = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitUtil.activityList.add(this);
		setContentView(R.layout.activity_login);
		sp = getSharedPreferences("userinfo", 0);//获取存储用户的信息
		InitView();
		//设置是否自动登录
		autologin.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
					Editor editor = sp.edit();
					editor.putBoolean("autologin", isChecked);
					editor.commit();
//					if (sp.getBoolean("autologin",false)) {
//						Log.i("login checked", "true");
//					}
				
			}
		});
		sp.edit().putBoolean("autologin", autologin.isChecked()).commit();//保存自动登录属性到userinfo
		  handler= new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0://登录成功
//					Toast.makeText(getApplicationContext(), "登录成功！",
//							Toast.LENGTH_SHORT).show();
					Intent intent=new Intent(Login.this,P1.class);
					setResult(1, intent);
					finish();
					break;
				case 1://输入的密码错误
					Toast.makeText(getApplicationContext(), "输入的密码错误",
							Toast.LENGTH_SHORT).show();
					break;
				}

			}
		};
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode==RESULT_OK) {
			Bundle bundle=data.getExtras();
			LoginName.setText(bundle.getString("email"));
			Password.setText(bundle.getString("password"));
		}
	}
	//初始化界面
	private void InitView() {
		// TODO Auto-generated method stub
		registertextview = (TextView) findViewById(R.id.register_text);
		login_cancle = (TextView) findViewById(R.id.login_cancle);

		LoginName = (EditText) findViewById(R.id.LoginName);
		Password = (EditText) findViewById(R.id.password);

		usernameerrorid = (TextView) findViewById(R.id.usernameerrorid);
		passworderrorid = (TextView) findViewById(R.id.passworderrorid);


		autologin = (CheckBox) findViewById(R.id.autologin);

		BtnMenulogin = (Button) findViewById(R.id.BtnMenulogin);

		registertextview.setOnClickListener(this);
		login_cancle.setOnClickListener(this);
		LoginName.setOnClickListener(this);
		Password.setOnClickListener(this);
//		login_name_clear_btn.setOnClickListener(this);
//		clear_btn2.setOnClickListener(this);
		BtnMenulogin.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_cancle://登录取消
			this.finish();
			break;

		case R.id.BtnMenulogin://登录

			Loginbtn();

			break;
		case R.id.register_text://注册
			Intent intent=new Intent(Login.this, Register.class);
			startActivityForResult(intent, 2);
		default:
			break;
		}
	}
	public void Loginbtn() {

		if (LoginName.getText().toString().trim().equals("")
				|| LoginName.getText().toString().trim().length() > 20
				|| LoginName.getText().toString().trim().length() < 4) {
			usernameerrorid.setVisibility(View.VISIBLE);
			usernameerrorid.setText("用户名不规范");
		} 
		else if (Password.getText().toString().trim().equals("")
				|| Password.getText().toString().trim().length() > 16
				|| Password.getText().toString().trim().length() < 3) {
			passworderrorid.setVisibility(View.VISIBLE);
			passworderrorid.setText("密码不规范");
		} 
		else {
			Thread loginThread = new Thread(new LoginThread());

			loginThread.start();//启动登录线程
		}
	}
	// LoginThread线程类
		class LoginThread implements Runnable {

			@Override
			public void run() {
				String username = LoginName.getText().toString();
				String password = Password.getText().toString();
				password = PicUtil.getMD5(password);
				Log.i("Login", username);
				Log.i("login",password);
				// URL合法，但是这一步并不验证密码是否正确
				boolean loginValidate = loginServer(username, password);
				Message msg = handler.obtainMessage();
					if (loginValidate) {
						Editor editor=sp.edit();
						try {
							editor.putString("cid", jsonObject.getString("cid"));
							editor.putString("email", jsonObject.getString("email"));
							editor.putString("password", password);
							editor.putString("name", jsonObject.getString("name"));
							editor.putString("phone", jsonObject.getString("phone"));
							editor.putString("shen", jsonObject.getString("shen"));
							editor.putString("shi", jsonObject.getString("shi"));
							editor.putString("xian", jsonObject.getString("xian"));
							editor.putBoolean("isLogin", true);
							editor.commit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						msg.what = 0;
						handler.sendMessage(msg);
					} else {
						msg.what = 1;
						handler.sendMessage(msg);
					}

			}

		}




		private boolean loginServer(String email, String password) {
			boolean loginValidate = false;
			String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/customerManage/verify";
			HttpPost request = new HttpPost(urlStr);
			// 如果传递参数多的话，可以丢传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// 添加用户名和密码
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("password", password));
			try {
				// 设置请求参数项
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpClient client = getHttpClient();
				// 执行请求返回相应
				HttpResponse response = client.execute(request);

				// 判断是否请求成功
				if (response.getStatusLine().getStatusCode() == 200) {
					responseMsg = EntityUtils.toString(response.getEntity());
					jsonObject=new JSONObject(responseMsg);
					Log.i("login", responseMsg);
					Log.i("login", jsonObject.toString());
					if (jsonObject.has("email") ) {
						loginValidate = true;
					}
				}else {
					Log.i("login", response.getStatusLine().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return loginValidate;
		}

		// // 初始化HttpClient，并设置超时
		public HttpClient getHttpClient() {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			HttpClient client = new DefaultHttpClient(httpParams);
			return client;
		}
}
