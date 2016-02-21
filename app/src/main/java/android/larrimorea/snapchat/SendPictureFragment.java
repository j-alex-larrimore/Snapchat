package android.larrimorea.snapchat;

        import android.app.Activity;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.support.v7.app.AlertDialog;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.Toast;

        import com.backendless.Backendless;
        import com.backendless.BackendlessCollection;
        import com.backendless.BackendlessUser;
        import com.backendless.async.callback.AsyncCallback;
        import com.backendless.exceptions.BackendlessFault;
        import com.backendless.persistence.BackendlessDataQuery;
        import com.backendless.persistence.QueryOptions;

        import java.lang.reflect.Array;
        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;

        import weborb.client.Responder;


public class SendPictureFragment extends Fragment {
    private List<String> arrayStrings  = new ArrayList<String>();
    private ArrayAdapter<String> mArrayAdapter;
    protected ListView listView;
    private static Uri selectedPic = null;
    private String mFriendReqName;
    private View mView;
    private boolean pause = false;

    private List<BackendlessUser> friendsList = new ArrayList<BackendlessUser>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.send_image, container, false);

        pause = false;

        getFriends();

        return mView;
    }

    public void getFriends(){

        final AsyncCallback<BackendlessCollection<BackendlessUser>> responder = new AsyncCallback<BackendlessCollection<BackendlessUser>>()
        {
            @Override
            public void handleResponse(BackendlessCollection<BackendlessUser> backendlessUserBackendlessCollection) {
                Iterator<BackendlessUser> userIterator = backendlessUserBackendlessCollection.getCurrentPage().iterator();

                while( userIterator.hasNext() ){

                    BackendlessUser user = userIterator.next();

                    if(user.getProperty("name").toString().equals(Backendless.UserService.CurrentUser().getProperty("name").toString())){
                        Log.i("User found", user.getProperty("name").toString());
                        BackendlessUser[] friends = (BackendlessUser[])user.getProperty("friends");
                        for(BackendlessUser friend: friends){
                            Log.i("Responder2b", friend.getProperty("name").toString());
                            friendsList.add(friend);
                        }
                        fillFriends(friendsList);
                        displayFriends();
                    }

                }
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {
                Log.e("GetFRIENDs", "ERROR");
            }
        };


        BackendlessDataQuery query = new BackendlessDataQuery();
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addRelated("friends");
        query.setQueryOptions(queryOptions);
        Backendless.Data.of( BackendlessUser.class ).find(query, responder);


    }

    private void fillFriends(List<BackendlessUser> list){
        for(BackendlessUser ob: list){
            arrayStrings.add(ob.getProperty("name").toString());
        }
        if(arrayStrings == null){
            arrayStrings.add("No Friends Yet. Add some Friends!");
        }
    }

    private void displayFriends(){
        listView = (ListView) mView.findViewById(R.id.listViewSend);
        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayStrings);
        listView.setAdapter(mArrayAdapter);
        setClickListener();
    }

    private void setClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (pause == false) {
                    pause = true;
                    Intent intent = new Intent(getActivity(), ChoosePicActivity.class);
                    String str = (String) mArrayAdapter.getItem(position);
                    intent.putExtra("to", str);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static void setPicture(Uri pic){
        selectedPic = pic;
    }

    public static Uri getPicture(){
        return selectedPic;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.send_image, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_add_friend:
                makePopup();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void makePopup(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Type your Friend's Name");

// Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFriendReqName = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(mFriendReqName!= null){
                    searchForFriend();
                    dialog.cancel();
                }
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

    public void searchForFriend(){
        Backendless.Data.of( BackendlessUser.class ).find( new AsyncCallback<BackendlessCollection<BackendlessUser>>()
        {
            @Override
            public void handleResponse( BackendlessCollection<BackendlessUser> users )
            {
                Iterator<BackendlessUser> userIterator = users.getCurrentPage().iterator();

                while( userIterator.hasNext() )
                {
                    BackendlessUser user = userIterator.next();

                   if(mFriendReqName.equals(user.getProperty("name").toString())){
                       sendFriendRequest(user);
                   }
                }
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {
                System.out.println( "Server reported an error - " + backendlessFault.getMessage() );
            }
        } );
    }

    public void sendFriendRequest(BackendlessUser user){
        String from = Backendless.UserService.CurrentUser().getProperty("name").toString();
        FriendRequests friendReq = new FriendRequests();
        friendReq.setFrom(from);
        friendReq.setTo(user.getProperty("name").toString());
        Backendless.Persistence.save(friendReq, new AsyncCallback<FriendRequests>() {
            public void handleResponse(FriendRequests req) {
                Toast.makeText(getActivity(), "Friend Requested!", Toast.LENGTH_SHORT).show();
            }

            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        pause = false;
    }
}
