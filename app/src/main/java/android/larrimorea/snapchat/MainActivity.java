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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;


public class MainActivity extends Activity {
    protected ProgressBar progressBar;
    protected ListView listView;

    public static List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    private IntentFilter filter;
    public static BluetoothAdapter mBluetoothAdapter;
    public static Handler mHandler;
    private int REQUEST_ENABLE_BT = 1;

    //For receiving files
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] arrayStrings = new String[]{
                "Inbox",
                "Take a Picture",
                "Send a Picture"
        };

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        listView = (ListView)findViewById(R.id.listView);



        listView.setEmptyView(progressBar);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayStrings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(id==0){
                    Intent intent = new Intent(getApplicationContext(), Inbox.class);
                    startActivity(intent);
                }else if(id==1){
                    Intent intent = new Intent(getApplicationContext(), TakePicture.class);
                    startActivity(intent);
                }else if(id==2){
                    Intent intent = new Intent(getApplicationContext(), SendPicture.class);
                    startActivity(intent);
                }else{
                    //Toast.makeText(this., "Image capture Failed!", Toast.LENGTH_LONG).show();
                }

            }
        });

        //Uri blogUri = Uri.parse(BlogPostParser.get().posts.get(position).url);
        //intent.setData(blogUri);

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
            Toast.makeText(getApplicationContext(), "Bluetooth enabled!", Toast.LENGTH_LONG).show();
        }

        //Searching all paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            for(BluetoothDevice device : pairedDevices){
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                //deviceList.add(device);
                Toast.makeText(getApplicationContext(), "Paired Device!", Toast.LENGTH_LONG).show();
            }
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("Handling", "HandleMessage");
                byte[] readBuf = (byte[]) msg.obj;
                //NEED TO MAKE THIS TURN readBuf into a URI
            }
        };

        registerReceiver(mReceiver, filter);
        new BluetoothServer().execute(mBluetoothAdapter);

        //0 instead of a number of seconds means that this android will be permanently set to discoverable
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);


       // SendPicture.AcceptThread.start();
    }

    @Override
    protected void onPause() {
        mBluetoothAdapter.cancelDiscovery();
       // Toast.makeText(this, "Discovery Canceled", Toast.LENGTH_LONG).show();
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
                SendPicture.mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                deviceList.add(device);
                //Toast.makeText(context, "Device found!" + device.getName(), Toast.LENGTH_LONG).show();
            }
        }
    };

    public static BluetoothAdapter getBTAdapter(){
        return mBluetoothAdapter;
    }
//
//    private File createImageFile() throws IOException {
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "PNG_" + timestamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".png", storageDir);
//
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//
//        return image;
//    }
//
//    private void galleryAddPic(){
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
//        sendBroadcast(mediaScanIntent);
//    }

}
