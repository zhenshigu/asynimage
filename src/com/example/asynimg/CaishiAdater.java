package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.PrivateCredentialPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.asynimg.BitmapWorkerTask.AsyncDrawable;
import com.example.asynimg.UpdateAdater2.viewholder;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	private Callback mCallback;
	public  CaishiAdater(Context context,List<Map<String, String>> caishi,Map<String, SoftReference<Bitmap>> caches,PullToRefreshListView mListView,Callback callback) {
		this.context=context;
		this.caishiList=caishi;
		this.caches=caches;
		mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mListView=mListView;
		this.mCallback=callback;
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
		final int pos=position;
		final viewholder holder;
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.caishi, null);
			holder=new viewholder();
			holder.imageView=(ImageView)convertView.findViewById(R.id.caiimg);
			holder.name=(TextView)convertView.findViewById(R.id.cname);
			holder.price=(TextView)convertView.findViewById(R.id.cprice);
			holder.description=(TextView)convertView.findViewById(R.id.description);
			holder.button=(Button)convertView.findViewById(R.id.cbuy);
			holder.delButton=(Button)convertView.findViewById(R.id.cdel);
			holder.count=(TextView)convertView.findViewById(R.id.count);
			convertView.setTag(holder);
		}else {
			holder=(viewholder)convertView.getTag();
		}
		holder.name.getPaint().setFakeBoldText(true);
		holder.name.setText("["+caishiList.get(position).get("name")+"]");
		holder.price.setText("  ￥"+caishiList.get(position).get("price"));
		holder.description.setText(caishiList.get(position).get("descrition"));
		holder.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.i("caishiadater",holder.count.getText().toString());
				String tmp=holder.count.getText().toString();
				int count=Integer.valueOf(tmp).intValue();
				count++;
				Log.i("caishiadater count",String.valueOf(count));
				holder.count.setText(String.valueOf(count));
				holder.count.setVisibility(View.VISIBLE);
				holder.delButton.setVisibility(View.VISIBLE);
				SharedPreferences sp=context.getSharedPreferences("mycart", 0);
				Editor editor=sp.edit();
				editor.remove(caishiList.get(pos).get("name")).commit();
				JSONObject item=new JSONObject();
				try {
					item.put("name", caishiList.get(pos).get("name"));
					item.put("vid", caishiList.get(pos).get("vid"));
					item.put("price", caishiList.get(pos).get("price"));
					item.put("count", count);
					editor.putString(caishiList.get(pos).get("name"), item.toString());
					editor.commit();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i("caishiadater add", sp.getString(caishiList.get(pos).get("name"), "null"));
				Map mylist=sp.getAll();
				Iterator iterator=mylist.entrySet().iterator();
			    int sum=0;
				while(iterator.hasNext()){
					Map.Entry entry = (Map.Entry) iterator.next();  
				    Object key = entry.getKey();  
				    Object val = entry.getValue(); 
				    try {
						JSONObject item2=new JSONObject(val.toString());
						Log.i("caishiadater buttonclick  price", String.valueOf(item2.getInt("price")));
						Log.i("caishiadater buttonclick  count", String.valueOf(item2.getInt("count")));
						sum=sum+item2.getInt("price")*item2.getInt("count");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
				}
				Log.i("caishiadater buttonclick  sum", String.valueOf(sum));
				arg0.setTag(sum);
				mCallback.click(arg0);
			}
		});
		holder.delButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String tmp=holder.count.getText().toString();
				int count=Integer.valueOf(tmp).intValue();
				count--;
				holder.count.setText(String.valueOf(count));
				if (count==0) {
					holder.count.setVisibility(View.GONE);
					holder.delButton.setVisibility(View.GONE);
				}
				SharedPreferences sp=context.getSharedPreferences("mycart", 0);
				Editor editor=sp.edit();
				editor.remove(caishiList.get(pos).get("name"));
					JSONObject item=new JSONObject();
					try {
						item.put("name", caishiList.get(pos).get("name"));
						item.put("vid", caishiList.get(pos).get("vid"));
						item.put("price", caishiList.get(pos).get("price"));
						item.put("count", count);
						editor.putString(caishiList.get(pos).get("name"), item.toString());
						editor.commit();
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				Map mylist=sp.getAll();
				Iterator iterator=mylist.entrySet().iterator();
			    int sum=0;
				while(iterator.hasNext()){
					Map.Entry entry = (Map.Entry) iterator.next();  
				    Object val = entry.getValue(); 
				    try {
						JSONObject item2=new JSONObject(val.toString());
						Log.i("caishiadater buttonclick  price", String.valueOf(item2.getInt("price")));
						Log.i("caishiadater buttonclick  count", String.valueOf(item2.getInt("count")));
						sum=sum+item2.getInt("price")*item2.getInt("count");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    
				}
				arg0.setTag(sum);
				mCallback.click(arg0);
			}
		});
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
		Button delButton;
		TextView count;
	}
	public void addItems(List<Map<String, String>> rList) {
		caishiList.addAll(rList);
		notifyDataSetChanged();
		mListView.onRefreshComplete();
	}
	//回调接口，用于activity响应listview item button点击事件
	public interface Callback {
		        public void click(View v);
	}
}
