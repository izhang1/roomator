package com.example.izhang.roomator;

import android.app.Activity;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;

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
        final EditText newGroupName = (EditText) this.findViewById(R.id.groupName);
        final EditText joinGroupId = (EditText) this.findViewById(R.id.groupID);

        //todo: When this button is clicked, verify group with joinGroupId. If there, add that group to the user's account. New activity to navi.
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroupButton.setElevation(8);
                final String groupId = joinGroupId.getText().toString();
                myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("group").hasChild(groupId)){
                            String userId = dataSnapshot.child("didlogin").child(android_id).getValue().toString();
                            myFirebaseRef.child("account").child(userId).child("group").setValue(groupId);
                            Toast.makeText(getApplicationContext(), "UserId " + userId, Toast.LENGTH_LONG).show();

                        }else{
                            Toast.makeText(getApplicationContext(),"That is not a valid group. Create a new group!", Toast.LENGTH_LONG).show();
                        }
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
                final String groupName = newGroupName.getText().toString();
                myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        int newId = idGen();
                        while(dataSnapshot.child("group").hasChild(Integer.toString(newId))){
                            newId = idGen();
                        }

                        myFirebaseRef.child("group").child(Integer.toString(newId)).child("name").setValue(groupName);
                        String userId = dataSnapshot.child("didlogin").child(android_id).getValue().toString();
                        myFirebaseRef.child("account").child(userId).child("group").setValue(newId);

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }
        });



    }

    public int idGen(){
        Random r = new Random( System.currentTimeMillis() );
        return (1 + r.nextInt(2)) * 10000 + r.nextInt(10000);
    }


}
