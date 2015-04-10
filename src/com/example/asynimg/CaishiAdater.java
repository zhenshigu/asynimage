package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;

import com.example.asynimg.BitmapWorkerTask.AsyncDrawable;
import com.example.asynimg.UpdateAdater2.viewholder;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CaishiAdater extends BaseAdapter{
	private Context context;
	private List<Map<String, String>> caishiList;
	private Map<String, SoftReference<Bitmap>> caches;
	private LayoutInflater mInflater;
	private PullToRefreshListView mListView;
	public  CaishiAdater(Context context,List<Map<String, String>> caishi,Map<String, SoftReference<Bitmap>> caches,PullToRefreshListView mListView) {
		this.context=context;
		this.caishiList=caishi;
		this.caches=caches;
		mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mListView=mListView;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return caishiList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return caishiList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View  convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		viewholder holder;
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.caishi, null);
			holder=new viewholder();
			holder.imageView=(ImageView)convertView.findViewById(R.id.caiimg);
			holder.name=(TextView)convertView.findViewById(R.id.cname);
			holder.price=(TextView)convertView.findViewById(R.id.cprice);
			holder.description=(TextView)convertView.findViewById(R.id.description);
			holder.button=(Button)convertView.findViewById(R.id.cbuy);
			convertView.setTag(holder);
		}else {
			holder=(viewholder)convertView.getTag();
		}
		holder.name.setText(caishiList.get(position).get("name"));
		holder.price.setText(caishiList.get(position).get("price"));
		holder.description.setText(caishiList.get(position).get("descrition"));
		Log.i("caishiadater-getview", caishiList.get(position).get("imageurl"));
		loadBitmap(caishiList.get(position).get("imageurl"), holder.imageView);
		return convertView;
	}
	public void loadBitmap(String url,ImageView imageView) {
		 if (caches.containsKey(url)) {
			
			SoftReference<Bitmap> rf=caches.get(url);
			Bitmap bitmap=rf.get();
			if (bitmap==null) {
				caches.remove(url);
			}else {
				Log.i("caishiadater loadBitmap", url+"in caches");
				imageView.setImageBitmap(bitmap);
				return;
			}
		}
		 if ((PicUtil.getFromFile(url))!=null) {
			imageView.setImageURI(PicUtil.getFromFile(url));
			Log.i("caishiadater loadbitmap",url+" exsits in file");
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
		TextView name;
		TextView price;
		TextView description;
		Button button;
	}
	public void addItems(List<Map<String, String>> rList) {
		caishiList.addAll(rList);
		notifyDataSetChanged();
		mListView.onRefreshComplete();
	}
}
