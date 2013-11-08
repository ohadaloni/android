package com.theora.M;

import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
/*------------------------------------------------------------*/
public class Mp3 {
	/*------------------------------------------------------------*/
	private Activity a = null;
	@SuppressWarnings("unused")
	private Mcontroller m = null;
	String urlString = null;
	private boolean isFile = false;
	private boolean isNet = false;
	private boolean isResource = false;
	private boolean isError = false;
	private boolean looping = false;
	private int resId;
	private MediaPlayer mp = null;
	private Mblob blob;
	private byte[] ba;
	/*------------------------------------------------------------*/
	private Mp3(Activity a) {
		this.a = a;
		m = new Mcontroller(this.a, "mdb");
	}
	/*------------------------------------------------------------*/
	public Mp3(Activity a, String urlString) {
		this(a);
		this.urlString = urlString;
		if ( urlString == null ) {
			isError = true;
			return;
		}
		int len = urlString.length();
		if (urlString.indexOf('/') < 0 ) {
			Resources resources = a.getResources();
			String packageName = a.getPackageName();
			resId = resources.getIdentifier(urlString, "raw", packageName);
			if (  resId != 0 )
				isResource = true;
			else
				isError = true;
		} else if ( len > 10 && urlString.substring(0, 7).equals("file:///") ) {
			isFile = true;
		}
		else if ( len > 10 && urlString.substring(0, 4).equals("http") )
			isNet = true;
		else
			isError = true;
			
			
		// not sure how a downloaded blob can be later played
		// see: http://code.google.com/p/android/issues/detail?id=739
		// so skip downloading this file and caching it in the database
//		if ( isNet )
//			blob = new Mblob(a, urlString);
//		McallBackWithByteArray cb = new McallBackWithByteArray(){
//			@Override
//			public void f(byte[] ba) {
//				gotBlob(ba);
//			}			
//		};
//		blob.get(cb);
	}
	/*------------------------------------------------------------*/
	public boolean play() {
		if ( isError )
			return(false);
		if ( isResource )
			return(playResource());
		if ( isFile )
			return(playFile());
		if ( isNet )
			return(playFile()); // ideally, playBlob();
		isError = true;
		return(false);
	}
	/*------------------------------------------------------------*/
	public void setLooping(boolean b) {
		looping = b;
	}
	/*------------------------------------------------------------*/
	public void stop() {
		releaseMp();
	}
	/*------------------------------------------------------------*/
	private void releaseMp() {
		try {
			if (mp != null) {
				mp.stop();
				mp.release();
			}
		} catch (Exception e) {
		}
	}
	/*------------------------------------------------------------*/
	public boolean playResource() {
		try {
			releaseMp();
			mp = MediaPlayer.create(a, resId);
			if (mp == null)
				return(false);
			else {
				mp.setLooping(looping);
				mp.start();
				return(true);
			}
		} catch (Exception e) {
//			isError = true;
			return(false);
		}
	}
	/*------------------------------------------------------------*/
	public boolean playFile() {
    	try {
    		releaseMp();
			mp = MediaPlayer.create(a, Uri.parse(urlString));
			if (mp == null)
				return(false);
			else {
				mp.setLooping(looping);
	        	mp.start();
	        	return(true);
			}
		} catch (Exception e) {
			isError = true;
			return(false);
		}
	}
	/*------------------------------------------------------------*/
	// not sure how a downloaded blob can be later played
	// see: http://code.google.com/p/android/issues/detail?id=739
	// so skipping downloading this file and caching it in the database
	// ba is always null

	@SuppressWarnings("unused")
	private boolean playBlob() {
		if ( ba == null ) // the blob is not here
			return(false);
		return false;
	}
	/*------------------------------------------------------------*/
	/**
	 * TODO: Mp3 record
	 * should be able to record something and store it in the db
	 * create a source to pass back, and get it back from this source
	 * AudioRecord ar = new AudioRecord(resId, resId, resId, resId, resId);
	 */
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private void gotBlob(byte[] ba) {
		if ( ba == null) {
			isError = true;
			return;
		}
		this.ba = ba;
		blob.annul();
		blob = null;
	}
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private void log(String msg) {
		Log.d("Mp3", msg);
	}
	/*------------------------------------------------------------*/
}
