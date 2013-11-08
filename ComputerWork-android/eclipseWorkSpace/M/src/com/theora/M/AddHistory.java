package com.theora.M;

import com.theora.M.Mcontroller;
import com.theora.M.Mview.RowClickListener;
import com.theora.M.Mview.RowLongPressListener;

import android.app.Activity;
import android.os.Bundle;

public class AddHistory extends Activity implements RowClickListener, RowLongPressListener {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private AddUtils utils = null;
	private static final int maxRows = 40;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new AddUtils(this);	
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		resume();
	}
	/*------------------------------------------------------------*/
	private void resume() {
        m.utils.logInfo();
		String sql = "select id, name, decimals from addInstances order by id desc limit " + maxRows;
		MmodelRow[] rows = m.model.getRows(sql);
		for(MmodelRow row : rows ) {
			int instanceId = row.id();
			int decimals = row.getInt("decimals");
			double total = utils.total(instanceId);
			String sTotal = Mview.format(total, decimals);
			row.push("total", sTotal, Mmodel.type_string);
		}
		m.view.reset();
		m.view.hide("decimals");
		m.view.rightAlign("total");
		m.view.setRowClickListener(this);
		m.view.setRowLongPressListener(this);
		m.view.showRows(rows, Mview.sizeFromRows(rows.length));
	}
	/*------------------------------------------------------------*/
	@Override
	public void rowLongPressed(final int rowId) {
        m.utils.logInfo();
		MmodelRow row = m.model.getById("addInstances", rowId);
		final String name = row.getValue("name");
		int decimals = row.getInt("decimals");
		double total = utils.total(rowId);
		final String sTotal = Mview.format(total, decimals);
		String title;
		if ( name != null && name.length() != 0 )
			title = name + ": " + sTotal;
		else
			title = sTotal;
		String opts[] = {"Rename", "Delete", "Go To", "Never Mind" };
		m.view.select(title, opts, new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null || s.length() == 0 )
					return;
				else if ( s.compareTo("Rename") == 0 )
					renameInstance(rowId, name);
				else if ( s.compareTo("Delete") == 0 )
					utils.deleteInstance(rowId);
				else if ( s.compareTo("Go To") == 0 ) {
			        m.utils.logInfo("Go To");
					setResult(rowId);
					finish();
					return;
				} else if ( s.compareTo("Never Mind") == 0 )
					return;
				else
					m.utils.logError(s);
				resume();
			}
		});
	}
	/*------------------------------------------------------------*/
	private void renameInstance(final int instanceId, final String name) {
        m.utils.logInfo();
		String defaultValue;
		if ( utils.isAutoName(name) )
			defaultValue = "";
		else
			defaultValue = name;
		m.view.prompt(name, "Rename " + name, defaultValue, new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null || s.length() == 0 || s.compareTo(name) == 0 )
					return;
				utils.rename(instanceId, s);
				resume();
			}
		});
	}
	/*------------------------------------------------------------*/
	@Override
	public void rowClicked(int rowId) {
		setResult(rowId);
		finish();
	}
	/*------------------------------------------------------------*/
}
