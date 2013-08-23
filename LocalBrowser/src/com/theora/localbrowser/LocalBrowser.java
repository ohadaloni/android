
package com.theora.localbrowser;

import com.theora.M.Mutil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocalBrowser extends Activity {
   /*------------------------------------------------------------*/
   private final Handler handler = new Handler(); 
   private WebView webView;
   private TextView textView;
   private Button button;
   private Mutil mutil;
   /*------------------------------------------------------------*/
   public LocalBrowser() {
	  mutil = new Mutil(this);
   }
   /*------------------------------------------------------------*/
   /** Object exposed to JavaScript */
   private class Magic444 {
      public void magic123(final String arg) { // must be final
         handler.post(new Runnable() {
            public void run() {
               Log.d("Mutil", "magic123(" + arg + ")");
               textView.setText(arg);
            }
         });
      }
   }
   /*------------------------------------------------------------*/
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);

      // Find the Android controls on the screen
      webView = (WebView) findViewById(R.id.web_view);
      textView = (TextView) findViewById(R.id.text_view);
      button = (Button) findViewById(R.id.button);
      // Rest of onCreate follows...

      // Turn on JavaScript in the embedded browser
      webView.getSettings().setJavaScriptEnabled(true);

      // Expose a Java object to JavaScript in the browser
      webView.addJavascriptInterface(new Magic444(), "magic1961");

      // Set up a function to be called when JavaScript tries
      // to open an alert window
      webView.setWebChromeClient(new WebChromeClient() {
         @Override
         public boolean onJsAlert(final WebView view,
               final String url, final String message,
               JsResult result) {
            Log.d("Mutil", "onJsAlert(" + view + ", " + url + ", "
                  + message + ", " + result + ")");
            // TODO - why is the third argument not LENGTH_???
            Toast.makeText(LocalBrowser.this, message, 3000).show();
            result.confirm();
            return true; // I handled it
         }
      });

      // Load the web page from a local asset
      webView.loadUrl("file:///android_asset/index.html");

      // This function will be called when the user presses the
      // button on the Android side
      button.setOnClickListener(new OnClickListener() {
         public void onClick(View view) {
            Log.d("Mutil", "onClick(" + view + ")");
            webView.loadUrl("javascript:callJS('Hello from Android')");
         }
      });
   }
   /*------------------------------------------------------------*/
}
/*------------------------------------------------------------*/
