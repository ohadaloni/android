package com.theora.M;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;

/*------------------------------------------------------------*/
/**
 * new Mblob(this, "http://www.theora.com/images/2guitars.jpg").get(cb);
 */
public class Mblob {
	/*------------------------------------------------------------*/
	private static final int status_notReady = 1;
	private static final int status_notFound = 2;
	private static final int status_anulled = 3;
	private static final int status_error = 4;
	private static final int status_ready = 5;

	private static final int maxTries = 3;

	private static final String dbName = "mdb";
	private final String tname = "MblobCache";
	private final String sourceFname = "sourceUrl";
	private final String blobFname = "Mblob";

	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private Activity a = null;
	private String sourceUrl;
	private byte[] ba;
	private MdbHelper helper = null;
	private int status;
	private int tryCnt = 0;
	private boolean annulled = false;
	private int getFromUrlStartTime = 0;
	/*------------------------------------------------------------*/
	/**
	 * construct a blob from its source
	 * the source is used as a key to store this blob in the blob cache database
	 * if this blob is already in the db, nothing else need be done.
	 * otherwise, obtain the blob and store it.
	 */
	public Mblob(Activity a, String sourceUrl) {
		this.a = a;
		m = new Mcontroller(this.a, dbName);
		helper = new MdbHelper(a, dbName);
		status = status_notReady;
		this.sourceUrl = sourceUrl;
		createTable();
	}
	/*------------------------------------------------------------*/
	/**
	 * an object without a source, for utilities.
	 */
	public Mblob(Activity a) {
		this(a, null);
	}
	/*------------------------------------------------------------*/
	private void createTable() {
		String sql = "create table if not exists " + tname + "(" +
		"id integer primary key autoincrement" +
		", " + sourceFname + " text" +
		", " + blobFname + " blob" +
		" )";
		m.model.sql(sql);
	}
	/*------------------------------------------------------------*/
	/**
	 * get the data for this blob whenever it becomes available.
	 * this might be immediately, if it is in the database cache or sometimes later.
	 * call with null if/when  failed to get the data.
	 * however, if the data has been requested recently and is not yet here,
	 * skip threading and calling back all together.
	 * the caller might be called from a previous call.
	 * Nautilus (for example) rather counts on extras.
	 */
	public void get(final McallBackWithByteArray cb) {
		if ( annulled )
			return;
		if ( sourceUrl == null ) {
			m.utils.logError("no sourceUrl");
			return;
		}
		if ( status == status_anulled)
			return;
		else if ( status == status_error || status == status_notFound ) {
			log("get: " + sourceUrl);
			cb.f(null);
		}
		if ( ba == null )
			getFromDb();
		if ( ba != null) {
			cb.f(ba);
			ba = null;
			return;
		}
		int now = Mdate.time();
		if ( getFromUrlStartTime != 0 && ( now - getFromUrlStartTime ) < 60 )
			return;
		getFromUrlStartTime = now;
		final  int done = 1;
		final  int failed = 2;
		final Handler handler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case done:
					cb.f(ba);
					ba = null;
					break;
				case failed:
				default:
					cb.f(null);
					break;
				}
				return true;
			}		
		});
		new Thread(){
			public void run() {
					if ( getFromUrl() )
						handler.sendEmptyMessage(done);
					else
						handler.sendEmptyMessage(failed);
				}
			}.start();
	}
	/*------------------------------------------------------------*/
	private boolean getFromUrl() {
		try {
			URL url = new URL(sourceUrl);
			URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(1024);
            int current = 0;
            while ((current = bis.read()) != -1)
            	baf.append((byte) current);
			ba = baf.toByteArray();
			status = status_ready;
			save();
			return(true);
		} catch ( OutOfMemoryError e) {
			tryCnt++;
			if ( tryCnt > maxTries ) {
				status = status_error;
				m.utils.logError(sourceUrl + e.toString());
			}
			log(e.toString());
			return(false);
		} catch (Exception e) {
			tryCnt++;
			if ( tryCnt > maxTries ) {
				status = status_error;
				m.utils.logError(sourceUrl + e.toString());
			}
			log(e.toString());
			return(false);
		}
	}
	/*----------------------------------------*/
	public void get(final McallbackWithDrawable cb) {
		if ( annulled )
			return;
		McallBackWithByteArray bcb = new McallBackWithByteArray() {
			@Override
			public void f(byte[] ba) {
				if ( ba == null )
					return;
				try {
					InputStream is = new ByteArrayInputStream(ba);
					Drawable d = Drawable.createFromStream(is, "src");
					cb.f(d);
				} catch ( OutOfMemoryError e) {
					status = status_error;
					m.utils.logError("OutOfMemoryError");										
				} catch ( NullPointerException e) {
					status = status_error;
					m.utils.logError("NullPointerException");					
				} catch (Exception e) {
					status = status_error;
					log(e.toString());
				}
			}			
		};
		get(bcb);
	}
	/*------------------------------------------------------------*/
	private int save() {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();
		} catch (SQLiteException e) {
			db = null;
		}
		if ( db == null ) {
			m.utils.logError("Cannot getWritableDatabase");
			return(0);
		}
	    ContentValues cv = new ContentValues();
	    cv.put("sourceUrl", sourceUrl);
	    cv.put("Mblob", ba);
	    long rowId = db.insert(tname, null, cv);
	    db.close();
		int rowNum = m.model.rowNum(tname);
		if ( rowNum < 200 && rowNum % 25 == 0 || rowNum % 100 == 0 )
			m.utils.logInfo("Cache Size: " + rowNum);
	    if ( rowId == -1 )
	    	return(0);
	     else
	    	return((int)rowId);
	}
	/*------------------------------------------------------------*/
	/*
	 * it is never an error for the data to not be in the db cache
	 * on success, ba is set to the blob
	 */
	private void getFromDb() {
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();
		} catch (SQLiteException e1) {
			db = null;
		}
		if ( db == null ) {
			m.utils.logError("Cannot getReadableDatabase");
			return;
		}
		Cursor cursor;
		String sql = String.format("select %s from %s where %s = '%s'",
				blobFname, tname, sourceFname, m.model.str(sourceUrl));
		try {
			cursor = db.rawQuery(sql, null);
			if ( ! cursor.moveToFirst() ) {
				cursor.close();
				db.close();
				return;
			}

			ba = cursor.getBlob(0);
			cursor.close();
			db.close();
		} catch ( Exception e ) {
			return;
		}
	}
	/*------------------------------------------------------------*/
	public static byte[] drawableToPngByteArray(Drawable d) {
		Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
		byte[] bitmapdata = stream.toByteArray();
		return(bitmapdata);
	}
	/*------------------------------------------------------------*/
	public static byte[] drawableToJpgByteArray(Drawable d) {
		Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		byte[] bitmapdata = stream.toByteArray();
		return(bitmapdata);
	}
	/*------------------------------------------------------------*/
	public void annul() {
		annulled = true;
		sourceUrl = null;
		ba = null;
		helper = null;
	}
	/*------------------------------------------------------------*/
	public void clearDb() {
		m.model.sql("delete from " + tname);
	}
	/*------------------------------------------------------------*/
	public void clearImages() {
		String like = "sourceUrl like '%.png' or sourceUrl like '%.jpg' or sourceUrl like '%.gif' or sourceUrl like '%.jpeg'";
		m.model.sql("delete from " + tname + " where " + like);
	}
	/*------------------------------------------------------------*/
	public void clearAudio() {
		String like = "sourceUrl like '%.mp3'";
		m.model.sql("delete from " + tname + " where " + like);
	}
	/*------------------------------------------------------------*/
	public void clearVideo() {
		String like = "sourceUrl like '%.3gp'";
		m.model.sql("delete from " + tname + " where " + like);
	}
	/*------------------------------------------------------------*/
	private void log(String msg) {
		if ( Mutils.stristr(msg, "NullPointerException") )
			m.utils.logError(msg);
//		Log.d("Mblob", msg);
	}
	/*------------------------------------------------------------*/
}
