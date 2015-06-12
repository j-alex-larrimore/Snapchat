package android.larrimorea.snapchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

/**
 * Created by Alex on 6/9/2015.
 */
public class SendPicture extends Activity {
    private int REQUEST_ENABLE_BT = 1;
    private static int MESSAGE_READ = 2;
    private List<String> arrayStrings  = new ArrayList<String>();
    private ArrayAdapter<String> mArrayAdapter;
    private IntentFilter filter;
    private static BluetoothAdapter mBluetoothAdapter;
    private static Handler mHandler;
    public static UUID myUUID;
    protected ListView listView;
    private static Uri selectedPic = null;
    private static String targetDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_image);
        listView = (ListView)findViewById(R.id.listViewSend);
        AcceptThread thread = new AcceptThread();
        thread.run();
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

            }
        };
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayStrings);

        listView.setAdapter(mArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChoosePic.class);
                String str = (String)mArrayAdapter.getItem(position);
                targetDevice = str;
                Toast.makeText(getApplicationContext(), "Clicked -" + str, Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Log.i("SendPicture", "Bluetooth Not Enabled");
        }

        //Code to enable the Bluetooth Adapter
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            Toast.makeText(this, "Bluetooth enabled!", Toast.LENGTH_LONG).show();
        }

        //Searching all paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            for(BluetoothDevice device : pairedDevices){
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Bluetooth Connection!", Toast.LENGTH_LONG).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Bluetooth Canceled!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Bluetooth Failed!", Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        mBluetoothAdapter.startDiscovery();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //NEED TO CALL THIS FUNCTION WHEN WE FIND ALL DEVICES
        mBluetoothAdapter.cancelDiscovery();
        Toast.makeText(this, "Discovery Canceled", Toast.LENGTH_LONG).show();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket temp = null;

            //Got this uuid from using a random UUID generator online. Students should generate their own.
            myUUID = UUID.fromString("10d28dc0-105f-11e5-b939-0800200c9a66");
            try{
                temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("name", myUUID);
                Toast.makeText(getApplicationContext(), "AcceptThread", Toast.LENGTH_LONG).show();
            }catch (IOException e){

            }
            mmServerSocket = temp;
        }

        public void run(){
            BluetoothSocket socket = null;
            while(true){
                try{
                    socket = mmServerSocket.accept();
                    if(socket != null){
                        ConnectedThread ct = new ConnectedThread(socket);
                        mmServerSocket.close();
                        break;
                    }
                }catch(IOException e){
                    break;
                }
            }
        }



        public void cancel(){
            try{
                mmServerSocket.close();
            }catch(IOException e){

            }
        }
    }

    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device){
            BluetoothSocket temp = null;
            mmDevice = device;

            try{
                temp = device.createRfcommSocketToServiceRecord(myUUID);
            }catch(IOException e){

            }
            mmSocket = temp;
        }

        public void run(){
            mBluetoothAdapter.cancelDiscovery();

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

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                //Toast.makeText(context, "Device found!" + device.getName(), Toast.LENGTH_LONG).show();
            }
        }
    };

    public static void setPicture(Uri pic){
        selectedPic = pic;
    }

    public static Uri getPicture(){
        return selectedPic;
    }

    public static void setTargetDevice(String target){
        targetDevice = target;
    }

    public static String getTargetDevice(){
        return targetDevice;
    }
}
