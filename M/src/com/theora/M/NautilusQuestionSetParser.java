package com.theora.M;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
/*------------------------------------------------------------*/
public class NautilusQuestionSetParser extends DefaultHandler {
	/*------------------------------------------------------------*/
	private NautilusQuestionSet nautilusQuestionSet = null;
	private boolean inFile = false;
	private boolean inOption = false;
	private NautilusUtils nutils = null;
	private int gameId;
	private int difficulty;
	/*------------------------------------------------------------*/
	public NautilusQuestionSetParser(Activity a, int gameId, int difficulty, int maxNumChoices) {
		this.gameId = gameId;
		this.difficulty = difficulty;
		nutils = new NautilusUtils(a, gameId);
		nautilusQuestionSet = new NautilusQuestionSet(a, this.gameId, this.difficulty, maxNumChoices);
	}
	/*------------------------------------------------------------*/
	/**
	 * get the final parse result
	 */
	public NautilusQuestionSet getNautilusQuestionSet() {
		return this.nautilusQuestionSet;
	}
	/*------------------------------------------------------------*/
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("questions") ) {
		} else if (localName.equals("question") ) {
			nautilusQuestionSet.startAddingQuestion();
		} else if (localName.equals("file") ) {
			inFile = true;
		} else if (localName.equals("options") ) {
		} else if (localName.equals("option") ) {
			inOption = true;
		}
	}
	/*------------------------------------------------------------*/
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("questions") ) {
		} else if (localName.equals("question") ) {
			nautilusQuestionSet.endAddingQuestion();
		} else if (localName.equals("file") ) {
			inFile = false;
		} else if (localName.equals("options") ) {
		} else if (localName.equals("option") ) {
			inOption = false;
		}
	}
	/*------------------------------------------------------------*/
	@Override
    public void characters(char ch[], int start, int length) {
			String s = new String(ch, start, length).trim();
			if ( inFile )
				nautilusQuestionSet.setFileName(s);
			else if ( inOption )
				nautilusQuestionSet.addOption(s);
    }
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private void log(String msg) {
		nutils.log(msg);
	}
	/*------------------------------------------------------------*/
}