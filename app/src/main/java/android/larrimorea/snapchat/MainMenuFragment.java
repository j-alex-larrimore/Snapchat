package android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;


public class MainMenuFragment extends Fragment{
    private int LOGGED_IN = 0;
    private int LOGGED_OUT = 1;

    protected ListView listView;
    public boolean loggedIn = false;
    private ArrayAdapter<String> mAdapter;
    private String[] inArrayStrings;
    private String[] outArrayStrings;
    private ParseUser mFriend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainmenu_fragment, container, false);

        inArrayStrings = new String[]{
            "Inbox",
            "Take a Picture",
            "Send a Picture",
            "Sign Out"
    };

        outArrayStrings = new String[]{
                "Register",
                "Log In"
        };

        listView = (ListView) view.findViewById(R.id.listView);

        setMenu();

        return view;
    }

    private void deleteRequest(ParseObject request) {
        try {
            request.delete();
        }catch(ParseException e){
            Log.e("Main", "DeleteRequest error " + e);
        }
    }

    public void setMenu(){
        if(loggedIn) {
            loggedIn();
        }else{
            loggedOut();
        }
    }

    public void loggedIn(){
        updateFriends();

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, inArrayStrings);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == 0) {
                    Intent intent = new Intent(getActivity(), InboxActivity.class);
                    startActivity(intent);
                } else if (id == 1) {
                    Intent intent = new Intent(getActivity(), TakePictureActivity.class);
                    startActivity(intent);
                } else if (id == 2) {
                    Intent intent = new Intent(getActivity(), SendPictureActivity.class);
                    startActivity(intent);
                } else if (id == 3) {
                    Intent intent = new Intent(getActivity(), LogOutActivity.class);
                    startActivityForResult(intent, LOGGED_OUT);
                } else {
                    //Toast.makeText(this., "Image capture Failed!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void updateFriends(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
        query.whereEqualTo("From", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("Accepted", true);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject ob : list) {
                        updateFriendList(ob);
                        deleteRequest(ob);
                    }
                } else {
                    Toast.makeText(getActivity(), "No Friend Requests Accepted", Toast.LENGTH_SHORT);
                    Log.e("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void updateFriendList(ParseObject request){
        mFriend = null;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", request.getString("To"));
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, com.parse.ParseException e) {
                if (e == null) {
                    mFriend = parseUser;
                    ParseRelation<ParseUser> relation = ParseUser.getCurrentUser().getRelation("friends");
                    relation.add(mFriend);
                    ParseUser.getCurrentUser().saveInBackground();
                } else {
                    Log.e("inbox", "getFriendError: " + e.getMessage());

                }
            }
        });
    }

    public void loggedOut(){
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, outArrayStrings);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == 0) {
                    Intent intent = new Intent(getActivity(), RegisterActivity.class);
                    startActivityForResult(intent, LOGGED_IN);
                } else if (id == 1) {
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    startActivityForResult(intent, LOGGED_IN);
                } else {
                    //Toast.makeText(this., "Image capture Failed!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            if(requestCode == LOGGED_IN){
                loggedIn = true;
                Log.i("MM", "user: " + ParseUser.getCurrentUser().getUsername());
                setMenu();
            }else if(requestCode == LOGGED_OUT){
                ParseUser.getCurrentUser().logOut();
                loggedIn = false;
                setMenu();
                Log.i("MM", "LOGGED OUT");
            }
        }else{
            Log.i("MainMenuFragment", "Error Registering/Logging in");
        }

    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }


}
