package android.larrimorea.snapchat;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Alex on 6/29/2015.
 */
public class TakePictureActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new TakePictureFragment();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
