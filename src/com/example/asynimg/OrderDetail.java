package com.example.asynimg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.PrivateCredentialPermission;

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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetail extends Activity {
	private Bundle bundle;
	private Handler handler;
	private String mylid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		initView();
		new Thread(addRunnable).start();
	}
	private void initView() {
		
		TextView statusTextView=(TextView)findViewById(R.id.status);
		TextView cartTextView=(TextView)findViewById(R.id.cart);
		final TextView totalTextView=(TextView)findViewById(R.id.total);
		final TextView xdaTextView=(TextView)findViewById(R.id.xdate);
		final TextView phoneTextView=(TextView)findViewById(R.id.phone);
		final TextView destinationTextView=(TextView)findViewById(R.id.destination);
		final ListView listView1=(ListView)findViewById(R.id.listView1);
		final TextView lidTextView=(TextView)findViewById(R.id.lid);
		LinearLayout buttongruop=(LinearLayout)findViewById(R.id.buttongroup);
		final Button cancelButton=(Button)findViewById(R.id.cancel);
		final Button confirmButton=(Button)findViewById(R.id.confirm);
		bundle=getIntent().getExtras();
		mylid=bundle.getString("lid");
		if (Integer.valueOf(bundle.getString("status"))==0) {
			statusTextView.setText("下单成功,美食正在做中...");
			buttongruop.setVisibility(View.VISIBLE);
		}else if (Integer.valueOf(bundle.getString("status"))==2) {
			statusTextView.setText("订单已经取消");
		}
		else {
			statusTextView.setText("订单完成");
		}
		cartTextView.setText(bundle.getString("rname"));
		handler=new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					List<Map<String, String>> tmpList=new ArrayList<Map<String,String>>();
					try {
						JSONArray resJsonArray=new JSONArray((String)msg.obj);
						int total=0;
						for (int i = 0; i < resJsonArray.length(); i++) {
							JSONObject tmp=resJsonArray.getJSONObject(i);
							Map<String, String> tmpMap=new HashMap<String, String>();
							tmpMap.put("lid", tmp.getString("lid"));
							tmpMap.put("vname", tmp.getString("vname"));
							tmpMap.put("vid", tmp.getString("vid"));
							tmpMap.put("count", tmp.getString("count"));
							tmpMap.put("price", tmp.getString("price"));
							tmpMap.put("descrition", tmp.getString("descrition"));
							tmpMap.put("rid", tmp.getString("rid"));
							tmpList.add(tmpMap);
							total=total+Integer.valueOf(tmp.getString("count"))*Integer.valueOf(tmp.getString("price"));
						}
						String[] from={"vname","count","price"};
						int[] to={R.id.textView1,R.id.textView2,R.id.textView3};
						SimpleAdapter adater=new SimpleAdapter(OrderDetail.this, tmpList, R.layout.orderdetail, from, to);
						listView1.setAdapter(adater);
						totalTextView.setText("合计:￥"+total);
						destinationTextView.setText(destinationTextView.getText().toString()+bundle.getString("destination"));
						phoneTextView.setText(phoneTextView.getText().toString()+bundle.getString("telephone"));
						xdaTextView.setText(xdaTextView.getText().toString()+bundle.getString("xdate"));
						lidTextView.setText(lidTextView.getText().toString()+bundle.getString("lid"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 3:
					String result=(String) msg.obj;
					if (result.equals("success")) {
						Toast.makeText(OrderDetail.this, "订单取消成功", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(OrderDetail.this, OrderManage.class));
					}else {
						Toast.makeText(OrderDetail.this, "订单取消失败", Toast.LENGTH_SHORT).show();
					}
					break;
				case 5:
					String result2=(String) msg.obj;
					if (result2.equals("success")) {
						Toast.makeText(OrderDetail.this, "订单确认成功", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(OrderDetail.this, OrderManage.class));
					}else {
						Toast.makeText(OrderDetail.this, "订单确认失败", Toast.LENGTH_SHORT).show();
					}
					break;
				}
			};
		};
		//取消订单事件
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/cancelOrder";
						HttpPost request = new HttpPost(urlStr);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						String lidString=bundle.getString("lid");
						params.add(new BasicNameValuePair("lid",lidString));
						try {
							// 设置请求参数项
							request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
							HttpClient client = PicUtil.getHttpClient();
							// 执行请求返回相应
							HttpResponse response = client.execute(request);
							
							// 判断是否请求成功
							if (response.getStatusLine().getStatusCode() == 200) {
								String responseMsg = EntityUtils.toString(response.getEntity());
								Log.i("orderdetail cancelOrder", responseMsg);
									Message message=handler.obtainMessage(3,1,1,responseMsg);
									handler.sendMessage(message);
							}else {
								Message message=handler.obtainMessage(4);
								handler.sendMessage(message);
								Log.i("orderdetail ordercancel", response.getStatusLine().toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		//确定订单事件
		confirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/confirmOrder";
						HttpPost request = new HttpPost(urlStr);
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						String lidString=bundle.getString("lid");
						params.add(new BasicNameValuePair("lid",lidString));
						try {
							// 设置请求参数项
							request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
							HttpClient client = PicUtil.getHttpClient();
							// 执行请求返回相应
							HttpResponse response = client.execute(request);
							
							// 判断是否请求成功
							if (response.getStatusLine().getStatusCode() == 200) {
								String responseMsg = EntityUtils.toString(response.getEntity());
								Log.i("orderdetail cancelOrder", responseMsg);
									Message message=handler.obtainMessage(5,1,1,responseMsg);
									handler.sendMessage(message);
							}else {
								Message message=handler.obtainMessage(6);
								handler.sendMessage(message);
								Log.i("orderdetail ordercancel", response.getStatusLine().toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}
Runnable addRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/getVd";
			HttpPost request = new HttpPost(urlStr);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			String lidString=bundle.getString("lid");
			params.add(new BasicNameValuePair("lid",lidString));
			try {
				// 设置请求参数项
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpClient client = PicUtil.getHttpClient();
				// 执行请求返回相应
				HttpResponse response = client.execute(request);
				
				// 判断是否请求成功
				if (response.getStatusLine().getStatusCode() == 200) {
					String responseMsg = EntityUtils.toString(response.getEntity());
					Log.i("orderdetail run", responseMsg);
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
}
