package com.theora.sudoku;
import com.theora.M.*;
import com.theora.M.Mview.RowClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/*------------------------------------------------------------*/
public class History extends Activity implements RowClickListener {
	/*------------------------------------------------------------*/
	private Activity a = null;
	private Mcontroller m = null;
	 private SudokuUtils utils;
	/*------------------------------------------------------------*/
	public History() {
		this.a = this;
		m = new Mcontroller(a, "sudoku");
		utils = new SudokuUtils(this);
	}
	/*------------------------------------------------------------*/
	public History(Activity a) {
		this.a = a;
		m = new Mcontroller(a, "sudoku");
	}
	/*------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m.util.msg("History: onCreate");
		createTable();
		if ( ! show() )
			finish();
	}
	/*------------------------------------------------------------*/
	@Override
	protected void onResume() {
		super.onResume();
		// m.util.msg("History: onResume");
		if ( ! show() )
			finish();
	}
	/*------------------------------------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return(utils.onCreateOptionsMenu(menu));
	}
	/*------------------------------*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = utils.onOptionsItemSelected(item);
		if ( m.model.rowNum("history") == 0 )
			finish();
		return(ret);
	}
	/*------------------------------------------------------------*/
	private boolean show() {
		Intent intent = getIntent();
		String w = "";
		if ( intent != null ) {
			Bundle extras = intent.getExtras();
			if ( extras != null ) {
				int difficulty = extras.getInt("difficulty", -1);
				if ( difficulty >= 0 )
					w = "where difficulty = " + difficulty;
					String difficulties[] = { "Easy", "Medium", "Hard" };
					String descr = difficulties[difficulty];
					m.utils.msg("History of " + descr + " Games");
				}
		}
		String fields = "id, id as '#', datetime as 'When', difficultyDescription as 'Level', tilesLeft as 'Todo', corner as Sig";
		String sql = "select " + fields + " from history " + w + " order by id desc limit 30";
		m.model.reset();
		m.model.setType("Sig", Mmodel.type_string);
		MmodelRow[] rows = m.model.getRows(sql);
		if ( rows == null || rows.length == 0 ) {
			if ( rows == null )
				m.utils.msg("No History");
			else
				m.utils.msg("No Games");
			return(false);
		}
		m.view.reset();
		m.view.setVerticalScrolling();
		m.view.setRowClickListener(this);
		m.view.registerModifier("When", new Mview.Modifier() {
			public String modify(String datetime) {
				return(formatDateTime(datetime));
			}
		});
		m.view.registerModifier("Todo", new Mview.Modifier() {
			public String modify(String left) {
				if ( left.compareTo("0") == 0 )
					return("Won");
				else
					return(left);
			}
		});
		
		m.utils.msg("Click line to continue game");
		m.view.showRows(rows, "Sudoku Game History", Mview.MEDIUM);
		return(true);
	}
	/*------------------------------------------------------------*/
	private String formatDateTime(String datetime) {
		String dt[] = datetime.split(" ");
		String ymd[] = dt[0].split("-");
		int m = Integer.parseInt(ymd[1]);
		int d = Integer.parseInt(ymd[2]);
		String hm[] = dt[1].split(":");
		int h = Integer.parseInt(hm[0]);
		String ret = String.format("%d/%d %d:%s", m, d,  h, hm[1]);				
		return ret;	
	}
	/*------------------------------------------------------------*/
	public void rowClicked(int id) {
		MmodelRow row = m.model.getById("history", id);
		String difficulty = row.getValue("difficultyDescription");
		m.utils.logInfo("Continue " + difficulty + " Game");
		Intent intent = new Intent(History.this, Game.class);
		intent.putExtra(Game.GAME_ID, id);
		startActivity(intent);
	}
	/*------------------------------------------------------------*/
	public void fake() {
		utils.eraseHistory();
		for(int i=0;i<17;i++)
			fake(i);
		m.utils.msg("History now Faked");
	}
	/*------------------------------*/
	private void fake(int i) {
		int difficulty = rand(0, 2);
		int tilesLeft = fakeTilesLeft(difficulty);
		String startingPuzzle = new SudokuMaker(this).make(difficulty);
		String sql = String.format("insert into history ( " +
			" datetime, start, state, difficulty, difficultyDescription, win, selX, selY, tilesLeft, corner )  " +
			"values ( '%s %s', '%s', '%s', %d, '%s', '%s', %d, %d, %d, '%s' )",
				Mdate.dash(fakeDate()),
				Mtime.fmt(fakeTime()),
				startingPuzzle,
				startingPuzzle,
				difficulty,
				SudokuUtils.difficultyDescription(difficulty),
				tilesLeft == 0 ? "Won" : "Lost",
				rand(0, 8),
				rand(0, 8),
				tilesLeft,
				corner(startingPuzzle)
			);
		m.utils.log(sql);
		m.model.sql(sql);	

	}
	/*------------------------------*/
	// alias rand Mutils.rand
	private int rand(int n, int m) {
		return(Mutils.rand(n, m));
	}
	/*------------------------------*/
	private int fakeDate() {
		int y = 2011;
		int m = rand(1, 4);
		int d = rand(1, 28);
		return(Mdate.compose(y, m, d));
	}
	/*------------------------------*/
	private int fakeTime() {
		int h = rand (0, 23);
		int m = rand(0, 59);
		return(h*100+m);
	}
	/*------------------------------*/
	private int fakeTilesLeft(int difficulty) {
		if ( difficulty == 0 ) {
			if ( rand(1, 100) < 70 )
				return(0);
			else
				return(rand(18, 36));
		} else if ( difficulty == 1 ) {
			if ( rand(1, 100) < 50 )
				return(0);
			else
				return(rand(7, 40));
		} else {
			if ( rand(1, 100) < 30 )
				return(0);
			else
				return(rand(24, 40));
		}
	}
	/*------------------------------*/
	private String corner(String p) {
		return(SudokuUtils.corner(p));
	}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
