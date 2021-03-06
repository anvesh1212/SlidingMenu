package info.androidhive.slidingmenu;

import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.feed.app.AppController;
import info.androidhive.slidingmenu.data.AppConstants;
import info.androidhive.slidingmenu.feed.writePost;
import info.androidhive.slidingmenu.model.NavDrawerItem;
import info.androidhive.slidingmenu.data.userData;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
    private String URL_MIX_FEED = "http://"+ AppConstants.IpAddress+"/post/json_posts.php?user_id="+userData.getUserId()+"&posts_from=";
	private String URL_GROUP_LIST = "http://"+AppConstants.IpAddress+"/post/group_list.php?user_id=";
    private String GroupId;
    private String URL_GROUP_FEED;

//image uploading data.
	int MAX_IMAGE_SIZE = 500;
	private static int RESULT_LOAD_IMG = 1;
	String imgDecodableString;
    public static Bitmap scaledBitmap;

 	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private ArrayList<String> navMenuTitles = new ArrayList<String>();
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;


    // a static variable to get a reference of our application context
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }


	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

			setContentView(R.layout.activity_main);


			mTitle = mDrawerTitle = getTitle();
        contextOfApplication = getApplicationContext();
			// load slide menu items
//			navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuTitles.add("NewsFeed") ;


		// making fresh volley request and getting json
		JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
				URL_GROUP_LIST+userData.getUserId(), null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				VolleyLog.d(TAG, "Response: " + response.toString());
				if (response != null) {

					try {
						JSONArray groupArray = response.getJSONArray("GROUPS");

						for (int i = 0; i < groupArray.length(); i++) {
							JSONObject groupObj = (JSONObject) groupArray.get(i);
                            userData.addGroup(groupObj.getString("group_name"),groupObj.getString("group_id"));

						}

                        makeNavDrawer(savedInstanceState);

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "group request failed.");

/*                String[] gl = userData.getGroupNames();
                for (String Name: gl) {
                    navMenuTitles.add(Name);
                }
*/
            }
		});
		jsonReq.setShouldCache(false);

        		// Adding request to volley request queue
		AppController.getInstance().addToRequestQueue(jsonReq);

        makeNavDrawer(savedInstanceState);


    }






    public void makeNavDrawer(Bundle savedInstanceState){



        // nav drawer icons from resources
        //navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem("NewsFeeds", R.drawable.ic_pages));
        Log.d(TAG, "trying to create navDrawerItems");
        String[] GL = userData.getGroupNames();
        int mx = 0;
        for (String Name: GL) {
            Log.d(TAG, "creating navDrawerItem  : "+mx++);
            navMenuTitles.add(Name);
            navDrawerItems.add(new NavDrawerItem(Name, R.drawable.ic_pages));
            Log.d(TAG, "Group List : just added group name : "+Name);
        }


 /*
			// adding nav drawer items to array
			// Home
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
			// Find People
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
			// Photos
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
			// Communities, Will add a counter here
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
			// Pages
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
			// What's hot, We  will add a counter here
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
			// rockstarpip.comlu.com
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
*/

        // Recycle the typed array
        // navMenuIcons.recycle();


        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);





        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

    }



	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;

        case R.id.action_logout:
            Logout();
            return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

    public void resetGroupUrl(){
        this.URL_GROUP_FEED = "http://"+AppConstants.IpAddress+"/post/json_group_posts.php?group_id="+this.GroupId+"&posts_from=";
    }


	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;

        if(position == 0){
            fragment = new FeedFragment(URL_MIX_FEED);
        }
        else {
            this.GroupId = userData.getGroupId(navMenuTitles.get(position));
            resetGroupUrl();
            Log.d(TAG, "request for group feed : "+this.GroupId+", url : "+ this.URL_GROUP_FEED );
            fragment = new FeedFragment(this.URL_GROUP_FEED);
        }


 /*

		switch (position) {
		case 0:
            fragment = new FeedFragment(URL_MIX_FEED+"");
			break;
		case 1:
            this.GroupId = userData.getGroupId(navMenuTitles.get(position));
			fragment = new FeedFragment(URL_GROUP_FEED+"");
			break;
		case 2:
			fragment = new FeedFragment(URL_FEED+"aravind.json");
			break;
		case 3:
			fragment = new FeedFragment(URL_FEED+"kittu.json");
			break;
		case 4:
			fragment = new FeedFragment(URL_FEED+"sahitya.json");
			break;
		case 5:

            Intent login = new Intent(this,user_login.class);
            startActivity(login);
            break;

		case 6:
			fragment = new FeedFragment("http://192.168.0.8:8008/practice/post/json_posts.php?user_id="+userData.getUserId()+"&posts_from=");
			break;

		default:
			break;
		}
*/

		if (fragment != null) {
			//FragmentManager fragmentManager =     getFragmentManager();
			//fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles.get(position));
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}




    public void Logout(){
		userData.clearData();
        Intent login = new Intent(this,user_login.class);
        startActivity(login);

    }



    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

	public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
								   boolean filter) {
		float ratio = Math.min(
				(float) maxImageSize / realImage.getWidth(),
				(float) maxImageSize / realImage.getHeight());
		int width = Math.round((float) ratio * realImage.getWidth());
		int height = Math.round((float) ratio * realImage.getHeight());

		Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
				height, filter);
		return newBitmap;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Context applicationContext = MainActivity.getContextOfApplication();
                Cursor cursor = applicationContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                //set image selected.
                writePost.isImageSelected = true;

                // Set the Image in ImageView after decoding the String
                ImageView imageView = (ImageView) findViewById(R.id.writePost_image_view) ;
				scaledBitmap = scaleDown(BitmapFactory.decodeFile(imgDecodableString), MAX_IMAGE_SIZE, true);
                //imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
				imageView.setImageBitmap(scaledBitmap);

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

  /*  @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
*/


}
