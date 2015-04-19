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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.asynimg.OrderAdater.OrderCallback;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class OrderManage extends Activity implements OrderCallback{
	private ListView orderListView;
	private SharedPreferences spPreferences;
	private RelativeLayout noorderLayout;
	private LinearLayout hasorderLayout;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_manage);
		initView();
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		boolean isLogin=spPreferences.getBoolean("isLogin", false);
		Log.i("ordermanage islogin", String.valueOf(isLogin));
		if (isLogin) {
			new Thread(addRunnable).start();
		}
	}
	private void initView() {
		 orderListView=(ListView)findViewById(R.id.listView1);
		 spPreferences=getSharedPreferences("userinfo", 0);
		 noorderLayout=(RelativeLayout)findViewById(R.id.noorder);
		 hasorderLayout=(LinearLayout)findViewById(R.id.hasorder);
		final OrderAdater  adater=new OrderAdater(this, new ArrayList<Map<String,String>>(), this);
		 orderListView.setAdapter(adater);
		 handler=new Handler(){
			 public void handleMessage(Message msg) {
				 switch (msg.what) {
				case 1:
					List<Map<String, String>> tmpList=new ArrayList<Map<String,String>>();
					
					try {
						JSONArray resJsonArray=new JSONArray((String)msg.obj);
						if (resJsonArray.length()==0) {
							return;
						}
						for (int i = 0; i < resJsonArray.length(); i++) {
							JSONObject tmp=resJsonArray.getJSONObject(i);
							Map<String, String> tmpMap=new HashMap<String, String>();
							tmpMap.put("lid", tmp.getString("lid"));
							tmpMap.put("xdate", tmp.getString("xdate"));
							tmpMap.put("status", tmp.getString("status"));
							tmpMap.put("tdate", tmp.getString("tdate"));
							tmpMap.put("destination", tmp.getString("destination"));
							tmpMap.put("sum", tmp.getString("sum"));
							tmpMap.put("rid", tmp.getString("rid"));
							tmpMap.put("rname", tmp.getString("rname"));
							tmpMap.put("telephone", tmp.getString("telephone"));
							tmpList.add(tmpMap);
						}
						noorderLayout.setVisibility(View.GONE);
						hasorderLayout.setVisibility(View.VISIBLE);
						adater.addItems(tmpList);
						Log.i("ordermanage handlemessage", String.valueOf(tmpList.size()));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				default:
					break;
				}
			 };
		 };
	}
Runnable addRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/getDingdan";
			HttpPost request = new HttpPost(urlStr);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("cid",spPreferences.getString("cid", "") ));
			try {
				// 设置请求参数项
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpClient client = PicUtil.getHttpClient();
				// 执行请求返回相应
				HttpResponse response = client.execute(request);

				// 判断是否请求成功
				if (response.getStatusLine().getStatusCode() == 200) {
					String responseMsg = EntityUtils.toString(response.getEntity());
					Log.i("orderManage getDingdan", responseMsg);
						Message message=handler.obtainMessage(1,1,1,responseMsg);
						handler.sendMessage(message);
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
@Override
public void click(View v) {
	// TODO Auto-generated method stub
	Map<String, String> tmpMap=(Map<String, String>) v.getTag();
	Bundle bundle=new Bundle();
	bundle.putString("lid", tmpMap.get("lid"));
	bundle.putString("xdate", tmpMap.get("xdate"));
	bundle.putString("status", tmpMap.get("status"));
	bundle.putString("tdate", tmpMap.get("tdate"));
	bundle.putString("destination", tmpMap.get("destination"));
	bundle.putString("sum", tmpMap.get("sum"));
	bundle.putString("rid", tmpMap.get("rid"));
	bundle.putString("rname", tmpMap.get("rname"));
	bundle.putString("telephone", tmpMap.get("telephone"));
	Intent intent=new Intent();
	intent.setClass(OrderManage.this, OrderDetail.class);
	intent.putExtras(bundle);
	startActivity(intent);
}
}
