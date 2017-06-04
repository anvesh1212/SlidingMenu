package info.androidhive.slidingmenu.data;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.feed.app.AppController;


/**
 * Created by anves on 17-10-2016.
 */

public class userData {

    private static final String TAG = userData.class.getSimpleName();
    private static boolean validUser = false;
    private static String userId;
    private static String firstName;
    private static String lastName;
    private static String profilePicLink;
    //GroupsList<GroupName, GroupId>
    private static ArrayMap<String,String> GroupsList = new ArrayMap<String,String>();
    private static String URL_GROUP_LIST = "http://"+AppConstants.IpAddress+"/post/group_list.php?user_id=";

    public static void setUserId(String userId1){
        userId = userId1;
        validUser = true;
    }

    public static String getUserId(){
        return userId;
    }

    public static String getFullName(){
        return firstName+" "+lastName;
    }

    public static String getFirstName(){
        return firstName;
    }

    public static void setFirstName(String name){
        firstName = name;
        return;
    }

    public static String getLastName(){
        return lastName;
    }

    public static void setLastName(String name){
        lastName = name;
        return;
    }

    public static String getProfilePicLink(){
        return profilePicLink;
    }

    public static void setProfilePicLink(String link){
        profilePicLink = new String(link) ;
        AppConstants.updateURLProfilePic();
        return;
    }

    public static boolean isValidUser(){
        return validUser;
    }


    public static void addGroup(String groupName, String groupId){
        GroupsList.put(groupName,groupId);
        Log.d(TAG, "group added to user data "+groupName+"  "+groupId);
    }

    public static int GroupListCount(){
        Log.d(TAG, "group list size is requested to user data, size :"+GroupsList.size());
        return GroupsList.size();
    }


    public static String getGroupId(String groupName){
        return GroupsList.get(groupName);
    }

    public static String[] getGroupNames(){
        int i = 0;
        String[] groupNames = new String[GroupsList.size()];
        for (String name : GroupsList.keySet() ) {
            groupNames[i++] = name;
        }
        Log.d(TAG, "group list array is requested to user data ");
        return groupNames;
    }

    public static boolean clearData(){
       boolean wiped = false;

        {
            validUser = false;
            userId = null;
            GroupsList.clear();
            firstName = null;
            lastName = null;
            profilePicLink = null;
            wiped = true;
        }

        return  wiped;

    }

    public static void requestUserDetails(){


        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                AppConstants.URL_USER_DETAILS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {

                    try {
                        JSONObject detailsObj = response.getJSONObject("DETAILS");
                        firstName =  detailsObj.getString("first_name");
                        lastName =  detailsObj.getString("last_name");
                        profilePicLink = detailsObj.getString("profile_pic");

                        Log.d(TAG, "user details are updated.");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "user details request failed.");

            }
        });
        jsonReq.setShouldCache(false);

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);

    }


}
