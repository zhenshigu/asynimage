package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.lifelink.cn.DBManager;
import cc.lifelink.cn.MyAdapter;
import cc.lifelink.cn.MyListItem;

import com.example.asynimg.BitmapWorkerTask.AsyncDrawable;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Main extends Activity {
	//===20150506显示地址====
	private DBManager dbm;
	private SQLiteDatabase db;
	private Spinner spinner1 = null;
	private Spinner spinner2=null;
	private Spinner spinner3=null;
	private String province="";
	private String city="";
	private String district="";
	private UpdateAdater2 updateAdater2;
	private ListView listView1;
	//======================
	private PullToRefreshListView mPullToRefreshListView;
	 Map<String, SoftReference<Bitmap>>  caches;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       ExitUtil.activityList.add(this);
       setContentView(R.layout.activity_main);
//       ImageView imageView1=(ImageView)findViewById(R.id.imageView1);
       final String url="http://10.0.2.2:8080/DingCan/index.php/server/showResturant/getResByPlace";
//       loadBitmap(url, imageView1);

       caches=new HashMap<String, SoftReference<Bitmap>>();
//       ListView listView1=(ListView)findViewById(R.id.listView1);
       mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);
        listView1 = mPullToRefreshListView.getRefreshableView();
       mPullToRefreshListView.setMode(Mode.BOTH);

       //获取餐厅列表方法1.0版本
    //   UpdateAdater updateAdater=new UpdateAdater(this, new ArrayList<String>(),caches);
//       new ResturantWorker(updateAdater).execute(url,"0","2");
//       listView1.setAdapter(updateAdater);
//       List<String> urlStrings=new ArrayList<String>();
//       urlStrings.add("http://10.0.2.2:8080/img/2.jpg");
//       urlStrings.add("http://10.0.2.2:8080/img/3.jpg");
//       urlStrings.add("http://10.0.2.2:8080/img/4.jpg");
//       urlStrings.add("http://10.0.2.2:8080/img/5.jpg");
//       urlStrings.add("http://10.0.2.2:8080/img/6.jpg");
//       urlStrings.add("http://10.0.2.2:8080/img/7.jpg");
//       urlStrings.add("http://10.0.2.2:8080/img/8.jpg");
//       updateAdater.addItems(urlStrings);
       
       //获取餐厅列表方法2.0测试
       updateAdater2=new UpdateAdater2(this, new ArrayList<Map<String,String>>(), caches,mPullToRefreshListView);
       listView1.setAdapter(updateAdater2);
       String  current=String.valueOf(updateAdater2.getCount());
       //============20150506修改-增加选择地点参数===============
       spinner1=(Spinner)findViewById(R.id.spinner1);
       spinner2=(Spinner)findViewById(R.id.spinner2);
       spinner3=(Spinner)findViewById(R.id.spinner3);
		spinner1.setPrompt("省");
		spinner2.setPrompt("城市");		
		spinner3.setPrompt("地区");
		
       initSpinner1();
       String theshenString="广东";
       String theshiString="普宁";
       String thexianString="流沙";
       new ResturantWorker(updateAdater2).execute(url,current,"1",province,city,district);
//       new ResturantWorker(updateAdater2).execute(url,current,"2");the origin code
       //======================================================
       //上拉下拉更新餐厅列表测试
       mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
       	  @Override
       	  public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
       		 String  current=String.valueOf(updateAdater2.getCount());
//              new ResturantWorker(updateAdater2).execute(url,current,"2");out-date
       		new ResturantWorker(updateAdater2).execute(url,current,"1",province,city,district);//20150510 new added
       	  }
       	  @Override
       	  public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
       		 String  current=String.valueOf(updateAdater2.getCount());
//              new ResturantWorker(updateAdater2).execute(url,current,"2");out-date
       		new ResturantWorker(updateAdater2).execute(url,current,"1",province,city,district);//20150510 new added
       	  }
       	});
   }
   public void loadBitmap(String url,ImageView imageView) {
		if (BitmapWorkerTask.cancelPotentialWork(url, imageView)) {
			final BitmapWorkerTask task=new BitmapWorkerTask(imageView,caches);
			Bitmap defaultBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			final AsyncDrawable asyncDrawable=new AsyncDrawable(getResources(), defaultBitmap, task);
			imageView.setImageDrawable(asyncDrawable);
			task.execute(url);
		}
	}

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.main, menu);
       return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       // Handle action bar item clicks here. The action bar will
       // automatically handle clicks on the Home/Up button, so long
       // as you specify a parent activity in AndroidManifest.xml.
       int id = item.getItemId();
       if (id == R.id.action_settings) {
           return true;
       }
       return super.onOptionsItemSelected(item);
   }
   @Override
