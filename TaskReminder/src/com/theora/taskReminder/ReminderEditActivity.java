package com.theora.taskReminder;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

/*------------------------------------------------------------*/
public class ReminderEditActivity extends Activity {
	/*------------------------------------------------------------*/
	private static final int DATE_PICKER_DIALOG = 0;
	private static final int TIME_PICKER_DIALOG = 1;
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String TIME_FORMAT = "kk:mm";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";
	/*----------------------------------*/
	private RemindersDbAdapter mDbHelper;
	private Calendar mCalendar;
	private Button mDateButton;
	private Button mTimeButton;
	private EditText mTitleText;
	private Button mConfirmButton;
	private EditText mBodyText;
	/*------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new RemindersDbAdapter(this);
		setContentView(R.layout.reminder_edit);
		mConfirmButton = (Button) findViewById(R.id.confirm);
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		mCalendar = Calendar.getInstance();
		mDateButton = (Button) findViewById(R.id.reminder_date);
		mTimeButton = (Button) findViewById(R.id.reminder_time);
		registerButtonListenersAndSetDefaultText();
		
		// Log.d some info
		Intent intent = getIntent();
		if (intent == null) {
			Log.d("tasks", "OnCreate:  No Intent");
			return;
		}
		Bundle extras = intent.getExtras();
		if (extras == null) {
			Log.d("tasks", "ReminderEditActivity::OnCreate:  No Extras");
		} else {
			int rowId = (int) extras.getLong("rowId");
			int position = extras.getInt("position");
			Log.d("tasks", String.format(
					"ReminderEditActivity::OnCreate: rowId=%d, position=%d",
					rowId, position));
		}
	}

	/*------------------------------------------------------------*/

	private void registerButtonListenersAndSetDefaultText() {
		Log.d("tasks", "ReminderEditActivity::registerButtonListenersAndSetDefaultText - 0");
		mDateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("tasks", "registerButtonListenersAndSetDefaultText:Mdate.setOnClickListener:OnClickListener:onClick");
				showDialog(DATE_PICKER_DIALOG);
			}
		});
		Log.d("tasks", "ReminderEditActivity::registerButtonListenersAndSetDefaultText - 1");

		mTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("tasks", "registerButtonListenersAndSetDefaultText:Mtime.setOnClickListener:OnClickListener:onClick");

				showDialog(TIME_PICKER_DIALOG);
			}
		});
		Log.d("tasks", "ReminderEditActivity::registerButtonListenersAndSetDefaultText - 2");

		updateDateButtonText();
		Log.d("tasks", "ReminderEditActivity::registerButtonListenersAndSetDefaultText - 2");

		updateTimeButtonText();
		Log.d("tasks", "ReminderEditActivity::registerButtonListenersAndSetDefaultText - 2");
		
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
			saveState();
			setResult(RESULT_OK);
			Toast.makeText(ReminderEditActivity.this,
			getString(R.string.task_saved_message),
			Toast.LENGTH_SHORT).show();
			finish();
			}
			});
	}
		
	/*------------------------------------------------------------*/
	private void saveState() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		SimpleDateFormat dateTimeFormat = new
		SimpleDateFormat(DATE_TIME_FORMAT);
		String reminderDateTime =
			dateTimeFormat.format(mCalendar.getTime());
		long id = mDbHelper.createReminder(title, body, reminderDateTime);
	}
	/*------------------------------------------------------------*/
	@Override
	protected Dialog onCreateDialog(int id) {
		 switch (id) {
			case DATE_PICKER_DIALOG:
				return showDatePicker();
			case TIME_PICKER_DIALOG:
				return showTimePicker();
		}
		return super.onCreateDialog(id);
	}

	/*------------------------------------------------------------*/
	private DatePickerDialog showDatePicker() {
		DatePickerDialog datePicker = new DatePickerDialog(
				ReminderEditActivity.this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						mCalendar.set(Calendar.YEAR, year);
						mCalendar.set(Calendar.MONTH, monthOfYear);
						mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						updateDateButtonText();
					}
				}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
				mCalendar.get(Calendar.DAY_OF_MONTH));
		return datePicker;
	}
	/*------------------------------------------------------------*/
	private void updateDateButtonText() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		String dateForButton = dateFormat.format(mCalendar.getTime());
		mDateButton.setText(dateForButton);
	}
	/*------------------------------------------------------------*/
	private TimePickerDialog showTimePicker() {
		TimePickerDialog timePicker = new TimePickerDialog(this, new
		TimePickerDialog.OnTimeSetListener() { 
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute){
		mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		mCalendar.set(Calendar.MINUTE, minute);
		updateTimeButtonText();
		}
		}, mCalendar.get(Calendar.HOUR_OF_DAY),
		mCalendar.get(Calendar.MINUTE), true);
		return timePicker;
		}
	/*------------------------------------------------------------*/
	private void updateTimeButtonText() {
		SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
		String timeForButton = timeFormat.format(mCalendar.getTime());
		mTimeButton.setText(timeForButton);
		}
	/*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
