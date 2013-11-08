package com.theora.M;
/*------------------------------------------------------------*/
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
/*------------------------------------------------------------*/
/**
 * M view class
 */
public class Mview {
	/*------------------------------------------------------------*/
	public static final int black = 0xff000000 ;
	public static final int white = 0xffffffff ;
	public static final int blue = 0xff0000ff ;
	public static final int zebra0 = 0xffc0c0c0 ;
	public static final int zebra1 = 0xff88bbff ;
	public static final int zebra2 = 0xffaaccff ;
	public static final int zebra3 = 0xff99ccff ;
	public static final int yellow = 0xffffff00 ;
	public static final int orange = 0xffffaa00 ;
	public static final int red = 0xffff0000;
	/*-----------------------------------*/
	public static final int hilite = yellow ;
	public final static float base_size = 16f;
	public final static float title_base_size = 20f;
	public final static float SMALL = 1f;
	public final static float MEDIUM = 1.2f;
	public final static float LARGE = 1.5f;
	public final static float EXTRALARGE = 2f;
	public final static float SUPERSIZE = 3f;
	/*------------------------------------------------------------*/
	Activity a = null;
	private Context ctx = null;
	private Mutils mutils = null;
	private LinearLayout topView = null;
	private LinearLayout showRowsView = null;
	private boolean stretchLeft = false;
	private TableLayout tl;
	private ScrollView scv;
	private HorizontalScrollView hscv;
	private boolean scrollHorizontally = false;
	private boolean scrollBoth = true;
	private RowClickListener rcl = null;
	private RowLongPressListener rlpl = null;
	private boolean showHeaders = true;
	private boolean isId = false;
	/*------------------------------------------------------------*/
	public Mview(Activity a, Context context, LinearLayout v) {
		this.a = a;
		this.ctx = context;
		this.mutils = new Mutils(a, ctx);
		this.topView = v;
	}
	/*------------------------------------------------------------*/
	public Mview(Activity a, LinearLayout v) {
		this(a, a, v);
	}
	/*------------------------------------------------------------*/
	public Mview(Activity a, Context context) {
		this(a, context, null);
	}
	/*------------------------------------------------------------*/
	public Mview(Activity a) {
		this(a, a, null);
	}
	/*------------------------------------------------------------*/
	public Mview(Context context) {
		this(null, context, null);
	}
	/*------------------------------------------------------------*/
	private void log(String msg) {
		Log.d("Mview", msg);
	}
	/*------------------------------------------------------------*/
	/**
	 * figure out the size of text based on the number of rows that are displayed, to fit
	 * best on a single screen without scrolling, if the number of rows is small enough
	 * using this will cause wide rows to be very scrollable if few rows are present
	 */
	public static float sizeFromRows(int numRows) {
		if (numRows <= 8 )
			return(EXTRALARGE);
		if (numRows <= 12 )
			return(LARGE);
		if (numRows <= 16)
			return(MEDIUM);
		return(Mview.SMALL);
	}
	/*--------------------------------------*/
	/**
	 * show these rows on a full screen
	 */
	public boolean showRows(MmodelRow[] rows) {
		return(showRows(rows, null, sizeFromRows(rows.length)));
	}
	/*------------------------------*/
	/**
	 * show rows on full screen with  size from Mview.* sizes
	 */
	public boolean showRows(MmodelRow[] rows, float size) {
		return(showRows(rows, null, size));
	}
	/*------------------------------*/
	/**
	 * show rows on full screen with title row
	 */
	public boolean showRows(MmodelRow[] rows, String title) {
		return(showRows(rows, title, sizeFromRows(rows.length)));
	}
	/*------------------------------*/
	public void setShowRowsView(LinearLayout v) {
		showRowsView = v;
	}
	/*------------------------------*/
	/**
	 * show rows on full screen with title row and size from Mview.* sizes
	 */
	public boolean showRows(MmodelRow[] rows, String title, float size) {
		if ( showRowsView == null ) {
			if ( topView == null )
				initView();
			showRowsView = topView;
		}
		showRowsView.removeAllViews();
		showRowsView.setBackgroundColor(white);
		if ( rows == null || rows.length == 0 ) {
			String msg = "No Rows to Display";
			log(msg);
			mutils.msg(msg);
			TextView tv = new TextView(ctx);
			tv.setText(msg);
			tv.setTextColor(black);
			tv.setTextSize(title_base_size * size);
			showRowsView.addView(tv);
			return(true);
		}
		tl = new TableLayout(ctx);
		TableLayout.LayoutParams tlpr = new TableLayout.LayoutParams(  
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.FILL_PARENT);
		tl.setLayoutParams(tlpr);
		tl.setBackgroundColor(white);
		tl.setPadding(5, 5, 5, 5);

		if ( title != null ) {
			tl.addView(titleRow(title, size));
			tl.addView(separatorLine());
		}
		if ( showHeaders )
			tl.addView(headersRow(rows, size));

		for(int i=0;i<rows.length;i++) {
			tl.addView(separatorLine());
			int background = i%2 == 0 ? zebra1 : zebra2;
			TableRow tr = rowView(rows[i], size, background);
			tr.setTag(new RowTag(rows[i], i+1, background, RowTag.dataRow));
			setBackground(tr, background);
			tl.addView(tr);
		}

		if ( scrollBoth ) {
				hscv = new HorizontalScrollView(ctx);
				scv = new ScrollView(ctx);
				hscv.setBackgroundColor(white);
				hscv.addView(tl);
				scv.setBackgroundColor(white);
				scv.addView(hscv);
				showRowsView.addView(scv);
		} else if ( scrollHorizontally ) {
			scv = null;
			HorizontalScrollView hscv = new HorizontalScrollView(ctx);
			hscv.addView(tl);
			hscv.setBackgroundColor(white);
			showRowsView.addView(hscv);
		} else {
			hscv = null;
			scv = new ScrollView(ctx);
			scv.addView(tl);
			scv.setBackgroundColor(white);
			showRowsView.addView(scv);
			if ( stretchLeft )
				tl.setColumnStretchable(0, true);
			else {
				int numFields = rows[0].cnt;
				int numInvisibleFields = isId ? 1 : 0;
				if ( hiddens != null )
					numInvisibleFields += hiddens.size();
				int numVisibleFields = numFields - numInvisibleFields;
				int numColumns = numVisibleFields * 2 - 1;
				int leftColumnIndex = numColumns-1;
				tl.setColumnStretchable(leftColumnIndex, true);
			}
		}
		return(true);
	}
	/*------------------------------------------------------------*/
	private View separatorLine() {
			View ret = new View(ctx);
			ret.setBackgroundColor(white);
			TableLayout.LayoutParams lp = new TableLayout.LayoutParams(
					TableLayout.LayoutParams.MATCH_PARENT,
					2); 
			ret.setLayoutParams(lp);
			return(ret);
	}
	/*------------------------------------------------------------*/
	private TextView titleRow(String title, float size) {
		TextView tv = rowCell("The Title", title, title_base_size * size, false, zebra0);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setTag(new RowTag(RowTag.titleRow));
		return(tv);
	}
	/*------------------------------------------------------------*/
	private View separator() {
			View ret = new View(ctx);
			ret.setBackgroundColor(white);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(
						2,
						TableRow.LayoutParams.MATCH_PARENT); 
			ret.setLayoutParams(lp);
			return(ret);
	}
	/*------------------------------*/
	private TableRow headersRow(MmodelRow[] rows, float size) {
		TableRow headers = new TableRow(ctx);
		MmodelRow row = rows[0];
		boolean starting = true;
		for(int i=0;i<row.cnt;i++) {
			if ( ! isHidden(row.names[i]) && ! row.names[i].equals("id") && ! row.names[i].equals("_id") ) {
				if ( ! starting )
					headers.addView(separator());
				starting = false;
				headers.addView(
					rowCell(row.names[i], row.names[i], base_size * size, isRightAlign(row.names[i], row.types[i]), zebra0)
				);
			}
		}
		/* touch icon header
		if ( rcl != null  || rlpl != null ) {
			headers.addView(separator());
			headers.addView(
				rowCell("", base_size * size, false, zebra0)
			);
		}
		*/
		headers.setTag(new RowTag(RowTag.headersRow));
		return(headers);
	}
	/*------------------------------
	private View touchIcon(float size) {
    	int handId = mutil.resourceId("hand", "drawable");
    	if ( handId != 0 ) {
			ImageView iv = new ImageView(ctx);    		
			iv.setImageResource(handId);
	    	iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			iv.setBackgroundColor(Mview.blue);
    		return(iv);
    	}
		TextView tv;
		tv = rowCell("...", size, false, blue);
		tv.setTextColor(white);
		return(tv);
	}
	/*------------------------------*/
	private TableRow rowView(MmodelRow row, float size, int background) {
		int rowId = -1;
		TableRow tr = new TableRow(ctx);
		boolean starting = true;
		for(int i=0;i<row.cnt;i++) {
			if (  row.names[i].equals("id") || row.names[i].equals("_id") ) {
				rowId = Integer.parseInt(row.values[i]);
				tr.setId(rowId);
				isId = true;
			}
			if ( ! isHidden(row.names[i]) && ! row.names[i].equals("id") && ! row.names[i].equals("_id") ) {			
				if ( ! starting )
					tr.addView(separator());
				/*	tr.addView(cell(row.values[i], size, row.types[i]));	*/
				tr.addView(rowCell(
						row.names[i],
						format(row.values[i], row.names[i], row.types[i]),
						base_size * size,
						isRightAlign(row.names[i], row.types[i]),
						background
				));
				starting = false;
			}
		}
		/*
		if ( rcl != null || rlpl != null ) {
			tr.addView(separator());
			tr.addView(touchIcon(base_size * size));
		}
		*/
		tr.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if ( isSingleTouch(v, event) )
					hiliteRow(v);
				return false;
			}
		});
		tr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deHiliteRow(v);
				rowClicked(v);
			}
		});
		tr.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				deHiliteRow(v);
				return(rowLongPressed(v));
			}
		});
		return(tr);
	}
	/*----------------------------*/
	private class SavedMotionEvent {
		public View view;
		public int x;
		public int y;
		public long when;
		/*----------------------------*/	
		public SavedMotionEvent(View v, MotionEvent ev) {
			view = v;
			x = Math.round(ev.getX() * 10000);
			y = Math.round(ev.getY() * 10000);
			when = SystemClock.elapsedRealtime() ;

		}
		/*----------------------------*/	
		public boolean isSimilar(SavedMotionEvent sme) {
			if ( this.view != sme.view )
				return(false);
			if ( Math.abs(this.when - sme.when) > 50 )
				return(false);
			if ( this.x != sme.x )
				return(false);
			if ( this.y != sme.y )
				return(false);
			return(true);
		}
		/*----------------------------*/	
	}
	/*----------------------------*/
	/**
	 * many tough events are generated with a single touch:
	 * return true only once for a single short click or long press
	 * and false with all other events and cases (such as a scroll/drag gesture)
	 * so that the caller can filter a single touch event from a sequence.
	 * the logic is to collect N similar events.
	 * when the N'th similar event is collected, true s returned
	 */
	private static final int singleTouchN = 6;
	private SavedMotionEvent singleTouchEvents[] = new SavedMotionEvent[singleTouchN];
	private int singleTouchI = 0;
	private boolean isSingleTouch(View v, MotionEvent ev) {
		SavedMotionEvent sme = new SavedMotionEvent(v, ev);
		if ( singleTouchI == 0 ) {
			singleTouchEvents[singleTouchI++] = sme;
			return(false);				
		}
		if ( ! sme.isSimilar(singleTouchEvents[singleTouchI-1]) ) {
			singleTouchI = 0;
			singleTouchEvents[singleTouchI++] = sme;
			return(false);
		}
		if ( singleTouchI == singleTouchN )
			return(false);
		singleTouchEvents[singleTouchI++] = sme;
		return(singleTouchI == singleTouchN);
	}
	/*----------------------------*/
	/**
	 * find if zebra1 or zebra2
	 * go only through actual TableRows
	 * there are also title, headerRow and separator line children.
	 */
	private int background(TableRow tr) {
		RowTag rt = (RowTag)tr.getTag();
		if ( rt == null || ! rt.isDataRow() )
			return(zebra0);
		return(rt.background());
	}
	/*----------------------------*/
	/*
	 * set the background of a row
	 * also used for hiliting and dehiliting
	 * but if a column has its own background color
	 * then show it instead of the passed background
	 */
	private void setBackground(TableRow tr, int color) {
		int allCols = tr.getChildCount();

		for(int i=0;i<allCols;i++)
			if (i % 2 == 0) {
				View v = tr.getChildAt(i);
				String fname = (String)v.getTag();

				if ( fname != null ) {
					int columnColor = columnColor(fname);
					if ( columnColor != 0) {
						v.setBackgroundColor(columnColor);
						continue;
					}
				}
				v.setBackgroundColor(color);
			}
	}
	/*----------------------------*/
	private TableRow lastHilitedRow = null;
	private long lastDehilteTime = 0;
	/*----------------------------*/
	private void hiliteRow(View v) {
		TableRow tr = (TableRow)v;
		if ( tr == lastHilitedRow )
			return;
		/**
		 * made redundant by isSingleTouch()
		 */
		long now = SystemClock.elapsedRealtime() ;
		long millisecondsSinceDehilte = now - lastDehilteTime ;
		// with long press, more touch events occur after the row was dehilited
		// so do not re-hilite when the finger is still pressing and generates these events
		if ( millisecondsSinceDehilte < 1250 )
			return;

		setBackground(tr, yellow);
		lastHilitedRow = tr ;
	}
	/*----------------------------*/
	private void deHiliteRow(View v) {
		TableRow tr = (TableRow)v;
		if ( tr != lastHilitedRow )
			return;
		long now = SystemClock.elapsedRealtime() ;
		setBackground(tr, background(tr));
		lastHilitedRow = null;
		lastDehilteTime = now;
	}
	/*----------------------------*/
	private boolean rowLongPressed(View v) {
		TableRow tr = (TableRow) v;
		int rowId = tr.getId();
		// android view ids can start with 0 but table row ids must be 1 or more
		if ( rowId > 0 && rlpl != null )
			rlpl.rowLongPressed(rowId);
		mutils.log("rowLongPressed: rowId=" + rowId);
		return(true);
		
	}
	/*----------------------------*/
	private boolean rowClicked(View v) {
		TableRow tr = (TableRow) v;
		int rowId = tr.getId();
		// android view ids can start with 0 but table row ids must be 1 or more
		if ( rowId > 0 && rcl != null )
			rcl.rowClicked(rowId);
		else
			mutils.log("rowClicked: rowId=" + rowId);
		return(true);
	}
	/*------------------------------------------------------------*/
	private TextView rowCell(String fname, String text, float size, boolean rightAlign, int backGround) {
		TextView c = new TextView(ctx);
		c.setTextColor(black);
		c.setText(text);
		c.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		c.setPadding(2, 1, 3, 1);
		c.setBackgroundColor(backGround);
		c.setTag(fname);

		if (rightAlign)
			c.setGravity(Gravity.RIGHT);
		return(c);		
	}
	/*------------------------------------------------------------*/
	/**
	 * initialize a top LinearLayout view, setContentView() it
	 * remember it as such, and return it
	 */
	public LinearLayout initView() {
		topView = new LinearLayout(ctx);
		topView.setOrientation(LinearLayout.VERTICAL);
		topView.setBackgroundColor(white);
        a.setContentView(topView);
        return(topView);
	}
	/*------------------------------------------------------------*/
	private String browseStartingUrl = null;
	/*------------------------------*/
	private String getHost(String url) {
		try {
			URL u = new URL(url);
			return(u.getHost());
		} catch ( Exception e ) {
			msg("Bad Url: " + url);
			return(null);
		}
	}
	/*------------------------------*/
	private boolean isApk(String url) {
		try {
			int i = url.lastIndexOf('.');
			if (i <= 0)
				return(false);
			String ext = url.substring(i);
			if ( ext.equals(".apk") )
				return(true);
			return(false);
		} catch (Exception e) {
			return(false);
		}		
	}
	/*------------------------------*/
	private boolean handleUrl(Activity activity, WebView view,	String url) {
		if ( browseStartingUrl == null )
			return(false);
		if ( isApk(url) )
			return(false);
		String baseHost = getHost(browseStartingUrl);
		String thisHost = getHost(url);
		if ( baseHost == null || thisHost == null || baseHost.compareTo(thisHost) != 0)
			return(false);
	    view.loadUrl(url);
		return(true);
}
	/*------------------------------*/
	private boolean urlHandler(Activity activity, WebView view,	String url) {
	    if ( handleUrl(activity, view, url) )
	    	return(true);
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		this.a.startActivity(intent);
    	return(true);
	}
	/*------------------------------*/
	/**
	 * browse a web site on the Internet
	 * any click to urls in the same server are handled internally
	 * other urls invoke the activity manager
	 */
	public boolean browse(String url) {
		browseStartingUrl = url;
		if ( topView == null )
				initView();
		topView.removeAllViews();
		WebView wv = new WebView(ctx);
		topView.addView(wv);
				
		final Activity activity = this.a;
		wv.setWebViewClient(new WebViewClient() {
		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		    	return(urlHandler(activity, view, url));
		    }
		});

		WebSettings ws = wv.getSettings();
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);
		wv.loadUrl(url);
