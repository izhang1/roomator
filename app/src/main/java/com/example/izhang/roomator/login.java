/* Author: Ivan Zhang
*  Company: NovusApp.com
*  Application: Roomator
*
*  login.java: Allows a user to login, also allows them to login via another application such as Facebook
*
 */
package com.example.izhang.roomator;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class login extends Activity {

    ValueEventListener loginListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Removes Header
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button registerButton = (Button)findViewById(R.id.regButton);
        Button loginButton = (Button)findViewById(R.id.loginButton);
        final EditText emailBox = (EditText)findViewById(R.id.emailBox);
        final EditText passwordBox = (EditText)findViewById(R.id.passBox);

        final Firebase myFirebaseRef = new Firebase("https://roomator.firebaseio.com/");

        // Do a check on the android_id, proceed onto the next page if the user has already logged in with this android_id
        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initial check to see if there are values in the text boxes
                if (emailBox.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_LONG).show();
                } else if (passwordBox.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_LONG).show();
                }else {

                    final Firebase loginRef = myFirebaseRef.child("account");

                    //Toast.makeText(getApplicationContext(), "Email : " + emailBox.getText().toString(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Password :" + passwordBox.getText().toString(), Toast.LENGTH_LONG).show();
                    // Firebase event listener. Loop through registered users to determine if the login is correct or not.
                    loginRef.addValueEventListener(loginListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int count = (int) dataSnapshot.getChildrenCount();
                            Log.v("Event", "Count: "+ count);
                            String dbEmail = "";
                            String dbPass = "";
                            for (int i = 0; i <= count; i++) {
                                String in = Integer.toString(i);

                                if (dataSnapshot.child(in).child("email").getValue() != null) {
                                    dbEmail = dataSnapshot.child(in).child("email").getValue().toString();
                                }

                                if (dataSnapshot.child(in).child("password").getValue() != null) {
                                    dbPass = dataSnapshot.child(in).child("password").getValue().toString();
                                }

                                if (dbEmail.equals(emailBox.getText().toString())) {
                                    String encryptedPass = "";
                                    try {
                                        MessageDigest digester = java.security.MessageDigest.getInstance("MD5");
                                        digester.update(passwordBox.getText().toString().getBytes());
                                        byte[] hash = digester.digest();
                                        StringBuffer hexString = new StringBuffer();
                                        for (int o = 0; o < hash.length; o++) {
                                            if ((0xff & hash[o]) < 0x10) {
                                                hexString.append("0" + Integer.toHexString((0xFF & hash[o])));
                                            }
                                            else {
                                                hexString.append(Integer.toHexString(0xFF & hash[o]));
                                            }
                                        }
                                        encryptedPass = hexString.toString();
                                    } catch (NoSuchAlgorithmException e) {
                                        Log.v("ErrorRegister", "No Algorithm Exception!");
                                    }
                                    if(dbPass.equals(encryptedPass)) {
                                        Log.v("login", "Login and password succeeded " + i);
                                        myFirebaseRef.child("didlogin").child(android_id).setValue(i);
                                        myFirebaseRef.child("account").child(Integer.toString(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Intent naviIntent = dataSnapshot.hasChild("group") ? new Intent(getApplicationContext(), navi.class) : new Intent(getApplicationContext(), newGroup.class);
                                                startActivity(naviIntent);
                                                myFirebaseRef.removeEventListener(loginListener);
                                                finish();
                                            }

                                            public void onCancelled(FirebaseError error) {

                                            }
                                        });

                                    }
                                } else {
                                    Log.v("Event", "Email Box: "+emailBox.getText().toString());
                                    Log.v("Event", "Password Box: " + passwordBox.getText().toString());
                                    Log.v("Event", "ELSE HIT!!");
                                    Toast.makeText(getApplicationContext(), "The password and/or email is incorrect", Toast.LENGTH_LONG);
                                }
                            }
                            passwordBox.getText().clear();
                            emailBox.getText().clear();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(getApplicationContext(), register.class);
                startActivity(regIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
