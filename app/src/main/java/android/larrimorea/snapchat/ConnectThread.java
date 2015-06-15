package android.larrimorea.snapchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

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

    public ConnectThread(BluetoothDevice device, BluetoothAdapter blueAdapter){
        BluetoothSocket temp = null;
        mmDevice = device;
        btAdapter = blueAdapter;
        myUUID = UUID.fromString("10d28dc0-105f-11e5-b939-0800200c9a66");
        try{
            temp = device.createRfcommSocketToServiceRecord(myUUID);
        }catch(IOException e){

        }
        mmSocket = temp;
    }

    public void run(){
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
        ConnectedThread ct = new ConnectedThread(mmSocket);
    }

    public void cancel(){
        try{
            mmSocket.close();
        }catch(IOException closeException){

        }
    }
}