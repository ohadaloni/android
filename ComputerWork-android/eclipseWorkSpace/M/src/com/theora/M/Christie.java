package com.theora.M;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;

public class Christie extends Activity {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	/*------------------------------------------------------------*/
	public Christie() {
		m = new Mcontroller(this, "mdb");
	}
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    String sql = "CREATE TABLE if not exists storedImages (_id INTEGER PRIMARY KEY, myImage BLOB)";
	    m.model.sql(sql);
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		resume();
	}
	/*------------------------------------------------------------*/
	// http://www.tutorialforandroid.com/2009/10/how-to-insert-image-data-to-sqlite.html
	public void resume() {
		try {
			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			HttpGet mHttpGet = new HttpGet("your image url");
			HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
			if (mHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = mHttpResponse.getEntity();
				if (entity != null) {
					// insert to database
					ContentValues values = new ContentValues();
					values.put("myImage", EntityUtils.toByteArray(entity));
					// TODO M.Christie.resume() have yet to figure this one out, see 'creating your own content providor'
					// getContentResolver().insert(MyBaseColumn.MyTable.CONTENT_URI, values);
				}
			}
		} catch (Exception e) {
			return; 
		}
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}
