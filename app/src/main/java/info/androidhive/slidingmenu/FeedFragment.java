package info.androidhive.slidingmenu;

import android.support.v4.app.Fragment;
//import android.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.androidhive.slidingmenu.data.userData;
import info.androidhive.slidingmenu.data.AppConstants;
import info.androidhive.slidingmenu.feed.adapter.FeedListAdapter;
import info.androidhive.slidingmenu.feed.app.AppController;
import info.androidhive.slidingmenu.feed.data.FeedItem;
import info.androidhive.slidingmenu.feed.app.EndlessScrollListener;
import info.androidhive.slidingmenu.fragment.HomeFragment;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;



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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HomeFragment.OnFragmentInteractionListener mListener;




    public FeedFragment(String URL_FEED){
        this.URL_FEED = URL_FEED;
        Log.d(TAG, "Constructor : request for posts to url : "+URL_FEED);

    }

    public FeedFragment(){

    }


    private LayoutInflater inflater;

    public LayoutInflater getLayoutInflater(){
        return inflater;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        this.inflater = inflater;


/*        ImageButton FAB;
        FAB = (ImageButton)rootView.findViewById(R.id.floating_button_edit);

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, new writePost()).addToBackStack(null).commit();

            }
        });
*/

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

            boolean isItemAdded ;
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

                    isItemAdded = false;
                    Log.d(TAG, "parseJsonFeed: just added post id : "+feedObj.getInt("id"));
                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setGroupName(feedObj.getString("group_name"));

                    // Image might be null sometimes
                    String image = feedObj.isNull("image") ? null : AppConstants.URL_POST_DIRECTORY + feedObj
                            .getString("image");
                    item.setImge(image);
                    item.setStatus(feedObj.getString("status"));
                    item.setProfilePic(AppConstants.URL_POST_DIRECTORY + feedObj.getString("profilePic"));
                    item.setTimeStamp(feedObj.getString("timeStamp"));

                    // url might be null sometimes
                    String feedUrl = feedObj.isNull("url") ? null :  feedObj
                            .getString("url");
                    item.setUrl(feedUrl);

                    for (FeedItem iterator:feedItems) {
                        if(iterator.getId()<item.getId() ){
                            feedItems.add(feedItems.indexOf(iterator),item);
                            isItemAdded = true;
                            break;
                        }
                    }
                    if(!isItemAdded)
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


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



}





