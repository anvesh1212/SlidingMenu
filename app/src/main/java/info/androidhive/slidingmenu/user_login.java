package info.androidhive.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.slidingmenu.activity.MainActivity2;
import info.androidhive.slidingmenu.data.AppConstants;
import info.androidhive.slidingmenu.data.userData;
import info.androidhive.slidingmenu.feed.app.AppController;


public class user_login extends Activity {

    private static final String TAG = user_login.class.getSimpleName();
    private static String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

    }


    public void loginButtonClick(View view){

        final EditText login_user_id = (EditText) findViewById(R.id.login_user_id);

        userId =login_user_id.getText().toString();
        userData.setUserId(userId);
        userDetailsRequest();
    }

    public void userDetailsRequest(){


        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                AppConstants.URL_USER_DETAILS, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {

                    try {
                        JSONObject detailsObj = response.getJSONObject("DETAILS");
                        userData.setFirstName( detailsObj.getString("first_name"));
                        userData.setLastName( detailsObj.getString("last_name"));
                        userData.setProfilePicLink( detailsObj.getString("profile_pic"));

                        Log.d(TAG, "user details are updated.");
                        Log.d(TAG, "onResponse: "+response.toString());

                        Intent goFeed = new Intent(user_login.this, MainActivity2.class);
                        startActivity(goFeed);

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
