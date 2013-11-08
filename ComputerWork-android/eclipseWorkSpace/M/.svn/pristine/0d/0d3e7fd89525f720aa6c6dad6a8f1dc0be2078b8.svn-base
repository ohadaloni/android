package com.theora.M;
/*------------------------------------------------------------*/
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/*------------------------------------------------------------*/
public class MdbHelper extends SQLiteOpenHelper {
	// private Context context = null;
	// private String dbName = null;
	// private Mutils util = null;
	public SQLiteDatabase db = null;
	/*------------------------------------------------------------*/
	public MdbHelper(Context context, String dbName) {
		super(context, dbName, null, 1);
		// this.context = context;
		// this.dbName = dbName;
		// this.util = new Mutils(this.context);
		/*	util.log("MdbHelper:construct: dbName=" + this.dbName);	*/
	}
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		/*	util.log("MdbHelper:onCreate");	*/
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*	util.log("MdbHelper:onCreate");	*/
	}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
