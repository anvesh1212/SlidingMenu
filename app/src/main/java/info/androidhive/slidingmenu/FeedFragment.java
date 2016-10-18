package info.androidhive.slidingmenu;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import info.androidhive.slidingmenu.feed.writePost;
import info.androidhive.slidingmenu.feed.adapter.FeedListAdapter;
import info.androidhive.slidingmenu.feed.app.AppController;
import info.androidhive.slidingmenu.feed.data.FeedItem;
import info.androidhive.slidingmenu.feed.app.EndlessScrollListener;
import info.androidhive.slidingmenu.data.userData;

import info.androidhive.slidingmenu.R;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;



import android.app.Activity;
import android.content.Entity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout;



/**
 * Created by anves on 26-07-2016.
 */

public class FeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = FeedFragment.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED;
    //= "http://192.168.0.8:8008/practice/post/json_posts.php?user_id="+userData.getUserId()+"&posts_from=";
    //getResources().getString(R.string.web_url) + getResources().getString(R.string.url_feed);
    //"http://192.168.0.8:8008/practice/post/json_posts.php?user_id=14JJ1A1237&posts_from=";
    //private String GroupId;
    //private String URL_GROUP_FEED = "http://192.168.0.8:8008/practice/post/json_group_posts.php?group_id="+GroupId+"&posts_from=-1";

    private SwipeRefreshLayout swipeRefreshLayout;






    public FeedFragment(String URL_FEED){
        this.URL_FEED = URL_FEED;
    }

    private LayoutInflater inflater;

    public LayoutInflater getLayoutInflater(){
        return inflater;

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        this.inflater = inflater;


        ImageButton FAB;
        FAB = (ImageButton)rootView.findViewById(R.id.floating_button_edit);

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, new writePost()).commit();


            }
        });


        listView = (ListView) rootView.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems, savedInstanceState);
        listView.setAdapter(listAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
                  * Showing Swipe Refresh animation on activity create
                  * As animation won't start on onCreate, post runnable is used
                  */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getPosts("-1");
            }
        });


        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                //customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                getPosts(""+ feedItems.get(totalItemsCount-1).getId());
                Log.d(TAG, "onLoadMore: request from more posts from post_id : "+feedItems.get(totalItemsCount-1).getId());

                return true; // ONLY if more data is actually being loaded; false otherwise.

            }
        });




        return rootView;


    }




    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);


                int ind = 0, feedSize = feedItems.size();
                boolean isExists = false;
                FeedItem itemDummy;

                while(ind<feedSize){

                    itemDummy = feedItems.get(ind);
                    if( itemDummy.getId() == feedObj.getInt("id"))
                    {
                        isExists = true;
                        Log.d(TAG, "parseJsonFeed: already exists, post_id : "+feedObj.getInt("id"));
                        break;
                    }
                    ind++;
                }
                if(!isExists) {

                    Log.d(TAG, "parseJsonFeed: just added post id : "+feedObj.getInt("id"));
                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setGroupName(feedObj.getString("group_name"));

                    // Image might be null sometimes
                    String image = feedObj.isNull("image") ? null : "http://192.168.0.8:8008/practice/post/" + feedObj
                            .getString("image");
                    item.setImge(image);
                    item.setStatus(feedObj.getString("status"));
                    item.setProfilePic("http://192.168.0.8:8008/practice/post/" + feedObj.getString("profilePic"));
                    item.setTimeStamp(feedObj.getString("timeStamp"));

                    // url might be null sometimes
                    String feedUrl = feedObj.isNull("url") ? null :  feedObj
                            .getString("url");
                    item.setUrl(feedUrl);

                    feedItems.add(item);
                }
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getPosts(String offset){



        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        //   getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        //  getActionBar().setIcon(
        //         new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // We first check for cached request
        final Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(URL_FEED+offset);

        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");


                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
                    URL_FEED+offset, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    swipeRefreshLayout.setRefreshing(false);
                    if (response != null) {
                        parseJsonFeed(response);

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    swipeRefreshLayout.setRefreshing(false);
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            jsonReq.setShouldCache(false);

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }



    }


    /**
          * This method is called when swipe refresh is pulled down
          */
    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "onRefresh: page requested for reload.");
        getPosts("-1");

    }





}





