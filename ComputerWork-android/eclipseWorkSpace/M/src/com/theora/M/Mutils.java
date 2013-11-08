package com.theora.M;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
/*------------------------------------------------------------*/
public class Mutils {
	/*------------------------------------------------------------*/
	public Activity a;
	private Context context;
	/*------------------------------------------------------------*/
	public Mutils(Context ct) {
		context = ct;
	}
	/*------------------------------------------------------------*/
	public Mutils(Activity a, Context ctx) {
		this.a = a;
		this.context = ctx;
	}
	/*------------------------------------------------------------*/
	public void _msg(String msg, int length) {
		Toast toast = Toast.makeText(context, msg, length);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	/*------------------------------*/
	public void msg(String msg) {
		_msg(msg, Toast.LENGTH_SHORT);
	}
	/*------------------------------*/
	public void msg(String msg, boolean isLong) {
		// this function is always called with LENGTH_LONG
		_msg(msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
	}
	/*------------------------------*/
	public void msg(String msg, int milliSeconds) {
		//  passing milliSeconds to toast is deprecated
		_msg(msg, milliSeconds);
	}
	/*------------------------------------------------------------*/
	public void log(String msg) {
		Log.d("Mutils", msg);
	}
	/*------------------------------------------------------------*/
	public void cameraClick() {
		new Mp3(a, "file:///system/media/audio/ui/camera_click.ogg").play();
	}
	/*-----------------------------------*/
	public void click() {
		new Mp3(a, "file:///system/media/audio/ui/KeypressStandard.ogg").play();
	}
	/*-----------------------------------*/
	/*
	 * Deprecated
	 */
	public void piano(String note) {
		new Mp3(a, note).play();
	}
	/*------------------------------------------------------------*/
	// like php, but return boolean
	/*------------------------------*/
	public static boolean stristr(String hay, String str) {
		String lowHay = hay.toLowerCase();
		String lowStr = str.toLowerCase();
		return(lowHay.indexOf(lowStr) >= 0);
	}
	/*------------------------------*/
	public static boolean strncmp(String s1, String s2, int n) {
		if ( s1.length() != s2.length() )
			return(false);
		if ( s1.length() <= n )
			return(s1.compareTo(s2) == 0);
		String ns1 = s1.substring(0, n);
		String ns2 = s2.substring(0, n);
		return(ns1.compareTo(ns2) == 0);
	}
	/*------------------------------------------------------------*/
	public static boolean isNumeric(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch( Exception e) {
			return false;
		}
	}
	/*------------------------------------------------------------*/
	public static int indexOf(String s, String[] a) {
		if ( s == null )
			return(-1);
		for(int i=0;i<a.length;i++)
			if(s.equals(a[i]))
				return(i);
		return(-1);
	}
	/*----------------------------------------*/
	public static boolean inArray(String s, String[] a) {
		return(indexOf(s, a) >= 0 );
	}
	/*----------------------------------------*/
	public static boolean inArray(String s, ArrayList<String> a) {
		if ( a == null )
			return(false);
		return(a.contains(s));
	}
	/*------------------------------------------------------------*/
    public int resourceId(String name, String defType) {
        Resources resources = context.getResources();
        String packageName = context.getPackageName();
        int resId = resources.getIdentifier(name, defType, packageName);
        if (  resId == 0 )
        	log(String.format("Mutils:resourceId: %s/%s: Resource not found in %s", defType, name, packageName));
        return(resId);
    }
	/*------------------------------------------------------------*/
    public Drawable getDrawable(String name) {
        int resId;
        Drawable ret;
        if ( (resId = resourceId(name, "drawable")) == 0 )
        	return(null);
        Resources r = context.getResources();
        try {
        	ret = r.getDrawable(resId);
        	return(ret);
        } catch (Exception e) {
        	return(null);
        }
    }
	/*------------------------------------------------------------*/
	public static int rand(int n, int m) {
		if ( n == m )
			return(n);
		int span = m - n + 1;
		double r = Math.random();
		int raw = (int)(r * span);
		int ret = n + raw;
		return(ret);
	}
	/*------------------------------------------------------------*/
	public void vibrate(int milliseconds) {
		try {
			Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(milliseconds);
		} catch ( Exception e ){
			log(e.toString());
			// msg("no android.permission.VIBRATE?");
		}
	}
	/*------------------------------------------------------------*/
	public void logError(String detail) {
		logInfo(detail, true);
	}
	/*------------------------------*/
	public void logInfo(String detail) {
		logInfo(detail, false);
	}
	/*------------------------------*/
	public void logError() {
		logInfo(true);
	}
	/*------------------------------*/
	public void logInfo() {
		logInfo(false);
	}
	/*------------------------------*/
	private void logInfo(boolean isError) {
		logInfo("", isError);
	}
	/*------------------------------*/
	public String appName() {
		String appName = "N";
		try {
			String packageName = a.getPackageName();
			String packageParts[] = packageName.split("\\.");
			appName = packageParts[packageParts.length-1];
		} catch (Exception e) {
		}
		return(ucwords(appName));
	}
	/*------------------------------*/
	public String appVersion() {
		String versionName = "0.0";
		try {
			String packageName = a.getPackageName();
			PackageManager manager = a.getPackageManager();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			versionName = info.versionName;
		} catch (Exception e) {
		}
		return(versionName);
	}
	/*------------------------------*/
	public String about() {
		return(appName() + " Version " + appVersion());
	}
	/*------------------------------*/
	@SuppressWarnings("unused")
	private int appCodeVersion() {
		int versionCode = 0;
		try {
			String packageName = a.getPackageName();
			PackageManager manager = a.getPackageManager();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			versionCode = info.versionCode;
		} catch (Exception e) {
		}
		return(versionCode);
	}
	/*------------------------------*/
	private void logInfo(String detail, boolean isError) {
		if ( a == null ) {
			log("logInfo: a is null: " + ( isError ? "Error: " :  "Info: " ) + detail);
			return;
		}
		String appName = appName();
		int versionCode = 0;
		String versionName = "0.0";
		try {
			String packageName = a.getPackageName();
			PackageManager manager = a.getPackageManager();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			versionCode = info.versionCode;
			versionName = info.versionName;
		} catch (Exception e) {
		}
		Thread thread = Thread.currentThread();
		StackTraceElement[] stackTraceElements = thread.getStackTrace();
		
		for (int nestLevel = 0 ;nestLevel < stackTraceElements.length; nestLevel++) {
			StackTraceElement element = stackTraceElements[nestLevel];
			String fileName = element.getFileName();
			String fullClassName = element.getClassName();
			String classNameParts[] = fullClassName.split("\\.");
			String className = classNameParts[classNameParts.length-1];
			
			if ( fileName.compareTo("Mutils.java") == 0  )
				continue; // from Mutils pass full args list, do not call this method
			if ( 
					stristr(fileName, "Thread")
					|| stristr(fileName, "VMstack")
					|| stristr(className, "dalvik")
				)	
				continue;
			
			String fileParts[] = fileName.split("\\.");
			String fileClassName = fileParts[0];
			if ( className.compareTo(fileClassName) != 0)
				fileClassName = fileClassName + ":" + className ;
			String methodName = element.getMethodName();
			int lineNumber = element.getLineNumber();
			logInfo(appName, versionName, versionCode, fileClassName, methodName, lineNumber, detail, isError);
			return;
		}
		logInfo("M", versionName, versionCode, "Mutils", "logInfo", 0, "Stack Trace Failure", true);
		logInfo(appName, versionName, versionCode, "unknown", "unknown", 0, detail, isError);
	}
	/*------------------------------------------------------------*/
	private void logInfo(String appName, String appVersion, int codeVersion, String className, String methodName, int lineNumber, String detail, boolean isError) {
		String url = "http://www.theora.com/android/log.php?" +
			"appName=" + appName +
			"&appVersion=" + appVersion +
			"&codeVersion=" + codeVersion +
			"&className=" + className +
			"&methodName=" + methodName +
			"&lineNumber=" + lineNumber +
			"&isError=" + ( isError ? "true" : "false" ) +
			"&detail=" + ((detail != null) ?  URLEncoder.encode(detail) : "");
		sendUrl(url);
	}
	/*------------------------------------------------------------*/
	public void tellAFriend() {
		String appUrl = "https://market.android.com/details?id=" + a.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW);    
        intent.setData(Uri.parse("sms:"));
        String intro = "Hey, check out this cool app! Its free and ad free. Click to install it: ";
        String smsBody = intro + appUrl ;
        intent.putExtra("sms_body", smsBody);
        a.startActivity(intent);
	}
	/*------------------------------------------------------------*/
	/**
	 * send this url in a separate thread,
	 * without caring about the outcome or return text
	 */
	public void sendUrl(String urlString) {
		try {
			final URL url = new URL(urlString);
			Runnable sender = new Runnable() {
				public void run() {
					try {
						 HttpURLConnection con = (HttpURLConnection)url.openConnection();
				         con = (HttpURLConnection) url.openConnection();
				         con.setReadTimeout(10000 /* milliseconds */);
				         con.setConnectTimeout(15000000 /* milliseconds */);
				         con.setRequestMethod("GET");
				         con.setDoInput(true);
				         con.connect();
				         BufferedReader reader = new BufferedReader(
				                 new InputStreamReader(con.getInputStream(), "UTF-8"));
				           @SuppressWarnings("unused")
				           String answer = reader.readLine();
				           reader.close();
					} catch ( Exception e ) {
						log(e.toString());
					}
				}
			};
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.submit(sender);
		} catch (Exception e ) {
			log(e.toString());
		}
	}
	/*------------------------------------------------------------*/
	private String getUrlText(String urlString) {
		try {
			final URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
	        con = (HttpURLConnection) url.openConnection();
	        con.setReadTimeout(10000 /* milliseconds */);
	        con.setConnectTimeout(15000000 /* milliseconds */);
	        con.setRequestMethod("GET");
	        con.setDoInput(true);
	        con.connect();
	        BufferedReader reader = new BufferedReader(
	        		new InputStreamReader(con.getInputStream(), "UTF-8"));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
			    total.append(line);
	        reader.close();
	        return(total.toString());
		} catch ( Exception e ) {
			log(e.toString());
			return(null);
		}
	}
	/*----------------------------------------*/
	/**
	 * get text from the Internet in a separate thread
	 * call back in the same thread when it arrives.
	 * might call immediately if the cache already has this image.
	 * the hashMap serves two purposes:
	 * 1. it connects between urlStrings and texts across repetitive calls
	 * 2. it is a cache.
	 */
	private HashMap<String, String> getUrlTexts = null;
	/*----------------------------------------*/
	public void getUrlText(final String urlString, final McallbackWithString cb) {
		if ( getUrlTexts == null )
			getUrlTexts = new HashMap<String, String>();
		String s = getUrlTexts.get(urlString);
		if ( s != null ) {
			cb.f(s);
			return;
		}
		final  int done = 1;
		final  int failed = 2;
		final Handler handler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case done:
					String s = getUrlTexts.get(urlString);
					cb.f(s);
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
				try {
					String s = getUrlText(urlString);
					getUrlTexts.put(urlString, s);
					handler.sendEmptyMessage(done);
				} catch (Exception e) {
					handler.sendEmptyMessage(failed);
				}
			};
		}.start();
	}
	/*------------------------------------------------------------*/
	/*
	 * the hashMap serves two purposes:
	 * 1. it connects between urlStrings and drawables across repetitive calls
	 * 2. it is a cache.
	 */
	private HashMap<String, Drawable> getUrlDrawables = null;
	/*----------------------------------------*/
	private static Drawable getUrlImage(String urlString) {
		try {
			URL url = new URL(urlString);
			InputStream is = (InputStream)url.getContent();
			Drawable d = Drawable.createFromStream(is, "name");
			return (d);
		} catch (Exception e) {
			return(null);
		}
	}
	/*----------------------------------------*/
	/**
	 * get a drawable from the Internet in a separate thread
	 * call back in the same thread when it arrives.
	 * might call immediately if the cache already has this image.
	 */
	public void getUrlImage(final String urlString, final McallbackWithDrawable cb) {
		if ( getUrlDrawables == null )
			getUrlDrawables = new HashMap<String, Drawable>();
		Drawable d = getUrlDrawables.get(urlString);
		if ( d != null ) {
			cb.f(d);
			return;
		}
		final  int done = 1;
		final  int failed = 2;
		final Handler handler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case done:
					Drawable d = getUrlDrawables.get(urlString);
					cb.f(d);
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
				try {
					Drawable d = getUrlImage(urlString);
					getUrlDrawables.put(urlString, d);
					handler.sendEmptyMessage(done);
				} catch (Exception e) {
					handler.sendEmptyMessage(failed);
				}
			};
		}.start();
	}
	/*------------------------------------------------------------*/
	/**
	 * php like ucwords: capitalize first letter of each word in a string
	 */
	public String ucwords(String sourceString) {
		if ( sourceString == null )
			return(null);
		int len = sourceString.length();
		if ( len == 0 )
			return(sourceString);
		char prevc;
		StringBuilder sb = new StringBuilder();
		char c = sourceString.charAt(0);
		sb.append(Character.toUpperCase(c));
		for(int i=1;i<len;i++) {
			prevc = c;
			c = sourceString.charAt(i);
			if ( ! Character.isLetter(prevc) )
				c = Character.toUpperCase(c);
			sb.append(c);
		}
		return(sb.toString().trim().replaceAll(" +", " "));
	}
	/*---------------------*/
	/**
	 * add a new value at the end of this row
	 */
	public static String[] arrayPush(String a[], String v) {
		if ( a == null ) {
			String ret[] = { v };
			return(ret);
		}
		int length = a.length;
		String ret[] = new String[length+1];
		for(int i=0;i<length;i++)
			ret[i] = a[i];
		ret[length] = v;
		return(ret);
	}
	/*------------------------------------------------------------*/
	/**
	 * format double for data, with that many decimals, and without comma separators.
	 */
	public static String format(double d, int decimals) {
		return(Mutils.format(d, decimals, false));
	}
	/*------------------------------------------------------------*/
	/**
	 * format a double to this many decimals
	 * with or without comma separators
	 */
	public static String format(double d, int decimals, boolean withCommas) {
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(decimals);
		df.setMaximumFractionDigits(decimals);
		df.setGroupingUsed(withCommas);
		String ret = df.format(d);
		return(ret);
	}
	/*------------------------------------------------------------*/
	public static InputStream stringToInputStream(String text)  {
		try {
			return new ByteArrayInputStream(text.getBytes());
		} catch (Exception e) {
			return(null);
		}
	}
	/*------------------------------------------------------------*/
	public void AppsByOhad() {
		try {
			a.startActivity(new Intent(a, AppsByOhad.class));
		} catch (Exception e) {
			logError();
		}
	}
	/*------------------------------------------------------------*/
	public void rateMe() {
		String packageName = a.getPackageName();
		String url = "https://market.android.com/details?id=" + packageName;
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		a.startActivity(intent);
	}
	/*------------------------------------------------------------*/
	/**
	 * start the Browse activity
	 */
	public void Browse(String urlString, String title) {
		try {
			Intent i = new Intent(a, Browse.class);
			i.putExtra("com.theora.urlString", urlString);
			i.putExtra("com.theora.title", title);
			a.startActivity(i);
		} catch (Exception e) {
			logError();
		}
		
	}
	/*------------------------------------------------------------*/
	public static String implode(String delim, String[] a) {
		int len = a.length;
		if ( len == 0 )
			return(null);
		if ( len == 1 )
			return(a[0]);
		
		StringBuilder sb = new StringBuilder();
		sb.append(a[0]);

		for (int i=1;i<len;i++) {
			sb.append(delim);
			sb.append(a[i]);
		}

		String ret = sb.toString();
		return ret;
	}
	/*------------------------------------------------------------*/
	private Uri attachmentUri(String text, String fileName) {
		if ( fileName == null )
			fileName = "attachment.txt";
		File file = new File(Environment.getExternalStorageDirectory(), fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
		    fos.write(text.getBytes());
		    fos.flush();
		    fos.close();
		} catch (FileNotFoundException e) {
		    log("Could not write file " + e.getMessage());
			return(null);
		} catch (IOException e) {
		    log("Could not write file " + e.getMessage());
			return(null);
		}
		Uri ret = Uri.fromFile(file);
		return(ret);
	}
	/*--------------------------------------*/
	public void email(String to, String subject, String emailText, String attachmentContent, String attachmentFileName) {		
//		Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE); // crashes the gmail app if extra is not multiple, see below.  
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/csv");
//		emailIntent.setType("message/rfc822");
		if ( to != null )
			emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {to});
		if ( subject != null )
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		if ( emailText != null )
			emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
		if ( attachmentContent != null ) {
			Uri uri = attachmentUri(attachmentContent, attachmentFileName);
			if ( uri != null ) {
				emailIntent.putExtra(Intent.EXTRA_STREAM, uri); // crashes the gmail app with ACTION_SEND_MULTIPLE
//				ArrayList<Uri> uris = new ArrayList<Uri>();
//				uris.add(uri);
//			    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			}
		}
//		this.a.startActivity(emailIntent);
		this.a.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
	/*------------------------------------------------------------*/
	public void email(String to, String subject, String emailText) {
		email(to, subject, emailText, null, null);
	}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
