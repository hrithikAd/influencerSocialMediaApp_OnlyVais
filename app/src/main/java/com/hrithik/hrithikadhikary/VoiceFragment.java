package com.hrithik.hrithikadhikary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ui.main.MessageAdapter;
import com.hrithik.hrithikadhikary.ui.main.VoiceAdapter;

import java.util.ArrayList;


public class VoiceFragment extends Fragment  {

private RelativeLayout relativeLayout;
private FirebaseAuth mAuth;
private DatabaseReference mDatabaseReference;
private ArrayList<FriendlyMessage> voiceMember;
private VoiceAdapter voiceAdapter;
private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_voice, container, false);

        relativeLayout = RootView.findViewById(R.id.voiceLayout);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        recyclerView = RootView.findViewById(R.id.voiceRecycler);

        voiceMember = new ArrayList<FriendlyMessage>();
        //read voice member

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("voice");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot postSnap : snapshot.getChildren()){
                    FriendlyMessage member = postSnap.getValue(FriendlyMessage.class);
                    voiceMember.add(member);
                }

                voiceAdapter = new VoiceAdapter(getContext(),voiceMember);
                recyclerView.setAdapter(voiceAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

        //end


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent = new Intent(getActivity(), JitsiActivity.class);
                startActivity(intent);
            }
        });



        return RootView;




    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {

        deleteUserFirebase();
        super.onResume();
    }



    private void deleteUserFirebase() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        DatabaseReference ref;

        ref = FirebaseDatabase.getInstance().getReference()
                .child("voice").child(currentUser.getUid());
        ref.removeValue();


    }
}