package com.theora.sudoku;

import com.theora.M.AppsByOhad;
import com.theora.M.Mcontroller;
import com.theora.M.Mtime;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
/*------------------------------------------------------------*/
public class SudokuUtils {
	/*------------------------------------------------------------*/
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	protected static final int DIFFICULTY_CONTINUE = -1;
	/*------------------------------------------------------------*/
	private Activity a;
	private Mcontroller m;
	/*------------------------------------------------------------*/
	public SudokuUtils(Activity a) {
		this.a = a;
		m = new Mcontroller(a, "sudoku");
	}
	/*------------------------------------------------------------*/
	public  boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = a.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return(true);
	}
	/*------------------------------------------------------------*/
    public boolean createHistoryTable(){
		// m.model.sql("drop table history");
    	String sql = "create table if not exists history ( " +
    		"id integer primary key autoincrement" +
    		", datetime datetime" +
    		", start varchar(250)" +
    		", state varchar(250)" +
    		", difficulty int" +
    		", difficultyDescription varchar(20)" +
    		", win varchar(20)" +
    		", selX int" +
    		", selY int" +
    		", tilesLeft int" +
    		", corner varchar(10)" +
    		" )";
    	boolean ret = m.model.sql(sql);
    	if ( ! ret )
    		m.utils.msg("Failed to create history table", true);
    	return(ret);
    }
	/*------------------------------------------------------------*/
    public int saveNewGame(String puz, int difficulty) {
    	String nv[][] = {
    			{ "datetime", Mtime.dateTimeNow() }
    			, { "start", puz }
    			, { "state", puz }
    			, { "difficulty", "" + difficulty }
    			, { "difficultyDescription", SudokuUtils.difficultyDescription(difficulty) }
    			, { "win", "Lost" }
    			, { "selX", "5" }
    			, { "selY", "5" }
    			, { "tilesLeft", "" + tilesLeft(puz) }
    			, { "corner", corner(puz) }
    	};
    	int ret = m.model.insert("history", nv);
    	return(ret);
    }
	/*------------------------------------------------------------*/
    public void updateGameState(int gameId, String state, int selX, int selY) {
    	int tilesLeft = tilesLeft(state);
    	String nv[][] = {
    			{ "datetime", Mtime.dateTimeNow() }
    			, { "state", state }
    			, { "win", ( tilesLeft == 0 ) ? "Won" : "Lost" }
    			, { "selX", "" + selX }
    			, { "selY", "" + selY }
    			, { "tilesLeft", "" + tilesLeft }
    	};
    	m.model.update("history", gameId, nv);
    }
	/*------------------------------------------------------------*/
	public static String corner(String puz) {
		String ret = "" ;
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				char c = puz.charAt(i*9+j);
				if ( c != '0' )
					ret += c;
				if ( ret.length() == 4)
					return(ret);
			}
		}
		return(ret);
	}
	/*------------------------------------------------------------*/
    public static int tilesLeft(String puz) {
    	int ret = 0;
    	for(int i=0;i<puz.length();i++)
    		if ( puz.charAt(i) == '0' )
    			ret++;
    	return(ret);
    }
	/*------------------------------------------------------------*/
	public static String difficultyDescription(int d) {
		switch(d) {
		case DIFFICULTY_EASY:
			return("Easy");
		case DIFFICULTY_MEDIUM:
			return("Medium");
		case DIFFICULTY_HARD:
			return("Hard");
		default:
			return("UNK");
		}
	}
	/*------------------------------------------------------------*/
	public  boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings:
				m.utils.logInfo("Settings");
				a.startActivity(new Intent(a, Prefs.class));
				return(true);
			case R.id.resetHistory:
				m.utils.logInfo("resetHistory");
				eraseHistory();
				m.utils.msg("History Reset");
				return(true);
			case R.id.rateMe:
				m.utils.logInfo("rateMe");
				m.utils.rateMe();
				return(true);
			case R.id.restartGame:
				Game g = (Game)a;
				g.restart();
				return(true);
			case R.id.tellAfriend:
				m.utils.logInfo("tellAfriend");
				m.utils.tellAFriend();
				return(true);
			case R.id.appsByOhad:
				m.utils.logInfo("appsByOhad");
				a.startActivity(new Intent(a, AppsByOhad.class));
				return(true);
	}
	return false;
	}
	/*------------------------------------------------------------*/
	public int lastUnfinishedGame() {
		String sql = "select id from history where tilesLeft > 0 order by datetime desc limit 1";
		int ret = m.model.getInt(sql);
		return(ret);
	}
	/*------------------------------------------------------------*/
	public int lastDifficulty() {
		String sql = "select difficulty from history  order by datetime desc limit 1";
		int ret = m.model.getInt(sql);
		return(ret);
	}
	/*------------------------------------------------------------*/
	public void eraseHistory() {
		m.utils.logInfo();
		m.model.sql("delete from history");
	}
	/*------------------------------------------------------------*/
	public int numGames(int difficulty) {
		String sql = "select count(*) from history where difficulty = " + difficulty;
		return(m.model.getInt(sql));
	}
	/*------------------------------------------------------------*/
}
