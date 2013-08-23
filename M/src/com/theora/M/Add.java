package com.theora.M;

import com.theora.M.Mcontroller;
import com.theora.M.Mmodel;
import com.theora.M.MmodelRow;
import com.theora.M.Mutils;
import com.theora.M.Mview;
import com.theora.M.Mview.Modifier;
import com.theora.M.NumberDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class Add extends Activity implements Mview.RowClickListener, Mview.RowLongPressListener {
	/*------------------------------------------------------------*/
	private static final int historyMagic = 1812;
	private static final String historyLabel = new String("M");
	private static final String addLabel = new String("+");
//	private static final String decimalsLabel = "."; // not all unicode chars show in all devices
	private static final String decimalsLabel = "\u2027"; // a unicode char: a dot, the center
	private static final String newLabel = "C";
	private static final String saveLabel = "M+";
	private static final float buttonTextSize = 36f;
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private AddUtils utils = null;
	private TableLayout topView;
	private LinearLayout showRowsView;
	Display display;
	private MmodelRow[] rows;
	private Button addButton;
	private Button historyButton;
	private Button saveButton;
	private Button resetButton;
	private Button setDecimalsButton;
	private int decimals;
	private int currentId = 0;
	private boolean hScroll;
	/*------------------------------------------------------------*/
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		m = new Mcontroller(this, "mdb");
		utils = new AddUtils(this);
		display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        utils.createTables();
        setStartingCurrentId();
        decimals = utils.decimals(currentId);
        m.utils.logInfo("Starting");
    }
	/*------------------------------------------------------------*/
	// called right before onResume
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == historyMagic && resultCode != RESULT_CANCELED && resultCode > 0 )
			currentId = resultCode ;
	}
	/*------------------------------------------------------------*/
	@Override
	public void onResume() {
		super.onResume();
		layout();
	}
	/*------------------------------------------------------------*/
	@Override
	public void onPause() {
		super.onPause();
		utils.touch(currentId);
	}
	/*------------------------------------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Horizontal Scrolling");
        menu.add(0, 1, 1, "Other Applications");
		return(true);
	}
	/*------------------------------*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
        	hScroll = ! hScroll;
        	resume();
            return true;
        case 1:
        	startActivity(new Intent(this, AppsByOhad.class));
            return true;
		}
		return false;
	}
	/*------------------------------------------------------------*/
	/*
	 * called also with screen re-orientation
	 * if the last calculation is recent enough, start with it
	 * otherwise, start with a new one
	 */
	private void setStartingCurrentId() {
		String sql = "select id, strftime('%s','now') - touch as secondsSinceLast from addInstances order by touch desc limit 1";
		MmodelRow row = m.model.getRow(sql);
		if ( row == null ) {
			currentId = utils.newCalculation();
			return;
		}
		int secondsSinceLast = row.getInt("secondsSinceLast");
		if ( secondsSinceLast  < 5*60 )
			currentId = row.id();
		else
			currentId = utils.newCalculation();

	}
	/*------------------------------------------------------------*/
	private void resume() {
		layout();
	}
	/*------------------------------------------------------------*/
	private void layout() {
		setTopView();
		setInstanceTitle();
		showRows();
		showButtons();
		setContentView(topView);
		m.view.scrollToBottom();
		m.view.scrollToRight();
	}
	/*------------------------------------------------------------*/
	private void setTopView() {
		topView = new TableLayout(this);
		TableLayout.LayoutParams tlpr = new TableLayout.LayoutParams(  
				TableLayout.LayoutParams.FILL_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		topView.setLayoutParams(tlpr);
		for(int i=0;i<17;i++)
			topView.setColumnStretchable(i, true);
		topView.setBackgroundColor(Mview.white);
	}
	/*------------------------------------------------------------*/
	private void setInstanceTitle() {
		double dTotal = utils.total(currentId);
		String sTotal = Mview.format(dTotal, decimals);
		String name = utils.name(currentId);
		if ( name != null && name.length() != 0 )
			setTitle(name + ": " + sTotal);
		else
			setTitle(sTotal);
	}
	/*------------------------------------------------------------*/
	private void showButtons() {
		addButton = button(addLabel);
		historyButton  = button(historyLabel);
		saveButton  = button(saveLabel);
		resetButton  = button(newLabel);
		setDecimalsButton  = button(decimalsLabel);
		
		TableRow tr = new TableRow(this);
		tr.addView(setDecimalsButton);
		tr.addView(historyButton);
		tr.addView(saveButton);
		tr.addView(resetButton);
		tr.addView(addButton);
		
		topView.addView(tr);
	}
	/*------------------------------------------------------------*/
	private void showRows() {
		getRows();
		if ( rows == null || rows.length == 0 )
			makeDummyRows();
		totalRows();
		showRowsView = new LinearLayout(this);
		m.view.reset();
		m.view.setColumnColor("total", Mview.zebra0);
		m.view.setShowRowsView(showRowsView);
		m.view.setNoHeaders();
		if ( ! hScroll )
	        m.view.setVerticalScrolling();
        m.view.stretchLeft();
		Modifier fmt = new Modifier() {
			@Override
			public String modify(String value) {
				if ( value == null ) {
			        m.utils.logError("value is null");
					return("0");
				}
				double d;
				try {
					d = new Double(value);
				} catch (Exception e) {
//			        m.utils.logError("Double(value): exception");
			        m.utils.logError("Double(value): exception" + value);
					return("0");
				}
				String ret = Mview.format(d, decimals);
				return(ret);
			}
		};
		m.view.registerModifier("theNumber", fmt);
		m.view.registerModifier("total", fmt);
		m.view.showRows(rows, Mview.sizeFromRows(rows.length));
		m.view.setRowClickListener(this);
		m.view.setRowLongPressListener(this);
		topView.addView(showRowsView);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)showRowsView.getLayoutParams();
		int displayHeight = display.getHeight();
		int rotation = display.getRotation();
		boolean isSideWays = ( rotation % 2 != 0 );
		int showRowsPercent = isSideWays ? 60 : 80 ;
		int showRowsHeight = displayHeight * showRowsPercent / 100 ;
		params.height = showRowsHeight;
		params.width = LinearLayout.LayoutParams.FILL_PARENT;
		showRowsView.setLayoutParams(params);
		showRowsView.setGravity(Gravity.RIGHT);

	}
	/*------------------------------------------------------------*/
	private void textButtonClicked(String text) {
		 if ( text.compareTo(addLabel) == 0 )
			 addItemDialog();
		else if ( text.compareTo(decimalsLabel) == 0 )
			setDecimalsDialog();
		else if ( text.compareTo(historyLabel)== 0 )
			startActivityForResult(new Intent(this, AddHistory.class), historyMagic);
		else if ( text.compareTo(saveLabel)== 0 )
			save();
		else if ( text.compareTo(newLabel)== 0 ) {
	        currentId = utils.newCalculation();
	        addItemDialog();
		} else
			m.utils.logError("Unknown text Button: " + text);
	}
	/*------------------------------------------------------------*/
	private void makeDummyRows() {
		int length = 8;
		String dummyLabels[] = { "Press", "the", "+", "sign", "to", "start", "adding", "numbers"};
		rows = new MmodelRow[length];
		for(int i=0;i<length;i++) {
			int dollars = Mutils.rand(1, 7);
			int cents = Mutils.rand(1, 9) * 10; // don't get 98.7 as opposed to 98.70 or 98.07
			String value = dollars + "." +  cents;
			rows[i] = new MmodelRow();
			rows[i].push("id", "0", Mmodel.type_int);
			rows[i].push("label", dummyLabels[i%8], Mmodel.type_string);
			rows[i].push("theNumber", value, utils.mModelType(decimals));
		}
	}
	/*------------------------------------------------------------*/
	private void save() {
        m.utils.logInfo();
		String title = "Label";
		String currentName = utils.name(currentId);
		String autoName;
		String suggestedName;
		String msg;
		String whenOkingEmpty;
		if ( currentName == null || currentName.length() == 0 || utils.isAutoName(currentName)) {
			autoName = utils.autoName();
			msg = "Enter label (or OK for " + autoName + ")";
			suggestedName = "";
			whenOkingEmpty = autoName;
		} else {
			msg = "Enter Label";
			suggestedName = utils.makeNewName(currentName);
			whenOkingEmpty = suggestedName;
		}
		final String finalWhenOkingEmpty = whenOkingEmpty;
    	m.view.prompt(title, msg, suggestedName, new McallbackWithString() {
			@Override
			public void f(String s) {	
				if ( s == null)
					return;
				String label;
				if ( s.length() == 0 )
					label = finalWhenOkingEmpty;
				else
					label = s;
				currentId = utils.save(currentId, label);
				setInstanceTitle();
			}
		});

	}
	/*------------------------------------------------------------*/
	private void getRows() {
		String sql = "select id, label, theNumber from addItems where instanceId = " +
				currentId + " order by id" ;
		rows = m.model.getRows(sql);
	}
	/*------------------------------------------------------------*/
	private void totalRows() {
		String type = utils.mModelType(decimals);
		double total = 0.0;
		for ( MmodelRow row : rows ) {
			total += row.getDouble("theNumber");
			row.push("total", Mutils.format(total, decimals), type);
		}
	}
	/*------------------------------------------------------------*/
	private Button button(final String text) {
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textButtonClicked(text);
			}
		};	
		Button b = new Button(this);
		b.setTextColor(Mview.blue);
		b.setTextSize(buttonTextSize);
		b.setPadding(4, 0, 4, 8);
		b.setText(text);
		b.setClickable(true);
		b.setOnClickListener(listener);
		return(b);
	}
	/*------------------------------------------------------------*/
	/*
	 * if the current calculation is empty,
	 * automatically bring up the new number dialog
	 */
