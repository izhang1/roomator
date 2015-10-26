package com.example.izhang.roomator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.Toast;

import com.example.izhang.roomator.venmo.VenmoLibrary;
import com.example.izhang.roomator.venmo.venmoInfo;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class billing extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String account_id = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView billList;
    View view;
    private String venmo_appID = "";
    private String venmo_secret = "";

    public int REQUEST_CODE_VENMO_APP_SWITCH;

    private OnFragmentInteractionListener mListener;

    public static billing newInstance(String param1, String param2) {
        billing fragment = new billing();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public billing() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            account_id = getArguments().getString("account_id");
        }

        venmoInfo vobj = new venmoInfo();
        venmo_appID = vobj.getVenmoAppId();
        venmo_secret = vobj.getVenmoSecret();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_billing, container, false);
        billList = (ListView) view.findViewById(R.id.billList);


        //Setup Firebase
        Firebase.setAndroidContext(getActivity());
        final Firebase myFirebaseRef = new Firebase("https://roomator.firebaseio.com/");

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create list of bills for user

                final String groupID = dataSnapshot.child("account").child(account_id).child("group").getValue().toString();
                final ArrayList<bills> billings = new ArrayList<bills>();

                int choresCount = 1;
                final Iterable<DataSnapshot> billIter = dataSnapshot.child("group").child(groupID).child("bills").getChildren();
                for (DataSnapshot d : billIter) {
                    String ownerID = d.child("owner").getValue().toString();
                    String cost = d.child("amount").getValue().toString();
                    int numCost = Integer.getInteger(cost);
                    String description = d.child("description").getValue().toString();
                    bills temp = new bills(numCost, description, ownerID);
                    billings.add(temp);
                    choresCount++;
                }

                final ArrayAdapter adapter = new ArrayAdapter<bills>(getActivity(),
                        android.R.layout.simple_list_item_1, billings);
                billList.setAdapter(adapter);

                billList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AlertDialog.Builder payBuilder = new AlertDialog.Builder(getActivity());

                        payBuilder.setTitle("Confirm to pay?");
                        payBuilder.setMessage(billings.get(position).getDesc());

                        payBuilder.setPositiveButton("Confirm Payment", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(VenmoLibrary.isVenmoInstalled(getActivity())){
                                    Intent venmoIntent = VenmoLibrary.openVenmoPayment(venmo_appID, "Roomator", "Theresa Nguyen", "2.00", "1", "pay");
                                    startActivityForResult(venmoIntent, REQUEST_CODE_VENMO_APP_SWITCH);
                                }
                                Toast.makeText(getActivity(), "You have just paid for this!", Toast.LENGTH_LONG);
                            }
                        });
                        payBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        payBuilder.show();

                    }
                });

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.material_blue_grey_800));

        // Prompts user for input to record the cost per person and description
        fab.setOnClickListener(new View.OnClickListener() {
            String description;
            String cost;

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add New Bill");

                LinearLayout layout = new LinearLayout(view.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                // Set up the input for description
                final EditText descriptionInput = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                descriptionInput.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                descriptionInput.setHint("Cable Internet");
                layout.addView(descriptionInput);

                // Set up the input for per person billing
                final EditText costInput = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                costInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                costInput.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                costInput.setHint("15.00");
                layout.addView(costInput);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        description = descriptionInput.getText().toString();
                        cost = costInput.getText().toString();
                        Toast.makeText(getActivity(), "Description: " + description + "  CostPerPerson: " + cost, Toast.LENGTH_LONG).show();
                        //adapter.setNotifyOnChange(true);
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



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE_VENMO_APP_SWITCH ){
            if(resultCode == getActivity().RESULT_OK) {
                String signedrequest = data.getStringExtra("signedrequest");
                if(signedrequest != null) {
                    VenmoLibrary.VenmoResponse response = (new VenmoLibrary()).validateVenmoPaymentResponse(signedrequest, venmo_secret);
                    if(response.getSuccess().equals("1")) {
                        //Payment successful.  Use data from response object to display a success message
                        String note = response.getNote();
                        String amount = response.getAmount();
                    }
                }
                else {
                    String error_message = data.getStringExtra("error_message");
                    //An error ocurred.  Make sure to display the error_message to the user
                }
            }
            else if(resultCode == getActivity().RESULT_CANCELED) {
                //The user cancelled the payment
            }
        }
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
