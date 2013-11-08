package com.theora.sudoku;

import com.theora.M.Mcontroller;
import com.theora.sudoku.R;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;

public class Sudoku extends Activity implements OnClickListener {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private Music music = null;
	private SudokuUtils utils;
	/*------------------------------------------------------------*/
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "sudoku");
		music = new Music(this);
		utils = new SudokuUtils(this);
		setContentView(R.layout.main);
		utils.createHistoryTable();

		// Set up click listeners for all the buttons
		View continueButton = findViewById(R.id.continue_button);
		continueButton.setOnClickListener(this);
		View newButton = findViewById(R.id.new_button);
		newButton.setOnClickListener(this);
		View aboutButton = findViewById(R.id.about_button);
		aboutButton.setOnClickListener(this);
		View historyButton = findViewById(R.id.history_button);
		historyButton.setOnClickListener(this);
		View settingsButton = findViewById(R.id.settings_button);
		settingsButton.setOnClickListener(this);
		View statsButton = findViewById(R.id.stats_button);
		statsButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		m.utils.log("Sudoku: onCreate");
		m.utils.logInfo("Starting");
	}

	/*------------------------------------------------------------*/
	@Override
	protected void onResume() {
		super.onResume();
		music.startPlaying(this, R.raw.vang_ant_1);
	}
	/*---------------------------------------------*/
	@Override
	protected void onPause() {
		super.onPause();
		music.stopPlaying();
	}
	/*------------------------------------------------------------*/
	// ...
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.about_button:
			m.utils.logInfo("About");
			i = new Intent(this, About.class);
			startActivity(i);
			break;
		case R.id.stats_button:
			m.utils.logInfo("Stats");
			i = new Intent(this, Statistics.class);
			startActivity(i);
			break;
		case R.id.history_button:
			m.utils.logInfo("History");
			i = new Intent(this, History.class);
			startActivity(i);
			break;
		case R.id.settings_button:
			m.utils.logInfo("Settings");
			i = new Intent(this, Prefs.class);
			startActivity(i);
			break;
		case R.id.continue_button:
			m.utils.logInfo("Continue");
			startGame(SudokuUtils.DIFFICULTY_CONTINUE);
			break;
		case R.id.new_button:
			m.utils.logInfo("New Game");
			openNewGameDialog();
			break;
		case R.id.exit_button:
			m.utils.logInfo("Exit");
			// new History(this).fake();
			finish();
			break;
		}
	}

	/*------------------------------------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return(new SudokuUtils(this).onCreateOptionsMenu(menu));
	}
	/*------------------------------*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return(new SudokuUtils(this).onOptionsItemSelected(item));
	}
	/*------------------------------------------------------------*/
	private void openNewGameDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.new_game_title)
				.setItems(R.array.difficulty,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								startGame(i);
							}
						}).show();
	}

	/*------------------------------------------------------------*/
	private void startGame(int i) {
		if ( i >= 0 )
			m.utils.logInfo(SudokuUtils.difficultyDescription(i));
		Intent intent = new Intent(Sudoku.this, Game.class);
		intent.putExtra(Game.KEY_DIFFICULTY, i);
		startActivity(intent);
	}
	/*------------------------------------------------------------*/
}
