package android.larrimorea.snapchat;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Alex on 6/15/2015.
 */

public class ConnectedThread extends Thread{
    private static BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private static int MESSAGE_READ = 2;
    private int timerCount = 0;

    public ConnectedThread(BluetoothSocket socket){
        mmSocket = socket;
        InputStream tempIn = null;
        OutputStream tempOut = null;
        Log.i("ConnectedThread Init", "Start");

        //Timer to check our connection to see if it needs to be reconnected
        MyTimerTask yourTask = new MyTimerTask();
        Timer t = new Timer();
        //t.scheduleAtFixedRate(yourTask, 0, 5000);
        t.scheduleAtFixedRate(yourTask, 0, 30000);


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
        byte[] imageBuffer = new byte[1024*1024];
        int pos = 0;

        while(true){
            try{
                int bytes = mmInStream.read(buffer);
                System.arraycopy(buffer, 0, imageBuffer, pos, bytes);
                pos += bytes;
                SendPicture.mHandler.obtainMessage(MESSAGE_READ, pos, -1, imageBuffer).sendToTarget();
            }catch(IOException e){
                break;
            }
        }
    }

    //Call from main activity to send data to remote device
    public void write(byte[] bytes){
        try{
            //Log.i("ConnectedThread - Write", "Writing" + bytes.toString());
            mmOutStream.write(bytes);
        }catch(IOException e){

        }
    }

    //Call from the main activity to shutdown the connection
    public static void cancel(){
        try{
            mmSocket.close();
        }catch(IOException e){

        }
    }

    public class MyTimerTask extends TimerTask {
        public void run(){
//            if(!mmSocket.isConnected()){
//                //ConnectedThread.cancel();
//                Log.i("ConnectedThread", "Needs a Break");
//            }else{
//                Log.i("ConnectedThread", "Still going");
//            }
            if(timerCount == 0){
                timerCount++;
            }else {
                Log.i("MyTimer", "Canceling Connection");
                ConnectedThread.cancel();
            }
        }
    }
}