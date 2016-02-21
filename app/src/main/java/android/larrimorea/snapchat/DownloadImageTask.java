package android.larrimorea.snapchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Alex on 2/21/2016.
 */
public class DownloadImageTask extends AsyncTask{
    @Override
    protected Bitmap doInBackground(Object[] files) {
        try {
            URL url = new URL(files[0].toString());
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i("downloadFile", "Successfully downloaded");

                int contentLength = httpConn.getContentLength();
                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();

                Bitmap bmp = BitmapFactory.decodeStream(inputStream);

                inputStream.close();
                return bmp;

            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }

            httpConn.disconnect();
        }catch (IOException e){
            Log.e("downloadfile", "IO exception " + e);
        }
        return null;
    }
}
