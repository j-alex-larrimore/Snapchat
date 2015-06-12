package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

//        listView = (ListView)findViewById(R.id.listViewChoose);
//        //Intent intent = getIntent();
//        PicAdapter adapter = new PicAdapter(this, getPics());
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);

    }

    @Override
    protected void onResume() {
        //performFileSearch();
        //public static void performFileSearch(){

        //}
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == READ_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Uri uri = null;
                if(data != null){
                    uri = data.getData();
                   // Log.i(TAG, "Uri: " + uri.toString());
                   // showImage(uri);
                    SendPicture.setPicture(uri);
                }
                Toast.makeText(this, "Picture Search Success!" + SendPicture.getTargetDevice() + " " + uri.toString(), Toast.LENGTH_LONG).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Picture Search Canceled!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Picture Search Failed!", Toast.LENGTH_LONG).show();
            }
        }
        Intent intent = new Intent(getApplicationContext(), SendPicture.class);
        startActivity(intent);

        //super.onActivityResult(requestCode, resultCode, data);
    }

    public static ArrayList<Image> getPics(){
        //performFileSearch();
        return arrayImages;
    }
}
