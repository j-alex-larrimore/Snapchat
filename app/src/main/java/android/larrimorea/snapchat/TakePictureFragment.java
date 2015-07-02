package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 6/9/2015.
 */

public class TakePictureFragment extends Fragment {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    //private MediaScannerConnection msc;
    static final int REQUEST_TAKE_PHOTO = 1;
    //private Uri fileURI;
    String mCurrentPhotoPath;
    String imageFileName;
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.take_picture, container, false);

        pause = false;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = createImageFile();
                mCurrentPhotoPath = photoFile.getAbsolutePath();
            }catch (IOException ex){
                //Error
            }

            if(photoFile != null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }

        return view;
    }
    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == getActivity().RESULT_OK && pause == false){
                pause = true;
                galleryAddPic();
                parseAddPic();
                Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }else if(resultCode == getActivity().RESULT_CANCELED){
                Toast.makeText(getActivity(), "Image capture Canceled!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), "Image capture Failed!", Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    private void galleryAddPic(){
        //Code below scans media and makes it show correctly

        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
        //mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void parseAddPic(){
        Bitmap bitmap = null;
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
        } catch (Exception e) {
            Log.e("Error", "activityresult " + e);
        }
        byte[] scaledData;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        scaledData = stream.toByteArray();
        final ParseFile photoFile = new ParseFile(imageFileName, scaledData);
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    ParseUser.getCurrentUser().add("photos", photoFile);
                    ParseUser.getCurrentUser().saveInBackground();
                } else {
                    Log.e("TakePictureFragment", "ParseAddPic" + e);
                }
            }
        });




    }

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }
}
