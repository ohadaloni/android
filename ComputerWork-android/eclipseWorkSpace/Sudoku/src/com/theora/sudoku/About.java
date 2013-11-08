package com.theora.sudoku;

import android.app.Activity;
import android.os.Bundle;

import com.theora.M.McallbackWithBoolean;
import com.theora.M.Mcontroller;
/*------------------------------------------------------------*/
public class About extends Activity {
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private SudokuUtils utils;
	private Mcontroller m = null;
	/*------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		utils = new SudokuUtils(this);
		m = new Mcontroller(this, "sudoku");
		m.utils.logInfo();
		String aboutTitle = m.utils.about();
		String aboutText = "This Sudoko project started as an experiment with" +
				"developing and publishing an android application. " +
				"Its first version was indeed the very first application " +
				"I developed for the android platform. " +
				"By now it is probably one of the most advanced Sudoku games " +
				"in the market.	" +
				"If you find it to be your preferred Sudoku, " +
				"please rate it on the market. Its just a few clicks.";
		McallbackWithBoolean after = new McallbackWithBoolean() {
			public void f(boolean b) {
				if ( b ) {
					m.utils.rateMe();
					m.utils.logInfo("About:RateMe");
				}
				finish();
				
			}		
		};
		m.view.confirm(aboutTitle, aboutText, "Rate This App", "Maybe Later", after);			
	}
	/*------------------------------------------------------------*/
}
