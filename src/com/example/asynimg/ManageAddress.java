package com.example.asynimg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.asynimg.AddressAdater.myCallback;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

public class ManageAddress extends Activity implements myCallback{
	private Handler handler;
	private SharedPreferences sp;
	private AddressAdater adater;
	private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时10秒钟
	 private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_address);
		Button addAddr=(Button)findViewById(R.id.addAddr);
		addAddr.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(ManageAddress.this, AddAdr.class), 0);
			}
		});
		ListView addrListView=(ListView)findViewById(R.id.listView1);
		List<Map<String, String>> addList=new ArrayList<Map<String,String>>();
		adater=new AddressAdater(this,addList,this);
		addrListView.setAdapter(adater);
		sp=getSharedPreferences("userinfo", 0);
		new Thread(getAddr).start();
		handler=new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					List<Map<String, String>> tmpList=new ArrayList<Map<String,String>>();
					String responseMsg=(String)msg.obj;
					JSONArray addressArray;
					try {
						addressArray = new JSONArray(responseMsg);
						for(int i=0;i<addressArray.length();i++){
							JSONObject tmp=addressArray.getJSONObject(i);
							Map<String, String> tmpMap=new HashMap<String, String>();
							tmpMap.put("aid", tmp.getString("aid"));
							tmpMap.put("rname", tmp.getString("rname"));
							tmpMap.put("rphone", tmp.getString("rphone"));
							tmpMap.put("raddress", tmp.getString("raddress"));
							Log.i("aid", tmp.getString("aid"));
							Log.i("raddress", tmp.getString("raddress"));
							tmpList.add(tmpMap);
							
						}
						Log.i("tmplist size",String.valueOf( tmpList.size()));
						adater.addItems(tmpList);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 2:
					String res=(String)msg.obj;
					if (res.equals("success")) {
						Toast.makeText(ManageAddress.this, "删除成功", Toast.LENGTH_SHORT).show();
						new Thread(getAddr).start();
					}else {
						Toast.makeText(ManageAddress.this, "删除失败", Toast.LENGTH_SHORT).show();
					}
					
					break;
				}
			};
		};
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case 1:
			new Thread(getAddr).start();
			break;

		default:
			break;
		}
	}
	private void submitJson(){
		String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/getAddr";
		HttpPost request = new HttpPost(urlStr);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		Log.i("loginthread loginserver email", email);
		params.add(new BasicNameValuePair("cid",sp.getString("cid", "") ));
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
				
				Message message=handler.obtainMessage(1,1,1,responseMsg);
				handler.sendMessage(message);
			}else {
				Log.i("login", response.getStatusLine().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}
	Runnable getAddr=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			submitJson();
		}
	};
	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		final String aid=(String)v.getTag();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/delAddr";
				HttpPost request = new HttpPost(urlStr);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("aid",aid ));
				try {
					// 设置请求参数项
					request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpClient client = getHttpClient();
					// 执行请求返回相应
					HttpResponse response = client.execute(request);

					// 判断是否请求成功
					if (response.getStatusLine().getStatusCode() == 200) {
						String responseMsg = EntityUtils.toString(response.getEntity());
						Log.i("mycallback run", responseMsg);
						
						Message message=handler.obtainMessage(2,1,1,responseMsg);
						handler.sendMessage(message);
					}else {
						Log.i("login", response.getStatusLine().toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
