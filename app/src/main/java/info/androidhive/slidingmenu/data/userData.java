package info.androidhive.slidingmenu.data;

import android.support.v4.util.ArrayMap;
import android.util.Log;



/**
 * Created by anves on 17-10-2016.
 */

public class userData {

    private static final String TAG = userData.class.getSimpleName();
    private static boolean validUser = false;
    private static String userId;
    //GroupsList<GroupName, GroupId>
    private static ArrayMap<String,String> GroupsList = new ArrayMap<String,String>();

    public static void setUserId(String userId1){
        userId = userId1;
        validUser = true;
    }

    public static String getUserId(){
        return userId;
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




}
