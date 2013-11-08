package com.theora.M;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class Adams extends Activity {
	/*------------------------------------------------------------*/
	// images & Drawables
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	public Adams() {
		m = new Mcontroller(this, "mdb");
	}
	/*------------------------------------------------------------*/
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	m.utils.msg("Adams: onCreate");
    	ImageView iv = new ImageView(this);
    	Drawable gt =  m.utils.getDrawable("guitars2in1");
    	if ( gt == null ) {
    		m.utils.msg("Adams: m.util.getDrawable: guitars2in1: Failed");
    		return;
    	}
    	iv.setBackgroundColor(Mview.blue);
    	iv.setImageDrawable(gt);
    	setContentView(iv);
    	m.utils.vibrate(1000);
    }
	/*------------------------------------------------------------*/
}
