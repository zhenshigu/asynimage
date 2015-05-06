package com.example.asynimg;

import java.util.List;
import java.util.Map;

import com.example.asynimg.AddressAdater.myCallback;
import com.example.asynimg.AddressAdater.viewHolder;

import android.content.Context;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class OrderAdater extends BaseAdapter{
	private Context context;
	private List<Map<String, String>> orderList;
	private LayoutInflater mInflater;
	private OrderCallback mCallback;
	public  OrderAdater(Context context,List<Map<String, String>> addList,OrderCallback myCallback)
	{
		this.context=context;
		this.orderList=addList;
		mCallback=myCallback;
		mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return orderList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int pos=position;
		final viewHolder holder;
		if (convertView==null) {
			Log.i("addressadater getview", "convertview null");
			convertView=mInflater.inflate(R.layout.orderitem, null);
			holder=new viewHolder();
			holder.xdate=(TextView)convertView.findViewById(R.id.xdate);
			holder.rname=(TextView)convertView.findViewById(R.id.rname);
			holder.status=(TextView)convertView.findViewById(R.id.status);
			holder.sum=(TextView)convertView.findViewById(R.id.sum);
			holder.button=(Button)convertView.findViewById(R.id.button1);
			convertView.setTag(holder);
		}else {
			holder=(viewHolder)convertView.getTag();
		}
		String rname=orderList.get(position).get("rname");
		String xdate=orderList.get(position).get("xdate");
		String status=orderList.get(position).get("status");
		String sum=orderList.get(position).get("sum");
		holder.xdate.setText("下单时间:"+xdate);
		holder.rname.getPaint().setFakeBoldText(true);
		holder.rname.setText(rname);
		String tmpString;
		if (Integer.valueOf(status)==0) {
			tmpString="订单未完成";
		}else if (Integer.valueOf(status)==1) {
			tmpString="订单完成";
		}else {
			tmpString="订单取消";
		}
		TextPaint tp=holder.status.getPaint();
		tp.setFakeBoldText(true);
		holder.status.setText(tmpString);
		holder.sum.setText("￥"+sum);
		holder.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				arg0.setTag(orderList.get(pos));
				mCallback.click(arg0);
			}
		});
		return convertView;
	}
	class viewHolder{
		TextView xdate;
		TextView status;
		TextView sum;
		TextView rname;
		Button button;
	}
	public void addItems(List<Map<String, String>> rList) {
		orderList.clear();
		orderList.addAll(rList);
		notifyDataSetChanged();
	}
	//回调接口，用于activity响应listview item button点击事件
	public interface OrderCallback {
		        public void click(View v);
	}
}
