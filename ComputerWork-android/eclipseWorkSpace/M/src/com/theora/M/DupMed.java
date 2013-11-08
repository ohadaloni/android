package com.theora.M;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/*------------------------------------------------------------*/
public class DupMed extends Activity {
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
		McallbackWithBoolean done = new McallbackWithBoolean() {
			@Override
			public void f(boolean b) {
				if ( b )
					utils.newMedEntry(med);
				finish();
			}	
		};
		m.view.confirm("Taking " + med + " Now", "Are You sure?", done);
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}
