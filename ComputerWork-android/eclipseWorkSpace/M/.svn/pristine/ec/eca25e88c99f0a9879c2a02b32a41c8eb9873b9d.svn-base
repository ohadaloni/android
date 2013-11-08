package com.theora.M;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
/*------------------------------------------------------------*/

public class Browse extends Activity {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String urlString = extras.getString("com.theora.urlString");
        String title = extras.getString("com.theora.title");
		m = new Mcontroller(this, "mdb");
		if ( title != null )
			setTitle(title);
		if ( urlString == null ) {
			m.utils.logError("null urlString");
			finish();
		}
		m.view.browse(urlString);
	}
	/*------------------------------------------------------------*/
}
