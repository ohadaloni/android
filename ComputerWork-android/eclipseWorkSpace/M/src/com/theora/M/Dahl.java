package com.theora.M;

import android.app.Activity;
import android.os.Bundle;
/*------------------------------------------------------------*/

public class Dahl extends Activity {
	/*------------------------------------------------------------*/
	// Dialogs
	private Mcontroller m = null;
	/*------------------------------------------------------------*/
	public Dahl() {
		m = new Mcontroller(this, "mdb");
	}
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	m.utils.msg("Dahl: onCreate");
    	Mcallback yes = new Mcallback() { @Override public void f() { msg("yes"); } } ;
    	Mcallback no = new Mcallback() { @Override public void f() { msg("no"); } } ;
    	Mcallback ok = new Mcallback() { @Override public void f() { msg("ok"); } } ;
    	Mcallback cancel = new Mcallback() { @Override public void f() { msg("cancel"); } } ;
    	Mcallback OKed = new Mcallback() { @Override public void f() { msg("OKed"); } } ;
    	McallbackWithString thirdClicked = new McallbackWithString() {
			@Override
			public void f(String s) {
				msg("thirdClicked: " + s);
			}
    	};
    	McallbackWithString typed = new McallbackWithString() {
    		@Override
    		public void f(String s) {
    			if ( s == null )
    				msg("Cancelled");
    			else if ( s.length() == 0 )
    				msg("Empty string returned");
    			else
    				msg("OK: " + s);
    		}
    	} ;
    	McallbackWithInt dateChosen = new McallbackWithInt() {
    		@Override
    		public void f(int dt) {
    			if ( dt <= 0 )
    				msg("Date Selection Cancelled");
    			else
    				msg("Date chosen: " + Mdate.fmt(dt));
    		}
    	} ;
    	McallbackWithInt timeChosen = new McallbackWithInt() {
    		@Override
    		public void f(int tm) {
    			if ( tm < 0 )
    				msg("Time Selection Cancelled");
    			else
    				msg("Time chosen: " + Mtime.fmt(tm));
    		}
    	} ;
        McallbackWithString selected = new McallbackWithString() {
        	@Override
        	public void f(String s) {
        		if ( s == null )
        			msg("None Selected");
        		else
        			msg("Selected: " + s);
        	}
        } ;
        McallbackWithBoolean confirmed = new McallbackWithBoolean() {
        	@Override
        	public void f(boolean b) {
        		if ( b )
        			msg("Confirmed");
        		else
        			msg("Cancelled");
        	}
        } ;
    	McallbackWithInt intChosen = new McallbackWithInt() {
    		@Override
    		public void f(int i) {
    			if ( i == -1961 )
    				msg("Number Pad Cancelled");
    			else
    				msg("Number Pad: " + i);
    		}
    	} ;
    	McallbackWithDouble numberChosen = new McallbackWithDouble() {
    		@Override
    		public void f(Double d) {
    			if ( d == null )
    				msg("Number Pad Cancelled");
    			else
    				msg("Number Pad: " + d.doubleValue());
    		}
    	} ;
    	String longText = "these dialog will lay on top of one another," +
					"its ok for this dialog driver/test program" +
					"these dialog will lay on top of one another," +
					"its ok for this dialog driver/test program" +
					"these dialog will lay on top of one another," +
					"its ok for this dialog driver/test program" +
					"these dialog will lay on top of one another," +
					"its ok for this dialog driver/test program" +
					"these dialog will lay on top of one another," +
					"its ok for this dialog driver/test program" +
					"these dialog will lay on top of one another," +
					"its ok for this dialog driver/test program" +
					"these dialog will lay on top of one another," +
					"its ok for this dialog driver/test program" +
					"these dialog will lay on top of one another," +
			    	"its ok for this dialog driver/test program";
		// these dialog will lay on top of one another, its ok for this dialog driver/test program
//		m.view.confirm("Yes or No", "Are You Sure?", false, yes, no);
//		m.view.confirm("OK or Cancel", "Positive?", true, ok, cancel);		
//		m.view.alert("Thought You Should know", longText, OKed);
//		String choices[] = { "Emerson", "Alan", "David", "George", "Camel", "Frank", "Emerson" };
//		m.view.select("Make your selection",  choices, selected);
//		m.view.selectDate("Enter Date!!!", 0 | 20110816, dateChosen);
//		m.view.selectTime("Enter Time!!!", 0, timeChosen);
//		m.view.confirm("Yes or No", "Are You Sure?", false, confirmed);
//		m.view.confirm("OK or Cancel", "Positive?", true, confirmed);		
//		m.view.prompt("Please Enter Something", "I beg of you", typed);
//		m.view.prompt("Three button question", "Three button question message", typed, "some data", "Continue?", thirdClicked );
    	new NumberDialog(this, numberChosen).show();
//    	m.view.numberDialog("Add a double number", numberChosen, false, new Double(7.5));
   }
	/*------------------------------------------------------------*/
	private void msg(String s) {
		m.view.msg(s, 5000);
	}
	/*------------------------------------------------------------*/
}
