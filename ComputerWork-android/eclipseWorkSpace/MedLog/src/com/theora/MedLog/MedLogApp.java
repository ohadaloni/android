package com.theora.MedLog;

import com.theora.M.MedLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MedLogApp extends Activity {
	/*------------------------------------------------------------*/
	private static final int medLogMagic = 1961021557;
	/*------------------------------------------------------------*/
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MedLog.class);
		startActivityForResult(intent, medLogMagic);
    }
	/*------------------------------------------------------------*/
	// called right before onResume
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == medLogMagic  )
			finish();
	}
	/*------------------------------------------------------------*/
}