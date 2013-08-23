package com.theora.M;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

/*------------------------------------------------------------*/
public class Mcontroller {
	/*------------------------------------------------------------*/
	public Activity a;
	public Context ctx;
	public Mutils utils = null;
	public Mmodel model = null;
	public Mview view = null;
	/*------------------------------------------------------------*/
	public Mcontroller(Activity a, Context ctx, String dbName, LinearLayout topView) {
		this.a = a;
		this.ctx = ctx;
		this.utils = new Mutils(a, ctx);
		if ( dbName != null )
			this.model = new Mmodel(ctx, dbName);
		this.view = new Mview(a, ctx, topView);
	}
	/*------------------------------*/
	public Mcontroller(Activity a) {
		this(a, a, "mdb", null);
	}
	/*------------------------------*/
	public Mcontroller(Context ctx) {
		this(null, ctx, "mdb", null);
	}
	/*------------------------------*/
	public Mcontroller(Activity a, String dbName) {
		this(a, a, dbName, null);
	}
	/*------------------------------*/
	public Mcontroller(Context c, String dbName) {
		this(null, c, dbName, null);
	}
	/*------------------------------*/
	public Mcontroller(Activity a, LinearLayout topView) {
		this(a, a, null, topView);
	}
	/*------------------------------*/
	public Mcontroller(Activity a, String dbName, LinearLayout topView) {
		this(a, a, dbName, topView);
	}
	/*------------------------------------------------------------*/
}
