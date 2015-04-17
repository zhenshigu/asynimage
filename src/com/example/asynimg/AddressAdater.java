package com.example.asynimg;

import java.util.List;
import java.util.Map;

import com.example.asynimg.CaishiAdater.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AddressAdater extends BaseAdapter{
	private Context context;
	private List<Map<String, String>> addrList;
	private LayoutInflater mInflater;
	private myCallback mCallback;
	public  AddressAdater(Context context,List<Map<String, String>> addList,myCallback myCallback)
	{
		this.context=context;
		this.addrList=addList;
		mCallback=myCallback;
		mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return addrList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return addrList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final viewHolder holder;
		if (convertView==null) {
			Log.i("addressadater getview", "convertview null");
			convertView=mInflater.inflate(R.layout.address, null);
			holder=new viewHolder();
			holder.textView=(TextView)convertView.findViewById(R.id.textView1);
			holder.button=(Button)convertView.findViewById(R.id.button1);
			convertView.setTag(holder);
		}else {
			holder=(viewHolder)convertView.getTag();
		}
		String rname=addrList.get(position).get("rname");
		String rphone=addrList.get(position).get("rphone");
		String raddress=addrList.get(position).get("raddress");
		holder.textView.setText("收货人:"+rname+" 收货地址:"+raddress+"电话:"+rphone);
		holder.aid=addrList.get(position).get("aid");
		holder.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				arg0.setTag(holder.aid);
				mCallback.click(arg0);
			}
		});
		return convertView;
	}
	class viewHolder{
		TextView textView;
		Button button;
		String aid;
	}
	public void addItems(List<Map<String, String>> rList) {
		addrList.clear();
		addrList.addAll(rList);
		notifyDataSetChanged();
	}
	//回调接口，用于activity响应listview item button点击事件
	public interface myCallback {
		        public void click(View v);
	}
}
