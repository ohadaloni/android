package com.theora.M;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
/*------------------------------------------------------------*/
public class NautilusQuestionSetParserTest extends Activity {
	/*------------------------------------------------------------*/
	private Mcontroller m = null;
	private NautilusUtils nutils = null;
	private TextView tv;
	/*------------------------------------------------------------*/
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		m = new Mcontroller(this, "mdb");
		nutils = new NautilusUtils(this, 1);
		tv = new TextView(this);
		tv.setText("Nothing Parsed Yet");
		setContentView(tv);

		setTitle("Nautilus Question Parser Test");
		
		final int difficulty = 2;
		String urlString = "http://nautilus.theora.com/index.php?gameId=1&numItems=10&difficulty=" + difficulty;
		
		McallbackWithString cb = new McallbackWithString() {
			@Override
			public void f(String s) {
				NautilusQuestionSet questions = nutils.parseQuestionSet(s, difficulty, 5);
				String msg;
				if ( questions != null)
					msg = questions.toString();
				else
					msg = "Failed to parse xml text";
				tv.setText(msg);
				m.view.alert(msg);
				setTitle(msg);
				nutils.log(msg);
				
			}
		};
		m.utils.getUrlText(urlString, cb);
	}
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/
}