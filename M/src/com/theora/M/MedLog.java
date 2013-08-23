package com.theora.M;

import com.theora.M.Mview.Modifier;
import com.theora.M.Mview.RowClickListener;
import com.theora.M.Mview.RowLongPressListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/*------------------------------------------------------------*/
public class MedLog extends Activity implements RowClickListener, RowLongPressListener {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private MedLogUtils utils = null;
	private int numNewMedTries = 0;
	private boolean startedByNagger = false; 
	private boolean naggerHistoryShown = false; 
	private String nagMed = null;
	private boolean hScroll = false;
	static private boolean ampm = false;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new MedLogUtils(this);
		utils.createTables();
		m.utils.logInfo();
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		// sometimes, the activity lives on, even though you back out of it.
		// and so when the nagger starts it, it just 'resumes':
		startedByNagger = isStartedByNagger();
        hScroll = getPreferences(MODE_PRIVATE).getBoolean("hScroll", false);
        ampm = getPreferences(MODE_PRIVATE).getBoolean("ampm", false);
		utils.startInAppNagger();
		resume();
	}
	/*------------------------------------------------------------*/
	public static final int menuItemNewMedication = 5 ;
	public static final int menuItemHelp = 10 ;
	public static final int menuItemExport = 20 ;
	public static final int menuItemHorizontalScrolling = 30 ;
	public static final int menuItemAmPm = 50 ;
	public static final int menuItemDeleteEntire = 60 ;
	public static final int menuItemTellAfriend = 80 ;
	public static final int menuItemAbout = 83 ;
	public static final int menuItemRateMe = 86 ;
	public static final int menuItemOtherApps = 90 ;
	/*------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        menu.add(0, menuItemNewMedication, menuItemNewMedication, "New Medication");
        menu.add(0, menuItemHelp, menuItemHelp, "Help");
        menu.add(0, menuItemDeleteEntire, menuItemDeleteEntire, "Delete Entire History");
        menu.add(0, menuItemHorizontalScrolling, menuItemHorizontalScrolling, "Horizontal Scrolling");
        menu.add(0, menuItemAmPm, menuItemAmPm, "AM/PM");
        menu.add(0, menuItemTellAfriend, menuItemTellAfriend, "Tell A friend");
        menu.add(0, menuItemAbout, menuItemAbout, "About");
        menu.add(0, menuItemRateMe, menuItemRateMe, "Rate This App");
        menu.add(0, menuItemOtherApps, menuItemOtherApps, "Other Applications");
        menu.add(0, menuItemExport, menuItemExport, "Export");
		return(true);
	}
	/*------------------------------*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case menuItemNewMedication:
        	m.utils.logInfo("optionsMenu:newMed");
        	newMed();
            return true;
        case menuItemHelp:
        	m.utils.logInfo("optionsMenu:help");
        	utils.help();
            return true;
        case menuItemDeleteEntire:
        	m.utils.logInfo("optionsMenu:deleteEntireHistory");
        	deleteEntireHistory();
        	resume();
            return true;
        case menuItemHorizontalScrolling:
        	hScroll = ! hScroll;
        	m.utils.logInfo("hScroll " + (hScroll ? "On" : "Off"));
        	resume();
            return true;
        case menuItemAmPm:
			ampm = ! ampm;
        	m.utils.logInfo("optionsMenu:ampm");
        	resume();
            return true;
        case menuItemTellAfriend:
        	m.utils.logInfo("optionsMenu:tellAFriend");
        	m.utils.tellAFriend();
            return true;
        case menuItemAbout:
        	m.utils.logInfo("optionsMenu:About");
        	m.view.about();
            return true;
        case menuItemRateMe:
        	m.utils.logInfo("optionsMenu:RateMe");
        	m.utils.rateMe();
            return true;
        case menuItemOtherApps:
        	m.utils.logInfo("optionsMenu:AppsByOhad");
        	m.utils.AppsByOhad();
            return true;
        case menuItemExport:
        	m.utils.logInfo("optionsMenu:Export");
        	utils.export();
            return true;
		}
		return false;
	}
	/*------------------------------------------------------------*/
	@Override
	public void onPause() {
		super.onPause();
		utils.stopInAppNagger();
        getPreferences(MODE_PRIVATE).edit().putBoolean("hScroll", hScroll).putBoolean("ampm", ampm).commit();
		this.startService(new Intent(this, MedLogNagger.class));
	}
	/*------------------------------------------------------------*/
	private boolean isStartedByNagger() {
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if ( extras == null )
			return(false);
		nagMed = extras.getString(MedLogUtils.nag);
		if ( nagMed == null )
			return(false);
		return(true);
	}
	/*------------------------------------------------------------*/
	private String recentCond() {
		String threeMonthAgo = Mdate.dash(Mdate.subtractMonths(Mdate.today(), 3));
		String cond = "lastTaken > '" + threeMonthAgo + "'";
		return(cond);
	}
	/*------------------------------------------------------------*/
	private void resume() {
		if ( startedByNagger ) {
			if ( ! naggerHistoryShown ) {
				m.utils.logInfo("startedByNagger");
				naggerHistoryShown = true;
				history(nagMed, true);
			} else
				finish();
			return;
		}
		String sql = "select count(*) from medLogMeds where " + recentCond();
		int cnt = m.model.getInt(sql);
		if ( cnt > 0 )
			summary();
		else if ( numNewMedTries > 1 )
			utils.help(new Mcallback() {
				@Override
				public void f() {
					finish();				
				}			
			});
		else {
			numNewMedTries++;
			newMed();
		}
	}
	/*------------------------------------------------------------*/
	private void newMed() {
		startActivity(new Intent(this, NewMed.class));
	}
	/*------------------------------------------------------------*/
	@Override
	public void rowClicked(int rowId) {
		MmodelRow row = m.model.getById("medLogMeds", rowId);
		String med = row.getString("name");
		utils.dupMed(med);
	}
	/*------------------------------------------------------------*/
	private void summary() {
		String fields = "id, name as Medicine, lastTaken as 'Last Taken', nag as Nag";
		String ob = "order by lastTaken desc";
		String cond = recentCond();
		String sql = "select " + fields + " from medLogMeds where " + cond + " " + ob ;
		MmodelRow rows[] = m.model.getRows(sql);
		if ( rows == null ) {
			m.view.msg("No Rows");
			m.utils.logError("No Rows");
			return;
		}
		m.view.reset();
		Modifier dateTimeFormatter = new Modifier() {
			@Override
			public String modify(String value) {
				return(utils.timeFmt(value));
			}		
		};
		Modifier nagFormatter = new Modifier() {
			@Override
			public String modify(String value) {
				return(value.equals("true") ? "*" : "");
			}		
		};
		m.view.registerModifier("Last Taken", dateTimeFormatter);
		m.view.registerModifier("Nag", nagFormatter);
		m.view.setRowClickListener(this);
		m.view.setRowLongPressListener(this);
		if ( ! hScroll )
			m.view.setVerticalScrolling();
		m.view.showRows(rows, Mview.LARGE);
	}
	/*------------------------------------------------------------*/
	@Override
	public void rowLongPressed(int rowId) {
		final MmodelRow row = m.model.getById("medLogMeds", rowId);
		if ( row == null )
			return;
		final String med = row.getString("name");
		final boolean isNag = row.getBoolean("nag");
		final String nagLabel = isNag ? "Don't Nag" : "Nag me" ;
		final String choices[] = {
				"Taking Now"
				, "History"
				, "Change Name"
				, "Delete last entry"
				, nagLabel
				, "Delete Entire History"
		};
		McallbackWithString cb = new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null )
					return;
				int i = Mutils.indexOf(s, choices);
				switch ( i ) {
				case 0 :
					utils.dupMed(med);
					break;
				case 1 :
					history(med);
					break;
				case 2 :
					utils.medEdit(med);
					break;
				case 3 :
					utils.deleteLast(med, new McallbackWithInt() {
						@Override
						public void f(int n) {
							if ( n > 0 )
								history(med);
							else
								resume();
						}	
					});
					break;
				case 4 :
					utils.nag(med, ! isNag);
					resume();
					break;
				case 5 :
					deleteEntireHistory(med);
					break;
				}
			}
		};
		m.view.select(med, choices, cb );
	}
	/*------------------------------------------------------------*/
	private void history(String med) {
		history(med, false);
	}
	/*------------------------------------------------------------*/
	private void history(String med, boolean isNag) {
		Intent i = new Intent(this, MedHistory.class);
		i.putExtra(MedLogUtils.medName, med);
		i.putExtra(MedLogUtils.isNag, isNag);
		startActivity(i);
	}
	/*------------------------------------------------------------*/
	public void deleteEntireHistory() {
		Mcallback onConfirm = new Mcallback() {
			@Override
			public void f() {
				utils.deleteEntireHistory();
				resume();
			}
		};
		m.view.confirm("Delete entire history", "Are you absolutely positive about this?", onConfirm);
	}
	/*------------------------------------------------------------*/
	private void deleteEntireHistory(final String med) {
		Mcallback onConfirm = new Mcallback() {
			@Override
			public void f() {
				utils.deleteEntireHistory(med);
				resume();
			}
		};
		
		m.view.confirm("Delete " + med + "'s entire history", "Are you sure?", onConfirm);
	}
	/*------------------------------------------------------------*/
	static public boolean ampm() {
		return(ampm);
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}
