package info.androidhive.slidingmenu.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.slidingmenu.FeedFragment;
import info.androidhive.slidingmenu.R;
import info.androidhive.slidingmenu.data.userData;
import info.androidhive.slidingmenu.feed.app.AppController;
import info.androidhive.slidingmenu.data.AppConstants;
import info.androidhive.slidingmenu.fragment.GroupsFragment;
import info.androidhive.slidingmenu.fragment.HomeFragment;
import info.androidhive.slidingmenu.fragment.LibraryFragment;
import info.androidhive.slidingmenu.fragment.ScheduleFragment;
import info.androidhive.slidingmenu.fragment.SettingsFragment;
import info.androidhive.slidingmenu.fragment.TrendyFragment;
import info.androidhive.slidingmenu.other.CircleTransform;
import info.androidhive.slidingmenu.weekview.AsynchronousActivity;

import static info.androidhive.slidingmenu.data.AppConstants.URL_GROUP_LIST;
import static info.androidhive.slidingmenu.data.AppConstants.URL_MIX_FEED;
import static info.androidhive.slidingmenu.data.AppConstants.URL_WRITE_POST;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = MainActivity2.class.getSimpleName();


    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtCollege;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://rockstardaddy.tk/post/images/681437411.png";
    private static final String urlProfileImg = "http://rockstardaddy.tk/post/profile_pics/anvi.jpg";
    //private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    //private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_GROUPS = "groups";
    private static final String TAG_LIBRARY = "library";
    private static final String TAG_SCHEDULE = "schedule";
    private static final String TAG_TRENDY="trendy";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    //image uploading data.
    int MAX_IMAGE_SIZE = 500;
    public static Bitmap scaledBitmap;

    Spinner spinnerGroupName;
    ImageView imageView;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    public static Boolean isImageSelected = false;
    View tempPopupWindowLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);



        // Navigation view header
        navHeader = navigationView.inflateHeaderView(R.layout.nav_header_main);    //.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtCollege = (TextView) navHeader.findViewById(R.id.college);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                showPopup(view);
            }
        });

        loadNavHeader();

        // load nav menu header data after retriving userdetails from the web
        new Thread(new Runnable() {
            public void run() {
                getGroupsDetails();
            }
        }).start();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText(userData.getFullName());
        txtCollege.setText("JNTUH college of engineering, Jagityal");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        Log.d(TAG, "loadNavHeader: profile pic : "+AppConstants.URL_PROFILE_PIC);
        // Loading profile image
        Glide.with(this).load(AppConstants.URL_PROFILE_PIC)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                FeedFragment homeFragment = new FeedFragment(URL_MIX_FEED);
                return homeFragment;
            case 1:
                // photos

                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                // movies fragment
                LibraryFragment libraryFragment = new LibraryFragment();
                return libraryFragment;
            case 3:
                // notifications fragment
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                return scheduleFragment;
            case 4:
                // notifications fragment
                TrendyFragment trendyFragment = new TrendyFragment();
                return trendyFragment;
            case 5:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_groups:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_GROUPS;
                        break;
                    case R.id.nav_library:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_LIBRARY;
                        break;
                    case R.id.nav_schedule:

                        Intent intent = new Intent(MainActivity2.this, AsynchronousActivity.class);
                        startActivity(intent);

