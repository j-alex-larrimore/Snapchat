package android.larrimorea.snapchat;

/**
 * Created by Alex on 2/15/2016.
 */
public class FriendRequests {
    private boolean Accepted;
    private String From;
    private String To;

    public FriendRequests(){

    }

    public FriendRequests(String To, String From){
        this.To = To;
        this.From = From;
    }

    public void setAccepted(boolean acc){
        Accepted = acc;
    }

    public void setTo(String to){
        this.To = to;
    }

    public void setFrom(String from){
        this.From = from;
    }

    public boolean isAccepted() {
        return Accepted;
    }

    public String getFrom() {
        return From;
    }

    public String getTo() {
        return To;
    }
}
