package com.theora.Spots;

import com.theora.M.McallbackWithString;
import com.theora.M.Mcontroller;
import com.theora.M.Mdate;
import com.theora.M.Mtime;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
/*------------------------------------------------------------*/
public class NewSpot extends Activity {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private int latitude;
	private int longitude;
	private int zoomLevel;
	private boolean isCompass;
	private boolean isSatellite;
	private boolean isTraffic;
	SpotsUtils utils = null;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new SpotsUtils(this);
		utils.setBackground();
		setTitle("Labeling a Spot");
		
		
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
	    Intent i = getIntent();
	    Bundle extras = i.getExtras();
	    if ( extras == null ) {
			m.utils.logError("extras is null");
	    	m.view.msg("NewSpot: extras is null");
	    	// finish();
	    	return;
	    }
	    latitude = extras.getInt("latitude", 0);
	    longitude = extras.getInt("longitude", 0);
	    zoomLevel = extras.getInt("zoomLevel", 0);
	    isCompass = extras.getBoolean("isCompass", true);
	    isSatellite = extras.getBoolean("isSatellite", true);
	    isTraffic = extras.getBoolean("isTraffic", false);
	    if ( longitude == 0 || latitude == 0 || zoomLevel == 0 ) {
	    	m.view.msg("NewSpot: zero lat/lon/zoom");
	    	// finish();
	    	return;
	    }
		String latDegrees = utils.degreeString(latitude);
		String lonDegrees = utils.degreeString(longitude);

	    String promptText = String.format("Please Enter a Label for this Spot:\n%s,%s", latDegrees, lonDegrees);
	    McallbackWithString cb = new McallbackWithString() {
    		@Override
	    	public void f(String label) {
    			labelEntered(label);
    		}
	    };
	    McallbackWithString carCB = new McallbackWithString() {
    		@Override
	    	public void f(String label) {
    			labelEntered("Car");
    		}
	    };
    	m.view.prompt("Label Spot", promptText, cb, null, "Car", carCB);
	}
	/*------------------------------------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return(new SpotsUtils(this).onCreateOptionsMenu(menu));
	}
	/*------------------------------*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return(new SpotsUtils(this).onOptionsItemSelected(item));
	}
	/*------------------------------------------------------------*/
	private String autoLabel() {
		int today = Mdate.today();
		int ymd[] = Mdate.separate(today);
		String now = Mtime.fmt(Mtime.now());
		String label = String.format("%d/%d/%d %s", ymd[1], ymd[2], ymd[0]%100, now);
		return(label);
	}
	/*-----------------------------------*/
	private void labelEntered(String label) {
		if ( label == null ) {
			m.utils.logInfo("null");
			finish();
			return;
		}
		if ( label.length() == 0 ) {
			m.utils.logInfo("empty");
			label = autoLabel();
		}		
		// if this label is already in generate #2, $3, etc to mmake a unique label
		// but if the label is the Car label, then delete the old Car label if any
		if ( label.compareTo("Car") == 0 ) {
			m.utils.logInfo("Car");
			m.model.sql("delete from spots where label = 'Car'");
		} else {
			m.utils.logInfo("label");
			label = utils.makeNewLabel(label);
		}
		if ( insert(label) ) {
			// m.view.msg(label + ": written");
			finish();
			return;
		} else {
			m.view.msg(label + ": Failed");
			m.utils.logError("Insert Failed");
			return;
		}	
	}
	/*------------------------------------------------------------*/
	private boolean insert(String label) {
		return(utils.insert(label, latitude, longitude, zoomLevel, isCompass,  isSatellite,  isTraffic) > 0);
	}
	/*------------------------------------------------------------*/
}
