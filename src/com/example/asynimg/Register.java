package com.example.asynimg;

import java.lang.reflect.Array;
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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener{
	private ImageView register_back, user_name_clear, password_clear,
	confirm_password_clear;
private TextView user_name_error, password_error, confirm_password_error;
private Button register_button;
private EditText user_name_edit, password_edit, confirm_password_edit,emaiEditText,phoneEditText,shenEditText,shiEditText,xianEditText;
private Dialog registerDialog;
private String responseMsg = "";
private String sex;
private Spinner spinner;
private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时10秒钟
private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
private static final int LOGIN_OK = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		InitView();
		ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		arrayAdapter.add("男");
		arrayAdapter.add("女");
		spinner.setAdapter(arrayAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				sex=parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void InitView() {
		// TODO Auto-generated method stub
		register_back = (ImageView) findViewById(R.id.register_back);
		user_name_clear = (ImageView) findViewById(R.id.user_name_clear);
		password_clear = (ImageView) findViewById(R.id.password_clear);
		confirm_password_clear = (ImageView) findViewById(R.id.confirm_password_clear);
		register_button = (Button) findViewById(R.id.register_button);
		user_name_edit = (EditText) findViewById(R.id.user_name_edit);
		password_edit = (EditText) findViewById(R.id.password_edit);
		confirm_password_edit = (EditText) findViewById(R.id.confirm_password_edit);
		user_name_error = (TextView) findViewById(R.id.user_name_error);
		password_error = (TextView) findViewById(R.id.password_error);
		confirm_password_error = (TextView) findViewById(R.id.confirm_password_error);
		shenEditText=(EditText)findViewById(R.id.shen);
		shiEditText=(EditText)findViewById(R.id.shi);
		xianEditText=(EditText)findViewById(R.id.xian);
		phoneEditText=(EditText)findViewById(R.id.phone);
		emaiEditText=(EditText)findViewById(R.id.email);
		spinner=(Spinner)findViewById(R.id.sex);
		register_back.setOnClickListener(this);
		register_button.setOnClickListener(this);
		user_name_edit.setOnClickListener(this);
		password_edit.setOnClickListener(this);
		confirm_password_edit.setOnClickListener(this);
		user_name_clear.setOnClickListener(this);
		password_clear.setOnClickListener(this);
		confirm_password_clear.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_back:

			this.finish();
			break;
		case R.id.register_button:

			RegisterUser();
			break;
		case R.id.user_name_edit:
			user_name_error.setVisibility(View.GONE);
			user_name_clear.setVisibility(View.VISIBLE);
			password_clear.setVisibility(View.GONE);
			confirm_password_clear.setVisibility(View.GONE);
			break;
		case R.id.password_edit:
			password_error.setVisibility(View.GONE);
			user_name_clear.setVisibility(View.GONE);
			password_clear.setVisibility(View.VISIBLE);
			confirm_password_clear.setVisibility(View.GONE);

			break;
		case R.id.confirm_password_edit:
			confirm_password_error.setVisibility(View.GONE);
			user_name_clear.setVisibility(View.GONE);
			password_clear.setVisibility(View.GONE);
			confirm_password_clear.setVisibility(View.VISIBLE);
			break;
		case R.id.user_name_clear:
			user_name_edit.setText("");
			break;

		case R.id.password_clear:
			password_edit.setText("");
			break;
		case R.id.confirm_password_clear:
			confirm_password_edit.setText("");
			break;

		default:
			break;
		}
	}

	public void RegisterUser() {

		if (user_name_edit.getText().toString().trim().equals("")
				|| user_name_edit.getText().toString().trim().length() > 20
				|| user_name_edit.getText().toString().trim().length() < 4) {
			user_name_error.setVisibility(View.VISIBLE);
		} else if (password_edit.getText().toString().trim().equals("")
				|| password_edit.getText().toString().trim().length() > 16
				|| password_edit.getText().toString().trim().length() < 6) {
			password_error.setVisibility(View.VISIBLE);
		} else if (!confirm_password_edit.getText().toString().trim()
				.equals(password_edit.getText().toString().trim())) {
			confirm_password_error.setVisibility(View.VISIBLE);
		} else {

			String newusername = user_name_edit.getText().toString();
			String newpassword = PicUtil.getMD5(password_edit.getText().toString());
			String confirmpwd = PicUtil.getMD5(confirm_password_edit.getText()
					.toString());
			Toast.makeText(this, "注册中，请稍后", Toast.LENGTH_LONG).show();
			Thread loginThread = new Thread(new RegisterThread());
			loginThread.start();
		}

	}

	// 初始化HttpClient，并设置超时
	public HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	private boolean registerServer(String username, String password,String email,String phone,String sex,String shen,String shi,String xian) {
		boolean loginValidate = false;
		// 使用apache HTTP客户端实现
		String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/customerManage/addCustomer";
		HttpPost request = new HttpPost(urlStr);
		// 如果传递参数多的话，可以丢传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 添加用户名和密码
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("cname", username));
		params.add(new BasicNameValuePair("sex", sex));
		params.add(new BasicNameValuePair("phone", phone));
		params.add(new BasicNameValuePair("shen", shen));
		params.add(new BasicNameValuePair("shi", shi));
		params.add(new BasicNameValuePair("xian", xian));
		params.add(new BasicNameValuePair("password", password));
		try {
			// 设置请求参数项
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpClient client = getHttpClient();
			// 执行请求返回相应
			HttpResponse response = client.execute(request);

			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				loginValidate = true;
				// 获得响应信息
				responseMsg = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loginValidate;
	}

	// Handler
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Bundle bundle = new Bundle();
				bundle.putString("email", emaiEditText.getText()
						.toString());
				bundle.putString("password", password_edit.getText().toString());
				Intent intent = new Intent();
				intent.putExtras(bundle);
				// 返回intent
				setResult(RESULT_OK, intent);
				Register.this.finish();
				Toast.makeText(Register.this, "注册成功", 1).show();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), "注册失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "服务器连接失败！",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	// RegisterThread线程类
	class RegisterThread implements Runnable {

		@Override
		public void run() {
			String username = user_name_edit.getText().toString();
			String password = PicUtil.getMD5(password_edit.getText().toString());
			String email=emaiEditText.getText().toString();
			String phone=phoneEditText.getText().toString();
			String shen=shenEditText.getText().toString();
			String shi=shiEditText.getText().toString();
			String xian=xianEditText.getText().toString();
			// URL合法，但是这一步并不验证密码是否正确
			boolean registerValidate = registerServer(username, password,email,phone,sex,shen,shi,xian);
			// System.out.println("----------------------------bool is :"+registerValidate+"----------response:"+responseMsg);
			Message msg = handler.obtainMessage();
			if (registerValidate) {
				if (responseMsg.equals("success")) {
					msg.what = 0;
					handler.sendMessage(msg);
				} else {
					msg.what = 1;
					handler.sendMessage(msg);
				}

			} else {
				msg.what = 2;
				handler.sendMessage(msg);
			}
		}

	}

}

