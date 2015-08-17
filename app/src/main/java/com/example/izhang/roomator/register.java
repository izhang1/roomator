/* Author: Ivan Zhang
*  Company: NovusApp.com
*  Application: Roomator
*
*  register.java: Allows a new user to register
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class register extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Firebase.setAndroidContext(getApplicationContext());
        final Firebase myFirebaseRef = new Firebase("https://roomator.firebaseio.com/");

        final EditText usernameBox = (EditText) this.findViewById(R.id.usernameBox);
        final EditText emailBox = (EditText) this.findViewById(R.id.emailBox);
        final EditText passwordBox = (EditText) this.findViewById(R.id.passBox);
        final EditText fullnameBox = (EditText) this.findViewById(R.id.nameBox);
        final Button signup = (Button) this.findViewById(R.id.regButton);


        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final Calendar cal = Calendar.getInstance();
        cal.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Add listener to add new login for a person. Save the data.
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Checks to make sure the text is not blank when submitted
                        if (usernameBox.getText().toString().matches("")
                                || emailBox.getText().toString().matches("")
                                || passwordBox.getText().toString().matches("")) {

                            Toast.makeText(getApplicationContext(), "Please fill in all of the boxes", Toast.LENGTH_LONG).show();
                        } else {

                            int childCount = (int) dataSnapshot.child("account").getChildrenCount();
                            childCount++;
                            String strChildCount = Integer.toString(childCount);

                            boolean hasEmail = false;
                            for (int i = 0; i <= childCount; i++) {
                                if (dataSnapshot.child("account").child(Integer.toString(i)).child("email").exists()) {
                                    if (dataSnapshot.child("account").child(Integer.toString(i)).child("email").getValue().toString().contentEquals(emailBox.getText().toString())) {
                                        hasEmail = true;
                                        break;
                                    }
                                }
                            }

                            if (!hasEmail) {
                                // Add the account to the login
                                myFirebaseRef.child("account").child(strChildCount).child("email").setValue(emailBox.getText().toString());
                                myFirebaseRef.child("account").child(strChildCount).child("username").setValue(usernameBox.getText().toString());

                                Toast.makeText(getApplicationContext(), "Got to end of line!", Toast.LENGTH_LONG).show();

                                String encryptedPass = "";
                                try {
                                    MessageDigest digester = java.security.MessageDigest.getInstance("MD5");
                                    digester.update(passwordBox.getText().toString().getBytes());
                                    byte[] hash = digester.digest();
                                    StringBuffer hexString = new StringBuffer();
                                    for (int i = 0; i < hash.length; i++) {
                                        if ((0xff & hash[i]) < 0x10) {
                                            hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
                                        }
                                        else {
                                            hexString.append(Integer.toHexString(0xFF & hash[i]));
                                        }
                                    }
                                    encryptedPass = hexString.toString();
                                } catch (NoSuchAlgorithmException e) {
                                    Log.v("ErrorRegister", "No Algorithm Exception!");
                                }

                                myFirebaseRef.child("account").child(strChildCount).child("password").setValue(encryptedPass);
                                myFirebaseRef.child("account").child(strChildCount).child("fullname").setValue(fullnameBox.getText().toString());
                                myFirebaseRef.child("account").child(strChildCount).child("moneycount").setValue(0);
                                myFirebaseRef.child("account").child(strChildCount).child("startdate").setValue(cal.getTime().toString());
                                myFirebaseRef.child("didlogin").child(android_id).setValue(childCount);
                                Intent naviIntent = new Intent(getApplicationContext(), navi.class);
                                startActivity(naviIntent);
                                finish();

                            } else {
                                emailBox.getText().clear();
                                Toast.makeText(getApplicationContext(), "This email is already used", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
