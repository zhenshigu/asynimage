package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;

import com.example.asynimg.BitmapWorkerTask.AsyncDrawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class UpdateAdater extends BaseAdapter{
	private Context context;
	private List<String> bitmaps;
	private Map<String, SoftReference<Bitmap>> caches;
	private LayoutInflater mInflater;
	public  UpdateAdater(Context context,List<String> bitmaps,Map<String, SoftReference<Bitmap>> caches) {
		this.context=context;
		this.bitmaps=bitmaps;
		this.caches=caches;
		mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public int getCount() {
		return bitmaps.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return bitmaps.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		viewholder holder;
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.iv, null);
			holder=new viewholder();
			holder.imageView=(ImageView)convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}else {
			holder=(viewholder)convertView.getTag();
		}
		//holder.imageView.setImageDrawable(bitmaps.get(position));
//		String url="http://10.0.2.2:8080/img/2.jpg";
		loadBitmap(bitmaps.get(position), holder.imageView);
		return convertView;
	}
	 public void loadBitmap(String url,ImageView imageView) {
		 if (caches.containsKey(url)) {
			
			SoftReference<Bitmap> rf=caches.get(url);
			Bitmap bitmap=rf.get();
			if (bitmap==null) {
				caches.remove(url);
			}else {
				Log.i("loadBitmap", url+"in caches");
				imageView.setImageBitmap(bitmap);
				return;
			}
		}
		 if ((PicUtil.getFromFile(url))!=null) {
			imageView.setImageURI(PicUtil.getFromFile(url));
			Log.i("updateadater loadbitmap",url+" exsits in file");
			return;
		}
			if (BitmapWorkerTask.cancelPotentialWork(url, imageView)) {
				final BitmapWorkerTask task=new BitmapWorkerTask(imageView,caches);
				Bitmap defaultBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
				final AsyncDrawable asyncDrawable=new AsyncDrawable(context.getResources(), defaultBitmap, task);
				imageView.setImageDrawable(asyncDrawable);
				task.execute(url);
			}
		}
	class viewholder{
		ImageView imageView;
	}
	public void addItems(List<String> urls) {
		bitmaps.addAll(urls);
		notifyDataSetChanged();
	}
}
