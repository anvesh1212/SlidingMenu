package info.androidhive.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import info.androidhive.slidingmenu.data.userData;


public class user_login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

    }


    public void loginButtonClick(View view){

        final EditText login_user_id = (EditText) findViewById(R.id.login_user_id);

        userData.setUserId(login_user_id.getText().toString());
        Intent goFeed = new Intent(this, MainActivity.class);
        startActivity(goFeed);
    }



}
