package android.larrimorea.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 6/9/2015.
 */
public class SendPictureFragment extends Fragment {
    private List<String> arrayStrings  = new ArrayList<String>();
    private ArrayAdapter<String> mArrayAdapter;
    protected ListView listView;
    private static Uri selectedPic = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_image, container, false);

        listView = (ListView)view.findViewById(R.id.listViewSend);

        mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayStrings);

        listView.setAdapter(mArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChoosePicFragment.class);
                String str = (String)mArrayAdapter.getItem(position);
                Toast.makeText(getActivity(), "Clicked -" + str, Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        return view;
    }



    public static void setPicture(Uri pic){
        selectedPic = pic;
    }

    public static Uri getPicture(){
        return selectedPic;
    }
}
