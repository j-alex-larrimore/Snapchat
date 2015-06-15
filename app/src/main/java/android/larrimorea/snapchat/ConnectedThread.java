package android.larrimorea.snapchat;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

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
    private static Handler mHandler;

    public ConnectedThread(BluetoothSocket socket){
        mmSocket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

            }
        };

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
                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            }catch(IOException e){
                break;
            }
        }
    }

    //Call from main activity to send data to remote device
    public void write(byte[] bytes){
        try{
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