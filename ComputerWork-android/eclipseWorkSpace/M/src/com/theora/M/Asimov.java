package com.theora.M;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Asimov extends Activity {
	/*------------------------------------------------------------*/
	private static final int myBirthday = 19610215;
	private static final int gameId = 1;
	private static final int gwMagic = myBirthday + gameId;
	/*------------------------------------------------------------*/
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Nautilus.class);
        intent.putExtra("com.theora.Nautilus.gameId", gameId);
		startActivityForResult(intent, gwMagic);
    }
	/*------------------------------------------------------------*/
	// called right before onResume
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == gwMagic  )
			finish() ;
	}
	/*------------------------------------------------------------*/
}
