package android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.Parse;
import com.parse.ParseObject;


public class MainActivity extends SingleFragmentActivity {

    protected ProgressBar progressBar;
    protected ListView listView;

    private boolean loggedIn = false;

    @Override
    protected Fragment createFragment() {

        return new MainMenuFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.enableLocalDatastore(this);

        ParseObject.registerSubclass(User.class);

        Parse.initialize(this, "qh36Gr8U0NeaEEk8V7WyuOOvB6H6JnRsk56fsddW", "t1GXIOyuHB2yYaQMxVwPnEaPCCmEnmGEcDIp9QQc");
    }
}
