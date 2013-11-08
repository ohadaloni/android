package com.theora.M;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Handler.Callback;

/*------------------------------------------------------------*/
public class MedLogNagger extends Service {
	/*------------------------------------------------------------*/
	private final static int firstTime = 1;
	private final static int again = 2;
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private MedLogUtils utils;
	private Handler handler;
	/*------------------------------------------------------------*/
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}	
	/*------------------------------------------------------------*/
	@Override
	public void onCreate() {
		m = new Mcontroller(this, "mdb");
		utils = new MedLogUtils(this);
		handler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				tryNagging();
				handler.sendEmptyMessageDelayed(again, MedLogUtils.nagIterationIntervalInMillis);
				return(true);
			}
		});
	}
	/*------------------------------------------------------------*/
	@Override
	public void onDestroy() {
	}
	/*------------------------------------------------------------*/	
	@Override
	public void onStart(Intent intent, int startid) {
		utils.log("Nagger:onStart");
		handler.sendEmptyMessageDelayed(firstTime, MedLogUtils.nagMillisBeforeFirstTime);
	}
	/*------------------------------------------------------------*/
	private void tryNagging() {
		String med = utils.nagNeeded();
		if ( med == null )
			return;
		m.utils.logInfo("Nagging");
		Intent i = new Intent(this, MedLog.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra(MedLogUtils.nag, med);
		startActivity(i);
	}
	/*------------------------------------------------------------*/
}
