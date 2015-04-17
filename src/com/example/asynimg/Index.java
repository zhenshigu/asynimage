package com.example.asynimg;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class Index extends TabActivity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitUtil.activityList.add(this);
		final TabHost tabHost=getTabHost();
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("餐厅").setContent(new Intent(this,MainActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("订单").setContent(new Intent(this,OrderManage.class)));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("我的").setContent(new Intent(this,P1.class)));
	}
}
