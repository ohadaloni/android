
package com.theora.Events;

import com.theora.M.Mutil;
import static android.provider.BaseColumns._ID;
import static com.theora.Events.Constants.TABLE_NAME;
import static com.theora.Events.Constants.TIME;
import static com.theora.Events.Constants.TITLE;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventsData extends SQLiteOpenHelper {
   private static final String DATABASE_NAME = "events.db";
   private static final int DATABASE_VERSION = 1;
   private Context context = null;
   private Mutil mutil = null;

   /** Create a helper object for the Events database */
   public EventsData(Context ctx) { 
      super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
      this.context = ctx;
      this.mutil = new Mutil(this.context);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
	   String sql = "CREATE TABLE " + TABLE_NAME + " (" + _ID
       	+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIME
       	+ " INTEGER," + TITLE + " TEXT NOT NULL);";
	   
	   
	  mutil.log(sql);
      db.execSQL(sql);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	   // TODO: this deletes all the data on upgrade
      db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
      onCreate(db);
   }
}