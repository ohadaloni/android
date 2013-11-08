package com.theora.M;

import com.theora.M.Mview.Modifier;
import com.theora.M.Mview.RowClickListener;
import com.theora.M.Mview.RowLongPressListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/*------------------------------------------------------------*/
public class MedHistory extends Activity implements RowClickListener, RowLongPressListener {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private MedLogUtils utils = null;
	private String med;
	private boolean isNag;
	private boolean alreadyNagged;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new MedLogUtils(this);
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if ( extras == null ) {
			m.utils.logError("extras is null");
			finish();
			return;
		}
		med = extras.getString(MedLogUtils.medName);		
		isNag = extras.getBoolean(MedLogUtils.isNag);		
		utils.startInAppNagger();
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		resume();
	}
	/*------------------------------------------------------------*/
	private void resume() {
		String dbStr = m.model.str(med);
		String nameCond = String.format("name = '%s'", dbStr);
		String fields = "id, whence";
//		String fields = "id, whence, whence as raw";
		
		String sql = "select " + fields + " from medLog where " + nameCond + "order by whence desc" ;
		MmodelRow rows[] = m.model.getRows(sql);
		if ( rows == null || rows.length == 0 ) {
			// not reached
			utils.help(new Mcallback() {
				@Override
				public void f() {
					finish();
				}
			});
			return;
		}
		m.view.reset();
		m.view.setRowClickListener(this);
		m.view.setRowLongPressListener(this);
		m.view.setVerticalScrolling();
		m.view.registerModifier("whence", new Modifier() {
			@Override
			public String modify(String value) {
				return(utils.timeFmt(value));
			}
		});
		m.view.setNoHeaders();
		m.view.showRows(rows, med, Mview.LARGE);
		if ( isNag && ! alreadyNagged ) {
			alreadyNagged = true;
			utils.nag(med);
		}
	}
	/*------------------------------------------------------------*/
	@Override
	public void rowClicked(int rowId) {
		MmodelRow row = m.model.getById("medLog", rowId);
		String med = row.getString("name");
		utils.dupMed(med);
	}
	/*------------------------------------------------------------*/
	@Override
	public void rowLongPressed(final int rowId) {
		final String medDescription = utils.medDescription(rowId);
		final String choices[] = {
			"Delete"
			, "Set Date"
			, "Set Time"
		};
		McallbackWithString after = new McallbackWithString() {
			@Override
			public void f(String s) {
				int i = Mutils.indexOf(s, choices);
				switch ( i ) {
				case 0:
					delete(rowId);
					break;
				case 1:
					setDate(rowId);
					break;
				case 2:
					setTime(rowId);
					break;
				}
			}
		};
		m.view.select(medDescription, choices, after);
	}
	/*------------------------------------------------------------*/
	private void delete(final int rowId) {
		utils.deleteEntry(rowId, new McallbackWithInt() {
			@Override
			public void f(int numLeft) {
				if ( numLeft > 0 )
					resume();
				else
					finish();
			}
		});
	}
	/*------------------------------------------------------------*/
	private void setDate(final int rowId) {
		final MmodelRow row = m.model.getById("medLog", rowId);
		final String whence = row.getString("whence");
		final String dt[] = whence.split(" ");
		final int date = Mdate.undash(dt[0]);
		final String time = dt[1] ;
		McallbackWithInt gotDate = new McallbackWithInt() {
			@Override
			public void f(int n) {
				if ( n == 0 )
					return;
				String whence = Mdate.dash(n) + " " + time;
				String nv[][] = {
					{ "whence", whence }
				};
				m.model.update("medLog", rowId, nv);
				utils.updateLastTaken(row.getString("name"));
				resume();
			}
		};
		m.view.selectDate(utils.medDescription(row), date, gotDate);
	}
	/*------------------------------------------------------------*/
	private void setTime(final int rowId) {
		final MmodelRow row = m.model.getById("medLog", rowId);
		final String whence = row.getString("whence");
		final String dt[] = whence.split(" ");
		final String date = dt[0];
		final int time = Mtime.scan(dt[1]);
		McallbackWithInt gotTime = new McallbackWithInt() {
			@Override
			public void f(int n) {
				if ( n < 0 )
					return;
				String whence = date + " " + Mtime.dbFmt(n);
				String nv[][] = {
					{ "whence", whence }
				};
				m.model.update("medLog", rowId, nv);
				utils.updateLastTaken(row.getString("name"));
				resume();
			}
		};
		m.view.selectTime(utils.medDescription(row), time, gotTime);
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}
