
package com.theora.Events;

import static android.provider.BaseColumns._ID;
import static com.theora.Events.Constants.TABLE_NAME;
import static com.theora.Events.Constants.TIME;
import static com.theora.Events.Constants.TITLE;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class Events extends Activity {
   private static String[] FROM = { _ID, TIME, TITLE, };
   private static String ORDER_BY = TIME + " DESC";
   private EventsData events;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main); 
      events = new EventsData(this); 
      try {
         addEvent("Hello, Android!"); 
         Cursor cursor = getEvents(); 
         showEvents(cursor); 
      } finally {
         events.close(); 
      }
   }

   private void addEvent(String string) {
      // Insert a new record into the Events data source.
      // You would do something similar for delete and update.
      SQLiteDatabase db = events.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(TIME, System.currentTimeMillis());
      values.put(TITLE, string);
      db.insertOrThrow(TABLE_NAME, null, values);
   }

   private Cursor getEvents() {
      // Perform a managed query. The Activity will handle closing
      // and re-querying the cursor when needed.
      SQLiteDatabase db = events.getReadableDatabase();
      Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null,
            null, ORDER_BY);
      startManagingCursor(cursor);
      return cursor;
   }

   private void showEvents(Cursor cursor) {
      // Stuff them all into a big string
      StringBuilder builder = new StringBuilder( 
            "Saved events:\n");
      while (cursor.moveToNext()) { 
         // Could use getColumnIndexOrThrow() to get indexes
         long id = cursor.getLong(0); 
         long time = cursor.getLong(1);
         String title = cursor.getString(2);
         builder.append(id).append(": "); 
         builder.append(time).append(": ");
         builder.append(title).append("\n");
      }
      // Display on the screen
      TextView text = (TextView) findViewById(R.id.text); 
      text.setText(builder);
   }

}
