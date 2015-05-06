package com.example.asynimg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class ResturantWorker extends AsyncTask<String, Void, String>{
	private UpdateAdater2 adapter;
	public  ResturantWorker(UpdateAdater2 baseAdapter) {
		this.adapter=baseAdapter;
	}
	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		String resurl=arg0[0];
		String start=arg0[1];
		String num=arg0[2];
		//=====20150506修改增加地址选择=============
		String shenString=arg0[3];
		String shiString=arg0[4];
		String xianString=arg0[5];
		Log.i("resturantworker shen", shenString);
		Log.i("resturantworker shi", shiString);
		Log.i("resturantworker xian", xianString);
		//=============================================
		try {
			//=====20150506修改增加地址选择=============
			String jsonOfResturant=PicUtil.getResturant(resurl, start, num,shenString,shiString,xianString);
			//============================================
			return jsonOfResturant;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
//				super.onPostExecute(result);
				List<Map<String, String>> tmpList=new ArrayList<Map<String,String>>();
				
				try {
					JSONArray resJsonArray=new JSONArray(result);
					for (int i = 0; i < resJsonArray.length(); i++) {
						JSONObject tmp=resJsonArray.getJSONObject(i);
						Map<String, String> tmpMap=new HashMap<String, String>();
						//=======20140409 add the rid field===================
						tmpMap.put("rid", tmp.getString("rid"));
						//=================================================
						tmpMap.put("name", tmp.getString("name"));
						Log.i("resturantworker onpostexecute", tmp.getString("name"));
						tmpMap.put("phone", tmp.getString("phone"));
						tmpMap.put("image", tmp.getString("image"));
						tmpMap.put("shen", tmp.getString("shen"));
						tmpMap.put("shi", tmp.getString("shi"));
						tmpMap.put("xian", tmp.getString("xian"));
						tmpList.add(tmpMap);
					}
					adapter.addItems(tmpList);
					Log.i("resturantworker onpostexecute", String.valueOf(tmpList.size()));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
}
