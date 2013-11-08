package com.theora.M;

import java.util.ArrayList;
import java.util.Iterator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class NautilusQuestion {
	/*-------------------------------*/
	private static final String NoFile = "NoFile";
	private static final String fileSeparator = ", ";
	private static final String imagesUrl = Nautilus.serverUrl + "/images";
	/*-------------------------------*/
	private int gameId;
	private int difficulty;
	private int maxNumChoices;
	/*-------------------------------*/
	private Activity a = null;
	private Mcontroller m = null;
	private NautilusUtils nutils = null;
	/*-------------------------------*/
	private String fileName = null;
	private ArrayList<String> otherOptionsFileNames = new ArrayList<String>();
	
	// after preparation
	boolean isprepared = false;
	String canonicalName = null; // the correct one
	ArrayList<String> canonicalNames = new ArrayList<String>(); 
	private Mblob mblob = null;
	private Drawable d = null;
	boolean isUsed = false; // was this question used
	private boolean annulled = false;
	/*------------------------------------------------------------*/
	/**
	 * create an empty question for the parser to fill
	 */
	protected NautilusQuestion(Activity a, int gameId, int difficulty, int maxNumChoices) {
		this.a = a;
		this.gameId = gameId;
		this.difficulty = difficulty;
		this.maxNumChoices = maxNumChoices;
		m = new Mcontroller(a, "mdb");
		nutils = new NautilusUtils(a, gameId);
	}
	/*-------------------------------*/
	/*
	 * create a question from a save toString (not used)
	 */
//	protected  NautilusQuestion(Activity a, int gameId, int difficulty, String from) {
//		this(a, gameId, difficulty);
//		String files[] = from.split(fileSeparator);
//		if ( files.length < 1 )
//			return;
//		fileName = files[0];
//		for(int i=1;i<files.length;i++)
//			addOption(files[i]);
//		if ( fileName.equals(NoFile))
//			fileName = null;
//	}
	/*-------------------------------*/
	protected void setfileName(String fileName) {
		this.fileName = fileName;
	}
	/*-------------------------------*/
	protected void addOption(String fileName) {
		otherOptionsFileNames.add(fileName);
	}
	/*-------------------------------*/
	/**
	 *  prepare the question for use:
	 *  canonize names if necessary
	 *  if the image is here, call back with the question
	 *  otherwise, start fetching the image
	 *  and call back when its here
	 */
	public void prepare(NautilusQuestionCallBack nqcb) {
		if ( annulled )
			return;
		if ( isUsed )
			return;
		prepareNames();
		if ( d == null )
			prepareImage(nqcb);
		else
			nqcb.f(this);
	}
	/*-------------------------------*/
	public boolean isAnnulled() {
		return(annulled);
	}
	/*-------------------------------*/
	private void prepareNames() { 
		if ( isprepared )
			return;
		if ( canonicalName == null )
			canonicalName = canonize(fileName);
		canonicalNames.add(canonicalName);
		Iterator<String> itr = otherOptionsFileNames.iterator();
		while(canonicalNames.size() < maxNumChoices && itr.hasNext()) {
			String nextCandidate = canonize(itr.next());
			if ( ! canonicalNames.contains(nextCandidate) )
				canonicalNames.add(nextCandidate);
		}
			
		// check for dups
//		int num = canonicalNames.size();
//		for(int i=0;i<num;i++) {
//			for(int j=0;j<num;j++) {
//				if ( i != j && canonicalNames.get(i).equals(canonicalNames.get(j)) ) {
//					String msg = "dup " + canonicalNames.get(i) + " for " + canonicalName;
//					log(msg);
//					m.utils.logError(msg);
//				}
//			}
//		}
		isprepared = true;
	}
	/*-------------------------------*/
	private String imagePath() {
		String encodeFileName;
		encodeFileName = fileName.replace(" ", "%20");
		if ( ! encodeFileName.equals(fileName) )
			log("Encoding " + fileName + " to " + encodeFileName);
		String path = String.format("%s/%s/%s",
				nutils.gameName(gameId),
				nutils.difficultyDescription(difficulty),
				encodeFileName
			);
		return(path);
	}
	/*-------------------------------*/
	private void prepareImage(final NautilusQuestionCallBack nqcb) {
		if ( annulled )
			return;
		McallbackWithDrawable cb = new McallbackWithDrawable() {
			@Override
			public void f(Drawable d) {
				if ( annulled )
					return;
				imageArrived(d, nqcb);			
			}			
		};
		String imageUrl = imagesUrl + "/" + imagePath();
		mblob = new Mblob(a, imageUrl);
		mblob.get(cb);
	}
	/*-------------------------------*/
	private void imageArrived(Drawable d, NautilusQuestionCallBack nqcb) {
		if ( d == null ) {
			String path = imagePath();
			m.utils.logError(path + ":null");
			String msg = "Null image arrived: " + path;
			log(msg);
			return;
		}
		this.d = d;
		if ( nqcb != null )
			nqcb.f(this);
		
	}
	/*-------------------------------*/
	private String removeExt(String file) {
		try {
			int i = file.lastIndexOf('.');
			if (i <= 0)
				return(file);
			return (file.substring(0, i));
		} catch (Exception e) {
			log(file);
			return(file);
		}		
	}
	/*---------------------*/
	private String capitalize(String sourceString) {
		if ( sourceString == null )
			return(null);
		int len = sourceString.length();
		if ( len == 0 )
			return(sourceString);
		char prevc;
		StringBuilder sb = new StringBuilder();
		char c = sourceString.charAt(0);
		sb.append(Character.toUpperCase(c));
		for(int i=1;i<len;i++) {
			prevc = c;
			c = sourceString.charAt(i);
			if ( ! Character.isLetter(prevc) )
				c = Character.toUpperCase(c);
			sb.append(c);
		}
		return(sb.toString().trim().replaceAll(" +", " "));
	}
	/*---------------------*/
	private String cleanString(String sourceString) {
		if ( sourceString == null )
			return(null);
		int len = sourceString.length();
		if ( len == 0 )
			return(sourceString);
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<len;i++) {
			char c = sourceString.charAt(i);
			if ( c != '(' && c != ')' && c != '\'' && ! Character.isLetter(c) ) {
				sb.append(' ');
				continue;
			}
			if ( i == 0 ) {
				sb.append(c);
				continue;
			}
			int nexti = i + 1;
			if ( nexti == len ) {
				sb.append(c);
				break;
			}
			char nextc = sourceString.charAt(nexti);
			int sblen = sb.length();
			boolean isMc = sblen > 1 &&
				Character.toLowerCase(c) == 'c' && 
				Character.toLowerCase(sb.charAt(sblen-1)) == 'm' &&
				( sblen < 2 || sb.charAt(sblen-2) == ' ' )
			;
						
			
			if ( ! isMc && Character.isLowerCase(c) && Character.isUpperCase(nextc)	) {
				sb.append(c);
				sb.append(' ');
				continue;
			}
			sb.append(c);
		}
		len = sb.length();
		return(sb.toString().trim().replaceAll(" +", " "));
	}
	/*---------------------*/
	private String upperAfterMc(String str) {
		int Mc = str.indexOf("Mc");
		if ( Mc < 0 )
			return(str);
		String rhs = str.substring(Mc+2);
		String upper = Character.toUpperCase(rhs.charAt(0)) + rhs.substring(1);
		String ret = str.substring(0, Mc) + "Mc" + upper;
		return(ret);
	}
	/*---------------------*/
	private String addDotToStandaloneLetter(String str) {
		str = " " + str + " ";;
		StringBuilder sb = new StringBuilder();
		int len = str.length();
		char c, prevc, nextc;
		c = str.charAt(0);
		sb.append(c);
		nextc = str.charAt(1);
		for(int i=1;i<len-1;i++) {
			prevc = c;
			c = nextc;
			nextc = str.charAt(i+1);
			sb.append(c);
			if ( prevc == ' ' && nextc == ' ')
				sb.append('.');
		}
		return(sb.toString().trim());
	}
	/*---------------------*/
	private String addDotToJr(String str) {
		int len = str.length();
		if ( len < 4 )
			return(str);
		try {
			String lastThree = str.substring(len - 3, len);
			if (lastThree.equals(" Jr"))
				str += ".";
			return (str);
		} catch (Exception e) {
			String msg = e.toString();
			m.utils.logError(msg);
			return(str);
		}
	}
	/*---------------------*/
	private String removeEmptyParenthesis(String str) {
		String regex = "\\(\\s*\\)";
		String ret = str.replaceAll(regex, "");
		return(ret);
	}
	/*---------------------*/
	private String canonize(String str) {
		str = removeExt(str);
		str = cleanString(str);
		str = str.replaceAll("[0-9]+", " ").trim().toLowerCase();
		str = removeEmptyParenthesis(str);
		str = capitalize(str);
		str = upperAfterMc(str);
		str = addDotToJr(str);
		str = addDotToStandaloneLetter(str);
		return(str);
	}
	/*-------------------------------*/
	/**
	 * the string representation of a question set is parsable back to a set
	 * and can be stored in the database for retrieval
	 */
	@Override
	public String toString() {
		String ret = "";
		if ( fileName == null )
			ret += NoFile;
		else
			ret += fileName;
		Iterator<String> itr = otherOptionsFileNames.iterator();
		while(itr.hasNext())
			ret += fileSeparator + itr.next();
		return(ret);
	}
	/*-------------------------------*/
	public Drawable drawable() {
		return(d);
	}
	/*-------------------------------*/
	public boolean hasDrawable() {
		return(d != null);
	}
	/*-------------------------------*/
	public boolean isUsed() {
		return(isUsed);
	}
	/*-------------------------------*/
	public void use() {
		isUsed = true;
	}
	/*-------------------------------*/
	public void unUse() {
		isUsed = false;
	}
	/*-------------------------------*/
	/*
	 * randomize the order of the canonical names
	 * for presentation of guess choices
	 */
	public String[] choices() {
		int len = canonicalNames.size();
		String ret[] = new String[len];
		@SuppressWarnings("unchecked")
		ArrayList<String> names = (ArrayList<String>)canonicalNames.clone();
		for(int i=0;i<len;i++) {
			int left = len - i;
			int index = Mutils.rand(0, left-1);
			String choice = names.get(index);
			names.remove(index);
			ret[i] = choice;
		}
		return(ret);
	}
	/*-------------------------------*/
	public String answer() {
		if ( canonicalName == null )
			canonicalName = canonize(fileName);
		return(canonicalName);
	}
	/*-------------------------------*/
	/**
	 * this question is no longer in use
	 * do not cb from it
	 */
	public void annul() {
		annulled = true;
		d = null;
		m = null;
		nutils = null;
		a = null;
		fileName = null;
		otherOptionsFileNames = null;
		canonicalName = null;
		canonicalNames = null;
		if ( mblob != null )
			mblob.annul();
		mblob = null;
	}
	/*-------------------------------*/
	private void log(String msg) {
		Log.d("Nautilus", msg);
	}
	/*-------------------------------*/
}