package info.androidhive.slidingmenu.feed;

import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.slidingmenu.MainActivity;
import info.androidhive.slidingmenu.R;
import info.androidhive.slidingmenu.data.userData;
import info.androidhive.slidingmenu.data.AppConstants;

import static android.app.Activity.RESULT_OK;

/**
 * Created by anves on 17-10-2016.
 */



public class writePost extends Fragment  {

    public writePost(){}

    Spinner spinnerGroupName;
    ImageView imageView;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    public static Boolean isImageSelected = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.write_post, container, false);

        spinnerGroupName = (Spinner) rootView.findViewById(R.id.spinner_groupNames);
        imageView = (ImageView) rootView.findViewById(R.id.writePost_image_view);

        String [] values = userData.getGroupNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerGroupName.setAdapter(adapter);



        Button postButton = (Button) rootView.findViewById(R.id.writePost_post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String URL_WRITE_POST = AppConstants.URL_WRITE_POST;
                final String KEY_USER_ID = "user_id";
                final String KEY_GROUP_ID = "group_id";
                final String KEY_POST_STATUS = "post_status";
                final String KEY_POST_LINK = "post_link";
                final String KEY_IMAGE = "image";

                final EditText postText = (EditText) rootView.findViewById(R.id.writePost_status);
                final String groupSelected = spinnerGroupName.getSelectedItem().toString() ;
                final EditText postLink  = (EditText) rootView.findViewById(R.id.writePost_link);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_WRITE_POST,
                        new  Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                            }

                        }){

                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put(KEY_USER_ID,userData.getUserId());
                        params.put(KEY_GROUP_ID, userData.getGroupId(groupSelected));
                        params.put(KEY_POST_STATUS, postText.getText().toString());
                        params.put(KEY_POST_LINK, postLink.getText().toString());
                        if(writePost.isImageSelected == true)
                            params.put(KEY_IMAGE,getStringImage(MainActivity.scaledBitmap));
                        return params;
                    }


                };

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                requestQueue.add(stringRequest);



             //  getFragmentManager().popBackStack();
                getActivity().onBackPressed();

            }
        });

        return rootView;
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
                    // Set the Image in ImageView after decoding the String
                    imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

                } else {
                    Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }










}



