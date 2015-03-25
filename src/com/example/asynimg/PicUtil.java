package com.example.asynimg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
