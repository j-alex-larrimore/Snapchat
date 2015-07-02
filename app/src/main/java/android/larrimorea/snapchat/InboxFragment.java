package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 6/9/2015.
 */
public class InboxFragment extends Fragment {

    private ArrayList<String> frArrayStrings = new ArrayList<String>();
    private ArrayList<String> picArrayStrings = new ArrayList<String>();
    private ArrayAdapter mAdapter;
    private ArrayAdapter mPicAdapter;
    private ListView requestView;
    private ListView picView;
    private View mView;
    private ParseUser mFriend;
    private boolean pause = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.inbox, container, false);

        pause = false;

        //occasional nullPointerExceptions without the following two calls
        displayFriendRequests();
        displayPics();

        getFriendRequests();
        getSentPics();

        return mView;
    }

    private void getFriendRequests(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.whereNotEqualTo("Accepted", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    fillFriendRequests(list);
                    displayFriendRequests();
                } else {
                    Toast.makeText(getActivity(), "No Friend Requests Found", Toast.LENGTH_SHORT);
                    Log.e("Inbox", "getFriendReqError: " + e.getMessage());
                }
            }
        });
    }

    private void fillFriendRequests(List<ParseObject> list){
        for(ParseObject ob: list){
            frArrayStrings.add(ob.get("From").toString());
        }
        if(frArrayStrings == null){
            frArrayStrings.add("No Pending Friend Requests");
        }
    }

    private void displayFriendRequests(){
        requestView = (ListView) mView.findViewById(R.id.listViewFriendReqs);
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, frArrayStrings);
        requestView.setAdapter(mAdapter);

    }


    private void acceptRequest(final String from){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("From", from);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, com.parse.ParseException e) {
                if (e == null) {
                    parseObject.put("Accepted", true);
                    parseObject.saveInBackground();
                    ParseRelation<ParseUser> relation = ParseUser.getCurrentUser().getRelation("friends");
                    addFriend(from, relation);
                } else {
                    Log.e("Inbox", "acceptRequestError: " + e.getMessage());
                }
            }
        });
    }

    private void addFriend(final String from, final ParseRelation relation){
        Log.i("Inbox", "Adding Friend");
        mFriend = null;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", from);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, com.parse.ParseException e) {
                if (e == null) {
                    mFriend = parseUser;
                    relation.add(mFriend);
                    ParseUser.getCurrentUser().saveInBackground();
                } else {
                    Log.e("inbox", "getFriendError: " + e.getMessage());

                }
            }
        });
    }

    private void setClickListener(){
        requestView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
                    makePopup(requestView.getItemAtPosition(position).toString());
                }
            }
        });
        picView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
                    acceptPic(picView.getItemAtPosition(position).toString());
                }
            }
        });
    }

    private void getSentPics(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SentPicture");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    fillSentPics(list);
                    displayPics();
                } else {
                    Toast.makeText(getActivity(), "No Pictures Found", Toast.LENGTH_SHORT);
                    Log.e("Inbox", "getPicsError: " + e.getMessage());
                }
            }
        });
    }

    private void fillSentPics(List<ParseObject> list){
        for(ParseObject ob: list){
            picArrayStrings.add(ob.get("From").toString());
        }
        if(picArrayStrings == null){
            picArrayStrings.add("No Pending Friend Requests");
        }
    }

    private void displayPics(){
        picView = (ListView) mView.findViewById(R.id.listViewInbox);
        mPicAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, picArrayStrings);
        picView.setAdapter(mPicAdapter);
        setClickListener();
    }

    private void makePopup(final String from){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Accept Friend Request?");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                acceptRequest(from);
                dialog.cancel();

                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    private void acceptPic(String from){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SentPicture");
        query.whereEqualTo("To", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("From", from);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, com.parse.ParseException e) {
                if (e == null) {
                    viewPic(parseObject.getParseFile("Picture"));
                    try {
                        parseObject.delete();
                    } catch (com.parse.ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.e("Inbox", "InboxError: " + e.getMessage());
                }
            }
        });
    }

    private void viewPic(ParseFile pic){
        if(pic != null){
            makePicPopup(pic);

        }
    }

    private void makePicPopup(ParseFile pic){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        final ParseImageView image = new ParseImageView(getActivity());
        alert.setView(image);

        image.setParseFile(pic);
        image.loadInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, com.parse.ParseException e) {

            }
        });

        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                getActivity().finish();
            }
        });

        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }
}