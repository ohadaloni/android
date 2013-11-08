package com.theora.Spots;

import com.theora.M.*;
import com.theora.M.Mview.Modifier;
import com.theora.M.Mview.RowClickListener;
import com.theora.M.Mview.RowLongPressListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ListSpots extends Activity implements RowClickListener, RowLongPressListener {
	/*------------------------------------------------------------*/
	Mcontroller m = null;
	SpotsUtils utils = null;
	/*------------------------------------------------------------*/
	public ListSpots() {
		m = new Mcontroller(this, "mdb");
		utils = new SpotsUtils(this);
	}
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
	    super.onResume();
	    m.view.initView();
	    resume();
	}
	/*------------------------------------------------------------*/
	private void resume() {
		setTitle("List of Saved Spots");
	    String sql = "select id, label as Label, created as Created from spots where label != '" + SpotsUtils.lastLabel + "' order by label";
	    MmodelRow rows[] = m.model.getRows(sql);
	    if ( rows == null || rows.length == 0 ) {
	    	m.view.msg("You have not saved any spots yet");
	    	finish();
	    	return;
	    }
	    m.view.reset();
	    m.view.setRowClickListener(this);
	    m.view.setRowLongPressListener(this);
		m.view.registerModifier("Created", new Modifier() {
			@Override
			public String modify(String value) {
				return(utils.timeFmt(value));
			}
		});
	    m.view.showRows(rows, "Spots");

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
	@Override
	public void rowLongPressed(final int rowId) {
		String[] actions = { "Go To", "From Here", "Edit", "Adjust", "SMS", "Delete" };
		final String label = utils.label(rowId);
		if ( label.compareTo("Car") == 0 ) {
			
		}
		setTitle("Actions for " + label);
		m.view.select(label, actions, new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null ) {
					return;
				}else if (s.compareTo("Go To") == 0 ) {
					m.utils.logInfo("Go To");
					utils.goTo(rowId);
					finish();
				} else if ( s.compareTo("Edit") == 0 ) {
					m.utils.logInfo("Edit");
					utils.edit(rowId);
				} else if ( s.compareTo("From Here") == 0 ) {
					m.utils.logInfo("From Here");
					Spots.fromHereReqeuest(rowId);
					finish();
				} else if ( s.compareTo("Adjust") == 0 ) {
					m.utils.logInfo("Adjust");
					Spots.adjustOnNextTap(rowId);
					utils.goTo(rowId);
					finish();
				} else if ( s.compareTo("SMS") == 0 ) {
					m.utils.logInfo("SMS");
					utils.sms(rowId);
				} else if ( s.compareTo("Delete") == 0 ) {
					m.utils.logInfo("Delete");
					utils.delete(rowId);
					resume();
				}
			}
		});
	}
	/*------------------------------------------------------------*/
	@Override
	public void rowClicked(int rowId) {
		m.utils.logInfo();
		utils.goTo(rowId);
		finish();
	}
	/*------------------------------------------------------------*/
}
