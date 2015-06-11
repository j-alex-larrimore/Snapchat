package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 6/11/2015.
 */
public class ChoosePic extends Activity{
    private static final int READ_REQUEST_CODE = 42;
    private static ArrayList<Image> arrayImages = new ArrayList<Image>();
    protected ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_pic);
        Intent intent = getIntent();
        PicAdapter adapter = new PicAdapter(this, getPics());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChoosePic.class);
                //intent.putExtra(Intent.EXTRA_TEXT, )
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        performFileSearch();
        super.onResume();
    }

    public void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public static ArrayList<Image> getPics(){
        return arrayImages;
    }
}
