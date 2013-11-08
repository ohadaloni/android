package com.theora.M;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
/*------------------------------------------------------------*/
public class NautilusQuestionSet {
	/*------------------------------------------------------------*/
	private static final String questionSeparator = "\n"; 
	/*------------------------------------------------------------*/
	// data from the fetched xml
	private ArrayList<NautilusQuestion> questions = new ArrayList<NautilusQuestion>();
	private NautilusQuestion currentlyParsingQuestion = null;
	private NautilusQuestionCallBack callWhenReady = null;
	private boolean annulled = false;
	
	/*-------------------------------*/
	private Activity a = null;
	@SuppressWarnings("unused")
	private Mcontroller m = null;
	private NautilusUtils nutils = null;
	private int gameId;
	private int difficulty;
	private int maxNumChoices;
	/*------------------------------------------------------------*/
	/**
	 * create an empty question set for the parser to fill
	 */
	public NautilusQuestionSet(Activity a, int gameId, int difficulty, int maxNumChoices) {
		this.a = a;
		this.gameId = gameId;
		this.difficulty = difficulty;
		this.maxNumChoices = maxNumChoices;
		m = new Mcontroller(a, "mdb");
		nutils = new NautilusUtils(a, gameId);
	}
	/*--------------------------------------*/
	/**
	 * create a question set from a string created by NautilusQuestionSet.toString() (not used)
	 */
//	public NautilusQuestionSet(Activity a, int gameId, int difficulty, String from) {
//		this(a, gameId, difficulty, maxNumChoices);
//		String questionsStrings[] = from.split(questionSeparator);
//		for( String questionsString : questionsStrings )
//			if ( questionsString.length() > 0 )
//				// at the end there is a newline with no question after it
//				questions.add(new NautilusQuestion(a, gameId, difficulty, questionsString, 0));
//	}
	/*------------------------------------------------------------*/
	/**
	 * set the fileName of the currently parsed question
	 */
	public void setFileName(String fileName) {
		if (currentlyParsingQuestion == null ) {
			log("setFileName: no question");
		}
		currentlyParsingQuestion.setfileName(fileName);
	}
	/*------------------------------------------------------------*/
	/**
	 * add an option to the currently parsed question
	 */
	public void addOption(String fileName) {
		if (currentlyParsingQuestion == null ) {
			log("setFileName: no question");
		}
		currentlyParsingQuestion.addOption(fileName);
	}
	/*------------------------------------------------------------*/
	/**
	 * start adding a new currently parsed question
	 */
	public void startAddingQuestion() {
		currentlyParsingQuestion = new NautilusQuestion(a, gameId, difficulty, maxNumChoices);
	}
	/*------------------------------------------------------------*/
	/**
	 * end adding a new currently parsed question
	 */
	public void endAddingQuestion() {
		questions.add(currentlyParsingQuestion);
		currentlyParsingQuestion = null;
	}
	/*------------------------------------------------------------*/
	private void annulDupNames() {
		ArrayList<String> names = new ArrayList<String>();
		Iterator<NautilusQuestion> itr = questions.iterator();
		while(itr.hasNext()) {
			NautilusQuestion question = itr.next();
			if ( question.isAnnulled() )
				continue;
			String answer = question.answer();
			if ( names.contains(answer) )
				question.annul();
			else
				names.add(answer);
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * prepare the question set (as might be necessary)
	 * and start loading images
	 * if callWhenReady is set, then callback with the first ready question
	 */
	public void prepare() {
		if ( annulled )
			return;
		annulDupNames();
		
		NautilusQuestionCallBack cb = new NautilusQuestionCallBack() {
			@Override
			public void f(NautilusQuestion nq) {
				if ( annulled )
					return;
				if ( callWhenReady != null ) {
					NautilusQuestionCallBack tmp = callWhenReady;
					callWhenReady = null;
					nq.use();
					tmp.f(nq);
				}
			}			
		};
		Iterator<NautilusQuestion> itr = questions.iterator();
		while(itr.hasNext())
			itr.next().prepare(cb); 
	}
	/*------------------------------------------------------------*/
	/**
	 * call back when the next question is ready.
	 * if no question are left in this test, call with null.
	 * if some question are not ready, call when the first one is
	 */
	public void next(NautilusQuestionCallBack cb) {
		if ( annulled )
			return;
		prepare();
		int unReady = 0;
		Iterator<NautilusQuestion> itr = questions.iterator();
		while(itr.hasNext()) {
			NautilusQuestion nq = itr.next();
			if ( nq.isUsed )
				continue;
			else if ( nq.hasDrawable()) {
				nq.use();
				cb.f(nq);
				return;
			} else
				unReady++;
		}
		if ( unReady > 0 )
			callWhenReady = cb ;
		else
			cb.f(null);
	}
	/*------------------------------------------------------------*/
	/**
	 * the string representation of a question set is parsable back to a set
	 * and can be store in the database for retrieval
	 */
	@Override
	public String toString() {
		String ret = "";
		Iterator<NautilusQuestion> itr = questions.iterator();
		while(itr.hasNext()) {
			NautilusQuestion nq = itr.next();
			ret += nq.toString() + questionSeparator;
		}
		return(ret);
	}
	/*------------------------------------------------------------*/
	private void log(String msg) {
		nutils.log(msg);
	}
	/*------------------------------------------------------------*/
	public void reset() {
		Iterator<NautilusQuestion> itr = questions.iterator();
		while(itr.hasNext())
			itr.next().unUse();		
	}
	/*------------------------------------------------------------*/
	/**
	 * this nqs is no longer in use
	 * do not cb from it
	 */
	public void annul() {
		annulled = true;
		Iterator<NautilusQuestion> itr = questions.iterator();
		while(itr.hasNext())
			itr.next().annul();		

	}
	/*------------------------------------------------------------*/
}
