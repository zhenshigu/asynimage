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

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LoginThread implements Runnable{
	private String email;
	private String password;
	private Handler handler;
	private JSONObject jsonObject;
	private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	private SharedPreferences sp;
	public LoginThread(String email,String password,Handler handler,SharedPreferences sharedPreferences) {
		this.email=email;
		this.password=password;
		this.handler=handler;
		this.sp=sharedPreferences;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean loginValidate = loginServer(email, password);
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
	private boolean loginServer(String email, String password) {
		boolean loginValidate = false;
		String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/customerManage/verify";
		HttpPost request = new HttpPost(urlStr);
		// 如果传递参数多的话，可以丢传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 添加用户名和密码
		Log.i("loginthread loginserver email", email);
		Log.i("loginthread loginserver password", password);
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
				String responseMsg = EntityUtils.toString(response.getEntity());
				 jsonObject=new JSONObject(responseMsg);
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
	public HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}
}
