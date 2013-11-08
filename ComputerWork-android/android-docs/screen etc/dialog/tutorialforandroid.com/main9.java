package com.monmonja.firstDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends Activity implements View.OnClickListener {
	private AlertDialog alertDialog;
	private Button alertBtn;
	private Button alert2Btn;
	private Button alert3Btn;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        this.alertBtn = (Button)this.findViewById(R.id.alertBtn);
        this.alertBtn.setOnClickListener(this);
        this.alert2Btn = (Button)this.findViewById(R.id.alert2Btn);
        this.alert2Btn.setOnClickListener(this);
        this.alert3Btn = (Button)this.findViewById(R.id.alert3Btn);
        this.alert3Btn.setOnClickListener(this);
    }

	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view == this.alertBtn){
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Alert 1");
			alertDialog.setMessage("This is an alert");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int which) {
	                     return;
	             }
	         }); 
		}else if(view  == this.alert2Btn){
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Alert 2");
			alertDialog.setMessage("This is another alert");
			alertDialog.setIcon(R.drawable.search);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int which) {
	                     return;
	             }
	         }); 
			alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int which) {
	                     return;
	             }
	         }); 
		}else if(view  == this.alert3Btn){
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Alert 3");
			alertDialog.setMessage("This is the third alert");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int which) {
	                     return;
	             }
	         }); 
			alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int which) {
	                     return;
	             }
	         }); 
			alertDialog.setButton3("Middle", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int which) {
	                     return;
	             }
	         });
		}
		alertDialog.show();
	}

}