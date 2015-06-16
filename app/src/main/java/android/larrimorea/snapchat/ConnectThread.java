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
            while(!mmSocket.isConnected()) {
                mmSocket.connect();
            }
        }catch(IOException connectException){
            try{
//                Class<?> clazz = mmSocket.getRemoteDevice().getClass();
//                Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
//                Method m = clazz.getMethod("getPort", paramTypes);
//                Object[] params = new Object[] {Integer.valueOf(1)};
                //mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{int.class}).invoke(mmDevice, 1);
                //mmSocket.connect();
                mmSocket.close();
            }catch(IOException closeException){

            }
            return;
        }
        Log.i("ConnectThread", "Calling Connected Thread");
        ConnectedThread cdt = new ConnectedThread(mmSocket);
        //String str = uri.toString();
        //byte[] bytes = str.getBytes();
//        Bitmap icon = BitmapFactory.decodeFile(uri.toString());
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        icon.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
//        byte[] image = bytes.toByteArray();
//        cdt.write(image);
        File f = new File(uri.getPath());
        byte[] buffer = null;
        try {
            buffer = read(f);
        }catch(IOException e){

        }
        cdt.write(buffer);
    }

    public void cancel(){
        try{
            mmSocket.close();
        }catch(IOException closeException){

        }
    }

    public byte[] read(File file) throws IOException{
        byte[] buffer = new byte[(int)file.length()];
        InputStream ios = null;
        try{
            if(file.exists()) {
                ios = new FileInputStream(file);
            }
            if(ios.read(buffer) == -1){
                throw new IOException("EOF reached");
            }
        }finally {
            try{
                if (ios != null){ ios.close();
                }
            }catch(IOException e){
                Log.e("FiletoBuffer", "Error");
            }
        }

        return buffer;
    }
}