package info.androidhive.slidingmenu.feed;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import info.androidhive.slidingmenu.R;

/**
 * Created by anves on 17-10-2016.
 */



public class writePost extends Fragment {

    public writePost(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.write_post, container, false);

        Button postButton = (Button) rootView.findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView postDisplay = (TextView) rootView.findViewById(R.id.textView2);
                EditText postText = (EditText) rootView.findViewById(R.id.postText);
                postDisplay.setText(postText.getText());
            }
        });


        return rootView;
    }
}
