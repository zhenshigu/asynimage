package com.example.asynimg;

import android.R.color;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class Index extends TabActivity implements OnTabChangeListener {

	private TabHost tabHost;
	private int[] selected={R.drawable.takeout_ic_poi_selected,R.drawable.takeout_ic_order_selected,R.drawable.takeout_ic_user_selected};
	private int[] normal={R.drawable.takeout_ic_poi_normal,R.drawable.takeout_ic_order_normal,R.drawable.takeout_ic_user_normal};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitUtil.activityList.add(this);
		 tabHost=getTabHost();
//		tabHost.setBackgroundColor(Color.argb(150, 22, 70, 150));
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("餐厅",getResources().getDrawable(R.drawable.takeout_ic_poi_normal)).setContent(new Intent(this,Main.class)));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("订单",getResources().getDrawable(R.drawable.takeout_ic_order_normal)).setContent(new Intent(this,OrderManage.class)));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("我的",getResources().getDrawable(R.drawable.takeout_ic_user_normal)).setContent(new Intent(this,P1.class)));
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			View view = tabHost.getTabWidget().getChildAt(i); 
			view.setBackgroundDrawable(getResources().getDrawable(R.drawable.takeout_bg_poi_filter_layout));
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		 tabHost.setCurrentTabByTag(tabId); 
		 updateTab(tabHost); 
	}
	 private void updateTab(final TabHost tabHost) { 
	        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) { 
	            View view = tabHost.getTabWidget().getChildAt(i); 
	            if (tabHost.getCurrentTab() == i) {//选中  
	                view.setBackgroundResource(selected[i]);//选中后的背景  
	            } else {//不选中  
	            	 view.setBackgroundResource(normal[i]);//非选择的背景  
	            } 
	        } 
	    } 
	
}
