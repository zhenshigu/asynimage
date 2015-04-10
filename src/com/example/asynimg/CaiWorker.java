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

public class CaiWorker extends AsyncTask<String, Void, String>{

	private CaishiAdater adapter;
	public  CaiWorker(CaishiAdater baseAdapter) {
		this.adapter=baseAdapter;
	}
	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		String caiurl=arg0[0];
		String rid=arg0[1];
		String start=arg0[2];
		String num=arg0[3];
		try {
			String jsonOfResturant=PicUtil.getCai(caiurl, start, num, rid);
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
//		super.onPostExecute(result);
		List<Map<String, String>> tmpList=new ArrayList<Map<String,String>>();
		
		try {
			JSONArray resJsonArray=new JSONArray(result);
			for (int i = 0; i < resJsonArray.length(); i++) {
				JSONObject tmp=resJsonArray.getJSONObject(i);
				Map<String, String> tmpMap=new HashMap<String, String>();
				tmpMap.put("vid", tmp.getString("vid"));
				tmpMap.put("name", tmp.getString("name"));
				Log.i("caiworker onpostexecute", tmp.getString("name"));
				tmpMap.put("price", tmp.getString("price"));
				tmpMap.put("descrition", tmp.getString("descrition"));
				tmpMap.put("imageurl", tmp.getString("imageurl"));
				tmpMap.put("rid", tmp.getString("rid"));
				tmpList.add(tmpMap);
			}
			adapter.addItems(tmpList);
			Log.i("caiworker onpostexecute", String.valueOf(tmpList.size()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
