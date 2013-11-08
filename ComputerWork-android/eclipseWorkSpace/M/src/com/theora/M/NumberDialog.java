package com.theora.M;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
/*------------------------------------------------------------*/
public class NumberDialog extends Dialog {
	/*------------------------------------------------------------*/
	private Context ctx;
	private Mview mview;
	private Mutils mutils;
	private String title;
	private McallbackWithDouble after;
	private Double defaultValue;
	private String currentString = "";
	private int decimals = 2;
	private Window window;
	private TableLayout tl;
	Button tv;
	Button done;
	String doneLabel = "Enter";
	Button dot;
	Button del;
	Button plusMinus;
	Button cancel;
	boolean phoneStyle = true;
	boolean singleDigit = false;
	boolean isCreated = false;
	boolean emptyIsNagative;
	/*------------------------------------------------------------*/
	public NumberDialog(Context ctx, McallbackWithDouble after, double defaultValue) {
		this(ctx, null, after, 2, new Double(defaultValue));

	}
	/*-------------------------------*/
	public NumberDialog(Context ctx, McallbackWithDouble after) {
		this(ctx, null, after, 2, null);
	}
	/*-------------------------------*/
	public NumberDialog(Context ctx, McallbackWithInt after) {
		this(ctx, null, after, null);
	}
	/*-------------------------------*/
	public NumberDialog(Context ctx, String title, final McallbackWithInt after, Integer defaultValue) {
		this(ctx, title,
				new McallbackWithDouble() {
					public void f(Double d) {
						int ret;
						if ( d == null )
							ret = -1961;
						else
							ret = d.intValue();
						after.f(ret);
					}
				},
				0, defaultValue == null ? null : defaultValue.doubleValue());
	}
	/*------------------------------------------------------------*/		
	public NumberDialog(Context ctx, String title, McallbackWithDouble after, int decimals, Double defaultValue) {
		super(ctx);
		this.ctx = ctx;
		this.title = title;
		this.after = after;
		this.decimals = decimals;
		this.defaultValue = defaultValue;
		mview = new Mview(ctx);
		mutils = new Mutils(ctx);
	}
	/*------------------------------------------------------------*/
	/**
	 * set the layout style to calculator style
	 * call this before show();
	 */
	public void setCalculatorStyle() {
		if ( ! isCreated )
			phoneStyle = false;
	}
	/*------------------------------------------------------------*/		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		window = getWindow();
		layout();
		isCreated = true;


	}
	/*------------------------------------------------------------*/
	private void layout() {
		if ( phoneStyle )
			layoutPhoneStyle();
		else
			layoutCalculatorStyle();
	}
	/*------------------------------------------------------------*/
	private void layoutCalculatorStyle() {
		
	}
	/*------------------------------------------------------------*/
	private void layoutPhoneStyle() {
		setTheTitle();
		setTl();
		setTv();
		tl.addView(tv);
		TableRow tr;
		for (int row=0;row<3;row++) {
			 tr = new TableRow(ctx);
			for (int col=0;col<3;col++) {
				Button b = digitButton(row*3 + col + 1);
				tr.addView(b);
			}
			tl.addView(tr);
		}
		tr = new TableRow(ctx);
		if ( singleDigit )
			decimals = 0;
		if ( singleDigit )
			tr.addView(textButton("", false));
		else
			tr.addView(textButton("+/-"));
		tr.addView(digitButton(0));
		tr.addView(dotButton());
		tr.setBackgroundColor(Mview.zebra0);
		tl.addView(tr);
		
		if ( ! singleDigit ) {
			done = textButton(doneLabel);
			del = textButton("Del");
			tr = new TableRow(ctx);
			tr.addView(del);
			tr.addView(done);
			TableRow.LayoutParams params = (TableRow.LayoutParams)done.getLayoutParams();
			params.span = 2;
			done.setLayoutParams(params);
			tl.addView(tr);
		}
		setContentView(tl);
	}
	/*------------------------------------------------------------*/
	private void setTheTitle() {
		if ( title != null )
			setTitle(title);
		else
			window.requestFeature(Window.FEATURE_NO_TITLE);
	}
	/*------------------------------------------------------------*/
	/**
	 * call before show to set the Done button label
	 */
	public void setDoneLabel(String label) {
		doneLabel = label ;
	}
	/*------------------------------------------------------------*/
	public void setSingleDigit() {
		singleDigit = true;
	}
	/*------------------------------------------------------------*/
	private void setTv() {
		tv = new Button(this.ctx);
		tv.setGravity(Gravity.RIGHT);
		tv.setTextColor(Mview.black);
		tv.setTextSize(24f);
		tv.setBackgroundColor(Mview.zebra2 );
		if ( defaultValue != null ) {
			if ( decimals == 0 )
				currentString = "" + defaultValue.intValue();
			else
				currentString = Mutils.format(defaultValue.doubleValue(), decimals);
			setText();
		}
	}
	/*------------------------------------------------------------*/
	private void setTl() {
		tl = new TableLayout(ctx);
		TableLayout.LayoutParams tlpr = new TableLayout.LayoutParams(  
				TableLayout.LayoutParams.FILL_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		tl.setLayoutParams(tlpr);
		tl.setColumnStretchable(0, true);
		tl.setColumnStretchable(1, true);
		tl.setColumnStretchable(2, true);
		int bg = Mview.zebra0 &  0x00ffffff | 0x60000000 ;
		tl.setBackgroundColor(bg);
	}
	
	/*------------------------------------------------------------*/
	private Button digitButton(final int digit) {
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				digitClicked(digit);
			}
		};	
		Button b = button("" + digit, listener);
		return(b);
	}
	/*------------------------------------------------------------*/
	private Button dotButton() {
		if ( decimals == 0 )
			return(textButton("", false));
		else
			return(textButton(".", true));
		
	}
	/*------------------------------------------------------------*/
	private Button textButton(String text) {
		return(textButton(text, true));
	}
	/*------------------------------------------------------------*/
	private Button textButton(final String text, boolean isClickable) {
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textButtonClicked(text);
			}
		};	
		Button b = button(text, listener, isClickable);
		return(b);
	}
	/*------------------------------------------------------------*/
	private Button button(final String text, View.OnClickListener listener) {
		return(button(text, listener, true));
	}
	/*------------------------------------------------------------*/
	private Button button(final String text, View.OnClickListener listener, boolean isClickable) {
		Button b = new Button(ctx);
		b.setTextColor(Mview.blue);
		b.setTextSize(36f);
		b.setPadding(4, 4, 4, 4);
		b.setText(text);
		if ( isClickable ) {
			b.setClickable(true);
			b.setOnClickListener(listener);
		}
		return(b);
	}
	/*------------------------------------------------------------*/
	/*
	 * from currentString to the screen
	 */
	private void setText() {
		mutils.click();
		tv.setText(currentString);
	}
	/*------------------------------------------------------------*/
	private void digitClicked(int digit) {
		if ( ! allow() )
			return;
		if ( singleDigit ) {
			currentString = "" + digit; // ignore a possible default
			done();
		} else {
			currentString += digit;
			setText();
		}
	}
	/*------------------------------------------------------------*/
	private void textButtonClicked(String text) {
		if ( text.compareTo(".") == 0 )
			dot();
		else if ( text.compareTo(doneLabel)== 0 )
			done();
		else if ( text.compareTo("+/-")== 0 )
			plusMinus();
		else if ( text.compareTo("Cancel")== 0 )
			// cancel() is a dialog native function that does almost the same
			dismiss();
		else if ( text.compareTo("Del")== 0 )
			delete();
		else
			mview.msg("Unknown text Button: " + text, true);
	}
	/*------------------------------------------------------------*/
	private void dot() {
		if ( ! hasDot() ) {
			currentString += ".";
			setText();
		}
	}
	/*------------------------------------------------------------*/
	private boolean hasDot() {
		return(currentString.indexOf('.') >= 0);
	}
	/*------------------------------------------------------------*/
	/*
	 * do not allow data entry if the number of decimals has reached its limit.
	 */
	private boolean allow() {
		if ( ! hasDot() )
			return(true);
		int dotIndex = currentString.indexOf('.');
		int decimalsAfterTheDot = currentString.length() - dotIndex - 1;
		return(decimalsAfterTheDot < decimals);
	}
	/*------------------------------------------------------------*/
	private void plusMinus() {
		if ( currentString.length() == 0 )
			currentString = "-";
		else if ( currentString.charAt(0) == '-')
			currentString = currentString.substring(1);
		else
			currentString = "-" + currentString;
		setText();
	}
	/*------------------------------------------------------------*/
	private void delete() {
		int length = currentString.length();
		if ( length == 0 )
			return;
		if ( length == 1 )
			currentString = "";
		else
			currentString = currentString.substring(0, length -1);
		setText();
	}
	/*------------------------------------------------------------*/
	private void done() {
		if ( currentString.length() == 0 )
			after.f(null);
		else {
			if ( hasDot() ) // will double new correctly on "15."
				currentString += "0" ;	
			after.f(new Double(currentString));
		}
		dismiss();
	}
	/*------------------------------------------------------------*/
}
