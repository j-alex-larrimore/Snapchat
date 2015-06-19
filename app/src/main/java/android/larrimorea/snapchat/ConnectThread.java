package android.larrimorea.snapchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
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

    }


    public void run(Bitmap bmp, Context context){
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
            buffer = read(bmp, context);
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

    public byte[] read(Bitmap bmp, Context context) throws IOException{
        int bytes = bmp.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bmp.copyPixelsToBuffer(buffer);
        byte[] array = buffer.array();
        return array;
    }
}