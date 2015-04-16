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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddAdr extends Activity {

	private SharedPreferences spPreferences;
	private Handler handler;
	private String rnameString;
	private String rphoneString;
	private String raddrString;
	private String cid;
	private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时10秒钟
	 private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_adr);
		final EditText rname=(EditText)findViewById(R.id.editText1);
		final EditText rphone=(EditText)findViewById(R.id.editText2);
		final EditText raddr=(EditText)findViewById(R.id.editText3);
		Button btnok=(Button)findViewById(R.id.button1);
		spPreferences=getSharedPreferences("userinfo", 0);
		handler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 1:
					Toast.makeText(AddAdr.this, "添加成功", Toast.LENGTH_SHORT).show();
					setResult(1);
					finish();
					break;

				case 0:
					Toast.makeText(AddAdr.this, "添加失败", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(AddAdr.this, "服务器错误", Toast.LENGTH_SHORT).show();
					break;
				}
			};
		};
		btnok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 rnameString=rname.getText().toString();
				 rphoneString=rphone.getText().toString();
				 raddrString=raddr.getText().toString();
				 cid=spPreferences.getString("cid", "");
				new Thread(addRunnable).start();
			}
		});
	}
	Runnable addRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/addAddr";
			HttpPost request = new HttpPost(urlStr);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("rname",rnameString ));
			params.add(new BasicNameValuePair("rphone",rphoneString ));
			params.add(new BasicNameValuePair("raddress",raddrString ));
			params.add(new BasicNameValuePair("cid",cid ));
			try {
				// 设置请求参数项
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpClient client = getHttpClient();
				// 执行请求返回相应
				HttpResponse response = client.execute(request);

				// 判断是否请求成功
				if (response.getStatusLine().getStatusCode() == 200) {
					String responseMsg = EntityUtils.toString(response.getEntity());
					Log.i("manageaddress run", responseMsg);
					if (responseMsg.equals("success")) {
						Message message=handler.obtainMessage(1);
						handler.sendMessage(message);
					}else {
						Message message=handler.obtainMessage(0);
						handler.sendMessage(message);
					}
				}else {
					Message message=handler.obtainMessage(2);
					handler.sendMessage(message);
					Log.i("login", response.getStatusLine().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	public HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}
}
