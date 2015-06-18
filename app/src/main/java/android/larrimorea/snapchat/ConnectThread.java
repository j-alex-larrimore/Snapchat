package android.larrimorea.snapchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Alex on 6/15/2015.
 */

public class ConnectThread extends Thread{
    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter btAdapter;
    public UUID myUUID;
    private int timerCount = 0;
    private ConnectedThread cdt;

    public ConnectThread(BluetoothDevice device){
        BluetoothSocket temp = null;
        mmDevice = device;
        btAdapter = MainActivity.getBTAdapter();
        myUUID = UUID.fromString("10d28dc0-105f-11e5-b939-0800200c9a66");
        try{
            //secure did not work, trying insecure
            temp = device.createInsecureRfcommSocketToServiceRecord(myUUID);
        }catch(IOException e){

        }
        mmSocket = temp;

//        //Timer to check our connection to see if it needs to be reconnected
//        MyTimerTask yourTask = new MyTimerTask();
//        Timer t = new Timer();
//        //t.scheduleAtFixedRate(yourTask, 0, 5000);
//        t.scheduleAtFixedRate(yourTask, 0, 30000);
    }

    public void run(Uri uri, Context context){
        btAdapter.cancelDiscovery();


        try{
            if(!mmSocket.isConnected()) {
                Log.i("ConnectThread", "Connect Attempt");
                mmSocket.connect();
            }
            else{
                Log.i("ConnectThread", "Already Connected");
            }

        }catch(IOException connectException){
            Log.i("run", "connection failure" + connectException);
            try{
                mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocketFromRecord", new Class[] {int.class}).invoke(mmDevice,1);
                mmSocket.connect();
                mmSocket.close();
            }catch(IOException closeException){
                Log.e("Socketssss", "IOException " + closeException);
            }catch(Exception e){
                Log.e("Socketssss", "Exception " + e);
            }
            return;
        }
        Log.i("ConnectThread", "Calling Connected Thread");
        cdt = new ConnectedThread(mmSocket);
        byte[] buffer = null;
        try {
            buffer = read(uri, context);
        }catch(IOException e){

        }
        cdt.write(buffer);
        cdt.cancel();
        cdt = null;
        Log.i("ConnectAfterWrite", "Connected Thread closed");
        new BluetoothServer().execute(MainActivity.mBluetoothAdapter);
        //Trying to close more often
        // cdt.cancel();
    }

    public void cancel(){
        try{
            Log.i("ConnectThread", "Canceling");
            mmSocket.close();
        }catch(IOException closeException){
            Log.e("closeException", "error: " + closeException);
        }
    }

    public byte[] read(Uri uri, Context context) throws IOException{
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            stringBuilder.append(line);
        }
        inputStream.close();
        reader.close();
        return stringBuilder.toString().getBytes();


//        Log.i("read", "project working " + uri.toString());
//        File extStore = Environment.getExternalStorageDirectory();
//        final File file = new File(extStore.getAbsolutePath() + uri.getPath());
//        String ap = file.getAbsolutePath();
//        byte[] buffer = new byte[(int)file.length()];
//        InputStream ios = null;
//        try{
//            if(file.isFile()) {
//                Log.i("FileDir ", "File Found!");
//                ios = new FileInputStream(file);
//            }else{
//                Log.i("File dir error", "Path " + ap);
//            }
//
//            //Code below causes null pointer exception
////            if(ios.read(buffer) == -1){
////                throw new IOException("EOF reached");
////            }
//        }finally {
//            try{
//                if (ios != null){
//                    ios.close();
//                }
//            }catch(IOException e){
//                Log.e("FiletoBuffer", "Error");
//            }
//        }
//
//        return buffer;
//    }


//    public class MyTimerTask extends TimerTask {
//        public void run(){
////            if(!mmSocket.isConnected()){
////                //ConnectedThread.cancel();
////                Log.i("ConnectedThread", "Needs a Break");
////            }else{
////                Log.i("ConnectedThread", "Still going");
////            }
//            if(timerCount == 0){
//                timerCount++;
//            }else {
//                Log.i("MyTimerConnect", "Canceling Connection");
//                cdt = null;
//            }
//        }
    }

}