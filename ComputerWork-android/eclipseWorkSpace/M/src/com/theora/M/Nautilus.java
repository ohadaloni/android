package com.theora.M;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
/*------------------------------------------------------------*/
public class Nautilus extends Activity {
	/*------------------------------------------------------------*/
	public static final String theoraUrl  = "http://nautilus.theora.com";
	public static final String pumaUrl  = "http://192.168.1.42/nautilus";
//	public static final String serverUrl  = pumaUrl;
	public static final String serverUrl  = theoraUrl;
	/*--------------------------*/
	private static final int stateInit = 1;
	private static final int stateFailedToGetQuestionSet = 2;
	private static final int stateFailedToParseQuestionSet = 3;
	private static final int stateNoNqs = 4;
	private static final int stateWaitingForQuestion = 5;
	private static final int stateReadyToGuess = 6;
	private static final int stateGuessing = 7;
	private static final int stateGuessed = 8;
	private static final int stateGameOver = 9;
	/*--------------------------*/
	private static final String stateDescriptions[] = {
		""
		, "Starting new game"
		, "Failed to get question set from game server"
		, "Failed to parse question set from game server"
		, "Waiting for question set from game server"
		, "Waiting for next image to arrive"
		, "Ready for next Guess"
		, "Guessing"
		, "Guess Processed"
		, "Game Over"
	};
	/*--------------------------*/
	private Mcontroller m = null;
	private NautilusUtils nutils = null;
	private int gameId;
	private String gameSubject;
	private ImageView iv = null;
	private int difficulty = NautilusUtils.difficultyNone;
	private int difficultyType = 0;
	private boolean isSound = true;
	private int numChoices = 5;
	private int itemsPerGame = 10;
	private Mp3 yippie = null;
	private Mp3 nay = null;
	private boolean tellWhenWrongGuess = false;
	/*--------------------------*/
	private NautilusQuestionSet nqs = null;
	private NautilusQuestion currentQuestion = null;
	private int state = 0 ;
	private int numWrongs = 0;
	private int numRights = 0;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        gameId = extras.getInt("com.theora.Nautilus.gameId");
		m = new Mcontroller(this, "mdb");
		nutils = new NautilusUtils(this, gameId);
		gameSubject = nutils.gameSubject(gameId);
		difficultyType = nutils.difficultyType(gameId);
		difficulty = nutils.defaultDifficulty(gameId);
	    nutils.createTables();
	    yippie = new Mp3(this, "yippie");
	    nay = new Mp3(this, "nay");
	    m.utils.logInfo(gameSubject);
		loadGameInfo();
	    layout();
	    newGame();
	}
	/*------------------------------------------------------------*/
	@Override
	public void onPause() {
		super.onPause();
		nutils.saveGameInfo(difficulty, isSound, tellWhenWrongGuess);
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
	}
	/*------------------------------------------------------------*/
	private void loadGameInfo() {
		MmodelRow row = nutils.getGameState(gameId);
		if ( row == null )
			return;
		difficulty = row.getInt("difficulty");
		isSound = row.getBoolean("isSound");
		tellWhenWrongGuess = row.getBoolean("tellWhenWrongGuess");
	}
	/*------------------------------------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        menu.add(0, 10, 10, "New Game");
        if ( difficultyType != NautilusUtils.difficultyNoOptions)
        	menu.add(0, 20, 20, "Difficulty");
        menu.add(0, 30, 30, "Tell When Wrong Guess");
        menu.add(0, 40, 40, "Sounds On/Off");
        menu.add(0, 50, 50, "Restart Game");
        menu.add(0, 60, 60, "Rate This App");
        menu.add(0, 65, 65, "About");
        menu.add(0, 70, 70, "Tell A friend");
        menu.add(0, 80, 80, "Other Applications");
		return(true);
	}
	/*------------------------------*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 10:
        	newGame();
            return true;
        case 20:
        	setDifficulty();
            return true;
        case 30:
        	m.utils.logInfo("toggleTellWhenWrongGuess: " + (tellWhenWrongGuess ? "off" : "on"));
        	toggleTellWhenWrongGuess();
            return true;
        case 40:
        	m.utils.logInfo("toggleSound: " + (isSound ? "off" : "on"));
        	toggleSound();
            return true;
        case 50:
        	m.utils.logInfo("restartGame");
        	restartGame();
            return true;
        case 60:
        	m.utils.logInfo("Rate It");
        	m.utils.rateMe();
            return true;
        case 65:
        	m.utils.logInfo("About");
        	m.view.about();
            return true;
        case 70:
        	m.utils.logInfo("optionsMenu:tellAFriend");
        	m.utils.tellAFriend();
            return true;
        case 80:
        	m.utils.logInfo("optionsMenu:AppsByOhad");
        	m.utils.AppsByOhad();
            return true;
		}
		return false;
	}
	/*------------------------------------------------------------*/
	private void toggleTellWhenWrongGuess() {
		tellWhenWrongGuess = ! tellWhenWrongGuess ;
	}
	/*--------------------------------*/
	private void playedWith(String artist) {
		if ( currentQuestion == null )
			return;
		String encoded = artist.replace(' ', '+');
		String serverUrl = "http://www.RockFamilyTrees.theora.com/" ;
		String query = "action=androidShowArtist&artist=" + encoded;
		String urlString = serverUrl + "?" + query ;
		m.utils.Browse(urlString, "Rock Family Trees");
	}
	/*------------------------------------------------------------*/
	private void wikipedia(String answer) {
		String encAnswer = answer.replace(' ', '+');
		String urlString = "http://www.wikipedia.org/w/index.php?" +
				"title=Special%3ASearch&search=" + encAnswer;
		nutils.log(urlString);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(urlString));
		startActivity(i);
	}
	/*------------------------------------------------------------*/
	private void setDifficulty() {
		McallbackWithString cb = new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s != null )
					setDifficulty(nutils.difficulty(s));
			}		
		};
		String choices[] = nutils.difficultyDescriptions(difficultyType);
		m.view.select("Set Game Difficulty", choices, cb);
	}
	/*-----------------------------*/
	private void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
		newGame();
	}
	/*------------------------------------------------------------*/
	private void layout() {
    	iv = new ImageView(this);
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageClicked();
			}
		};	
    	iv.setOnClickListener(listener);
    	iv.setBackgroundColor(Mview.blue);
    	String backgroundUrl = nutils.backgroundUrl(gameId);
    	McallbackWithDrawable cb = new McallbackWithDrawable() {
			@Override
			public void f(Drawable d) {
				if ( d != null )
					iv.setBackgroundDrawable(d);
			}
    	};
		Mblob bgBlob = new Mblob(this, backgroundUrl);
		bgBlob.get(cb);
		setContentView(iv);
	}
	/*------------------------------------------------------------*/
	private void reset() {
		setState(stateInit);
		if ( nqs != null)
			nqs.annul();
		nqs = null;
		currentQuestion = null;
		numWrongs = 0;
		numRights = 0;
	}
	/*--------------------------*/
	private void newGame() {
		String name = nutils.gameName(gameId);	
		String diffDescr = nutils.difficultyDescription(difficulty);	
		m.utils.logInfo(name + ":" + diffDescr);
		setTitle("Starting new Game");
		reset();
		int itemsToGet = itemsPerGame * 2;
		String query = String.format("gameId=%d&numItems=%d&difficulty=%d&numChoices=%d",
					gameId, itemsToGet, difficulty, numChoices * 2);
		// getUrlText() is caching and will returned
		// immediately with old data
		// if the url is the same, so randomize it
		String random = "&rand=" + Mutils.rand(1, 999999);
		String urlString = serverUrl + "/index.php?" + query  + random;
		McallbackWithString cb = new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null ) {
					setState(stateFailedToGetQuestionSet);
					String msg = "Failed to get question set from Game Server.";
					setTitle(msg);
					m.utils.logError(msg);
					return;
				}
				nqs = nutils.parseQuestionSet(s, difficulty, numChoices);
				if ( nqs == null) {
					setState(stateFailedToParseQuestionSet);
					String msg = "Failed to parse question set from Game Server.";
					setTitle(msg);
					m.utils.logError(msg);
					return;
				}
				next();
			}
		};
		setState(stateNoNqs);
		m.utils.getUrlText(urlString, cb);
	}
	/*------------------------------------------------------------*/
	public void imageClicked() {
		switch ( state ) {
			case stateInit :
					msg(stateDescriptions[state]);
		        break;
			case stateFailedToGetQuestionSet :
			case stateFailedToParseQuestionSet :
					msg("Restarting Game");
					newGame();
		        break;
			case stateNoNqs :
				msg(stateDescriptions[state]);
		        break;
			case stateWaitingForQuestion :
				msg(stateDescriptions[state]);
		        break;
			case stateReadyToGuess :
					guess();
		        break;
			case stateGuessing :
				// do nothing, the select dialog is already open
	        break;
			case stateGuessed :
				next();
	        break;
			case stateGameOver :
					gameOver();
		        break;
		    default:
		    		String msg = "Unknown game state";
		    		m.utils.logError(msg);
					msg(msg);
		        break;
		}
	}
	/*------------------------------------------------------------*/	
	private void guess() {
		final String correct = currentQuestion.answer();
		String choices[] = currentQuestion.choices();
		McallbackWithString cb = new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null ) {
					setState(stateReadyToGuess);
					return;
				}
				setState(stateGuessed);
				if (s.equals(correct) )
					yippie();
				else
					nay();
			}			
		};
		String title = nutils.guessDialogTitle(gameId, numWrongs + numRights);
		setState(stateGuessing);
		m.view.select(title, choices, cb);
	}
	/*------------------------------------------------------------*/
	/*
	 * next item requested
	 * get the next question and show it
	 */
	private void next() {
		NautilusQuestionCallBack cb = new NautilusQuestionCallBack() {
			@Override
			public void f(NautilusQuestion nq) {
				setQuestion(nq);
			}
		};
		setState(stateWaitingForQuestion);
		nqs.next(cb);
	}
	/*------------------------------------------------------------*/
	private void setState(int state) {
		this.state = state;
		setTitle(stateDescriptions[state]);
	}
	/*------------------------------------------------------------*/
	private void setQuestion(NautilusQuestion nq) {
		if ( nq == null ) {
			m.utils.msg("No more questions");
			gameOver();
			return;
		}
		Drawable d = nq.drawable();
		if ( d == null ) {
			String msg = nq.answer() + " has no image";
			m.utils.logError(msg);
			next();
			return;
		}
		currentQuestion = nq;
		iv.setImageDrawable(d);
		setState(stateReadyToGuess);
		String title = nutils.guessDialogTitle(gameId, numWrongs + numRights);
		setTitle(title);
	}
	/*------------------------------------------------------------*/
	private void gameOver() {
		setState(stateGameOver);
		int questions = numRights + numWrongs;
		int missing = itemsPerGame - questions;
		String detail = nutils.gameName(gameId);
        if ( difficultyType != NautilusUtils.difficultyNoOptions) {
        	String diffDescr = nutils.difficultyDescription(difficulty);
        	detail += ":" + diffDescr;
        }
		m.utils.logInfo(detail);

		String title = "Game Over";
		String msg;
		if ( missing > 0 ) {
			msg = String.format("All: %d, Right: %d, Wrong: %d, Missing: %d",
					itemsPerGame, numRights, numWrongs, missing);
			m.utils.logError("missing " + missing);
		} else
			msg = "You got " + numRights + " out of " + questions + " questions right";
		
		String titleString = title + " (" + msg + ")";
		setTitle(titleString);
		Mcallback afterAlert = new Mcallback() {
			@Override
			public void f() {
				afterGameOverAlert();
			}
		};
		m.view.alert(title, msg, afterAlert);
	}
	/*------------------------------------------------------------*/
	private String newGame = "New Game";
	private String restart = "Restart Same Game";
	private String higher = "Higher Difficulty";
	private String tell = "Tell a Friend";
	private String otherApps = "Other Applications";
	/*--------------------------------*/
	private void afterGameOverAlert() {
		String choicesWithHigher[] = {
				newGame, restart, higher, tell, otherApps
		};
		String choicesWithoutHigher[] = {
				newGame, restart, tell, otherApps
		};
		String nonFinalChoices[];
		if ( difficultyType == NautilusUtils.difficultyNoOptions )
			nonFinalChoices = choicesWithoutHigher;
		else
			nonFinalChoices = choicesWithHigher;
		
		final String[] choices = nonFinalChoices;
		McallbackWithString after = new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null )
					return;
				if ( s.equals(newGame))
					newGame();
				else if ( s.equals(restart))
					restartGame();
				else if ( s.equals(higher)) {
					if ( difficulty < NautilusUtils.difficultyHard ) {
						m.utils.logInfo("Higher Difficulty");
						difficulty++;
						newGame();
					} else {
						m.utils.logInfo("No Higher Difficulty");
						m.view.msg("This is as hard as it gets");
					}
				}
				else if ( s.equals(tell)) {
		        	m.utils.logInfo("gameOver:tellAFriend");
		        	m.utils.tellAFriend();
				}
				else if ( s.equals(otherApps)) {
					m.utils.logInfo("gameOver:appsByOhad");
					m.utils.AppsByOhad();
				}
			}
		};
		m.view.select("What's Next?", choices, after);
	}
	/*------------------------------------------------------------*/
	private boolean gameIsOver() {
		int questionsAnswered = numRights + numWrongs;
		return(questionsAnswered == itemsPerGame);
	}
	/*------------------------------------------------------------*/
	private void afterGuess() {
		if ( gameIsOver() )
			gameOver();
		else
			next();
	}
	/*------------------------------------------------------------*/
	private void moreInfo(String answer) {
		if ( gameId == 4 )
			playedWith(answer);
		else
			wikipedia(answer);
	}
	/*------------------------------------------------------------*/
	private void nay() {
		numWrongs++;
		if ( isSound )
			nay.play();
		if ( ! tellWhenWrongGuess ) {
			afterGuess();
			return;
		}
		final String answer = currentQuestion.answer();
		McallbackWithBoolean cb = new McallbackWithBoolean() {
			@Override
			public void f(boolean b) {
				if ( b )
					moreInfo(answer);
				afterGuess();
			}			
		};
		m.view.confirm(answer, "See more info?", cb);
	}
	/*------------------------------------------------------------*/
	private void yippie() {
		numRights++;
		if ( isSound )
			yippie.play();
		afterGuess();

	}
	/*------------------------------------------------------------*/
	private void restartGame() {
		m.utils.logInfo();
		currentQuestion = null;
		numRights = numWrongs = 0;
		if ( nqs != null)
			nqs.reset();
		setState(stateInit);
		next();
	}
	/*------------------------------------------------------------*/
	private void toggleSound() {
		isSound = ! isSound;
	}
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private void log(String msg) {
		Log.d("Nautilus", msg);
	}
	/*------------------------------------------------------------*/
	private void msg(String msg) {
		m.view.msg(msg);
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}
