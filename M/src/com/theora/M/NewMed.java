package com.theora.M;

import android.app.Activity;
import android.os.Bundle;

/*------------------------------------------------------------*/
public class NewMed extends Activity {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private MedLogUtils utils = null;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new MedLogUtils(this);
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		resume();
	}
	/*------------------------------------------------------------*/
	private void resume() {
		McallbackWithString after = new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s != null  && s.length() > 0 )
					utils.newMedEntry(s);
				finish();
			}	
		};
		m.view.prompt("Taking New Medication Now", "Enter name of medication", after );
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}
