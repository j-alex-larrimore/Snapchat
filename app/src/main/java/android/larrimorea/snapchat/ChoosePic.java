package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by Alex on 6/11/2015.
 */
public class ChoosePic extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_pic);
        Intent intent = getIntent();
        //Uri blogUri = intent.getData();

        //WebView webView = (WebView)findViewById(R.id.webView);
        //webView.loadUrl(blogUri.toString());
    }
}
