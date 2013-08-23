package com.theora.M;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.theora.M.Mutils;
/*------------------------------------------------------------*/
public class Mmodel {
	/*------------------------------------------------------------*/
	private Context context = null;
	private String dbName = null;
	private static Mutils mutils = null;
	private MdbHelper helper = null;
	private String[] columnTypes = null;
	/*------------------------*/
	public static final String type_string = "string";
	public static final String type_int = "int";
	public static final String type_float = "float";
	public static final String type_money = "money";
	public static final String type_date = "date";
	public static final String type_time = "time";
	public static final String type_datetime = "datetime";
	public static final String type_yesno = "yesno";
	/*------------------------------------------------------------*/
	public Mmodel(Context context, String dbName) {
		this.context = context;
		mutils = new Mutils(this.context);
		this.dbName = dbName;
		helper = new MdbHelper(context, this.dbName);
		// log("Constructor: dbName=" + dbName);
	}
	/*------------------------------------------------------------*/
	/**
	 * logcat a message with tag Mmodel
	 */
	public void log(String msg) {
		Log.d("Mmodel", msg);
		mutils.msg(msg);
	}
	/*------------------------------------------------------------*/
	/*
	 * execute a sql query returning true if it succeeded
	 */
	public boolean sql(String s) {
		SQLiteDatabase db;
		try {
			db = helper.getWritableDatabase();
		} catch (SQLiteException e) {
			db = null ;
		}
		if ( db == null ) {
			mutils.logError("sql: Cannot getWritableDatabase");
			return(false);
		}
		try {
			db.execSQL(s);
			return(true);
		} catch ( SQLException e ) {
			log("sql: " + e.toString());
			log(s);
			return(false);
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * insert a row to the database using the given sql
	 * and return the row id
	 */
	public int insert(String insertSql) {
		if ( ! sql(insertSql) )
			return(0);
		return(getInt("select last_insert_rowid()"));
		
	}
	/*------------------------------------------------------------*/
	/**
	 * prepare a value for sql by pre-quoting single quotes.
	 */
	public String str(String value) {
		if ( value == null || value.length() == 0 )
			return(value);
		String ret = value;
		ret = ret.replace("'", "''");
		return(ret);
	}
	/*------------------------------------------------------------*/
	private String insertSql(String table, String[][] nameValuePairs) {
		String nv[][] = nameValuePairs;
		String ret = "insert into " + table + " ( " ;
		int i;
		String value;
		for(i=0;i<nv.length;i++)
			ret += nv[i][0] + (( i == nv.length - 1 ) ? "" : ", ");
		ret += " ) values ( ";
		for(i=0;i<nv.length;i++) {
			value = nv[i][1];
			if ( value.charAt(0) == '@' )
				value = value.substring(1);
			else
				value = "'" + str(value) + "'";
			if ( i < nv.length - 1 )
				value = value + ", ";
			ret += value;
		}
		ret += " )";
		return(ret);
	}
	/*------------------------------------------------------------*/
	/**
	 * insert a row of values from nameValuePairs into table
	 * return the row id on success or 0 on failure
	 */
	public int insert(String table, String[][] nameValuePairs) {
		String sql = insertSql(table, nameValuePairs);
		return(insert(sql));
	}
	/*------------------------------------------------------------*/
	public boolean update(String table, int rowId, String name, String value) {
		String nv[][] = {{name, value}};
		return(update(table, rowId, nv));
	}
	/*------------------------------------------------------------*/
	public boolean update(String table, int rowId, String[][] nameValuePairs) {
		String sql = "update " + table + " set" ;
		int num = nameValuePairs.length;
		for(int i=0;i<num;i++)
			sql += String.format(" %s='%s'%s",
					nameValuePairs[i][0],
					str(nameValuePairs[i][1]),
					(i == num - 1) ? "" : ","
				);
		sql += " where id = " + rowId ;
		return(sql(sql));
	}
	
	/*------------------------------------------------------------*/
	public boolean delete(String table, int rowId) {
		return(sql("delete from " + table + " where id = " + rowId));
	}
	/*------------------------------------------------------------*/
	/**
	 * number of rows in table
	 */
	public int rowNum(String tableName) {
		String sql = "select count(*) from " + tableName;
		return(getInt(sql));
	}
	/*------------------------------------------------------------*/
	/**
	 * get a single int number from database using query sql
	 */
	public int getInt(String sql) {
		try {
			String s;
			if ((s = getString(sql)) == null)
				return(-1961);
			return (Integer.parseInt(s));
		} catch (Exception e) {
			return (-1961);
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * get a single double number from database using query sql
	 */
	public double getDouble(String sql) {
		try {
			String s;
			if ((s = getString(sql)) == null)
				return(-1961.0);
			return (new Double(s).doubleValue());
		} catch (Exception e) {
			return(-1961.0);
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * get a single item from database as a string
	 */
	public String getString(String sql) {
		String strings[] = getStrings(sql);
		if ( strings == null || strings.length == 0 )
			return(null);
		return(strings[0]);
	}
	/*------------------------------------------------------------*/
	/**
	 * get a column of items from database using the sql query
	 * return and array of strings
	 */
	public String[] getStrings(String sql) {
		String ret[] = {}; 
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();
		} catch (SQLiteException  e1) {
			db = null;
		}
		if ( db == null ) {
			mutils.logError("getStrings: Cannot getReadableDatabase");
			return(null);
		}
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
		} catch ( SQLException e ) {
			log("getStrings: " + e.toString());
			log(sql);
			return(null);
		}
		if ( cursor == null ) {
			log("getStrings: cursor is still null. eh?");
			db.close();
			return(null);
		}
		
		if ( ! cursor.moveToFirst() ) {
			cursor.close();
			db.close();
			return(ret);
		}
		ArrayList<String> list = new ArrayList<String>();

		String str;
		while ( true ) {
			str = cursor.getString(0);
			list.add(str);
			if ( ! cursor.moveToNext() )
				break;
		}
		ret = new String[list.size()];
		ret = (String[])(list.toArray(ret));
		cursor.close();
		db.close();
		return(ret);
	}
	/*------------------------------------------------------------*/
	/**
	 * get from the database the row in the table identified by id
	 */
	public MmodelRow getById(String tname, int id) {
		return(getRow("select * from " + tname + " where id = " + id));
	}
	/*------------------------------------------------------------*/
	/**
	 * get one row of data using query
	 */
	public MmodelRow getRow(String sql) {
		MmodelRow rows[] = getRows(sql);
		if ( rows == null || rows.length < 1 )
			return(null);
		return(rows[0]);
	}
	/*------------------------------------------------------------*/
	/**
	 * reset all setting set by previous calls to set*
	 * set functions:
	 * setType()
	 */
	public void reset() {
		typesSet = null;
	}
	/*----------------------------------------------*/
	private ArrayList<typePair> typesSet = null;
	/*-----------------------------------*/	
	private class typePair {
		public String fname;
		public String type;
		public typePair(String fname, String type) {
			this.fname = fname;
			this.type = type;
		}
	}
	/*-----------------------------------*/	
	/**
	 * set the type of fname so Mmodel doesn't have to guess
	 * and Mview can correctly format data
	 */
	public void setType(String fname, String type) {
		if ( typesSet == null )
			typesSet = new ArrayList<typePair>();
		typesSet.add(new typePair(fname, type));
	}
	/*-----------------------------------*/
	private String typeBySet(String fname) {
		if ( fname == null || typesSet == null )
			return(null);
		int n = typesSet.size();
		typePair p;
		for(int i=0;i<n;i++) {
			p = typesSet.get(i);
			if ( p.fname.compareTo(fname) == 0 )
				return(p.type);
		}
		return(null);

	}
	/*-------------------------------------------------------------*/
	/**
	 * get rows from database
	 */
	public MmodelRow[] getRows(String sql) {
		MmodelRow ret[] = { }; 
		SQLiteDatabase db;
		try {
			db = helper.getReadableDatabase();
		} catch (SQLiteException e) {
			db = null;
		}
		if ( db == null ) {
			log("getRows: Cannot getReadableDatabase");
			return(null);
		}
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
		} catch ( SQLException e ) {
			log("getRows: " + e.toString());
			log(sql);
			return(null);
		}
		if ( cursor == null ) {
			log("getRows: cursor is still null. eh?");
			db.close();
			return(null);
		}
		
		if ( ! cursor.moveToFirst() ) {
			cursor.close();
			db.close();
			return(ret);
		}
		ArrayList<MmodelRow> list = new ArrayList<MmodelRow>();

		clearTypes();
		while ( true ) {
			MmodelRow row = new MmodelRow(cursor);
			setTypes(row);
			list.add(row);
			if ( ! cursor.moveToNext() )
				break;
		}

		ret = new MmodelRow[list.size()];
		ret = (MmodelRow[])(list.toArray(ret));
		cursor.close();
		db.close();
		return(ret);
	}
	/*------------------------------------------------------------*/
	private void clearTypes() {
		columnTypes = null;
	}
	/*----------------------------------*/
	private void setTypes(MmodelRow row) {
		if ( columnTypes != null ) {
			row.setTypes(columnTypes);
			return;
		}
		columnTypes = new String[row.cnt];
		for(int i=0;i<row.cnt;i++)
			columnTypes[i] = type(row, i);
		row.setTypes(columnTypes);
	}
	/*---------------------------------*/
	// a type can be set by the caller by issuing: m.model.reset(); m.model.setType(fname, Mmodel.type);
	// the name of a column can indicate types money date time datetime & yesno
	// the value determines if its date, float, int, or string.
	private String type(MmodelRow row, int n) {
		String ret;
		if ( ( ret = typeBySet(row.names[n])) != null )
			return(ret);
		if ( ( ret = Mmodel.typeByName(row.names[n])) != null )
			return(ret);
		if ( ( ret = Mmodel.typeByValue(row.values[n])) != null )
			return(ret);
		return(type_string);
	}
	/*------------------------------------------------------------*/
	// the name of a column can indicate types money date time datetime & yesno
	private static String typeByName(String name) {
		// String typeNames[] = { "string", "int", "float", "money", "date", "time", "datetime", "yesno" };
		if ( name == null || name.length() == 0 )
			return(null);
		if ( name.compareTo("id") == 0 || name.compareTo("_id") == 0 )
			return(type_int);
		if ( Mutils.strncmp(name, "is_", 3) )
			return(type_yesno);
		if ( Mutils.strncmp(name, "is", 2) && Character.isUpperCase(name.charAt(2)) )
			return(type_yesno);
		if (Mutils.stristr(name, "datetime"))
			return(type_datetime);
		if (Mutils.stristr(name, "date"))
			return(type_date);
		if (Mutils.stristr(name, "time"))
			return(type_time);
		if ( 
				Mutils.stristr(name, "money") ||
				Mutils.stristr(name, "price") ||
				Mutils.stristr(name, "cost") ||
				Mutils.stristr(name, "amount")
			)
			return(type_money);
		return(null);
	}
	/*------------------------------------------------------------*/
	/**
	 * guess the type of data based on the content of the data
	 * mostly private, it is also used by the Mdate scanner
	 * to quickly identify database formatted dates
	 * the value determines if its a float, int or date.
	 * if no guess can be made, string is returned for lack of better knowledge
	 */
	public static String typeByValue(String value) {
		if ( value == null )
			return(type_string);
		
		if ( Mutils.isNumeric(value)) {
			if ( value.contains(".") )
				return(type_float);
			else
				return(type_int);
		}
		if ( value.length() >= 10 &&
					value.charAt(4) == '-' &&
					value.charAt(7) == '-' &&
					Mutils.isNumeric(value.substring(0, 4)) &&
					Mutils.isNumeric(value.substring(5, 7)) &&
					Mutils.isNumeric(value.substring(8, 10))
				)
			return(type_date);
		return(type_string);
	}
	/*------------------------------------------------------------*/
	public String toCsv(MmodelRow[] rows) {
		int numRows = rows.length;
		if ( numRows == 0 )
			return null;
		String ret = "";
		ret += rows[0].csvHeaders() + "\r\n";
		for(int i=0;i<numRows;i++)
			ret += rows[i].csv() + "\r\n";
		return(ret);
	}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
