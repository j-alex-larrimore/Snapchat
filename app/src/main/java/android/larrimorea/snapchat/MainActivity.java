package android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;


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

        String appVersion = "v1";
        String appID = "60001609-65BC-FFE7-FF49-6609EF9E0C00";
        String key = "A62C20B3-9215-A84E-FFB6-AADDBF224B00";
        Backendless.initApp(this, appID, key, appVersion);

    }



}
