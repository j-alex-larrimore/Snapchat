package android.larrimorea.snapchat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 6/9/2015.
 */
public class InboxFragment extends Fragment {

    private ArrayList<String> frArrayStrings = new ArrayList<String>();
    private ArrayAdapter mAdapter;
    private ListView requestView;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.inbox, container, false);

        getFriendRequests();

        return mView;
    }

    private void displayFriendRequests(){
        requestView = (ListView) mView.findViewById(R.id.listViewFriendReqs);
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, frArrayStrings);
        requestView.setAdapter(mAdapter);
    }

    private void getFriendRequests(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.whereNotEqualTo("Accepted", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    Log.i("friendreqs", "Retrieved " + list.size() + " reqs");
                    fillFriendRequests(list);
                    displayFriendRequests();
                } else {
                    Toast.makeText(getActivity(), "No Friend Requests Found", Toast.LENGTH_SHORT);
                    Log.e("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void fillFriendRequests(List<ParseObject> list){
        for(ParseObject ob: list){
            Log.i("friendreqs", "Adding: " + ob.get("From").toString());
            frArrayStrings.add(ob.get("From").toString());
        }
        if(frArrayStrings == null){
            frArrayStrings.add("No Pending Friend Requests");
        }
    }

    //    protected ListView listView;
//    private List<String> arrayStrings  = new ArrayList<String>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.inbox);
//
//        listView = (ListView)findViewById(R.id.listViewInbox);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayStrings);
//        listView.setAdapter(adapter);
//    }
}
