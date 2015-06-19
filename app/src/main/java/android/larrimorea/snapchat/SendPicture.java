package android.larrimorea.snapchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class SendPicture extends Activity {
    private List<String> arrayStrings  = new ArrayList<String>();
    public static ArrayAdapter<String> mArrayAdapter;

    private int REQUEST_ENABLE_BT = 1;

    protected ListView listView;
    private static String selectedPic = null;
    private static String targetDevice = null;
    public static BluetoothDevice clickedDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.send_image);
        listView = (ListView)findViewById(R.id.listViewSend);

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayStrings);


        listView.setAdapter(mArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChoosePic.class);
                String str = (String) mArrayAdapter.getItem(position);
                targetDevice = str;
                clickedDevice = MainActivity.deviceList.get(position);
                Toast.makeText(getApplicationContext(), "Clicked -" + str, Toast.LENGTH_LONG).show();

                startActivity(intent);
            }
        });


    }




    public static void setPicture(Uri pic){

        selectedPic = pic.toString();
    }


    @Override
    protected void onResume() {
        mArrayAdapter.clear();
        MainActivity.mBluetoothAdapter.startDiscovery();
        super.onResume();
    }


}
