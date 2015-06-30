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


public class MainMenuFragment extends Fragment{
    private int LOGGED_IN = 0;
    private int LOGGED_OUT = 1;

    protected ListView listView;
    public boolean loggedIn = false;
    private ArrayAdapter<String> mAdapter;
    private String[] inArrayStrings;
    private String[] outArrayStrings;

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

    public void setMenu(){
        if(loggedIn) {
            loggedIn();
        }else{
            loggedOut();
        }
    }

    public void loggedIn(){
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
                } else if(id == 3) {
                    Intent intent = new Intent(getActivity(), LogOutActivity.class);
                    startActivityForResult(intent, LOGGED_OUT);
                }else {
                    //Toast.makeText(this., "Image capture Failed!", Toast.LENGTH_LONG).show();
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
                setMenu();
                Log.i("MM", "LOGGED IN");
            }else if(requestCode == LOGGED_OUT){
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
