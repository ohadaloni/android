package com.theora.taskReminder;
/*------------------------------------------------------------*/
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/*------------------------------------------------------------*/
public class ReminderListActivity extends ListActivity {
	/*------------------------------------------------------------*/
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.reminder_list);

		String[] items = new String[] { "Foo", "Bar", "Fizz", "Bin" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.reminder_row, R.id.text1, items);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
		Log.d("tasks", "ReminderListActivity::onCreate");
	}

	/*------------------------------------------------------------*/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// This is an example of how to make a chooser menu appear
		/* Intent i = new Intent(Intent.ACTION_SEND); i.setType("text/plain"); */
		/* i.putExtra(Intent.EXTRA_TEXT, "Hey Everybody!"); */
		/* i.putExtra(Intent.EXTRA_SUBJECT, "My Subject"); Intent chooser = */
		/* Intent.createChooser(i, "Who Should Handle this?"); */
		/* startActivity(chooser); */

		Log.d("tasks", "ReminderListActivity::onListItemClick:" + position
				+ ":" + id);
		Intent i = new Intent(this, ReminderEditActivity.class);
		i.putExtra("position", position);
		i.putExtra("rowId", id);
		i.putExtra("newRow", 1);
		startActivity(i);
	}

	/*------------------------------------------------------------*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.d("tasks", "ReminderListActivity::onCreateContextMenu");
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.list_menu_item_longpress, menu);
	}

	/*------------------------------------------------------------*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		Log.d("tasks", "ReminderListActivity::onCreateOptionsMenu");
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.list_menu, menu);
		return true;
	}

	/*------------------------------------------------------------*/
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete:
			// Delete the task
			Log.d("tasks",
					"ReminderListActivity::onContextItemSelected - Delete");
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/*------------------------------------------------------------*/
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_insert:
			Log.d("tasks",
					"ReminderListActivity::onMenuItemSelected: menu_insert");
			createReminder();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/*------------------------------------------------------------*/
	private static final int ACTIVITY_CREATE = 0;

	private void createReminder() {
		Intent i = new Intent(this, ReminderEditActivity.class);
		i.putExtra("newRow", 2);
		Log.d("tasks", "ReminderListActivity::createReminder");
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	/*------------------------------------------------------------*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d("tasks", "ReminderListActivity::onActivityResult:" + requestCode
				+ ":" + resultCode);
		// Reload the list here
	}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
