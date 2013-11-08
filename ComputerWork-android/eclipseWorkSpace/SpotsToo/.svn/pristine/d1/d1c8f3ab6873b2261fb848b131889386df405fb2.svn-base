package com.theora.Spots;

import com.theora.M.McallbackWithString;
import com.theora.M.Mcontroller;
import com.theora.M.Mtime;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Edit extends Activity {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	SpotsUtils utils = null;
	/*------------------------------------------------------------*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new SpotsUtils(this);
		utils.setBackground();
	    Intent i = getIntent();
	    Bundle extras = i.getExtras();
	    if ( extras == null ) {
	    	m.view.msg("NewSpot: extras is null");
	    	// finish();
	    	return;
	    }
	    int rowId = extras.getInt("Spots.rowId", 0);
	    edit(rowId);
	}
	/*------------------------------------------------------------*/
	private void edit(final int rowId) {
		m.utils.logInfo();
		final String label = utils.label(rowId);
		String message = "Change lable for " + label ;
		setTitle(message);
		m.view.prompt("Edit Spot", message, label, new McallbackWithString() {
			@Override
			public void f(String newLabel) {
				if ( newLabel == null ||
						newLabel.length() == 0 ||
							newLabel.compareTo(label) == 0) {
					finish();
					return;

				}
				String evenNewerLabel = utils.makeNewLabel(newLabel, label);
				String[][] nameValuePairs = {
					{"label", evenNewerLabel}
					, { "updated", Mtime.dateTimeNow() }
				};
				if ( m.model.update("spots", rowId, nameValuePairs) ) {
					m.utils.logInfo("done");
					m.view.msg("Changed " + label + " to " + evenNewerLabel);
					finish();
				} else
					m.utils.logError("fail");

					
			}
		});
	}
	/*------------------------------------------------------------*/
}
