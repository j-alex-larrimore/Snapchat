package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alex on 6/11/2015.
 */
public class ChoosePicFragment extends Fragment {
    private static final int READ_REQUEST_CODE = 42;
    private static ArrayList<Image> arrayImages = new ArrayList<Image>();
    protected ListView listView;
    private String sendTo;
    private ArrayList<Bitmap> takenPhotos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_pic, container, false);

       sendTo = getActivity().getIntent().getStringExtra("to");
        getPhotos();
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);

        return view;
    }

    private void getPhotos(){

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == READ_REQUEST_CODE){
            if(resultCode == getActivity().RESULT_OK){
                Uri uri = null;
                Bitmap bitmap = null;
                if(data != null) {
                    uri = data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    } catch (Exception e) {
                        Log.e("Error", "activityresult " + e);
                    }
                    byte[] scaledData;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    scaledData = stream.toByteArray();
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timestamp + "_.jpg";
                    ParseFile photoFile = new ParseFile(imageFileName, scaledData);
                    ParseObject sentPic = new ParseObject("SentPicture");
                    sentPic.put("Picture", photoFile);
                    sentPic.put("From", ParseUser.getCurrentUser().getUsername());
                    sentPic.put("To", sendTo);
                    sentPic.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                makePopup();
                            } else {
                                Log.e("ChoosePic", "SendPic" + e);
                            }
                        }

                    });

                }
            }else if(resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Picture Search Canceled!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Picture Search Failed!", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void makePopup(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Picture Sent to " + sendTo + "!");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();

                // Do something with value!
            }
        });

        alert.show();
    }

    public static ArrayList<Image> getPics(){
        return arrayImages;
    }
}
