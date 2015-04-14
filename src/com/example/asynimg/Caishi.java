package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;

import com.example.asynimg.CaishiAdater.Callback;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Caishi extends Activity implements Callback{
//======20150409==================
	 private PullToRefreshListView mPullToRefreshListView;
	 Map<String, SoftReference<Bitmap>>  caches;
	 private String rid;
//==============================================
//=====20150414=====================
	 private SharedPreferences shopcart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitUtil.activityList.add(this);
		setContentView(R.layout.activity_caishi);
		 shopcart=getSharedPreferences("mycart",0);
		//===========20150409===================
		final String url="http://10.0.2.2:8080/DingCan/index.php/server/showResturant/getCaibyRes";
		caches=new HashMap<String, SoftReference<Bitmap>>();
		mPullToRefreshListView=(PullToRefreshListView)findViewById(R.id.refresh_caishi);
		 ListView listView1 = mPullToRefreshListView.getRefreshableView();
	      mPullToRefreshListView.setMode(Mode.BOTH);
	      final CaishiAdater caishiAdater=new CaishiAdater(this, new ArrayList<Map<String,String>>(), caches,mPullToRefreshListView,this);
	      listView1.setAdapter(caishiAdater);
	       String  current=String.valueOf(caishiAdater.getCount());
	       rid=getIntent().getExtras().getString("rid");
	       final String num="2";
	       new CaiWorker(caishiAdater).execute(url,rid,current,num);
	       //上拉下拉更新餐厅列表测试
	        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
	        	  @Override
	        	  public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
	        		 String  current=String.valueOf(caishiAdater.getCount());
	        		 new CaiWorker(caishiAdater).execute(url,rid,current,num);
	        	  }
	        	  @Override
	        	  public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
	        		 String  current=String.valueOf(caishiAdater.getCount());
	        		 new CaiWorker(caishiAdater).execute(url,rid,current,num);
	        	  }
	        	});
	      //==========================================
	       Button buycart=(Button)findViewById(R.id.buycar);
	       TextView carinfo=(TextView)findViewById(R.id.carinfo);
	       Button btnok=(Button)findViewById(R.id.btnok);
	       final JSONArray dingdan=new JSONArray();
	        btnok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Map mylist=shopcart.getAll();
//					 Log.i("caishi size",String.valueOf(mylist.size()));
					Iterator iterator=mylist.entrySet().iterator();
					while(iterator.hasNext()){
						Map.Entry entry = (Map.Entry) iterator.next();  
					    Object key = entry.getKey();  
					    Object val = entry.getValue(); 
//					    Log.i("caishi buy", val.toString());
						dingdan.put(val);
					}
					 Log.i("caishi buy", dingdan.toString());
					shopcart.edit().clear().commit();
				}
			});
	       
	}
	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
			TextView carinfo=(TextView)findViewById(R.id.carinfo);
			carinfo.setText(String.valueOf(v.getTag()));
	}
}
	
