package android.larrimorea.snapchat;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PicAdapter extends ArrayAdapter<Image> {
    public PicAdapter(Context context,  ArrayList<Image> images) {
        super(context, 0, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Image image = getItem(position);


        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        //ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);

        //TextView textView = (TextView)convertView.findViewById(android.R.id.text1);
        //textView.setText(post.title);

        return convertView;
    }
}