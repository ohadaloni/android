package com.theora.M;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Lem extends Activity {
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private Mcontroller m = null;
	private static final int myBirthday = 19610215;
	private static final int gameId = 1;
	private static final String gameSubject = "Animals"; 
	private static final String gameDescription = "Guess What Animal This Is"; 
	private static final int gwMagic = myBirthday + gameId;
	/*------------------------------------------------------------*/
	public Lem() {
		m = new Mcontroller(this, "mdb");
	}
	/*------------------------------------------------------------*/
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Nautilus.class);
        intent.putExtra("com.theora.Nautilus.gameId", gameId);
        intent.putExtra("com.theora.Nautilus.gameSubject", gameSubject);
        intent.putExtra("com.theora.Nautilus.gameDescription", gameDescription);
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