package com.hrithik.hrithikadhikary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hrithik.hrithikadhikary.ui.utils.MessageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.webrtc.ContextUtils.getApplicationContext;

public class ChatFragment extends Fragment {


    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;


    //Firebase stuff
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference mMessageDataBaseReference;
    private DatabaseReference mDatabaseReference2;

    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebsaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private DatabaseReference mDatabaseReference;

    private String mUsername;

    private LinearLayoutManager mManager;
    private ArrayList<FriendlyMessage> friendlyMessages;



    private String msgSendId=null;


    //noti
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =  "key=" + "AAAAlwfmk_8:APA91bHdvdb-0pU7wydH9pj_XPJSx9tP9xBd55VmCvcrgIXTlQfCPNhhEL-ConZjhxLRYMu9nHek5iazQbreul5eppVB84PJrna5qAhUmjUuCUJ23Ex3h_05Gjp1F_lY_R-rO-DhKeGv";
    private String contenttype = "application/json";




    Boolean blockedUser=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_chat, container, false);

        getActivity().setTitle("আড্ডা");

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));


                recyclerView = (RecyclerView) RootView.findViewById(R.id.messageRecycler);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsername = ANONYMOUS;
        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebsaseStorage = FirebaseStorage.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


        mMessageDataBaseReference = mFireBaseDatabase.getReference().child("messages");
        // Initialize references to views
        mProgressBar = (ProgressBar) RootView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) RootView.findViewById(R.id.messageRecycler);
        mMessageEditText = (EditText) RootView.findViewById(R.id.messageEditText);
        mSendButton = (Button) RootView.findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
         friendlyMessages = new ArrayList<>();

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        //current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


        //scroll msgs
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setStackFromEnd(true);
        //end






        //modCheck
       //***********bug
        if(currentUser.getUid()!=null) {

            mDatabaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            mDatabaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User thisUser = snapshot.getValue(User.class);

                    //***********bug
                    if (thisUser.getRole() != null && thisUser.getRole().contains("chatMute")) {

                        Toast.makeText(getContext(), "You are temporarily muted by mods!!", Toast.LENGTH_LONG).show();
                        mSendButton.setEnabled(false);
                        blockedUser = true;
                    } else {
                        mSendButton.setEnabled(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Firebase Error", Toast.LENGTH_LONG).show();
                }
            });


        }
        //end

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    if(blockedUser==false) {
                        mSendButton.setEnabled(true);
                    }
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});


        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click

                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), currentUser.getUid());
                mMessageDataBaseReference.push().setValue(friendlyMessage);

                //noti
                if(msgSendId!=null){

                    JSONObject notification = new JSONObject();
                    JSONObject notifcationBody = new JSONObject();
                    String topic = "/topics/"+msgSendId;
                    try {
                        notifcationBody.put("title", "New Message");
                        notifcationBody.put("message", currentUser.getDisplayName()+" mentioned you in the Chat!!")  ; //Enter your notification message
                        notification.put("to", topic);
                        notification.put("data", notifcationBody);

                    } catch (JSONException e) {

                    }

                    sendNotification(notification);
                }



                // Clear input box
                mMessageEditText.setText("");
                msgSendId=null;

            }
        });




        //read msg
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("messages");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                friendlyMessages.clear();
                for(DataSnapshot postSnap : snapshot.getChildren()){
                    FriendlyMessage msg = postSnap.getValue(FriendlyMessage.class);
                    friendlyMessages.add(msg);
                }


                messageAdapter = new MessageAdapter(getContext(),friendlyMessages);
                recyclerView.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });





        return RootView;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String ChatName = intent.getStringExtra("Chatname");

            msgSendId = intent.getStringExtra("UserId");

            Spannable WordtoSpan = new SpannableString("@"+ChatName+" ");
            WordtoSpan.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, WordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            mMessageEditText.setText(WordtoSpan);
            mMessageEditText.setSelection(mMessageEditText.getText().length());


        }
    };





    private void sendNotification(JSONObject notification) {

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,FCM_API,notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                      //  Toast.makeText(getContext(), response.toString(),Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contenttype);

                return params;
            }
        };

        queue.add(jsObjRequest);

    }




}