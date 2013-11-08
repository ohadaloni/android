package com.theora.M;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/*------------------------------------------------------------*/
public class Mactivity extends Activity implements Mview.RowLongPressListener, Mview.RowClickListener {
	/*------------------------------------------------------------*/
	private Mcontroller m;
	/*------------------------------------------------------------*/
	public Mactivity() {
		m = new Mcontroller(this, "mdb");
	}
	/*------------------------------------------------------------*/
	// called right before onResume
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			finish() ;
	}
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private void startNautilus(int gameId) {
        Intent intent = new Intent(this, Nautilus.class);
        intent.putExtra("com.theora.Nautilus.gameId", gameId);
		startActivityForResult(intent, 12345);
	}
	/*------------------------------------------------------------*/
    @SuppressWarnings("unused")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( m != null ) {
//        	String dt = "2011-06-26 9:58:23.678";
//        	int n = Mdate.datetimeToUnixTime(dt);
//        	int mdate = Mdate.fromUnixTime(n);
//        	String msg  = dt + " - " + n + " - " + Mdate.fmt(mdate);
//        	m.utils.log(msg);
//        	finish();
        	startActivityForResult(new Intent(this, MedLog.class), 12345);
//        	startNautilus(3);
//        	m.view.about();
        	return;
        }
        if ( ! createTableAuthors() )
			return;
        // createTableBooks();
        if ( ! addAuthors() )
			return;
        MmodelRow[] authors = m.model.getRows("select id, first as 'First Name', last as 'Last Name', dob as 'Date of Birth' from authors order by last, first");
    	m.view.reset();
        m.view.setRowLongPressListener(this);
//      m.view.setVertticalScrolling();
        m.view.setRowClickListener(this);
        // m.view.set2DScrolling();
        if ( authors != null) {
        	m.view.rightAlign("Last Name");
        	m.view.registerModifier("Last Name", new Mview.Modifier() {
				@Override
				public String modify(String value) {
					return(">> " + value);
				}
			});
        	m.view.showRows(authors, "authors", Mview.LARGE);
        } else {
			String msg = "No rows in authors";
			m.utils.log(msg);
			m.utils.msg(msg);
		}
    	McallbackWithInt intChosen = new McallbackWithInt() {
    		@Override
    		public void f(int i) {
    			if ( i == -1961 )
    				m.view.msg("Number Pad Cancelled");
    			else
    				m.view.msg("Number Pad: " + i);
    		}
    	} ;
//    	new NumberDialog(this, intChosen).show();
    }
	/*------------------------------------------------------------*/
    public void rowClicked(int id) {
    	MmodelRow row = m.model.getRow("select * from authors where id = " + id);
    	String msg;
    	if ( row == null ) {
    		m.utils.msg("clicked: counldn't get row " + id + "from database");
    		return;
    	}
    	msg = String.format("clicked: id=%d, author='%s %s'",
    			id,
    			row.getValue("first"),
    			row.getValue("last")
    		);
        m.utils.msg(msg);
    	// Execute a test
    	String last = row.getValue("last");
    	if ( last.compareTo("Adams") == 0 ) {
    		startActivity(new Intent(this, Adams.class));
        	m.utils.logInfo("infoTest");
    	} else if ( last.compareTo("Asimov") == 0 ) {
    		startActivity(new Intent(this, Asimov.class));
    	} else if ( last.compareTo("Dahl") == 0 ) {
    		startActivity(new Intent(this, Dahl.class));
    	} else if ( last.compareTo("Lem") == 0 ) {
    		startActivity(new Intent(this, Lem.class));
    	} else if ( last.compareTo("Christie") == 0 ) {
            startActivity(new Intent(this, Christie.class));
    	} else if ( last.compareTo("Dickens") == 0 ) {
            startActivity(new Intent(this, Add.class));
    	}
    		
    }
	/*------------------------------------------------------------*/
    private boolean createTableAuthors(){
    	m.model.sql("drop table authors");
    	String sql = "create table if not exists authors ( " +
    		"id integer primary key autoincrement" +
    		", first char(80)" +
    		", last char(80)" +
    		", dob date" +
    		" )";
    	boolean ret = m.model.sql(sql);
    	return(ret);
    }
	/*------------------------------------------------------------*/
    public int addAuthor(String first, String last, String dob) {
    	String sql = String.format(
    		"insert into authors ( first, last, dob ) values ( '%s', '%s', '%s' )",
    		first, last, dob);
    	int authorId  = m.model.insert(sql);
		if ( authorId == 0 )
			m.utils.log(String.format("addAuthor: Can not add %s %s", first, last));
    	return(authorId);
    }
	/*------------------------------------------------------------*/
    public boolean addAuthors() {
    	int numAuthors = m.model.rowNum("authors");
    	/*	m.util.log(String.format("addAuthors: authors has %d rows", numAuthors));	*/
    	if (  numAuthors > 0 )
    		return(true);
		addAuthor("Roald", "Dahl", "1916-09-13");
		addAuthor("Douglas", "Adams", "1952-03-11");
		addAuthor("Isaac", "Asimov", "1920-01-02");
		addAuthor("Charles", "Dickens", "1812-02-07");
		addAuthor("Robert", "Heinlein", "1907-07-07");
		addAuthor("Stanislaw", "Lem", "1921-09-12");
		addAuthor("J. R. R.", "Tolkien", "1892-01-03");
		addAuthor("William", "shakespeare", "1584-04-26");
		addAuthor("Agatha", "Christie", "1890-09-15");
    	numAuthors = m.model.rowNum("authors");
    	/*	m.util.log(String.format("addAuthors: authors has %d rows", numAuthors));	*/
		return(numAuthors > 0);

    }
	/*------------------------------------------------------------
    private boolean createTableBooks() {
    	m.util.log("createTableBooks");
    	return(true);
    }
	/*------------------------------------------------------------*/
	@Override
	public void rowLongPressed(int rowId) {
		String name = m.model.getString("select first || ' ' || last from authors where id = " + rowId);
		m.view.msg("Long Pressed: " + name);
	}
}
/*------------------------------------------------------------*/