//		wv.reload(); // refresh css, js, etc. during development
		return(true);
	}
	/*------------------------------------------------------------*/
	/**
	 * set a row click listener for showRows:
	 * call reset();setRowClickListener();showRows();
	 * the listener is called with the rowId
	 */
	public void setRowClickListener(RowClickListener l) {
		this.rcl = l;
	}
	/*-------------------------------------*/
	/**
	 * a row click listener for showRows called as rowClicked(int rowId)
	 */
	public interface RowClickListener {
		public void rowClicked(int rowId) ;
	}
	/*------------------------------------------------------------*/
	/**
	 * set a row long press listener for showRows:
	 * the listener is called with the rowId
	 */
	public void setRowLongPressListener(RowLongPressListener l) {
		this.rlpl = l;
	}
	/*-------------------------------------*/
	/**
	 * a row long press listener for showRows called as rowLongPressed(int rowId)
	 */
	public interface RowLongPressListener {
		public void rowLongPressed(int rowId) ;
	}
	/*------------------------------------------------------------*/
	private AlertDialog.Builder baseDialog(String title, String message) {
		AlertDialog.Builder base = new AlertDialog.Builder(a);
		base.setTitle(title != null ? title : "");
		if ( message != null )
			base.setMessage(message);
		return(base); 
	}
	/*------------------------------------------------------------*/
	public void alert(String msg, Mcallback after) {
		alert(null, msg, after);
	}
	/*------------------------------------------------------------*/
	public void alert(String msg) {
		alert(null, msg, null);
	}
	/*------------------------------------------------------------*/
	public void alert(String title, String msg) {
		alert(title, msg, null);
	}
	/*------------------------------------------------------------*/
	/**
	 * issue an alert dialog
	 * if after is not null, call after.f after it is dismissed.
	 */
	public void alert(String title, String message, final Mcallback after) {
		AlertDialog.Builder alert = baseDialog(title, message);
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				if ( after != null )
				  after.f();
			}
		});
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if ( after != null )
					after.f();
			}
		});
		showAlertDialogBuilder(alert);
	}
	/*------------------------------------------------------------*/
	private void showAlertDialogBuilder(AlertDialog.Builder adb) {
		try {
			adb.show();
		} catch (BadTokenException e) {
			mutils.logInfo("BadTokenException");
		} catch ( Exception e) {
			mutils.logInfo("Exception");
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * prompt for user input, call after.f with the result
	 * if thirdButton parameters are not null, then it will appear
	 * and clicking it will call that function with the value.
	 * when the user clicks cancel, a null is passed back
	 */
	public void prompt(String title, String message, final McallbackWithString after, String defaultValue,
			final String thirdButtonLabel, final McallbackWithString thirdButtonCallBack) {
		AlertDialog.Builder alert = baseDialog(title, message);
		final EditText input = new EditText(a);
		if ( defaultValue != null && defaultValue.length() > 0 )
			input.setText(defaultValue);
		alert.setView(input);
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				  after.f(null);
			}
		});
		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
					after.f(input.getText().toString());
			}
		});
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			  public void onClick(DialogInterface dialog, int whichButton) {
				  after.f(null);
			  }
			});
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
					after.f(null);
			}
		});

		if ( thirdButtonCallBack != null && thirdButtonLabel != null ) {
			alert.setNeutralButton(thirdButtonLabel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String value = input.getText().toString();
					thirdButtonCallBack.f(value);
				}
			});
		}
		showAlertDialogBuilder(alert);
	}
	/*------------------------------------------------------------*/
	/**
	 * prompt for user input, call after.f with the result
	 * when the user clicks cancel, a null is passed back
	 */
	public void prompt(String title, String message, final McallbackWithString after) {
		prompt(title, message, after, null, null, null);
	}
	/*------------------------------------------------------------*/
	/**
	 * prompt for user input, call after.f with the result.
	 * when the user clicks cancel, a null is passed back
	 */
	public void prompt(String title, String message, String defaultValue, final McallbackWithString after) {
		prompt(title, message, after, defaultValue, null, null);
	}
	/*------------------------------------------------------------*/
	/**
	 * A confirm dialog (base interface)
	 */
	public void confirm(String title, String message, String yesLabel, String noLabel, final Mcallback yes, final Mcallback no) {
		AlertDialog.Builder alert = baseDialog(title, message);
		alert.setPositiveButton(yesLabel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if ( yes != null )
					yes.f();
			}
		});
		alert.setNegativeButton(noLabel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if ( no != null )
					no.f();
			}
		});
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				if ( no != null )
					no.f();
			}
		});

		showAlertDialogBuilder(alert);
	}
	/*------------------------------------------------------------*/
	/**
	 * A confirm dialog.
	 * label buttons and call on confirm only
	 */
	public void confirm(String title, String message, String yesLabel, String noLabel, final Mcallback yes) {
		confirm(title, message, yesLabel, noLabel, yes, null);

	}
	/*------------------------------------------------------------*/
	/**
	 * confirm dialog.
	 * the callback is passed a true or false value.
	 */
	public void confirm(String title, String message, String yesLabel, String noLabel, final McallbackWithBoolean after) {
		final Mcallback yes = new Mcallback() {
			@Override
			public void f() {
				after.f(true);
			}
		};
		final Mcallback no = new Mcallback() {
			@Override
			public void f() {
				after.f(false);
			}
		};
		confirm(title, message, yesLabel, noLabel, yes, no);
	}
	/*------------------------------------------------------------*/
	/**
	 * Confirm Dialog with labels "Yes" & "No"
	 * call onConfirm if the user confirmed positively.
	 */
	public void confirm(String title, String message, final Mcallback onConfirm) {
		final Mcallback yes = new Mcallback() {
			@Override
			public void f() {
				onConfirm.f();
			}
		};
		confirm(title, message, "Yes", "No", yes, null);
	}
	/*------------------------------------------------------------*/
	/**
	 * Confirm Dialog with labels "OK" & "Cancel"
	 * call onConfirm if the user confirmed positively.
	 */
	public void ok(String title, String message, final Mcallback onConfirm) {
		final Mcallback yes = new Mcallback() {
			@Override
			public void f() {
				onConfirm.f();
			}
		};
		confirm(title, message, "OK", "Cancel", yes, null);
	}
	/*------------------------------------------------------------*/
	/**
	 * Confirm Dialog with labels "Yes" & "No"
	 * the callback is passed a true or false value.
	 */
	public void confirm(String title, String message, final McallbackWithBoolean after) {
		confirm(title, message, "Yes", "No", after);
	}
	/*------------------------------------------------------------*/
	/**
	 * Confirm Dialog with labels "OK" & "Cancel"
	 * the callback is passed a true or false value.
	 */
	public void ok(String title, String message, final McallbackWithBoolean after) {
		confirm(title, message, "OK", "Cancel", after);
	}
	/*------------------------------------------------------------*/
	/**
	 * select from list dialog
	 */
	public void select(String title, final String values[], final McallbackWithString after) {
		AlertDialog.Builder alert = baseDialog(title, null);
		alert.setItems(values, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				  String choice;
				  if ( whichButton >= 0 && whichButton < values.length )
					  choice = values[whichButton];
				  else
					  choice = null; // it seems this is never reached
				  after.f(choice);
			  }
		});
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				  after.f(null);
			}
		});
		showAlertDialogBuilder(alert);
	}
	/*------------------------------------------------------------*/
	/**
	 * TODO: multiSelect is not yet implemented
	 */
	public void multiSelect(String title, String values[], boolean checked[], McallbackWithStrings after) {
		// there is a separate call for each item clicked, then a call when the button is OKed.
		// collect the list of clicked items and call after.f(checkedItems[])
	}

	/*------------------------------------------------------------*/
	private static long lastSetDate = 0;
	/*-----------------------------*/
	/**
	 * date selection dialog
	 * call after(int date)
	 * date is standard with Mdate
	 * if cancel was chosen 0 is passed
	 */
	public void selectDate(String title, int startWithDate, final McallbackWithInt after) {
		DatePickerDialog.OnDateSetListener dtl = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker arg0, int y, int m, int d) {
				lastSetDate = SystemClock.elapsedRealtime() ;
				after.f(Mdate.compose(y, m+1, d));
			}
		};
		if ( startWithDate <= 0 )
			startWithDate = Mdate.today();
		int ymd[] = Mdate.separate(startWithDate);
		DatePickerDialog dp = new DatePickerDialog(a,  dtl, ymd[0], ymd[1]-1, ymd[2]);
		DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				// the dismiss dialog is called even when the dialog is OKed and date selected
				// call after(0) only if this is not the case for this dialog
				long now = SystemClock.elapsedRealtime() ;
				long sinceLastSetDate = now - lastSetDate; 
				if ( sinceLastSetDate > 500 )
					after.f(0);
			}
		};
		dp.setOnDismissListener(dismissListener);

		dp.setTitle(title);
		dp.show();
	}
	/*------------------------------------------------------------*/
	private static long lastSetTime = 0;
	/*-----------------------------*/
	/**
	 * time selection dialog
	 * call after(int time)
	 * time is standard with Mtime
	 * if cancel was chosen -1 is passed
	 */
	public void selectTime(String title, int startWithTime, final McallbackWithInt after) {
		TimePickerDialog.OnTimeSetListener tml = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker arg0, int h, int m) {
				lastSetTime = SystemClock.elapsedRealtime() ;
				after.f(Mtime.compose(h, m));
			}
		};
		if ( startWithTime <= 0 )
			startWithTime = Mtime.now();
		int hm[] = Mtime.separate(startWithTime);
		TimePickerDialog tp = new TimePickerDialog(a,  tml, hm[0], hm[1], true);
		DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				// the dismiss dialog is called even when the dialog is OKed and time selected
				// call after(-1) only if this is not the case for this dialog
				long now = SystemClock.elapsedRealtime() ;
				long sinceLastSetTime = now - lastSetTime; 
				if ( sinceLastSetTime > 500 )
					after.f(-1);
			}
		};
		tp.setOnDismissListener(dismissListener);
		tp.setTitle(title);
		tp.show();
	}
	/*------------------------------------------------------------*/
	private ArrayList<NamedInt> columnColors = null;
	/**
	 * set the background color of a column
	 * @param fname
	 * @param color
	 */
	public void setColumnColor(String fname, int color) {
		if (columnColors == null )
			columnColors = new ArrayList<NamedInt>();
		columnColors.add(new NamedInt(fname, color));

	}
	/*---------------------------------------*/
	private int columnColor(String fname) {
		if ( columnColors == null )
			return(0); // transparent
		int n = columnColors.size();
		NamedInt p;
		for(int i=0;i<n;i++) {
			p = columnColors.get(i);
			if ( p.name.compareTo(fname) == 0 )
				return(p.number);
		}
		return(0);
	}
	/*------------------------------------------------------------*/
	private ArrayList<ModifierPair> modifiers = null;
	/*----------------------------------*/
	private class ModifierPair  {
		public String fname;
		public Modifier modifier;
		public ModifierPair(String fname, Modifier modifier) {
			this.fname = fname;
			this.modifier = modifier;
		}
	}
	/*----------------------------------*/
	/**
	 * register a formatter for a showRows field
	 */
	public void registerModifier(String fname, Modifier modifier) {
		if (modifiers == null )
			modifiers = new ArrayList<ModifierPair>();
		modifiers.add(new ModifierPair(fname, modifier));
	}
	/*----------------------------------*/
	/**
	 * clear modifier and callback settings
	 */
	public void reset() {
		rcl = null;
		rlpl = null;
		modifiers = null;
		rightAligns = null;
		hiddens = null;
		showHeaders = true;
		scrollHorizontally = false;
		scrollBoth = true;
		showRowsView = null;
		columnColors = null;
		stretchLeft = false;
	}
	/*----------------------------------*/
	/**
	 * a showRows field modifier (formatter)
	 */
	public interface Modifier {
		public String modify(String value) ;
	}
	/*-------------------------------------*/
	private String formatByModifier(String data, String fname) {
		if ( modifiers == null )
			return(null);
		int n = modifiers.size();
		ModifierPair p;
		for(int i=0;i<n;i++) {
			p = modifiers.get(i);
			if ( p.fname.compareTo(fname) == 0 )
				return(p.modifier.modify(data));
		}
		return(null);
	}
	/*------------------------------------------------------------*/
	private ArrayList<String> hiddens = null;
	/*-----------------------------------*/
	/**
	 * hide this field in showRows
	 */
	public void hide(String fname) {
		if ( hiddens == null )
			hiddens = new ArrayList<String>();
		hiddens.add(fname);
	}
	/*-----------------------------------*/
	private boolean isHidden(String fname) {
		if (Mutils.inArray(fname, hiddens) )
			return(true);
		return(false);
	}
	/*------------------------------------------------------------*/
	private ArrayList<String> rightAligns = null;
	/*-----------------------------------*/	
	/**
	 * right align this field in showRows
	 */
	public void rightAlign(String fname) {
		if ( rightAligns == null )
			rightAligns = new ArrayList<String>();
		rightAligns.add(fname);
	}
	/*-----------------------------------*/
	private  boolean isRightAlign(String fname, String type) {
		if (Mutils.inArray(fname, rightAligns) )
			return(true);
		return(isRightAlign(type));
	}
	/*-----------------------------------*/
	private boolean isRightAlign(String type) {
		String rights[] = {
			Mmodel.type_date,
			Mmodel.type_int,
			Mmodel.type_float,
			Mmodel.type_money,
			Mmodel.type_date,
			Mmodel.type_time,
			Mmodel.type_datetime
		};
		return(Mutils.inArray(type, rights));
	}
	/*------------------------------------------------------------*/
	/**
	 * show no headers in showRows
	 */
	public void setNoHeaders() {
		showHeaders = false;
	}
	/*------------------------------------------------------------*/
	/**
	 * stretch left column rather than right
	 */
	public void stretchLeft() {
		stretchLeft = true;
	}
	/*------------------------------------------------------------*/
	/**
	 * set horizontal scrolling for showRows
	 */
	public void setHorizontalScrolling() {
		scrollHorizontally = true;
	}
	/*------------------------------------------------------------*/
	/**
	 * set vertical scrolling for showRows
	 */
	public void setVerticalScrolling() {
		scrollBoth = false;
		scrollHorizontally = false;
	}
	/*------------------------------------------------------------*/
	/**
	 * scroll showRows to the bottom 
	 */
	public void scrollToBottom() {
		if ( scv == null )
			return;
		scv.post(new Runnable() {            
		    @Override
		    public void run() {
		           scv.fullScroll(View.FOCUS_DOWN);              
		    }
		});
	}
	/*------------------------------------------------------------*/
	/**
	 * scroll showRows to the right 
	 */
	public void scrollToRight() {
		if ( hscv == null )
			return;
		hscv.post(new Runnable() {            
		    @Override
		    public void run() {
		           hscv.fullScroll(View.FOCUS_RIGHT);              
		    }
		});
	}
	/*------------------------------------------------------------*/
	
	/**
	 * set 2D scrolling for showRows (default)
	 */
	public void set2DScrolling() {
		scrollBoth = true;
	}
	/*------------------------------------------------------------*/
	private String format(String data, String fname, String type) {
		String ret;
		if ( (ret = formatByModifier(data, fname)) != null)
			return(ret);
		return(formatByType(data, type));
	}
	/*------------------------------------------------------------*/
	private String formatByType(String data, String type) {
		String ret;
		if ( type.compareTo(Mmodel.type_int) == 0 )
			ret = intFmt(data);
		else if ( type.compareTo(Mmodel.type_float) == 0 )
			ret = floatFmt(data);
		else if ( type.compareTo(Mmodel.type_money) == 0 )
			ret = moneyFmt(data);
		else if ( type.compareTo(Mmodel.type_date) == 0 )
			ret = data ;
		else if ( type.compareTo(Mmodel.type_time) == 0 )
			ret = data;
		else if ( type.compareTo(Mmodel.type_datetime) == 0 )
			ret = datetimeFmt(data);
		else if ( type.compareTo(Mmodel.type_yesno) == 0 )
			ret = ( data.compareTo("1") == 0 ) ? "Yes" : "No" ;
		else
			ret = data ;
		
		return(ret);
	}
	/*------------------------------------------------------------*/
	/**
	 * format double for viewing, with that many decimals, and comma separators.
	 */
	public static String format(double d, int decimals) {
		return(Mutils.format(d, decimals, true));
	}
	/*------------------------------------------------------------*/
	/**
	 * money formatter/modifier
	 */
	public static String moneyFmt(String data) {
		double d = Double.parseDouble(data);
		return(format(d, 2));
	}
	/*------------------------------------------------------------*/
	/**
	 * floating point formatter/modifier (3 decimals)
	 */
	public static String floatFmt(String data) {
		double d = Double.parseDouble(data);
		return(format(d, 3));
	}
	/*------------------------------------------------------------*/
	private static NumberFormat nf = null;
	public static String intFmt(String data) {
		try {
			if ( nf == null )
				nf = NumberFormat.getInstance();
			int n = Integer.parseInt(data);
			return(nf.format(n));
		} catch ( NumberFormatException e ) {
			return(data);
		} catch ( Exception e ) {
			return(data);
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * datetime formatter/modifier
	 */
	public static String datetimeFmt(String data) {
		return(data);
	}
	/*------------------------------------------------------------*/
	private void _msg(String msg, int length) {
		Toast toast = Toast.makeText(ctx, msg, length);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	/*------------------------------*/
	/**
	 * a toast message
	 */
	public void msg(String msg) {
		_msg(msg, Toast.LENGTH_SHORT);
	}
	/*------------------------------*/
	/**
	 * a long toast message (if isLong=true)
	 */
	public void msg(String msg, boolean isLong) {
		// this function is always called with LENGTH_LONG
		_msg(msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
	}
	/*------------------------------*/
	/**
	 * a toast message with specified length
	 */
	public void msg(String msg, int milliSeconds) {
		// while passing milliSeconds to toast is deprecated it works, and is useful
		_msg(msg, milliSeconds);
	}
	/*------------------------------------------------------------*/
	public void about() {
		alert(mutils.about());
	}
	/*------------------------------------------------------------*/
}
