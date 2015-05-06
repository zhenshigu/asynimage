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

import android.R.array;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ConfirmOrder extends Activity {
private Intent intent;
private Handler handler;
private JSONObject iteminf;
private SharedPreferences sp;
private int total;
private String str;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_order);
		initView();
	}
	private void initView(){
		ListView listView1=(ListView)findViewById(R.id.listView1);
		TextView totalTextView=(TextView)findViewById(R.id.total);
		final Spinner spinner=(Spinner)findViewById(R.id.spinner1);
		final Spinner spinner2=(Spinner)findViewById(R.id.spinner2);
		String[] typeStrings={"货到付款"};
		ArrayAdapter< String> paytype=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,typeStrings);
		spinner2.setAdapter(paytype);
		int color=Color.argb(50, 157, 210, 247);
		spinner.setBackgroundColor(color);
		spinner2.setBackgroundColor(color);
//		spinner.setBackgroundResource(R.drawable.spin);
//		spinner2.setBackgroundResource(R.drawable.spin);
		Button confirm=(Button)findViewById(R.id.confirm);
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new Thread(confirmOrder).start();
			}
		});
		sp=getSharedPreferences("userinfo", 0);
		//显示收获地址
		new Thread(getAddr).start();
		handler=new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					List<String> tmpList=new ArrayList<String>();
					String responseMsg=(String)msg.obj;
					JSONArray addressArray;
					ArrayAdapter<String> adapter;
					try {
						addressArray = new JSONArray(responseMsg);
						for(int i=0;i<addressArray.length();i++){
							JSONObject tmp=addressArray.getJSONObject(i);
							String myaddr="收货人:"+tmp.getString("rname")+";收货电话:"+tmp.getString("rphone")+"收货地址:"+tmp.getString("raddress");
							tmpList.add(myaddr);
						}
						Log.i("tmplist size",String.valueOf( tmpList.size()));
						adapter=new ArrayAdapter<String>(ConfirmOrder.this, android.R.layout.simple_spinner_item,tmpList);
						spinner.setAdapter(adapter);
						spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent,
									View arg1, int position, long arg3) {
								// TODO Auto-generated method stub
								 str=parent.getItemAtPosition(position).toString();
						        Toast.makeText(ConfirmOrder.this,str,Toast.LENGTH_LONG).show();
							}

							@Override
							public void onNothingSelected(AdapterView<?> parent) {
								// TODO Auto-generated method stub
								
							}
						});
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 3:
					startActivity(new Intent(ConfirmOrder.this, OrderManage.class));
				}
			};
		};
		//显示订单信息
		intent=getIntent();
		String json=intent.getStringExtra("json");
		JSONArray carinfoArray;
		List<Map<String, String>> tmpList=new ArrayList<Map<String,String>>();
		try {
			carinfoArray = new JSONArray(json);
			
			 total=0;
			for(int i=0;i<carinfoArray.length();i++){
				String aa=carinfoArray.getString(i);
				iteminf=new JSONObject(aa);
				Log.i("jsonobject", iteminf.getString("count"));
				Map<String, String> tmpMap=new HashMap<String, String>();
				tmpMap.put("name", iteminf.getString("name"));
				tmpMap.put("count", iteminf.getString("count"));
				tmpMap.put("price", iteminf.getString("price"));
				tmpList.add(tmpMap);
				total=total+Integer.valueOf(iteminf.getString("count"))*Integer.valueOf(iteminf.getString("price"));
			}
			String[] from={"name","count","price"};
			int[] to={R.id.textView1,R.id.textView2,R.id.textView3};
			SimpleAdapter adater=new SimpleAdapter(ConfirmOrder.this, tmpList, R.layout.orderdetail, from, to);
			listView1.setAdapter(adater);
			totalTextView.setText("合计:￥"+total);
//			Log.i("tmplist size",String.valueOf( tmpList.size()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//获得订餐地址
	Runnable getAddr=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/getAddr";
			HttpPost request = new HttpPost(urlStr);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			Log.i("loginthread loginserver email", email);
			params.add(new BasicNameValuePair("cid",sp.getString("cid", "") ));
			try {
				// 设置请求参数项
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpClient client = PicUtil.getHttpClient();
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
	};
	//提交订单
Runnable confirmOrder=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String urlStr = "http://10.0.2.2:8080/DingCan/index.php/server/dingdanManage/addDingdan";
			HttpPost request = new HttpPost(urlStr);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			Log.i("loginthread loginserver email", email);
			JSONObject tmpJsonObject;
			String dingdan="";
			String vd="";
			try {
				tmpJsonObject=new JSONObject();
				tmpJsonObject.put("cid", sp.getString("cid", ""));
				tmpJsonObject.put("rid", getIntent().getStringExtra("rid"));
				tmpJsonObject.put("sum", String.valueOf(total));
				tmpJsonObject.put("destination", str);
				dingdan=tmpJsonObject.toString();
				Log.i("confirmorder dingdan", dingdan);
				tmpJsonObject=null;
				vd=getIntent().getStringExtra("json");
				Log.i("confirmorder vd", vd);
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace(); 
			}
			
			params.add(new BasicNameValuePair("dingdan", dingdan));
			params.add(new BasicNameValuePair("vd",vd));
			try {
				// 设置请求参数项
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpClient client = PicUtil.getHttpClient();
				// 执行请求返回相应
				HttpResponse response = client.execute(request);

				// 判断是否请求成功
				if (response.getStatusLine().getStatusCode() == 200) {
					String responseMsg = EntityUtils.toString(response.getEntity());
					Log.i("manageaddress run", responseMsg);
					
					Message message=handler.obtainMessage(3,1,1,responseMsg);
					handler.sendMessage(message);
				}else {
					Log.i("login", response.getStatusLine().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
