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
    private int MESSAGE_READ = 2;
    private List<String> arrayStrings  = new ArrayList<String>();
    private ArrayAdapter<String> mArrayAdapter;
    private IntentFilter filter;
    private Handler mHandler;
    protected ListView listView;
    private static Uri selectedPic = null;
    private static String targetDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_image);
        listView = (ListView)findViewById(R.id.listViewSend);

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
    }


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
