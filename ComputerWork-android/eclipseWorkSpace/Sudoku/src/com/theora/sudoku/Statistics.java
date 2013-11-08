package com.theora.sudoku;
import com.theora.M.*;
import com.theora.M.Mview.RowClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/*------------------------------------------------------------*/
public class Statistics extends Activity implements RowClickListener {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private SudokuUtils utils;
	/*------------------------------------------------------------*/
	public Statistics() {
		m = new Mcontroller(this, "sudoku");
		utils = new SudokuUtils(this);
	}
	/*------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if statistics is shown from onCreate
		// then getting back to statistics show the previous statistics
		// OnResume is called after onCreate, but also when coming back
	}
	/*------------------------------------------------------------*/
	@Override
	protected void onResume() {
		super.onResume();
		if ( ! show() )
			finish();
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
	private boolean show() {
		int total = m.model.rowNum("history");
		if ( total < 0 )
			m.utils.msg("No history table");
		if ( total == 0 )
			m.utils.msg("No history");
		if ( total <= 0 )
			return(false);
		String selCount = "select count(*) from history ";
		String winCond = " win = 'Won' ";
		String looseCond = " win = 'Lost' ";
		String where = " where ";
		String and = " and ";
		String easyCond = " difficulty = 0 ";
		String mediumCond = " difficulty = 1 ";
		String hardCond = " difficulty = 2 ";
		int totalWins = m.model.getInt(selCount + where + winCond );
		int totalLosses = m.model.getInt(selCount + where + looseCond );
		int totalPercent = (totalWins * 100) / total;
		int easyWins = m.model.getInt(selCount + where +  easyCond + and + winCond );
		int easyLosses = m.model.getInt(selCount + where +  easyCond + and + looseCond );
		int easyGames =  easyWins + easyLosses;
		int easyPercent = ( easyGames > 0 ) ? (easyWins * 100) / easyGames : 0 ;
		int mediumWins = m.model.getInt(selCount + where +  mediumCond + and + winCond );
		int mediumLosses = m.model.getInt(selCount + where +  mediumCond + and + looseCond );
		int mediumGames =  mediumWins + mediumLosses;
		int mediumPercent = ( mediumGames > 0 ) ? (mediumWins * 100) / mediumGames : 0 ;
		int hardWins = m.model.getInt(selCount + where +  hardCond + and + winCond );
		int hardLosses = m.model.getInt(selCount + where +  hardCond + and + looseCond );
		int hardGames =  hardWins + hardLosses;
		int hardPercent = ( hardGames > 0 ) ? (hardWins * 100) / hardGames : 0 ;
		
		m.model.sql("drop table if exists stats");
		String crt = "create table stats (" +
				"id integer primary key autoincrement" +
	    		", difficulty int" +
	    		", Level varchar(20)" +
	    		", Games int" +
	    		", Won int" +
	    		", Lost int" +
	    		", Percent int" +
	    		" )";
		
		if ( ! m.model.sql(crt) )
			return(false);
		insert(-1, "Total", total, totalWins, totalLosses, totalPercent);
		insert(0, "Easy", easyGames, easyWins, easyLosses, easyPercent);
		insert(1, "Medium", mediumGames, mediumWins, mediumLosses, mediumPercent);
		insert(2, "Hard", hardGames, hardWins, hardLosses, hardPercent);

		MmodelRow rows[] = m.model.getRows("select * from stats order by id");	
		m.view.reset();
		m.view.rightAlign("Percent");
		m.view.hide("difficulty");
		m.view.setRowClickListener(this);
		m.view.setVerticalScrolling();
		m.view.showRows(rows, "Sudoku Game Statistics", Mview.MEDIUM);
		return(true);
	}
	/*------------------------------------------------------------*/
	private void insert(int difficulty, String level, int games, int won, int lost, int percent) {
		m.model.sql("insert into stats ( difficulty, Level, Games, Won, Lost, Percent ) values " +
				String.format("( %d, '%s', %d, %d, %d, '%s' )", difficulty, level, games, won, lost, "" + percent + '%'));
		
	}
	/*------------------------------------------------------------*/
	public void rowClicked(int id) {
		int difficulty = m.model.getInt("select difficulty from stats where id = "+ id);
		if ( difficulty < 0)
			return; // clicked Total, but also if getInt failed
		int numGames = utils.numGames(difficulty);
		if ( numGames > 0 ) {
			Intent i = new Intent(this, History.class);
			i.putExtra("difficulty", difficulty);
			startActivity(i);
			finish();
		} else {
			String descr = SudokuUtils.difficultyDescription(difficulty);
			m.utils.msg("No history of " + descr + " Games");
		}
	}
}
/*------------------------------------------------------------*/
