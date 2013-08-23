package com.theora.sudoku;

import com.theora.M.*;
import com.theora.sudoku.R;
import com.theora.sudoku.SudokuUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
/*------------------------------------------------------------*/
public class Game extends Activity {
	/*------------------------------------------------------------*/
	public static final String KEY_DIFFICULTY = "com.theora.sudoku.difficulty";
	public static final String GAME_ID = "com.theora.sudoku.gameId";
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	SudokuMaker maker = null;
	public Music music = null;
	private int puzzle[];
	private int startingPuzzle[];
	private int difficulty = 1961;
	private PuzzleView puzzleView;
	public int x = -1;
	public int y = -1;
	public int currentCellValue = -1;
	/** Cache of used tiles */
	private final int used[][][] = new int[9][9][];
	private SudokuUtils utils;
	private int gameId;
	/*------------------------------------------------------------*/
	public Game() {
		music = new Music(this);
		m = new Mcontroller(this, "sudoku");
		maker = new SudokuMaker(this);
		utils = new SudokuUtils(this);
	}

	/*------------------------------------------------------------*/
	private boolean setGame(int gameId) {
		MmodelRow row = m.model.getById("history", gameId);
		if ( row == null ) {
			m.utils.msg("Game not found");
			m.utils.logError("Game not found");
			finish();
			return(false);
		}
		this.gameId = gameId;
		startingPuzzle = fromPuzzleString(row.getValue("start"));
		puzzle = fromPuzzleString(row.getValue("state"));
		difficulty = row.getInt("difficulty");
		this.x = row.getInt("selX");
		this.y = row.getInt("selY");
		return(true);
	}
	/*------------------------------------------------------------*/
	@Override
	/*
	 * intentDifficulty is passed in as per the original hello-android
	 * and might be DIFFICULTY_CONTINUE (= -1 )
	 * but this.difficulty always maintains the real difficulty
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		int gameId = i.getIntExtra(GAME_ID, 0);
		int intentDifficulty = i.getIntExtra(KEY_DIFFICULTY, SudokuUtils.DIFFICULTY_EASY);
		if ( gameId > 0 )
			setGame(gameId);
		else if ( intentDifficulty == SudokuUtils.DIFFICULTY_CONTINUE )
			setLastUnfinishedGame();
		else
			setNewGame(intentDifficulty);
		// If the activity is restarted, do a continue next time
		i.putExtra(KEY_DIFFICULTY, SudokuUtils.DIFFICULTY_CONTINUE);		
	}
	/*------------------------------------------------------------*/
	private void setLastUnfinishedGame() {
		int lastUnfinishedGame = utils.lastUnfinishedGame();
		if ( lastUnfinishedGame > 0 )
			setGame(lastUnfinishedGame);
		else {
			m.utils.msg("No unfinished games to continue");
			int difficulty = utils.lastDifficulty();
			if ( difficulty < 0 )
				difficulty = SudokuUtils.DIFFICULTY_EASY;
			setNewGame(difficulty);
		}
	}
	/*------------------------------------------------------------*/
	private void setTheTitle() {
		String descr = SudokuUtils.difficultyDescription(difficulty);
		int tilesLeft = SudokuUtils.tilesLeft(toPuzzleString(puzzle));
		String left;
		if ( tilesLeft == 0 )
			left = "Game Over";
		else if ( tilesLeft == 1 )
			left = "1 tile left";
		else
			left = "" + tilesLeft + " tiles left"; 
		setTitle("Sudoku: " + descr + ": " + left);
	}
	/*------------------------------------------------------------*/
	private void setNewGame(int difficulty) {
		this.difficulty = difficulty;
		String puz = maker.make(difficulty);
		gameId = utils.saveNewGame(puz, difficulty);
		puzzle = fromPuzzleString(puz);
		startingPuzzle = puzzle.clone();
		x = y = 5;
	}
	/*------------------------------------------------------------*/
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		updateGameState();
	}
	/*------------------------------------------------------------*/
	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		setLastUnfinishedGame();
	}
	/*------------------------------------------------------------*/
	@Override
	protected void onResume() {
		super.onResume();
		resume();
	}
	/*---------------------------------------------*/
	private void resume() {
		setTheTitle();
		music.startPlaying(this, R.raw.vang_ant_2);
		calculateUsedTiles();
		puzzleView = new PuzzleView(this);
		setContentView(puzzleView);
		puzzleView.requestFocus();
		puzzleView.invalidate();
	}
	/*---------------------------------------------*/
	private void updateGameState() {
		utils.updateGameState(gameId, toPuzzleString(puzzle), this.x, this.y);
	}
	/*---------------------------------------------*/
	@Override
	protected void onPause() {
		super.onPause();
		updateGameState();
		music.stopPlaying();
	}
	/*------------------------------------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_menu, menu);
		return(true);
	}
	/*------------------------------*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return(utils.onOptionsItemSelected(item));
	}
	/*------------------------------------------------------------*/
	/** Convert an array into a puzzle string */
	static public String toPuzzleString(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for (int element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}

	/*------------------------------------------------------------*/
	/** Convert a puzzle string into an array */
	static public int[] fromPuzzleString(String string) {
		int[] puz = new int[string.length()];
		for (int i = 0; i < puz.length; i++) {
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}

	/*------------------------------------------------------------*/
	/** Return the tile at the given coordinates */
	public int getTile(int x, int y) {
		return puzzle[y * 9 + x];
	}

	/*------------------------------------------------------------*/
	/* starting tile value */
	protected int getStartTile(int x, int y) {
		return startingPuzzle[y * 9 + x];
	}

	/*------------------------------------------------------------*/
	/** Change the tile at the given coordinates */
	private boolean setTile(int x, int y, int value) {
		int n = y * 9 + x;
		if (startingPuzzle[n] == 0) {
			if ( puzzle[n] == value ) {
				if ( value == 0 )
					m.utils.msg(String.format("Cell %d,%d already empty", y+1, x+1));
				else
					m.utils.msg(String.format("Cell %d,%d already is %d", y+1, x+1, value));
				music.playEcho(R.raw.d);
				return(false);
			} else {
				if (value == 0)
					music.playEcho(R.raw.g);
				else
					music.piano("2b");
				puzzle[n] = value;
				setTheTitle();
				return(true);
			}
		} else {
			// not reached
			music.playEcho(R.raw.e_l);
			log("setTile: Trying to set starting tile. Why did we come to this!");
			return(false);
		}
	}

	/*------------------------------------------------------------*/
	/** Return a string for the tile at the given coordinates */
	protected String getTileString(int x, int y) {
		int v = getTile(x, y);
		if (v == 0)
			return "";
		else
			return String.valueOf(v);
	}

	/*------------------------------------------------------------*/
	private boolean gameIsDone() {
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				if (puzzle[i * 9 + j] == 0)
					return (false);
		return (true);
	}

	/*------------------------------------------------------------*/
	private void gameOver() {
		updateGameState();
		String descr = SudokuUtils.difficultyDescription(difficulty);
		m.utils.logInfo(descr);
		puzzleView.congrats();
		String choicesWithHigherDifficulty[] = {
				"New Game"
				, "Higher Difficulty"
				, "See Win/Lose Statistics"
				, "Tell A friend"
				, "Other Applications"
			};
		String choicesWithoutHigherDifficulty[] = {
				"New Game"
				, "See Win/Lose Statistics"
				, "Tell A friend"
				, "Other Applications"
			};
		String nonFinalChoices[];
		if ( difficulty < SudokuUtils.DIFFICULTY_HARD )
			nonFinalChoices = choicesWithHigherDifficulty;
		else
			nonFinalChoices = choicesWithoutHigherDifficulty;
		final String choices[] = nonFinalChoices;
		McallbackWithString after = new McallbackWithString() {
			public void f(String s) {
				if ( s == null )
					return;
				int choice = Mutils.indexOf(s, choices);
				if ( difficulty == SudokuUtils.DIFFICULTY_HARD && choice != 0 )
					choice++;
				switch ( choice ) {
				case 0 :
					m.utils.logInfo("GameOver:newGame");
					setNewGame(difficulty);
					resume();
					break;
				case 1 :
					m.utils.logInfo("GameOver:higherDifficulty");
					setNewGame(difficulty+1);
					resume();
					break;
				case 2 :
					m.utils.logInfo("GameOver:statistics");
					startStatistics();
					finish();
					break;
				case 3 :
					m.utils.logInfo("GameOver:tellAfriend");
					m.utils.tellAFriend();
					break;
				case 4 :
					m.utils.logInfo("GameOver:appByOhad");
					m.utils.AppsByOhad();
					break;
				}
			}			
		};
		m.view.select("Game Over", choices, after);
	}
	/*------------------------------------------------------------*/
	private void startStatistics() {
		startActivity(new Intent(this, Statistics.class));
	}
	/*------------------------------------------------------------*/
	/** Change the tile only if it's a valid move */
	protected boolean setTileIfValid(int x, int y, int value) {
		int tiles[] = getUsedTiles(x, y);
		if (value != 0) {
			for (int tile : tiles) {
				if (tile == value) {
					// not reached
					music.playEcho(R.raw.e_l);
					return false;
				}
			}
		}
		if ( setTile(x, y, value) ) {
			calculateUsedTiles();
			if ( gameIsDone())
				gameOver();
		}
		return true; // false tells PuzzleView this tile is invalid
	}

	/*------------------------------------------------------------*/
	/*
	 * which number is missing from this array of 8 
	 */
	private int notIn(int tiles[]) {
		for(int i=0;i<8;i++)
			if ( tiles[i] != (i + 1))
				return(i+1);
		return(9);
	}
	/*------------------------------*/
	/** Open the keypad if there are any valid moves */
	protected void showKeypadOrError(int x, int y) {
		if (getStartTile(x, y) != 0) {
			m.utils.msg("Don't Want to Change Starting Tile");
			music.playEcho(R.raw.a);
			puzzleView.shake();
			return;
		}
		int tiles[] = getUsedTiles(x, y);
		if (tiles.length == 9) {
			m.utils.msg("No Moves Left");
			music.playEcho(R.raw.e_l);
			puzzleView.shake();
		} else if ( getTile(x, y) == 0 && tiles.length == 8 && Prefs.getAuto(this) ) {
			puzzleView.setSelectedTile(notIn(tiles));
		} else {
			music.playEcho(R.raw.e);
			this.x = x;
			this.y = y;
			this.currentCellValue = puzzle[y*9+x];
			(new Keypad(this, tiles, puzzleView)).show();
		}
	}

	/*------------------------------------------------------------*/
	/** Return cached used tiles visible from the given coords */
	protected int[] getUsedTiles(int x, int y) {
		return used[x][y];
	}

	/*------------------------------------------------------------*/
	/** Compute the two dimensional array of used tiles */
	private void calculateUsedTiles() {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				used[x][y] = calculateUsedTiles(x, y);
			}
		}
	}

	/*------------------------------------------------------------*/
	/** Compute the used tiles visible from this position */
	private int[] calculateUsedTiles(int x, int y) {
		int c[] = new int[9];
		// horizontal
		for (int i = 0; i < 9; i++) {
			if (i == x)
				continue;
			int t = getTile(i, y);
			if (t != 0)
				c[t - 1] = t;
		}
		// vertical
		for (int i = 0; i < 9; i++) {
			if (i == y)
				continue;
			int t = getTile(x, i);
			if (t != 0)
				c[t - 1] = t;
		}
		// same cell block
		int startx = (x / 3) * 3;
		int starty = (y / 3) * 3;
		for (int i = startx; i < startx + 3; i++) {
			for (int j = starty; j < starty + 3; j++) {
				if (i == x && j == y)
					continue;
				int t = getTile(i, j);
				if (t != 0)
					c[t - 1] = t;
			}
		}
		// compress
		int nused = 0;
		for (int t : c) {
			if (t != 0)
				nused++;
		}
		int c1[] = new int[nused];
		nused = 0;
		for (int t : c) {
			if (t != 0)
				c1[nused++] = t;
		}
		return c1;
	}
	/*------------------------------------------------------------*/
	public void restart() {
		m.utils.msg("Restarting Game");
		String descr = SudokuUtils.difficultyDescription(difficulty);
		m.utils.logInfo("Restarting " + descr + " Game");
		for(int i=0;i<81;i++)
			puzzle[i] = startingPuzzle[i];
		calculateUsedTiles();
		setTheTitle();
		puzzleView.invalidate();
	}
	/*------------------------------------------------------------*/
	private void log(String msg) {
		Log.d("Sudoku", msg);
	}
	/*------------------------------------------------------------*/
	public int getDifficulty() {
		return(difficulty);
	}
	/*------------------------------------------------------------*/
}

