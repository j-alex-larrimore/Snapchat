package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 6/9/2015.
 */
public class Inbox extends Activity{
    protected ListView listView;
    private List<String> arrayStrings  = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);

        listView = (ListView)findViewById(R.id.listViewInbox);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayStrings);
        listView.setAdapter(adapter);


//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("image/jpeg");
//        i.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(i, "Send Image"));
    }
}
