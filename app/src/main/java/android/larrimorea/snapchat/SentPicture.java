package android.larrimorea.snapchat;

/**
 * Created by Alex on 2/15/2016.
 */
public class SentPicture {
    private boolean Viewed;
    private String From;
    private String To;
    private String picLocation;

    public SentPicture(){

    }

    public boolean isViewed() {
        return Viewed;
    }

    public void setViewed(boolean viewed) {
        Viewed = viewed;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getPicLocation() {
        return picLocation;
    }

    public void setPicLocation(String picLocation) {
        this.picLocation = picLocation;
    }
}
