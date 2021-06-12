package com.hrithik.hrithikadhikary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.util.ArrayList;

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

    Boolean blockedUser=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_chat, container, false);





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
        mDatabaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mDatabaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User thisUser = snapshot.getValue(User.class);

                if(thisUser.getRole().contains("chatMute")){

                    Toast.makeText(getContext(),"You are temporarily muted by mods!!",Toast.LENGTH_LONG).show();
                    mSendButton.setEnabled(false);
                    blockedUser=true;
                }


                else{
                    mSendButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

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

                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString());
                mMessageDataBaseReference.push().setValue(friendlyMessage);
                // Clear input box
                mMessageEditText.setText("");


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



}