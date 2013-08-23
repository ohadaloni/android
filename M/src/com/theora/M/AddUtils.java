package com.theora.M;

import com.theora.M.Mcontroller;
import com.theora.M.Mmodel;
import com.theora.M.Mtime;

import android.app.Activity;
/*------------------------------------------------------------*/
public class AddUtils {
	/*------------------------------------------------------------*/
	@SuppressWarnings("unused")
	private Activity a;
	private Mcontroller m;
	private static final String lastCalculation = "Last Calculation";
	/*------------------------------------------------------------*/
	public AddUtils(Activity a) {
		this.a = a;
		m = new Mcontroller(a, "mdb");
	}
	/*------------------------------------------------------------*/
	/**
	 * delete this instance from the database tables
	 */
	public void deleteInstance(int instanceId) {
        m.utils.logInfo();
		m.model.sql("delete from addItems where instanceId = " + instanceId);
		m.model.sql("delete from addInstances where id = " + instanceId);
	}
	/*------------------------------------------------------------*/
	/*
	 * create a new calculation instance
	 * the decimals are from the last calculation performed
	 * remove any unNamed leftovers
	 */
	public int newCalculation() {
        m.utils.logInfo();
		int decimals;
		int lastId = lastId();
		if ( lastId > 0 )
			decimals = decimals(lastId);
		else
			decimals = 2 ;
		String sql = "select id from addInstances where name = '" + lastCalculation + "'" ;
		int noNameId = m.model.getInt(sql);
		if ( noNameId > 0 )
			deleteInstance(noNameId);
		return(newInstance(lastCalculation, decimals));
	};
	/*------------------------------------------------------------*/
	/**
	 * create a new instance record with the given name
	 */
	public int newInstance(String name, int decimals) {
		String nv[][] = {
				{ "name", name }
				, { "decimals", "" + decimals }
				, { "created", Mtime.dateTimeNow() }
				, { "updated", Mtime.dateTimeNow() }
				, { "touch", "@strftime('%s','now')" }
				
		};
		int newId = m.model.insert("addInstances", nv);
		return(newId);

	}
	/*------------------------------------------------------------*/
	/**
	 * the number of decimals used by this instance
	 */
	public int decimals(int id) {
		String sql = "select decimals from addInstances where id = " + id;
		int decimals = m.model.getInt(sql);
		return(decimals);
	}
	/*------------------------------------------------------------*/
	/**
	 * the last instance
	 */
	public int lastId() {
		return(m.model.getInt("select id from addInstances order by touch desc limit 1"));
	}
	/*------------------------------------------------------------*/
	/**
	 * create the tables: addInstances and addItems
	 */
	public void createTables() {
//		m.model.sql("drop table if exists addInstances");
		String sql1 = "create table if not exists addInstances (" +
			"id integer primary key autoincrement" +
			", name char(80) collate nocase" +
			", decimals int" +
			", created datetime" +
			", updated datetime" +
			", touch int" +
			", unique (name)" +
			" )";
		m.model.sql(sql1);
//		m.model.sql("drop table if exists addItems");
		String sql2 = "create table if not exists addItems (" +
		"id integer primary key autoincrement" +
		", instanceId int" +
		", label char(20)" +
		", theNumber double" +
		" )";
		m.model.sql(sql2);
	}
	/*------------------------------------------------------------*/
	public String makeNewName(String name) {
		String tryLabel = name;
		for(int i=2;;i++) {
			if ( ! isName(tryLabel) )
				return(tryLabel);
			tryLabel = name + " #" + i;
		}
	}
	/*-------------------------------*/
	private boolean isName(String name) {
		String sql = String.format("select count(*) from addInstances where name = '%s'", m.model.str(name));
		return(m.model.getInt(sql) > 0);
	}
	/*------------------------------------------------------------*/
	private int clone(int instanceId, String name) {
		String sql = "select decimals from addInstances where id = " +instanceId ;
		int decimals = m.model.getInt(sql);
		int newId = newInstance(name, decimals);
		String copyItems = "insert into addItems ( instanceId, label, theNumber) " + 
					" select " + newId + ", label, theNumber from addItems " +
					" where instanceId = " + instanceId ;
		m.model.sql(copyItems);
		return(newId);
	}
	/*------------------------------------------------------------*/
	/**
	 * save the calculation -
	 * if this is the current calculation just change the label
	 * otherwise, copy the data with the new name
	 */
	public int save(int rowId, String name) {
        m.utils.logInfo();
		String originalName = name(rowId);
		if ( originalName == null || originalName.length() == 0 || originalName.compareTo(lastCalculation) == 0) {
			m.model.update("addInstances", rowId, "name", makeNewName(name));
			return(rowId);
		}
		int newId = clone(rowId, name);
		return(newId);
	}
	/*------------------------------------------------------------*/
	/**
	 * timestamp this instance
	 */
	public void touch(int instanceId) {
		if ( instanceId <= 0 )
			return;
		String sql = "update addInstances set touch = strftime('%s','now') where id = " + instanceId;
		m.model.sql(sql);
	}
	/*------------------------------------------------------------*/
	/**
	 * update the number of decimals used in this instance
	 */
	public void updateDecimals(int decimals, int id) {
        m.utils.logInfo("" + decimals);
		String sql = "update addInstances set decimals = " + decimals + " where id = " + id;
		m.model.sql(sql);
	}
	/*------------------------------------------------------------*/
	/**
	 * add integer item to database for this instance
	 */
	public void addInt(int n, int instanceId) {
        m.utils.logInfo();
		addString("" + n, instanceId);
	}
	/*------------------------------------------------------------*/
	/**
	 * add double valued item to database for this instance with exact decimals 
	 */
	public void addDouble(double d, int instanceId, int decimals) {
        m.utils.logInfo();
		String s = Mutils.format(d, decimals);
		addString(s, instanceId);
	}
	/*------------------------------------------------------------*/
	private void addString(String s, int instanceId) {
		String nv[][] = {
				{ "instanceId", "" + instanceId }
				, { "theNumber", s }
		};
		m.model.insert("addItems", nv);

	}
	/*------------------------------------------------------------*/
	/**
	 * total this calculation
	 */
	public double total(int instanceId) {
		String sql = "select sum(theNumber) from addItems where instanceId = " + instanceId;
		String ret = m.model.getString(sql);
		if ( ret == null )
			return(0.0);
		return((new Double(ret)).doubleValue());
	}
	/*------------------------------------------------------------*/
	/**
	 * what is the Mmodel type: int, money, or float
	 * @param decimals
	 */
	public String mModelType(int decimals) {
		if ( decimals == 0 )
			return(Mmodel.type_int);
		if ( decimals == 2 )
			return(Mmodel.type_money);
		return(Mmodel.type_float);
	}
	/*------------------------------------------------------------*/
	/**
	 * update label of an item
	 */
	public void updateLabel(int rowId, String label) {
		if ( label == null || label.length() == 0 )
			return;
		m.model.update("addItems", rowId, "label", label);
	}
	/*------------------------------------------------------------*/
	private void update(int rowId, String value) {
		if ( value == null || value.length() == 0 ) {
			m.utils.logError("null value");
			return;
		}
		m.model.update("addItems", rowId, "theNumber", value);
	}
	/*------------------------------------------------------------*/
	private String selectNumber(int rowId) {
		return("select theNumber from addItems where id = " + rowId);
	}
	/*------------------------------------------------------------*/
	/**
	 * get the int value of an item
	 */
	public int getInt(int rowId) {
		return(m.model.getInt(selectNumber(rowId)));

	}
	/*------------------------------------------------------------*/
	/**
	 * get the double value 
	 */
	public double getDouble(int rowId) {
		return(m.model.getDouble(selectNumber(rowId)));
	}
	/*------------------------------------------------------------*/
	/**
	 * change item value, use decimal to store exact string
	 */
	public void change(double d, int rowId, int decimals) {
		String sValue = Mutils.format(d, decimals);
		update(rowId, sValue);
	}
	/*------------------------------------------------------------*/
	/**
	 * the label of an item
	 */
	public String label(int itemId) {
		return(m.model.getString("select label from addItems where id = " + itemId));
	}
	/*------------------------------------------------------------*/
	/**
	 * the name of a calculation (instance)
	 */
	public String name(int instanceId) {
		String ret = m.model.getString("select name from addInstances where id = " + instanceId);
		if ( ret == null || ret.compareTo(lastCalculation) == 0 )
			return(null);
		return(ret);
	}
	/*------------------------------------------------------------*/
	/**
	 * rename this instance
	 */
	public void rename(int instanceId, String s) {
		m.model.update("addInstances", instanceId, "name", makeNewName(s));
	}
	/*------------------------------------------------------------*/
	/**
	 * guess if this name was made by autoName
	 */
	public boolean isAutoName(String name) {
		return(
				name != null &&
				name.length() != 0 &&
				Character.isDigit(name.charAt(0)) &&
				name.contains("/") && 
				name.contains(":")
		);
	}
	/*------------------------------*/
	/**
	 * offer/generate a name for an instance
	 */
	public String autoName() {
		int today = Mdate.today();
		int ymd[] = Mdate.separate(today);
		String now = Mtime.fmt(Mtime.now());
		String label = String.format("%d/%d %s", ymd[1], ymd[2], now);
		return(label);
	}
	/*------------------------------------------------------------*/
}
