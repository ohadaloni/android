package com.theora.M;

import android.app.Activity;
import android.os.Bundle;
/*------------------------------------------------------------*/
public class MedExport extends Activity {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private MedLogUtils utils = null;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new MedLogUtils(this);
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		resume();
	}
	/*------------------------------------------------------------*/
	private void resume() {
		export();
	}
	/*------------------------------------------------------------*/
	private String exportEmail = null;
	/*---------------------------------------*/
	public void export() {
		exportEmail = utils.getConfig("exportEmail");
		m.view.prompt("Email", "Send Exported CSV file to:", exportEmail, new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s != null && s.length() > 0 ) {
					exportEmail = s;
					utils.setConfig("exportEmail", exportEmail);
				}
				export(exportEmail);
			}	
		});
	}
	/*----------------------------------*/
	public void export(String email) {
		MmodelRow rows[];
		String sql = "select name, strftime('%m/%d/%Y', whence) as date, strftime('%H:%M', whence) as time from medLog order by whence desc";
		rows = m.model.getRows(sql);
		String csv = m.model.toCsv(rows);
		int today = Mdate.today();
		String attachmentFileName = String.format("MedLog-%d.csv", today);
		String subject = "Medication Log CSV";
		String content = "Attached is a comma separated values file for Medication Log.\nThank You for using Medication Log.\n";
		m.utils.email(email, subject, content, csv, attachmentFileName);
		finish();
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}
