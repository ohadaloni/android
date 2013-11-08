package com.theora.Add;

import com.theora.M.Add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
/*------------------------------------------------------------*/
public class AddApp extends Activity {
	/*------------------------------------------------------------*/
	private static final int addAppMagic = 689689;
	/*------------------------------------------------------------*/
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		startActivityForResult(new Intent(this, Add.class), addAppMagic);
    }
	/*------------------------------------------------------------*/
	// called right before onResume
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == addAppMagic  )
			finish() ;
	}
	/*------------------------------------------------------------*/
}