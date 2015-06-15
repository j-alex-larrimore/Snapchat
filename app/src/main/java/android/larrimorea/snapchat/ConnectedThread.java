package android.larrimorea.snapchat;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Alex on 6/15/2015.
 */

public class ConnectedThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private static int MESSAGE_READ = 2;

    public ConnectedThread(BluetoothSocket socket){
        mmSocket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;
        Log.i("ConnectedThread Init", "Start");


        try{
            tempIn = socket.getInputStream();
            tempOut = socket.getOutputStream();
        }catch(IOException e){

        }
        mmInStream = tempIn;
        mmOutStream = tempOut;
    }

    public void run(){
        byte[] buffer = new byte[1024];
        int bytes;

        while(true){
            try{
                bytes = mmInStream.read(buffer);
                SendPicture.mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            }catch(IOException e){
                break;
            }
        }
    }

    //Call from main activity to send data to remote device
    public void write(byte[] bytes){
        try{
            Log.i("ConnectedThread - Write", "Writing");
            mmOutStream.write(bytes);
        }catch(IOException e){

        }
    }

    //Call from the main activity to shutdown the connection
    public void cancel(){
        try{
            mmSocket.close();
        }catch(IOException e){

        }
    }
}