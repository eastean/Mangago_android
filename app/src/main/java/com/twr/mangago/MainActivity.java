package com.twr.mangago;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    private View decorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
             @Override
             public void onPageFinished(WebView view, String url) {
                 injectCSS();
                 super.onPageFinished(view, url);
                 //lazy way to see if the link is at the reading page
                 char slash = '/';
                 int count = 0;
                 for (int i = 0; i < url.length(); i++){
                     if (url.charAt(i) == slash) {
                        count++;
                     }
                 }
                 if (count == 8) {
                     //immersive mode when at the reading page
                    hideSystemBars();
                 }
                 else{
                     //exits reading page
                     showSystemBars();
                 }
             }
         }
        );
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("https://www.mangago.me/");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

    }
    private void injectCSS() {
        try {
            InputStream inputStream = getAssets().open("style.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private void hideSystemBars(){
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
           getWindow().setDecorFitsSystemWindows(false);
           WindowInsetsController controller = getWindow().getInsetsController();
           if(controller != null) {
               controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
               controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
           }
       }
       else{
           getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                   | View.SYSTEM_UI_FLAG_FULLSCREEN
                   | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                   | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                   | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                   | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
          );}

       }

   private void showSystemBars(){
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
           WindowInsetsControllerCompat controller =
                   ViewCompat.getWindowInsetsController(getWindow().getDecorView());
           controller.show(WindowInsetsCompat.Type.systemBars());

       /*TO ADD SUPPORT FOR <R. CURRENTLY LAZY*/
   }}

    @Override
    public void onBackPressed() {
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

