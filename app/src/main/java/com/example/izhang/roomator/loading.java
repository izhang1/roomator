/* Author: Ivan Zhang
*  Company: NovusApp.com
*  Application: Roomator
*
*  loading.java: Loading screen before getting to the application itself
*
 */
package com.example.izhang.roomator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class loading extends Activity {

    ValueEventListener eventListener = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Removes Header
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Firebase.setAndroidContext(getApplicationContext());

        TextView loadingText = (TextView) findViewById(R.id.textViewLoading);

        // Checks to see if the network is available, shows toast if it is not
        if(isNetworkAvailable() == false) {
            loadingText.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Please enable internet", Toast.LENGTH_LONG).show();
            Log.d("NetworkAvailable", "Hello");
        }else {

            // Setup Firebase
            final Firebase myFirebaseRef = new Firebase("https://roomator.firebaseio.com/");

            // Get android_id
            final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            myFirebaseRef.addValueEventListener(eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("didlogin").child(android_id).exists()) {
                        String account_id = dataSnapshot.child("didlogin").child(android_id).getValue().toString();
                        Intent naviIntent = dataSnapshot.child("account").child(account_id).hasChild("group") ? new Intent(getApplicationContext(), navi.class) : new Intent(getApplicationContext(), newGroup.class);
                        if(dataSnapshot.child("account").child(account_id).hasChild("group")){
                            Log.d("loading", "CHILD EXISTS");
                        }
                        Log.d("loading", "Started Activity");

                        myFirebaseRef.removeEventListener(eventListener);
                        startActivity(naviIntent);
                        finish();
                    } else {
                        myFirebaseRef.removeEventListener(eventListener);
                        Intent loginIntent = new Intent(getApplicationContext(), login.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }
    }

    // Network method to check if there is connetion to internet or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo != null) return true;
        else return false;
    }
}
