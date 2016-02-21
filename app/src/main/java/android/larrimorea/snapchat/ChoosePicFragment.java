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

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChoosePicFragment extends Fragment {
    private static final int READ_REQUEST_CODE = 42;
    private static ArrayList<Image> arrayImages = new ArrayList<Image>();
    protected ListView listView;
    private String sendTo;
    private ArrayList<Bitmap> takenPhotos;
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_pic, container, false);

        pause = false;

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
            if(resultCode == getActivity().RESULT_OK && pause == false){

                Uri uri = null;
                Bitmap bitmap = null;
                if(data != null) {
                    uri = data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    } catch (Exception e) {
                        Log.e("Error", "activityresult " + e);
                    }
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    final String imageFileName = "JPEG_" + timestamp + "_.jpg";

                    //This ends the activity early and the file upload occurs in the background
                    getActivity().finish();

                    Backendless.Files.Android.upload( bitmap, Bitmap.CompressFormat.PNG, 100, imageFileName, "mypics", new AsyncCallback<BackendlessFile>()
                            {
                                @Override
                                public void handleResponse( final BackendlessFile backendlessFile )
                                {
                                    String from = Backendless.UserService.CurrentUser().getProperty("name").toString();
                                    SentPicture sentPic = new SentPicture();
                                    sentPic.setFrom(from);
                                    sentPic.setTo(sendTo);
                                    sentPic.setPicLocation(imageFileName);
                                    Backendless.Persistence.save(sentPic, new AsyncCallback<SentPicture>() {
                                        public void handleResponse(SentPicture sent) {
                                            //Toast.makeText(getActivity(), "Pic Sent!", Toast.LENGTH_SHORT).show();
                                        }

                                        public void handleFault(BackendlessFault fault) {
                                            // an error has occurred, the error code can be retrieved with fault.getCode()
                                        }
                                    });
                                    pause = true;

                                }

                                @Override
                                public void handleFault( BackendlessFault backendlessFault )
                                {
                                    Toast.makeText( getActivity(), backendlessFault.toString(), Toast.LENGTH_SHORT ).show();
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


    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }
}
