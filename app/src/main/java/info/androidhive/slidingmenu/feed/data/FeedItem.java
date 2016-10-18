package info.androidhive.slidingmenu.feed.data;

/**
 * Created by anves on 07-07-2016.
 */


public class FeedItem {
    private int id, likes;
    private String name, status, image, profilePic, timeStamp, url, groupName;

    public FeedItem() {
    }

    public FeedItem(int id, String name, String image, String status,
                    String profilePic, String timeStamp, String url, String groupName, int likes) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
        this.groupName = groupName;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return image;
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroupName(){
        return groupName;
    }

    public void setGroupName(String groupName){
        this.groupName = groupName;
    }

    public void setLikes(int likes){
        this.likes = likes;
    }

    public int getLikes(){
        return likes;
    }
}
