package com.example.izhang.roomator;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link mainFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link mainFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;

    Firebase myFirebaseRef;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment mainFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static mainFrag newInstance() {
        mainFrag fragment = new mainFrag();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public mainFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
        myFirebaseRef = new Firebase("https://roomator.firebaseio.com/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);

        // Get android_id
        final String android_id = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final ListView mainFragList = (ListView) view.findViewById(R.id.mainFragList);

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userId;
                userId = dataSnapshot.child("didlogin").child(android_id).getValue().toString();
                if(dataSnapshot.child("account").child(userId).hasChild("group")){
                    String groupId = dataSnapshot.child("account").child(userId).child("group").getValue().toString();
                    String groupName = dataSnapshot.child("group").child(groupId).child("name").getValue().toString();

                    // Setup title
                    final TextView mainTitle = (TextView) view.findViewById(R.id.mainTitleView);
                    mainTitle.setText("Welcome To " + groupName);
                    Toast.makeText(getActivity(), "Welcome to " + groupName, Toast.LENGTH_LONG).show();

                    // Setup stats counter
                    // todo: Need to setup a elegant way to show stats. Either using a card of some sort.
                    ArrayList<String> myStringArray1 = new ArrayList<String>();
                    myStringArray1.add("Chores             20");
                    myStringArray1.add("Groceries          11");
                    myStringArray1.add("Bills               2");
                    ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, myStringArray1);
                    mainFragList.setAdapter(adapter);

                }else{
                    Toast.makeText(getActivity(), "Please join a group!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
