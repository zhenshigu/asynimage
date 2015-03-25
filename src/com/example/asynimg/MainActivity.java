package com.example.asynimg;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.example.asynimg.BitmapWorkerTask.AsyncDrawable;

import android.support.v7.app.ActionBarActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

	 Map<String, SoftReference<Bitmap>>  caches;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ImageView imageView1=(ImageView)findViewById(R.id.imageView1);
//        String url="http://10.0.2.2:8080/img/1.jpg";
//        loadBitmap(url, imageView1);
        caches=new HashMap<String, SoftReference<Bitmap>>();
        ListView listView1=(ListView)findViewById(R.id.listView1);
        UpdateAdater updateAdater=new UpdateAdater(this, new ArrayList<String>(),caches);
        listView1.setAdapter(updateAdater);
        List<String> urlStrings=new ArrayList<String>();
        urlStrings.add("http://10.0.2.2:8080/img/2.jpg");
        urlStrings.add("http://10.0.2.2:8080/img/3.jpg");
        urlStrings.add("http://10.0.2.2:8080/img/4.jpg");
        urlStrings.add("http://10.0.2.2:8080/img/5.jpg");
        urlStrings.add("http://10.0.2.2:8080/img/6.jpg");
        urlStrings.add("http://10.0.2.2:8080/img/7.jpg");
        urlStrings.add("http://10.0.2.2:8080/img/8.jpg");
        updateAdater.addItems(urlStrings);
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
}
