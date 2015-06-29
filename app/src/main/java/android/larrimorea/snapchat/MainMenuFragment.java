package android.larrimorea.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainMenuFragment extends Fragment{
    protected ListView listView;
    public boolean loggedIn = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainmenu_fragment, container, false);

        String[] inArrayStrings = new String[]{
            "Inbox",
            "Take a Picture",
            "Send a Picture",
            "Sign Out"
    };

        String[] outArrayStrings = new String[]{
                "Register",
                "Log In"
        };

        listView = (ListView) view.findViewById(R.id.listView);

        if(loggedIn) {

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, inArrayStrings);
            listView.setAdapter(adapter);

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
                        startActivity(intent);
                    }else {
                        //Toast.makeText(this., "Image capture Failed!", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else{
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, outArrayStrings);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (id == 0) {
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        startActivity(intent);
                    } else if (id == 1) {
                        Intent intent = new Intent(getActivity(), LogInActivity.class);
                        startActivity(intent);
                    }  else {
                        //Toast.makeText(this., "Image capture Failed!", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

        return view;
    }
}
