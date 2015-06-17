package android.larrimorea.snapchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Alex on 6/15/2015.
 */

public class ConnectThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter btAdapter;
    public UUID myUUID;

    public ConnectThread(BluetoothDevice device){
        BluetoothSocket temp = null;
        mmDevice = device;
        btAdapter = SendPicture.getBTAdapter();
        myUUID = UUID.fromString("10d28dc0-105f-11e5-b939-0800200c9a66");
        try{
            //secure did not work, trying insecure
            temp = device.createInsecureRfcommSocketToServiceRecord(myUUID);
        }catch(IOException e){

        }
        mmSocket = temp;
    }

    public void run(Uri uri){
        btAdapter.cancelDiscovery();


        try{
            mmSocket.connect();

        }catch(IOException connectException){
            Log.i("run", "connection failure");
            try{
                mmSocket.close();
            }catch(IOException closeException){
                Log.e("Socketssss", "IOException " + closeException);
            }
            return;
        }
        Log.i("ConnectThread", "Calling Connected Thread");
        ConnectedThread cdt = new ConnectedThread(mmSocket);
        byte[] buffer = null;
        try {
            buffer = read(uri);
        }catch(IOException e){

        }
        cdt.write(buffer);
    }

    public void cancel(){
        try{
            mmSocket.close();
        }catch(IOException closeException){
            Log.e("closeException", "error: " + closeException);
        }
    }

    public byte[] read(Uri uri) throws IOException{
        Log.i("read", "project working");
        final File file = new File(uri.getPath());
        String ap = file.getAbsolutePath();
        byte[] buffer = new byte[(int)file.length()];
        InputStream ios = null;
        try{
            if(file.isFile()) {
                ios = new FileInputStream(file);
            }else{
                Log.i("File dir error", "Path " + ap);
            }

            //Code below causes null pointer exception
//            if(ios.read(buffer) == -1){
//                throw new IOException("EOF reached");
//            }
        }finally {
            try{
                if (ios != null){
                    ios.close();
                }
            }catch(IOException e){
                Log.e("FiletoBuffer", "Error");
            }
        }

        return buffer;
    }

}