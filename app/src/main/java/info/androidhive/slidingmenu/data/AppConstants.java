package info.androidhive.slidingmenu.data;

/**
 * Created by anves on 20-10-2016.
 */

public class AppConstants {

//    public static final String IpAddress = "http://192.168.225.148:80/practice/";
    public static final String IpAddress = "http://192.168.0.3:80/practice/";
//    public static final String IpAddress = "http://www.rockstardaddy.tk";

    //USER DETAILS related links.
    public static final String URL_USER_DETAILS = AppConstants.IpAddress+"user/user_details.php?user_id="+userData.getUserId();
    public static String URL_PROFILE_PIC = AppConstants.IpAddress+"post/"+userData.getProfilePicLink() ;


    //POST related links.
    public static final String URL_MIX_FEED =  AppConstants.IpAddress+"post/json_posts.php?user_id="+userData.getUserId()+"&posts_from=";
    public static final String URL_GROUP_LIST = AppConstants.IpAddress+"post/group_list.php?user_id="+userData.getUserId();
    public static final String Url_GROUP_FEED = AppConstants.IpAddress+"post/json_group_posts.php?group_id=%s&posts_from=";
    public static final String URL_POST_DIRECTORY = AppConstants.IpAddress+"post/";
    public static final String URL_WRITE_POST = AppConstants.IpAddress+"post/write_post.php";

    //SCHEDULE related links.
    public static final String URL_MODIFY_WEEKLY_SCHEDULE =  AppConstants.IpAddress+"schedule/modify_weekly_schedule.php";
    public static final String URL_MODIFY_GROUP_SCHEDULE = AppConstants.IpAddress+"schedule/modify_schedule.php";
    public static final String URL_WRITE_EVENT = AppConstants.IpAddress+"schedule/write_event.php";
    public static final String URL_USER_SCHEDULE_JSON = "schedule/user_schedule_json.php";
    public static final String URL_WEEKLY_SCHEDULE = "schedule/weekly_schedule.php";
    public static final String URL_MODIFIED_WEEKLY_SCHEDULE_JSON = "schedule/modified_weekly_schedule_json.php";


    public static void updateURLProfilePic(){
        URL_PROFILE_PIC = AppConstants.IpAddress+"post/"+userData.getProfilePicLink() ;
    }



}
