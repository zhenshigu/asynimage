package com.example.asynimg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class PicUtil {
	private static final String TAG="PicUtil";
	public static  BitmapDrawable getfriendicon(URL imageUri) {
		BitmapDrawable icon=null;
		try {
			HttpURLConnection hp=(HttpURLConnection)imageUri.openConnection();
			icon=new BitmapDrawable(hp.getInputStream());
			hp.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return icon;
	}
	public static String getResturant(String resurl,String start,String num) throws ClientProtocolException, IOException {
		HttpClient client=new DefaultHttpClient();
		HttpPost request=new HttpPost(resurl);
		List<NameValuePair> postParameters=new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("start", start));
		postParameters.add(new BasicNameValuePair("num", num));
		String result=null;
		try {
			UrlEncodedFormEntity formEntity=new UrlEncodedFormEntity(postParameters);
			request.setEntity(formEntity);
			HttpResponse response=client.execute(request);
			 result=EntityUtils.toString(response.getEntity());
			 Log.i("picutil getresturant", result);
//			 JSONArray resJsonArray=new JSONArray(result);
//			 for (int i = 0; i < resJsonArray.length(); i++) {
//					JSONObject tmp=resJsonArray.getJSONObject(i);
//					Map<String, String> tmpMap=new HashMap<String, String>();
//					tmpMap.put("name", tmp.getString("name"));
//					Log.i("resturantworker onpostexecute", tmp.getString("name"));
//					tmpMap.put("phone", tmp.getString("phone"));
//					tmpMap.put("image", tmp.getString("image"));
//					tmpMap.put("shen", tmp.getString("shen"));
//					tmpMap.put("shi", tmp.getString("shi"));
//					tmpMap.put("xian", tmp.getString("xian"));
//					tmpList.add(tmpMap);
//				}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
//=======20150409 add function of getting caishi==================
	public static String getCai(String caiurl,String start,String num,String rid) throws ClientProtocolException, IOException {
		HttpClient client=new DefaultHttpClient();
		HttpPost request=new HttpPost(caiurl);
		List<NameValuePair> postParameters=new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("rid", rid));
		postParameters.add(new BasicNameValuePair("start", start));
		postParameters.add(new BasicNameValuePair("num", num));
		String result=null;
		try {
			UrlEncodedFormEntity formEntity=new UrlEncodedFormEntity(postParameters);
			request.setEntity(formEntity);
			HttpResponse response=client.execute(request);
			 result=EntityUtils.toString(response.getEntity());
			 Log.i("picutil getresturant", result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
	//============================================================
	public static Bitmap getbitmap(String imageUri) {
		Bitmap bitmap=null;
		try {
			URL myFileUrl=new URL(imageUri);
			HttpURLConnection conn=(HttpURLConnection)myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is=conn.getInputStream();
			bitmap=BitmapFactory.decodeStream(is);
			is.close();
			Log.i(TAG, "image download finished"+imageUri);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	public static void save2file(String url,Bitmap bitmap) {
		String name=getMD5(url)+url.substring(url.lastIndexOf("."));
		Log.i("picutil-save2file", name);
	    boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {             
            File dir = Environment.getExternalStorageDirectory();           
            String path=dir.getPath()+"/";
            File target= new File(path+ "dingcan");  
            if (!target.exists()) {
            	target.mkdir();
			}
        	Log.i("picutil-save2file","target "+ target.exists());
        	FileOutputStream fos=null;
    		try {
    			fos=new FileOutputStream("/sdcard/dingcan/"+name);
    			if (fos!=null) {
					bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
					fos.close();
				}
    		} catch (Exception e) {
    			// TODO: handle exception
    			e.printStackTrace();
    		}
        }
	}
	public static Uri getFromFile(String url) {
		String name=getMD5(url)+url.substring(url.lastIndexOf("."));
		File file=new File("/sdcard/dingcan/"+name);
		if (file.exists()) {
			return Uri.fromFile(file);
		}
		return null;
	}
	 public static String getMD5(String content) {
	        try {
	            MessageDigest digest = MessageDigest.getInstance("MD5");
	            digest.update(content.getBytes());
	            return getHashString(digest);
	            
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	 private static String getHashString(MessageDigest digest) {
	        StringBuilder builder = new StringBuilder();
	        for (byte b : digest.digest()) {
	            builder.append(Integer.toHexString((b >> 4) & 0xf));
	            builder.append(Integer.toHexString(b & 0xf));
	        }
	        return builder.toString();
	    }
}
