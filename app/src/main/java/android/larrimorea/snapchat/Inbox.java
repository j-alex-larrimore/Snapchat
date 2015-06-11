package android.larrimorea.snapchat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * Created by Alex on 6/9/2015.
 */
public class Inbox extends Activity{
    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);
    }
}
