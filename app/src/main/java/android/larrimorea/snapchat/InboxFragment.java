package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InboxFragment extends Fragment {

    private ArrayList<String> frArrayStrings = new ArrayList<String>();
    private ArrayList<String> picArrayStrings = new ArrayList<String>();
    private ArrayList<FriendRequests> friendRequests = new ArrayList<FriendRequests>();
    private ArrayList<SentPicture> receivedPictures = new ArrayList<SentPicture>();
    private BackendlessUser[] newFriends;
    private ArrayAdapter mAdapter;
    private ArrayAdapter mPicAdapter;
    private ListView requestView;
    private ListView picView;
    private View mView;
    private BackendlessUser mFriend;
    private boolean pause = false;
    private Bitmap picDownload;

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
        friendRequests = null;
        AsyncCallback<BackendlessCollection<FriendRequests>> callback=new AsyncCallback<BackendlessCollection<FriendRequests>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<FriendRequests> reqs )
            {
                Iterator<FriendRequests> iterator=reqs.getCurrentPage().iterator();

                while( iterator.hasNext() )
                {
                    FriendRequests requests =iterator.next();
                    if(requests.getTo().equals(Backendless.UserService.CurrentUser().getProperty("name").toString()) && requests.isAccepted() == false) {
                       friendRequests.add(requests);
                    }
                }
                fillFriendRequests();
                displayFriendRequests();
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {

            }
        };

        Backendless.Data.of( FriendRequests.class ).find(callback);
    }

    private void fillFriendRequests(){
        for(FriendRequests fr: friendRequests){
            frArrayStrings.add(fr.getFrom());
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

    public void addFriend(final String from){
        final BackendlessUser curUser = Backendless.UserService.CurrentUser();
        newFriends = null;

        final AsyncCallback<BackendlessCollection<BackendlessUser>> responder = new AsyncCallback<BackendlessCollection<BackendlessUser>>()
        {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> backendlessUserBackendlessCollection) {
                Iterator<BackendlessUser> userIterator = backendlessUserBackendlessCollection.getCurrentPage().iterator();

                while( userIterator.hasNext() ){

                    BackendlessUser user = userIterator.next();

                    //ONCE FRIENDS LIST IS LOADED, LOOKING THROUGH USERS TO FIND  CURRENT USER
                    if(user.getProperty("name").toString().equals(curUser.getProperty("name").toString())){
                        Log.i("User found", user.getProperty("name").toString());
                        Object[] temp = (Object[])user.getProperty("friends");

                        final BackendlessUser[] friends;

                        //IF FRIENDS LIST IS EMPTY IT CAN'T BE CAST TO A BACKENDLESS USER ARRAY
                        if(temp.length > 0 ) {
                            friends = (BackendlessUser[]) user.getProperty("friends");
                            Log.i("Friends", "Friends found:)");
                        }else{
                            friends = new BackendlessUser[0];
                            Log.i("Friends", "No friends found?");
                        }

                        //NEED TO FIND THE NEW FRIEND IN THE FRIENDS LIST
                        Backendless.Data.of( BackendlessUser.class ).find(new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                            @Override
                            public void handleResponse(BackendlessCollection<BackendlessUser> users) {
                                Iterator<BackendlessUser> userIterator = users.getCurrentPage().iterator();

                                while (userIterator.hasNext()) {
                                    BackendlessUser user1 = userIterator.next();

                                    if (from.equals(user1.getProperty("name").toString())) {
                                        newFriends = new BackendlessUser[friends.length + 1];

                                        for (int i = 0; i < friends.length; i++) {
                                            newFriends[i] = friends[i];
                                        }
                                        newFriends[friends.length] = user1;

                                    }
                                }
                                //DONT NEED TO KEEP THIS, JUST PRINTS OUT THE UPDATED FRIENDS LIST IF YOU THINK STUDENTS WANT TO SEE IT
                                for (int i = 0; i < newFriends.length; i++) {
                                    Log.i("Friends List to Save: ", newFriends[i].getProperty("name").toString());

                                }
                                curUser.setProperty("friends", newFriends);

                                Backendless.UserService.update(curUser, new AsyncCallback<BackendlessUser>() {
                                    @Override
                                    public void handleResponse(BackendlessUser backendlessUser) {
                                        Log.i("Inbox", "Add friend: User updated!");
                                        setAccepted(from);
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {

                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                System.out.println("Server reported an error - " + backendlessFault.getMessage());
                            }
                        });


                    }
                }
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {
                Log.e("GetFRIENDs", "ERROR");
            }
        };

    //NEED TO LOAD THE FRIENDS LIST USING THIS CODE AND WORK WITH IT IN THE RESPONDER UP ABOVE^^^^
        BackendlessDataQuery query = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addRelated("friends");
        query.setQueryOptions(queryOptions);
        Backendless.Data.of( BackendlessUser.class ).find(query, responder);
    }

    private void setAccepted(final String from){
        AsyncCallback<BackendlessCollection<FriendRequests>> callback=new AsyncCallback<BackendlessCollection<FriendRequests>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<FriendRequests> reqs )
            {
                Iterator<FriendRequests> iterator=reqs.getCurrentPage().iterator();

                while( iterator.hasNext() )
                {
                    FriendRequests requests =iterator.next();
                    if(requests.getTo().equals(Backendless.UserService.CurrentUser().getProperty("name").toString()) && from.equals(requests.getFrom().toString())) {
                        requests.setAccepted(true);
                        Backendless.Persistence.save(requests, new AsyncCallback<FriendRequests>() {
                            public void handleResponse(FriendRequests req) {
                                Log.i("inbox setAccepted", "FriendRequest Set to Accepted");
                            }

                            public void handleFault(BackendlessFault fault) {
                                Log.i("inbox setAccepted", "Error updating friend request" + fault);
                                // an error has occurred, the error code can be retrieved with fault.getCode()
                            }
                        });
                    }
                }
                getFriendRequests();
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {

            }
        };

        Backendless.Data.of( FriendRequests.class ).find(callback);
    }


    private void setClickListener() {
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
        AsyncCallback<BackendlessCollection<SentPicture>> callback=new AsyncCallback<BackendlessCollection<SentPicture>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<SentPicture> pics )
            {
                Iterator<SentPicture> iterator=pics.getCurrentPage().iterator();

                while( iterator.hasNext() )
                {
                    SentPicture pic =iterator.next();
                    if(pic.getTo().equals(Backendless.UserService.CurrentUser().getProperty("name").toString())) {
                        receivedPictures.add(pic);
                    }
                }
                fillSentPics();
                displayPics();
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {

            }
        };

        Backendless.Data.of( SentPicture.class ).find(callback);
    }

   private void fillSentPics(){
        for(SentPicture pic: receivedPictures){
            picArrayStrings.add(pic.getFrom());
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

                addFriend(from);


                dialog.cancel();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    private void acceptPic(final String from){
        AsyncCallback<BackendlessCollection<SentPicture>> callback=new AsyncCallback<BackendlessCollection<SentPicture>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<SentPicture> pics )
            {
                Iterator<SentPicture> iterator=pics.getCurrentPage().iterator();

                while( iterator.hasNext() )
                {
                    SentPicture pic =iterator.next();
                    if(pic.getTo().equals(Backendless.UserService.CurrentUser().getProperty("name").toString()) && pic.getFrom().equals(from)) {
                        /***********************************************************
                        If we want to add deleting viewed picutres feature it should go here
                         **********************************************************/

                        String fileName = "https://develop.backendless.com/console/60001609-65BC-FFE7-FF49-6609EF9E0C00/appversion/FB26B1D8-E700-FA56-FF36-8C1097AE7300/zxrqabgugfpuzeqqzqhmuiicuymbdubtpnhw/files/view/mypics/" + pic.getPicLocation();
                        String[] str = new String[1];
                        str[0] = fileName;
                        //downloadFile(fileName);
                        DownloadImageTask task = new DownloadImageTask();
                        task.execute(str);
                        try {
                            Bitmap bmp = (Bitmap)task.get(30, TimeUnit.SECONDS);
                            makePicPopup(bmp);

                        }catch(Exception e){
                            Log.i("Inbox", "HandleResponseError" + e);
                        }
                        return;
                    }
                }

            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {

            }
        };

        Backendless.Data.of( SentPicture.class ).find(callback);

    }

    private void makePicPopup(Bitmap bmp){
        if(bmp != null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            ImageView image = new ImageView(getActivity());

            alert.setView(image);
            image.setImageBitmap(bmp);

            alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                    getActivity().finish();
                }
            });

            alert.show();
        }else{
            Log.i("makePicPopup", "Error loading image");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }
}