//	private void start() {
//		String sql = "select count(*) from addItems where instanceId = " + currentId ;
//		int numItems = m.model.getInt(sql);
//		if ( numItems == 0 )
//			addItemDialog();
//	}	
	/*------------------------------------------------------------*/
	private void setDecimalsDialog() {
        m.utils.logInfo();
		McallbackWithInt cb = new McallbackWithInt() {
			@Override
			public void f(int n) {
				decimals = n;
				utils.updateDecimals(decimals, currentId);
				resume();
			}
		};
		NumberDialog d = new NumberDialog(this, "Decimals: ", cb, decimals);
		d.setSingleDigit();
		d.show();
	}
	/*------------------------------------------------------------*/
	private void addItemDialog() {
        m.utils.logInfo();
		if ( decimals == 0 )
			addIntDialog();
		else
			addDoubleDialog(null);
	}
	/*---------------------------*/
	private String addNumberDialogTitle() {
		double dTotal = utils.total(currentId);
		String sTotal = Mview.format(dTotal, decimals);
		String ret = sTotal + " + ?";
		return(ret);
	}
	/*---------------------------*/
	private void addIntDialog() {
		McallbackWithInt cb = new McallbackWithInt() {
			@Override
			public void f(int n) {
				if ( n != -1961 ) {
					utils.addInt(n, currentId);
					resume();
				}
			}
		};

		NumberDialog d = new NumberDialog(this, addNumberDialogTitle(), cb, null);
		d.setDoneLabel("=");
		d.show();
	}
	/*---------------------------*/
	private void addDoubleDialog(Double defaultValue) {
		McallbackWithDouble cb = new McallbackWithDouble() {
			@Override
			public void f(Double d) {
				if ( d != null ) {
					utils.addDouble(d, currentId, decimals);
					resume();
				}
			}
		};
		NumberDialog d = new NumberDialog(this, addNumberDialogTitle(), cb,  decimals, defaultValue);
		d.setDoneLabel("=");
		d.show();
	}
	/*-----------------------------------------------------------*/
	@Override
	public void rowLongPressed(final int rowId) {
		MmodelRow row = m.model.getById("addItems", rowId);
		double d = row.getDouble("theNumber");
		if ( d == -1961.0)
			d = 0.0;
		String value = Mview.format(d, decimals);

		String opts[] = { "Label", "Change", "Delete", "Never Mind" };
		m.view.select(value, opts, new McallbackWithString() {
			@Override
			public void f(String s) {
				if ( s == null || s.length() == 0 )
					return;
				else if ( s.compareTo("Label") == 0 )
					labelItem(rowId);
				else if ( s.compareTo("Change") == 0 )
					changeItem(rowId);
				else if ( s.compareTo("Delete") == 0 )
					m.model.delete("addItems", rowId);
				else if ( s.compareTo("Never Mind") == 0 )
					return;
				else
					m.utils.logError(s);
				resume();
			}
		});
	}
	/*------------------------------------------------------------*/
	@Override
	public void rowClicked(int rowId) {
		labelItem(rowId);
	}
	/*------------------------------------------------------------*/
	private void labelItem(final int rowId) {
		MmodelRow row = m.model.getById("addItems", rowId);
		String label = row.getValue("label");
		if ( label == null )
			label = "";
		double d = row.getDouble("theNumber");
		if ( d == -1961.0)
			d = 0.0;
		String value = Mview.format(d, decimals);
		String title = "Label for " + value;
		m.view.prompt(title, null, label, new McallbackWithString() {
			@Override
			public void f(String s) {
				utils.updateLabel(rowId, s);
				resume();
			}
		});	
	}
	/*------------------------------------------------------------*/
	private void changeItem(final int rowId) {
		MmodelRow row = m.model.getById("addItems", rowId);
		double d = row.getDouble("theNumber");
		if ( d == -1961.0)
			d = 0.0;
		String value = Mview.format(d, decimals);
		String label = row.getValue("label");
		String title;
		if ( label != null )
			title = "Change " + label ;
		else
			title = "Change " + value;
		McallbackWithDouble cb = new McallbackWithDouble() {
			@Override
			public void f(Double d) {
				if ( d != null ) {
					utils.change(d.doubleValue(), rowId, decimals);
					resume();
				}
			}
		};

		NumberDialog di = new NumberDialog(this, title, cb,  decimals, d);
		di.show();

	}
	/*------------------------------------------------------------*/

}