//                        navItemIndex = 3;
//                        CURRENT_TAG = TAG_SCHEDULE;
                        break;
                    case R.id.nav_trendy:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_TRENDY;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity2.this, AboutUsActivity.class));
                        drawer.closeDrawers();


                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity2.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            userData.setUserId("");
            if(navItemIndex!=0)
                onBackPressed();
            onBackPressed();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }


    public void getGroupsDetails(){

        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                URL_GROUP_LIST, null, new Response.Listener<JSONObject>() {

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
                        Log.d(TAG, "group list is updated.");

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



    }








    public void showPopup(View anchorView) {

        dimBackground();

    }

    private void dimBackground() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;

            final LayoutInflater inflater = getLayoutInflater();
                   // MainActivity2.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View backGround = inflater.inflate(R.layout.fadepopup,(ViewGroup) findViewById(R.id.fadePopup));
            final PopupWindow fadePopup = new PopupWindow(backGround, screenWidth, screenHeight, false);
            fadePopup.showAtLocation(backGround, Gravity.NO_GRAVITY, 0, 0);
            fadePopup.setFocusable(true);
            fadePopup.update();


            final View layout = inflater.inflate(R.layout.write_post,(ViewGroup) findViewById(R.id.writePost_linearLayout));
            final PopupWindow writePost = new PopupWindow(layout,DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT,false);
            writePost.showAtLocation(layout,Gravity.CENTER,0,0);
            writePost.setFocusable(true);
            writePost.setOutsideTouchable(false);
            writePost.update();
            tempPopupWindowLayout = layout;

            final Button cancel = (Button) layout.findViewById(R.id.writePost_cancel_button);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fadePopup.dismiss();
                    writePost.dismiss();
                    MainActivity2.isImageSelected = false;
                    tempPopupWindowLayout = null;

                }
            } );

        spinnerGroupName = (Spinner) layout.findViewById(R.id.spinner_groupNames);
        imageView = (ImageView) layout.findViewById(R.id.writePost_image_view);


        String [] values = userData.getGroupNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); //simple_dropdown_item_1line);
        spinnerGroupName.setAdapter(adapter);



        Button postButton = (Button) layout.findViewById(R.id.writePost_post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    public void run() {

                        final String KEY_USER_ID = "user_id";
                        final String KEY_GROUP_ID = "group_id";
                        final String KEY_POST_STATUS = "post_status";
                        final String KEY_POST_LINK = "post_link";
                        final String KEY_IMAGE = "image";

                        final EditText postText = (EditText) layout.findViewById(R.id.writePost_status);
                        final String groupSelected = spinnerGroupName.getSelectedItem().toString() ;
                        final EditText postLink  = (EditText) layout.findViewById(R.id.writePost_link);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_WRITE_POST,
                                new  Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(MainActivity2.this,response,Toast.LENGTH_LONG).show();
                                        MainActivity2.isImageSelected = false;
                                        tempPopupWindowLayout = null;

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(MainActivity2.this,error.toString(),Toast.LENGTH_LONG).show();
                                        MainActivity2.isImageSelected = false;
                                        tempPopupWindowLayout = null;

                                    }

                                }){

                            @Override
                            protected Map<String,String> getParams(){
                                Map<String,String> params = new HashMap<String, String>();
                                params.put(KEY_USER_ID,userData.getUserId());
                                params.put(KEY_GROUP_ID, userData.getGroupId(groupSelected));
                                params.put(KEY_POST_STATUS, postText.getText().toString());
                                params.put(KEY_POST_LINK, postLink.getText().toString());
                                if(MainActivity2.isImageSelected == true)
                                    params.put(KEY_IMAGE,getStringImage(MainActivity2.scaledBitmap));
                                return params;
                            }


                        };

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,-1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity2.this.getApplicationContext());
                        requestQueue.add(stringRequest);

                    }
                }).start();

                fadePopup.dismiss();
                writePost.dismiss();

            }

            public String getStringImage(Bitmap bmp){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                return encodedImage;
            }
       });
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
        //try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Context applicationContext = getApplicationContext();
                        //MainActivity2.getContextOfApplication();
                Cursor cursor = applicationContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                //set image selected.
                MainActivity2.isImageSelected = true;

                // Set the Image in ImageView after decoding the String
                ImageView imageView = (ImageView) tempPopupWindowLayout.findViewById(R.id.writePost_image_view) ;
                scaledBitmap = scaleDown(BitmapFactory.decodeFile(imgDecodableString), MAX_IMAGE_SIZE, true);

                //imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                imageView.setImageBitmap(scaledBitmap);


            } else {
                Toast.makeText(MainActivity2.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        //} catch (Exception e) {
         //   Toast.makeText(MainActivity2.this, "Something went wrong", Toast.LENGTH_LONG).show();
        //}

    }

}