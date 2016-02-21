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

    private void deleteRequests() {
        final AsyncCallback<Long> deleteResponder = new AsyncCallback<Long>()
        {
            @Override
            public void handleResponse( Long timestamp )
            {
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {

            }
        };

        AsyncCallback<BackendlessCollection<FriendRequests>> callback=new AsyncCallback<BackendlessCollection<FriendRequests>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<FriendRequests> reqs )
            {
                Iterator<FriendRequests> iterator=reqs.getCurrentPage().iterator();

                while( iterator.hasNext() )
                {
                    FriendRequests requests =iterator.next();
                    if(requests.getTo().equals(Backendless.UserService.CurrentUser().getProperty("name").toString()) && requests.isAccepted() == true) {
                        Backendless.Data.of(FriendRequests.class).remove(requests, deleteResponder);
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
       friendFound = false;
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
                           ArrayList<BackendlessUser> friends = new ArrayList<BackendlessUser>();
                           BackendlessUser newFriend = findFriend(requests.getTo().toString());
                           friends.add(newFriend);
                           Log.i("updateFriends", "saving friend");
                           curUser.setProperty("friends", friends);
                           Backendless.Data.of( BackendlessUser.class ).save(curUser);
                           friendFound = true;
                       }
                   }
                   if(friendFound) {
                       Log.i("updateFriends", "deleting request");
                       deleteRequests();
                   }

               }

               @Override
               public void handleFault( BackendlessFault backendlessFault )
               {

               }
           };

           Backendless.Data.of( FriendRequests.class ).find(callback);
    }

    public BackendlessUser findFriend(final String to){
        mFriend = null;
        Backendless.Data.of( BackendlessUser.class ).find( new AsyncCallback<BackendlessCollection<BackendlessUser>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<BackendlessUser> users )
            {
                Iterator<BackendlessUser> userIterator = users.getCurrentPage().iterator();

                while( userIterator.hasNext() )
                {
                    BackendlessUser user = userIterator.next();

                    if(to.equals(user.getProperty("name").toString())){
                        mFriend = user;
                    }
                }
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {
                System.out.println( "Server reported an error - " + backendlessFault.getMessage() );
            }
        } );

        return mFriend;
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
