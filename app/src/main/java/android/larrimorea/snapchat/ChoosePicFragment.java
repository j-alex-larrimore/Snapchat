package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Alex on 6/11/2015.
 */
public class ChoosePicFragment extends Fragment {
    private static final int READ_REQUEST_CODE = 42;
    private static ArrayList<Image> arrayImages = new ArrayList<Image>();
    protected ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_pic, container, false);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == READ_REQUEST_CODE){
            if(resultCode == getActivity().RESULT_OK){
                Uri uri = null;
//                Bitmap bitmap = null;
                if(data != null) {
                    uri = data.getData();

                    SendPictureFragment.setPicture(uri);
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                    } catch (Exception e) {
//                        Log.e("Error", "activityresult " + e);
//                    }
//                    byte[] scaledData;
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//
//                    scaledData = stream.toByteArray();
//                    ParseFile photoFile = new ParseFile("meal_photo.jpg", scaledData);
//                    ParseUser.getCurrentUser().add("photos", photoFile);
//                    ParseObject testObject = new ParseObject("TestObject");
//                    testObject.put("pic", photoFile);
                   // testObject.saveInBackground();




                }
            }else if(resultCode == getActivity().RESULT_CANCELED){
                Toast.makeText(getActivity(), "Picture Search Canceled!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), "Picture Search Failed!", Toast.LENGTH_LONG).show();
            }
        }
        Intent intent = new Intent(getActivity(), SendPictureFragment.class);
        startActivity(intent);

    }

    public static ArrayList<Image> getPics(){
        return arrayImages;
    }
}
