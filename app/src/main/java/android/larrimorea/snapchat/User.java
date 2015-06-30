package android.larrimorea.snapchat;

import android.graphics.Bitmap;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

//@ParseClassName("User")
//public class User extends ParseObject implements Serializable{
//
//    public User(){
//
//    }
//
//    public String getUsername(){
//
//        String s =  getString("username");
//        Log.i("User", "Username " + s);
//        return s;
//
//    }
//
//    public void setUsername(String username){
//        put("username", username);
//    }
//
//    public String getPassword(){
//        return getString("password");
//    }
//
//    public void setPassword(String password){
//        put("password", password);
//    }
//
//    public List<ParseFile> getPhotos(){
//        return getList("username");
//    }
//
//    public void setPhotos(ArrayList<ParseFile> photos){
//        put("photos", photos);
//    }
//
//    public List<String> getFriends(){
//        return getList("friends");
//    }
//
//    public void setFriends(ArrayList<String> friends){
//        put("friends", friends);
//    }
//
//    public List<String> getFriendRequests(){
//        return getList("friend_requests");
//    }
//
//    public void setFriendRequests(ArrayList<String> friendreqs){
//        put("friend_requests", friendreqs);
//    }
//
//    public List<ParseFile> getInbox(){
//        return getList("inbox");
//    }
//
//    public void setInbox(ArrayList<ParseFile> inbox){
//        put("inbox", inbox);
//    }
//}
