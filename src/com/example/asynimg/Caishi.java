package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class Caishi extends Activity {
//======20150409==================
	 private PullToRefreshListView mPullToRefreshListView;
	 Map<String, SoftReference<Bitmap>>  caches;
	 private String rid;
//==============================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitUtil.activityList.add(this);
		setContentView(R.layout.activity_caishi);
		//===========20150409===================
		final String url="http://10.0.2.2:8080/DingCan/index.php/server/showResturant/getCaibyRes";
		caches=new HashMap<String, SoftReference<Bitmap>>();
		mPullToRefreshListView=(PullToRefreshListView)findViewById(R.id.refresh_caishi);
		 ListView listView1 = mPullToRefreshListView.getRefreshableView();
	      mPullToRefreshListView.setMode(Mode.BOTH);
	      final CaishiAdater caishiAdater=new CaishiAdater(this, new ArrayList<Map<String,String>>(), caches,mPullToRefreshListView);
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
	    }
		//==========================================
	}
	
