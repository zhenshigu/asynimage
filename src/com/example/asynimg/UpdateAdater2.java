package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;

import com.example.asynimg.BitmapWorkerTask.AsyncDrawable;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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
import android.widget.ListView;
import android.widget.TextView;

public class UpdateAdater2 extends BaseAdapter{
	private Context context;
	private List<String> bitmaps;
	private List<Map<String, String>> resturantsList;
	private Map<String, SoftReference<Bitmap>> caches;
	private LayoutInflater mInflater;
	private PullToRefreshListView mListView;
	public  UpdateAdater2(Context context,List<Map<String, String>> resturants,Map<String, SoftReference<Bitmap>> caches,PullToRefreshListView mListView) {
		this.context=context;
	//	this.bitmaps=bitmaps;
		this.resturantsList=resturants;
		this.caches=caches;
		mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mListView=mListView;
	}
	public int getCount() {
//		return bitmaps.size();
		return resturantsList.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
//		return bitmaps.get(position);
		return resturantsList.get(position);
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
			convertView=mInflater.inflate(R.layout.reslist, null);
			holder=new viewholder();
			holder.imageView=(ImageView)convertView.findViewById(R.id.imageView1);
			holder.titleTextView=(TextView)convertView.findViewById(R.id.title);
			holder.addressTextView=(TextView)convertView.findViewById(R.id.address);
			convertView.setTag(holder);
		}else {
			holder=(viewholder)convertView.getTag();
		}
//		loadBitmap(bitmaps.get(position), holder.imageView);
		holder.titleTextView.setText(resturantsList.get(position).get("name"));
		holder.addressTextView.setText(resturantsList.get(position).get("shen")+resturantsList.get(position).get("shi")+resturantsList.get(position).get("xian"));
		loadBitmap(resturantsList.get(position).get("image"), holder.imageView);
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
		TextView titleTextView;
		TextView addressTextView;
	}
//	public void addItems(List<String> urls) {
//		bitmaps.addAll(urls);
//		notifyDataSetChanged();
//	}
	public void addItems(List<Map<String, String>> rList) {
		resturantsList.addAll(rList);
		notifyDataSetChanged();
		mListView.onRefreshComplete();
	}
}
