package com.theora.sudoku;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import com.theora.sudoku.R;

/*------------------------------------------------------------*/
public class Prefs extends PreferenceActivity {
	/*
	 * the names "music" and "hints" & "echos" match settings.xml
	 */
	/*------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	/*------------------------------------------------------------*/
	public static boolean getMusic(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("music", true);
	}
	/*------------------------------------------------------------*/
	public static boolean getEchoes(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("echoes", true);
	}
	/*------------------------------------------------------------*/
	public static boolean getHints(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("hints", true);
	}
	/*------------------------------------------------------------*/
	public static boolean getAuto(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean("auto", false);
	}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
