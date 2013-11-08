package com.theora.M;

import android.database.Cursor;

/*------------------------------------------------------------*/
public class MmodelRow {
	/*------------------------------------------------------------*/
	public int cnt = 0;
	public String names[] = null;
	public String values[] = null;
	public String types[] = null;
	/*------------------------------------------------------------*/
	/**
	 * construct a row instance from a database cursor
	 */
	public MmodelRow(Cursor c) {
		cnt = c.getColumnCount();
		names = new String[cnt];
		values = new String[cnt];
		types = new String[cnt];
		names = c.getColumnNames();
		for(int i=0;i<cnt;i++)
			values[i] = c.getString(i);
	}
	/*---------------------------------*/
	/**
	 * construct an empty row with no columns
	 * new columns can be push()'ed on later
	 */
	public MmodelRow() {
		// noop - there is nothing to do
	}
	/*------------------------------------------------------------*/
	/**
	 * the value of the i'th column
	 */
	public String value(int i) {
		return(values[i]);
	}
	/*------------------------------------------------------------*/
	/**
	 * the value of the column named
	 */
	public String getValue(String name) {
		for (int i=0;i<cnt;i++)
			if ( names[i].equals(name) )
				return(values[i]);
		return(null);
	}
	/*------------------------------------------------------------*/
	/**
	 * the value of the column named, alias for getValue
	 */
	public String getString(String name) {
		return(getValue(name));
	}

	/*------------------------------------------------------------*/
	/**
	 * the id of the row in the databases
	 */
	public int id() {
		String ret;
		if ( (ret = getValue("id")) == null )
			ret = getValue("_id");
		if ( ret == null )
			return(0);
		return(Integer.parseInt(ret));
		
	}
	/*------------------------------------------------------------*/
	/**
	 * value of name as an int
	 */
	public int getInt(String name) {
		String ret;
		if ( (ret = getValue(name)) == null )
			return(-1961);
		try {
			return(Integer.parseInt(ret));
		} catch (Exception e) {
			return(-1961);
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * value of name as a boolean
	 * if the value is a int number, all but zero are true
	 * otherwise, only the case insensitive word 'true' is true
	 * the default value is return when the the name is not at all in the row
	 */
	public boolean getBoolean(String name, boolean defaultValue) {
		int i;
		if ( (i=getInt(name)) >= 0 )
			return(i > 0);
		String ret;
		if ( (ret = getValue(name)) == null )
			return(defaultValue);
		return(ret.compareToIgnoreCase("true") == 0);
	}
	/*--------------------------*/
	/**
	 * value of name as a boolean
	 * if the value is a int number, all but zero are true
	 * otherwise, only the case insensitive word 'true' is true
	 */
	public boolean getBoolean(String name) {
		return(getBoolean(name, false));
	}
	/*------------------------------------------------------------*/
	/**
	 * value as a double
	 * returns a magic number -1961.0 on error
	 */
	public double getDouble(String name) {
		String ret;
		if ( (ret = getValue(name)) == null )
			return(-1961.0);
		try {
			return(Double.parseDouble(ret));
		} catch (Exception e) {
			return(-1961.0);
		}
	}
	/*------------------------------------------------------------*/
	/**
	 * get a date value as an Mdate integer
	 * 20110530 -> 20110530
	 * '2011-05-30' -> 20110530
	 * zero on error
	 */
	public int getDate(String name) {
		String ret;
		if ( (ret = getValue(name)) == null )
			return(0);
		return(Mdate.undash(ret));
	}
	/*------------------------------------------------------------*/
	/**
	 * get a time value as an Mtime integer
	 * see Mtime.scan()
	 * -1 is returned on error
	 */
	public int getTime(String name) {
		String ret;
		if ( (ret = getValue(name)) == null )
			return(-1);
		return(Mtime.scan(ret));
	}
	/*------------------------------------------------------------*/
	/**
	 * set the row types to the known set copied from a previous row
	 */
	public void setTypes(String[] types) {
		for(int i=0;i<cnt;i++)
			this.types[i] = types[i];
	}
	/*------------------------------------------------------------*/
	/**
	 * push a value at the end
	 */
	public void push(String name, String value, String type) {
		names = Mutils.arrayPush(names, name);
		values = Mutils.arrayPush(values, value);
		types = Mutils.arrayPush(types, type);
		cnt++;
	}
	/*------------------------------------------------------------*/
	public String csvHeaders() {
		String ret = Mutils.implode(",", names);
		return ret;
	}
	/*------------------------------------------------------------*/
	/**
	 * if there are commas or quotes, quote the string.
	 * if there are quotes, also replace each quote with two of them
	 */
	private String csvClean(String s) {
		int quote = s.indexOf('"');
		int comma = s.indexOf(',');
		if ( quote < 0 && comma < 0 )
			return(s);
		String ret = '"' + s.replaceAll("\"", "\"\"") + '"';
		return(ret);
	}
	/*-----------------------*/
	public String csv() {
		String data[];
		data = new String[cnt];
		for(int i=0;i<cnt;i++)
			data[i] = csvClean(values[i]);
		String ret = Mutils.implode(",", data);
		return ret;
	}
	/*------------------------------------------------------------*/
}
