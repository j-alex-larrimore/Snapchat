package android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
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

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainMenuFragment extends Fragment{
    private int LOGGED_IN = 0;
    private int LOGGED_OUT = 1;

    protected ListView listView;
    public boolean loggedIn = false;
    private ArrayAdapter<String> mAdapter;
    private String[] inArrayStrings;
    private String[] outArrayStrings;
    private BackendlessUser[] newFriends;
    private BackendlessUser mFriend;
    private boolean pause = false;
    private boolean friendFound = false;


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

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }



    public void setMenu(){
        if(loggedIn) {
            loggedIn();
        }else{
            loggedOut();
        }
    }

    public void loggedIn(){
        try {
            updateFriends();
        }catch(InterruptedException e){
            Log.e("LoggedIn error", "Error: " + e);
        }

        deleteRequests();

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, inArrayStrings);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
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
            }
        });
    }

   private void updateFriends() throws InterruptedException{
           AsyncCallback<BackendlessCollection<FriendRequests>> callback=new AsyncCallback<BackendlessCollection<FriendRequests>>()
           {
               @Override
               public void handleResponse( BackendlessCollection<FriendRequests> reqs )
               {
                   Iterator<FriendRequests> iterator=reqs.getCurrentPage().iterator();
                    Log.i("updateFriends", "handling response");
                   while( iterator.hasNext() )
                   {
                       BackendlessUser curUser = Backendless.UserService.CurrentUser();
                       FriendRequests requests =iterator.next();
                       if(requests.getFrom().equals(curUser.getProperty("name").toString()) && requests.isAccepted() == true){
                           System.out.println("Found accepted request!");
                           addFriend(requests.getTo().toString());
                       }
                   }

               }

               @Override
               public void handleFault( BackendlessFault backendlessFault )
               {

               }
           };

           Backendless.Data.of( FriendRequests.class ).find(callback);
    }

    public void addFriend(final String to){
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

                                    if (to.equals(user1.getProperty("name").toString())) {
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
                                        deleteRequests();
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

    private void deleteRequests() {

        AsyncCallback<BackendlessCollection<FriendRequests>> callback=new AsyncCallback<BackendlessCollection<FriendRequests>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<FriendRequests> reqs )
            {
                Iterator<FriendRequests> iterator=reqs.getCurrentPage().iterator();

                while( iterator.hasNext() )
                {
                    FriendRequests requests =iterator.next();
                    if(requests.getFrom().equals(Backendless.UserService.CurrentUser().getProperty("name").toString()) && requests.isAccepted() == true) {
                        Backendless.Persistence.save( requests, new AsyncCallback<FriendRequests>() {
                            public void handleResponse( FriendRequests delRequest )
                            {
                                Backendless.Persistence.of(FriendRequests.class).remove(delRequest, new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long aLong) {
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {

                                    }
                                });
                            }
                            @Override
                            public void handleFault( BackendlessFault fault )
                            {
                                // an error has occurred, the error code can be retrieved with fault.getCode()
                            }
                        });

                    }
                }
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {

            }
        };

        Backendless.Data.of( FriendRequests.class ).find(callback);

    }

    public void loggedOut(){
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, outArrayStrings);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
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
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("activity result", "Code: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            if(requestCode == LOGGED_IN){
                loggedIn = true;
                BackendlessUser currentUser = Backendless.UserService.CurrentUser();
                Log.i("MM", "user: " + currentUser.getProperty("name"));
                setMenu();
            }else if(requestCode == LOGGED_OUT){
                final AsyncCallback<Void> logoutResponder = new AsyncCallback<Void>()
                {
                    @Override
                    public void handleResponse( Void aVoid )
                    {
                        boolean isValidLogin = Backendless.UserService.isValidLogin();
                        System.out.println( "Is user logged in? - " + isValidLogin );
                    }

                    @Override
                    public void handleFault( BackendlessFault backendlessFault )
                    {
                        System.out.println( "Server reported an error " + backendlessFault.getMessage() );
                    }
                };

                Backendless.UserService.logout(logoutResponder);
                loggedIn = false;
                setMenu();
                Log.i("MM", "LOGGED OUT");
            }
        }else{
            Log.i("MainMenuFragment", "Error Registering/Logging in");
        }

    }
}
