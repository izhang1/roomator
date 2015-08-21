package com.example.izhang.roomator;

import android.app.Activity;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class newGroup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Removes Header
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        Firebase.setAndroidContext(getApplicationContext());



        // Setup Firebase
        final Firebase myFirebaseRef = new Firebase("https://roomator.firebaseio.com/");

        // Get android_id
        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final Button joinGroupButton = (Button) this.findViewById(R.id.joinGroupButton);
        final Button newGroupButton = (Button) this.findViewById(R.id.addGroupButton);
        EditText newGroupName = (EditText) this.findViewById(R.id.groupName);
        final EditText joinGroupId = (EditText) this.findViewById(R.id.groupID);

        //todo: When this button is clicked, verify group with joinGroupId. If there, add that group to the user's account. New activity to navi.
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroupButton.setElevation(8);
                String groupId = joinGroupId.toString();
                myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });

        //todo: When this button is clicked, verify that the name is new. Create a new group and add the user to that group. New activity to navi.
        newGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupButton.setElevation(8);

            }
        });


    }


}
