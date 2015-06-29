package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Alex on 6/11/2015.
 */
public class ChoosePicFragment extends Activity{
    private static final int READ_REQUEST_CODE = 42;
    private static ArrayList<Image> arrayImages = new ArrayList<Image>();
    protected ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_pic);

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
                Bitmap bitmap = null;
                if(data != null) {
                    uri = data.getData();

                    SendPictureFragment.setPicture(uri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (Exception e) {
                        Log.e("Error", "activityresult " + e);
                    }
                    byte[] scaledData;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    scaledData = stream.toByteArray();
                    ParseFile photoFile = new ParseFile("meal_photo.jpg", scaledData);
                    ParseObject testObject = new ParseObject("TestObject");
                    testObject.put("pic", photoFile);
                    testObject.saveInBackground();




                }
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Picture Search Canceled!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Picture Search Failed!", Toast.LENGTH_LONG).show();
            }
        }
        Intent intent = new Intent(getApplicationContext(), SendPictureFragment.class);
        startActivity(intent);

        //super.onActivityResult(requestCode, resultCode, data);
    }

    public static ArrayList<Image> getPics(){
        //performFileSearch();
        return arrayImages;
    }
}
