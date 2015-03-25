package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;

import android.R.integer;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>{

	private final WeakReference<ImageView> imageViewReference;
	public  Map<String, SoftReference<Bitmap>> caches;
	private String url="";
	public BitmapWorkerTask(ImageView imageView,Map<String, SoftReference<Bitmap>>caches) {
		// TODO Auto-generated constructor stub
		imageViewReference=new WeakReference<ImageView>(imageView);
		this.caches=caches;
	}
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView!=null) {
			final Drawable drawable=imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable=(AsyncDrawable)drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}
	@Override
	protected Bitmap doInBackground(String... urls) {
		// TODO Auto-generated method stub
		 url=urls[0];
		Bitmap bitmap=PicUtil.getbitmap(url);
		return bitmap;
	}
	@Override
	protected void onPostExecute(Bitmap	 bitmap) {
		// TODO Auto-generated method stub
	//	super.onPostExecute(result);
		caches.put(url, new SoftReference<Bitmap>(bitmap));
		PicUtil.save2file(url, bitmap);
		Log.i("bitmapworkerTask caches","caches has bitmaps:"+caches.size());
		if (isCancelled()) {
			//bitmap=null;
		}
		if (imageViewReference!=null&&bitmap!=null) {
			final ImageView imageView=imageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask=getBitmapWorkerTask(imageView);
			if (this==bitmapWorkerTask && imageView!=null) {
				imageView.setImageBitmap(bitmap);
				
			}
		}
	}
	public static boolean cancelPotentialWork(String url,ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask=getBitmapWorkerTask(imageView);
		if (bitmapWorkerTask!=null) {
			final String bitmapUrl=bitmapWorkerTask.url;
			if (bitmapUrl!=url) {
				bitmapWorkerTask.cancel(true);
			}else {
				return false;
			}
		}
		return true;
	}
	static class AsyncDrawable extends BitmapDrawable{
		private final WeakReference< BitmapWorkerTask> bitmapWorkeReference;
		public  AsyncDrawable(Resources res,Bitmap bitmap,BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkeReference=new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		
		}
		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkeReference.get();
		}
	}
	
}
