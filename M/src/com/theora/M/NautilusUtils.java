package com.theora.M;
/*------------------------------------------------------------*/
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.util.Log;
/*------------------------------------------------------------*/
public class NautilusUtils {
	/*------------------------------------------------------------*/
	public static final int difficultyNone = 0;
	public static final int difficultyBaby = 1 ;
	public static final int difficultyEasy = 2 ;
	public static final int difficultyMedium = 3 ;
	public static final int difficultyHard = 4 ;
	public static final int difficultyAll = 1;
	public static final int difficultyNoBaby = 2;
	public static final int difficultyNoOptions = 3;
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private Activity a = null;
	private int gameId;
	/*------------------------------------------------------------*/
	public NautilusUtils(Activity a, int gameId) {
		this.a = a;
		m = new Mcontroller(a, "mdb");
		this.gameId = gameId;
	}
	/*------------------------------------------------------------*/
	public NautilusQuestionSet parseQuestionSet(String text, int difficulty, int maxNumChoices)  {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			NautilusQuestionSetParser qp = new NautilusQuestionSetParser(a, gameId, difficulty, maxNumChoices);
			xr.setContentHandler(qp);
			InputStream stream = Mutils.stringToInputStream(text);
			xr.parse(new InputSource(stream));
			NautilusQuestionSet questions = qp.getNautilusQuestionSet();
			return(questions);
		} catch (Exception e) {
			log(e.getMessage());
			return(null);
		}

	}
	/*------------------------------------------------------------*/
	public void log(String msg) {
		Log.d("Nautilus", msg);
	}
	/*------------------------------------------------------------*/
	public void createTables() {
//		m.model.sql("drop table if exists nautilusState");
		String sql = "create table if not exists nautilusState (" +
			"id integer primary key autoincrement" +
			", gameId int" +
			", difficulty int" +
			", tellWhenWrongGuess int" +
			", isSound varchar(20)" +
			", epoch int" +
			" )";
		m.model.sql(sql);
	}
	/*------------------------------------------------------------*/
	public void saveGameInfo(int difficulty, boolean isSound, boolean tellWhenWrongGuess) {
		String nv[][] = {
		        { "gameId", "" + gameId },
		        { "difficulty", "" + difficulty },
		        { "isSound", isSound ? "true" : "false" },
		        { "tellWhenWrongGuess", tellWhenWrongGuess ? "true" : "false" },
		        { "epoch", "" + Mdate.time() }
		};
		m.model.sql("delete from nautilusState where gameId = " + gameId);
		m.model.insert("nautilusState", nv);
	}
	/*------------------------------------------------------------*/
	public MmodelRow getGameState(int gameId) {
		String sql = "select * from nautilusState where gameId = " + gameId;
		return(m.model.getRow(sql));
	}
	/*------------------------------------------------------------*/
	public String gameSubject(int gameId) {
		String subjects[] = {
				""
				, "Animals"
				, "Actors"
				, "Actresses"
				, "Musicians"
		};
		return subjects[gameId];
	}
	/*----------------------------*/
	public int difficultyType(int gameId) {
		int types[] = {
				0
				, difficultyAll
				, difficultyNoOptions
				, difficultyNoOptions
				, difficultyNoBaby
		};
		return types[gameId];
	}
	/*----------------------------*/
	public int defaultDifficulty(int gameId) {
		int difficulties[] = {
				0
				, difficultyBaby
				, difficultyNone
				, difficultyNone
				, difficultyEasy
		};
		return difficulties[gameId];
	}
	/*----------------------------*/
	public String gameName(int gameId) {
		String names[] = {
				""
				, "Animals"
				, "Actors"
				, "Actresses"
				, "Musicians"
		};
		return names[gameId];
	}
	/*----------------------------*/
	public String backgroundUrl(int gameId) {
		String  bgFiles[] = {
				null
				, "grasslands.jpg"
				, "hollywood.jpg"
				, "hollywood.jpg"
				, "guitars.jpg"
		};
		String bgFile = bgFiles[gameId];
		String ret =  Nautilus.serverUrl + "/images/" + bgFile;
		return(ret);
	}
	/*----------------------------*/
	public String guessDialogTitle(int gameId, int numAnsewered) {
		String guessDialogTitles[] = {
				""
				, "What is this Animal?"
				, "Who is this Actor?"
				, "Who is this Actress?"
				, "Who is this Musician?"
		};
		String num = "" + (numAnsewered + 1) + ". ";
		return num + guessDialogTitles[gameId];
	}
	/*------------------------------------------------------------*/
	private final String difficultyDescriptions[] = {
		""
		, "Baby"
		, "Easy"
		, "Medium"
		, "Hard"
	};
	/*--------------------------------*/
	public String[] difficultyDescriptions(int difficultyType) {
		int start, len;
		if ( difficultyType == difficultyNoBaby ) {
			start = 2;
			len = 3;
		} else {
			start = 1;
			len = 4;
		}
		String ret[] = new String[len];
		for(int i=0;i<len;i++)
			ret[i] = difficultyDescriptions[start+i];
		return(ret);
		
	}
	/*--------------------------------*/
	public String difficultyDescription(int difficulty) {
		return difficultyDescriptions[difficulty];
	}
	/*--------------------------------*/
	public int difficulty(String s) {
		for(int i=0;i<difficultyDescriptions.length;i++)
			if ( difficultyDescriptions[i].equals(s) )
				return(i);
		return(0);
	}
	/*------------------------------------------------------------*/
}
