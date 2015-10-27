/** Author: Ivan Zhang
 *  Company: NovusApp.com
 *  Application: Roomator
 *
 *  File: chores.java
 * -- Create and set chores for users in this group.
 */

package com.example.izhang.roomator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;


public class chores extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String account_id = "";
    private int choresCount;

    private String mParam1;
    private String mParam2;

    View view;

    ListView choresList;
    ListView doneChoresList;


    private OnFragmentInteractionListener mListener;

    public static chores newInstance(String param1, String param2) {
        chores fragment = new chores();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public chores() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            account_id = getArguments().getString("account_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chores, container, false);
        choresList = (ListView) view.findViewById(R.id.choresList);

       // final Button newChore = (Button) view.findViewById(R.id.choresButton);

        //Setup Firebase
        Firebase.setAndroidContext(getActivity());
        final Firebase myFirebaseRef = new Firebase("https://roomator.firebaseio.com/");

        final FloatingActionButton newChore = (FloatingActionButton) view.findViewById(R.id.addChore);
        newChore.setBackgroundTintList(getResources().getColorStateList(R.color.material_blue_grey_800));



        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                // Setup stats counter
                final ArrayList<String> myStringArray = new ArrayList<String>();

                final ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, myStringArray);
                choresList.setAdapter(adapter);

                final String groupID = dataSnapshot.child("account").child(account_id).child("group").getValue().toString();
                choresCount = 1;
                final Iterable<DataSnapshot> choreIter = dataSnapshot.child("group").child(groupID).child("chores").child("todo").getChildren();
                for (DataSnapshot d : choreIter) {
                    if(d.child("done").getValue() == false) {
                        myStringArray.add(d.child("title").getValue().toString());
                    }
                    choresCount++;
                }

                // Button to add a new chore to this group
                newChore.setOnClickListener(new View.OnClickListener() {
                    String m_Text;

                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Add New Chore");

                        // Set up the input
                        final EditText input = new EditText(getActivity());
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        input.setHint("Wash The Dishes");
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_Text = input.getText().toString();
                                myFirebaseRef.child("group").child(groupID).child("chores").child("todo").child(Integer.toString(choresCount)).child("title").setValue(m_Text);
                                myFirebaseRef.child("group").child(groupID).child("chores").child("todo").child(Integer.toString(choresCount)).child("createdBy").setValue(account_id);
                                myFirebaseRef.child("group").child(groupID).child("chores").child("todo").child(Integer.toString(choresCount)).child("done").setValue(false);

                                adapter.setNotifyOnChange(true);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });



                choresList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String item  = parent.getItemAtPosition(position).toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Chore: " + item);

                        // Set up the buttons
                        builder.setPositiveButton("Completed?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Iterable<DataSnapshot> choreIter2 = dataSnapshot.child("group").child(groupID).child("chores").child("todo").getChildren();
                                for (DataSnapshot d : choreIter2) {
                                    if(d.child("title").getValue() != null && d.child("title").getValue().toString().equals(item)) {
                                        Firebase childRef = d.getRef();
                                        childRef.child("done").setValue(true);
                                    }
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