public void onBackPressed() {
	// TODO Auto-generated method stub
	
	ExitUtil.exit();
}
   //=========20150506===============
   public void initSpinner1(){
		dbm = new DBManager(this);
	 	dbm.openDatabase();
	 	db = dbm.getDatabase();
	 	List<MyListItem> list = new ArrayList<MyListItem>();
		
	 	try {    
	        String sql = "select * from province";  
	        Cursor cursor = db.rawQuery(sql,null);  
	        cursor.moveToFirst();
	        while (!cursor.isLast()){ 
		        String code=cursor.getString(cursor.getColumnIndex("code")); 
		        byte bytes[]=cursor.getBlob(2); 
		        String name=new String(bytes,"gbk");
		        MyListItem myListItem=new MyListItem();
		        myListItem.setName(name);
		        myListItem.setPcode(code);
		        list.add(myListItem);
		        cursor.moveToNext();
	        }
	        String code=cursor.getString(cursor.getColumnIndex("code")); 
	        byte bytes[]=cursor.getBlob(2); 
	        String name=new String(bytes,"gbk");
	        MyListItem myListItem=new MyListItem();
	        myListItem.setName(name);
	        myListItem.setPcode(code);
	        list.add(myListItem);
	        
	    } catch (Exception e) {  
	    } 
	 	dbm.closeDatabase();
	 	db.close();	
	 	
	 	MyAdapter myAdapter = new MyAdapter(this,list);
	 	spinner1.setAdapter(myAdapter);
		spinner1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
	}
   public void initSpinner2(String pcode){
		dbm = new DBManager(this);
	 	dbm.openDatabase();
	 	db = dbm.getDatabase();
	 	List<MyListItem> list = new ArrayList<MyListItem>();
		
	 	try {    
	        String sql = "select * from city where pcode='"+pcode+"'";  
	        Cursor cursor = db.rawQuery(sql,null);  
	        cursor.moveToFirst();
	        while (!cursor.isLast()){ 
		        String code=cursor.getString(cursor.getColumnIndex("code")); 
		        byte bytes[]=cursor.getBlob(2); 
		        String name=new String(bytes,"gbk");
		        MyListItem myListItem=new MyListItem();
		        myListItem.setName(name);
		        myListItem.setPcode(code);
		        list.add(myListItem);
		        cursor.moveToNext();
	        }
	        String code=cursor.getString(cursor.getColumnIndex("code")); 
	        byte bytes[]=cursor.getBlob(2); 
	        String name=new String(bytes,"gbk");
	        MyListItem myListItem=new MyListItem();
	        myListItem.setName(name);
	        myListItem.setPcode(code);
	        list.add(myListItem);
	        
	    } catch (Exception e) {  
	    } 
	 	dbm.closeDatabase();
	 	db.close();	
	 	
	 	MyAdapter myAdapter = new MyAdapter(this,list);
	 	spinner2.setAdapter(myAdapter);
		spinner2.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
	}
   public void initSpinner3(String pcode){
		dbm = new DBManager(this);
	 	dbm.openDatabase();
	 	db = dbm.getDatabase();
	 	List<MyListItem> list = new ArrayList<MyListItem>();
		
	 	try {    
	        String sql = "select * from district where pcode='"+pcode+"'";  
	        Cursor cursor = db.rawQuery(sql,null);  
	        cursor.moveToFirst();
	        while (!cursor.isLast()){ 
		        String code=cursor.getString(cursor.getColumnIndex("code")); 
		        byte bytes[]=cursor.getBlob(2); 
		        String name=new String(bytes,"gbk");
		        MyListItem myListItem=new MyListItem();
		        myListItem.setName(name);
		        myListItem.setPcode(code);
		        list.add(myListItem);
		        cursor.moveToNext();
	        }
	        String code=cursor.getString(cursor.getColumnIndex("code")); 
	        byte bytes[]=cursor.getBlob(2); 
	        String name=new String(bytes,"gbk");
	        MyListItem myListItem=new MyListItem();
	        myListItem.setName(name);
	        myListItem.setPcode(code);
	        list.add(myListItem);
	        
	    } catch (Exception e) {  
	    } 
	 	dbm.closeDatabase();
	 	db.close();	
	 	
	 	MyAdapter myAdapter = new MyAdapter(this,list);
	 	spinner3.setAdapter(myAdapter);
		spinner3.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
	}
   
	class SpinnerOnSelectedListener1 implements OnItemSelectedListener{
		
		public void onItemSelected(AdapterView<?> adapterView, View view, int position,
				long id) {
			province=((MyListItem) adapterView.getItemAtPosition(position)).getName();
			province=province.substring(0,province.length()-1);
			String pcode =((MyListItem) adapterView.getItemAtPosition(position)).getPcode();
			
			initSpinner2(pcode);
			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}		
	}
	class SpinnerOnSelectedListener2 implements OnItemSelectedListener{
		
		public void onItemSelected(AdapterView<?> adapterView, View view, int position,
				long id) {
			city=((MyListItem) adapterView.getItemAtPosition(position)).getName();
			city=city.substring(0,city.length()-1);
			String pcode =((MyListItem) adapterView.getItemAtPosition(position)).getPcode();
			
			initSpinner3(pcode);
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}		
	}
	
	class SpinnerOnSelectedListener3 implements OnItemSelectedListener{
		
		public void onItemSelected(AdapterView<?> adapterView, View view, int position,
				long id) {
//			UpdateAdater2  myupdateAdater2=new UpdateAdater2(Main.this, new ArrayList<Map<String,String>>(), caches,mPullToRefreshListView);
			listView1.setAdapter(updateAdater2);
			district=((MyListItem) adapterView.getItemAtPosition(position)).getName();
		       district=district.substring(0, district.length()-1);
			final String url="http://10.0.2.2:8080/DingCan/index.php/server/showResturant/getResByPlace";
		
			 String  current=String.valueOf(updateAdater2.getCount());
			new ResturantWorker(updateAdater2).execute(url,current,"1",province,city,district);
			
//			district=((MyListItem) adapterView.getItemAtPosition(position)).getName();
//			district=district.substring(0, district.length()-1);
//			final String url="http://10.0.2.2:8080/DingCan/index.php/server/showResturant/getResByPlace";
//			 String  current=String.valueOf(updateAdater2.getCount());
//			 new ResturantWorker(updateAdater2).execute(url,current,"2",province,city,district);
			Toast.makeText(Main.this, province+" "+city+" "+district, Toast.LENGTH_LONG).show();
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			// TODO Auto-generated method stub
		}		
	}
	//=================================
}
