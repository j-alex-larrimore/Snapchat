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
import java.io.IOException;
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
            try{
                mmSocket.close();
            }catch(IOException closeException){

            }
            return;
        }
        Log.i("ConnectThread", "Calling Connected Thread");
        ConnectedThread cdt = new ConnectedThread(mmSocket);
        //String str = uri.toString();
        //byte[] bytes = str.getBytes();
        Bitmap icon = BitmapFactory.decodeFile(uri.toString());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        byte[] image = bytes.toByteArray();
        cdt.write(image);
    }

    public void cancel(){
        try{
            mmSocket.close();
        }catch(IOException closeException){

        }
    }
}