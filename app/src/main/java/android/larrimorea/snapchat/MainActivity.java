package android.larrimorea.snapchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;


public class MainActivity extends Activity {
    protected ProgressBar progressBar;
    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "qh36Gr8U0NeaEEk8V7WyuOOvB6H6JnRsk56fsddW", "t1GXIOyuHB2yYaQMxVwPnEaPCCmEnmGEcDIp9QQc");


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
                    //Intent intent = new Intent(getApplicationContext(), SendPicture.class);
                    Intent intent = new Intent(getApplicationContext(), ChoosePic.class);
                    startActivity(intent);
                }else{
                    //Toast.makeText(this., "Image capture Failed!", Toast.LENGTH_LONG).show();
                }

                //Uri blogUri = Uri.parse(BlogPostParser.get().posts.get(position).url);
                //intent.setData(blogUri);


            }
        });


        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "qh36Gr8U0NeaEEk8V7WyuOOvB6H6JnRsk56fsddW", "t1GXIOyuHB2yYaQMxVwPnEaPCCmEnmGEcDIp9QQc");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();


    }
}
