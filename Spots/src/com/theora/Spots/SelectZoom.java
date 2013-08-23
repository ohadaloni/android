package com.theora.Spots;

import com.theora.M.McallbackWithString;
import com.theora.M.Mcontroller;
import com.theora.M.Mutils;
import android.app.Activity;
import android.os.Bundle;

public class SelectZoom extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		final Mutils mutils = new Mutils(this);
		Mcontroller m = new Mcontroller(this, "mdb");
		final String zooms[] = { "Tiny", "Small", "Medium", "Large" };
		m.view.select("Zoom Map", zooms, new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s != null ) {
					int i = Mutils.indexOf(s, zooms);
					int zl = 4+i*5;
					mutils.logInfo("zoom level " + zl);
					Spots.zoomLevelRequest(zl);
				}
				finish();
			}
		});
	}
}
