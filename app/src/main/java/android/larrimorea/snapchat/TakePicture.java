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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alex on 6/9/2015.
 */

public class TakePicture extends Activity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    //private MediaScannerConnection msc;
    static final int REQUEST_TAKE_PHOTO = 1;
    //private Uri fileURI;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_picture);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // File imagesFolder = new File(Environment.getRootDirectory(), "MyImages");

        //imagesFolder.mkdirs();
        //File image = new File(imagesFolder, "image1.jpg");

        //fileURI = Uri.fromFile(image);

        //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
        if(intent.resolveActivity(getPackageManager()) != null) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                galleryAddPic();
                Toast.makeText(this, "Image Saved!", Toast.LENGTH_LONG).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Image capture Canceled!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Image capture Failed!", Toast.LENGTH_LONG).show();
            }
        }



        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
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
        sendBroadcast(mediaScanIntent);
    }


}
