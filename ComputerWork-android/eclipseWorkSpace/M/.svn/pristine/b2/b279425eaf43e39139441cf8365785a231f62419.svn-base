package com.theora.M;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
/*------------------------------------------------------------*/
public class MedEdit extends Activity {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private MedLogUtils utils = null;
	private String med;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new MedLogUtils(this);
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if ( extras == null ) {
			m.utils.logError("no extras");
			finish();
		}
		med = extras.getString(MedLogUtils.medName);
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		resume();
	}
	/*------------------------------------------------------------*/
	private void resume() {
		McallbackWithString done = new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s != null && s.length() > 0 )
					utils.changeMedName(med, s);
				finish();
			}	
		};
		m.view.prompt(med, "Changing Medication Description", med, done);
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}
