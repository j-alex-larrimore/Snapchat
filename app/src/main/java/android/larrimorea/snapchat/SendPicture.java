package android.larrimorea.snapchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SendPicture extends Activity {
    private int REQUEST_ENABLE_BT = 1;
    private List<String> arrayStrings  = new ArrayList<String>();
    private List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    private ArrayAdapter<String> mArrayAdapter;
    private IntentFilter filter;
    private static BluetoothAdapter mBluetoothAdapter;

    public static Handler mHandler;

    protected ListView listView;
    private static Uri selectedPic = null;
    private static String targetDevice = null;
    public static BluetoothDevice clickedDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_image);
        listView = (ListView)findViewById(R.id.listViewSend);

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayStrings);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("MHandler", "Init");
            }
        };

        listView.setAdapter(mArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChoosePic.class);
                String str = (String)mArrayAdapter.getItem(position);
                targetDevice = str;
                clickedDevice = deviceList.get(position);
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
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                //deviceList.add(device);
                Toast.makeText(this, "Paired Device!", Toast.LENGTH_LONG).show();
            }
        }

        registerReceiver(mReceiver, filter);
        new BluetoothServer().execute(mBluetoothAdapter);
       // AcceptThread thread = new AcceptThread();
        //thread.run();
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
        mArrayAdapter.clear();
        mBluetoothAdapter.startDiscovery();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mBluetoothAdapter.cancelDiscovery();
        Toast.makeText(this, "Discovery Canceled", Toast.LENGTH_LONG).show();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }



    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                deviceList.add(device);
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

    public static BluetoothAdapter getBTAdapter(){
        return mBluetoothAdapter;
    }
}
