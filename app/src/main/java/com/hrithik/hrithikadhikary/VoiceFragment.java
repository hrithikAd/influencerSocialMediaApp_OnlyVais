package com.hrithik.hrithikadhikary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ui.utils.VoiceAdapter;

import java.util.ArrayList;


public class VoiceFragment extends Fragment  {


private FirebaseAuth mAuth;
private DatabaseReference mDatabaseReference;
private DatabaseReference mDatabaseReferenceMod;


private ArrayList<User> voiceMember;
private VoiceAdapter voiceAdapter;
private RecyclerView recyclerView;
private ImageView voiceLogoView;
private Button joinButton;


    private ArrayList<User> voiceMember2;
    private VoiceAdapter voiceAdapter2;
    private RecyclerView recyclerView2;
    private ImageView voiceLogoView2;
    private Button joinButton2;
    private DatabaseReference mDatabaseReference2;

    private ArrayList<User> voiceMember3;
    private VoiceAdapter voiceAdapter3;
    private RecyclerView recyclerView3;
    private ImageView voiceLogoView3;
    private Button joinButton3;
    private DatabaseReference mDatabaseReference3;


    private DatabaseReference mDatabaseReferenceSquad;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_voice, container, false);

        getActivity().setTitle("গল্প");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        voiceLogoView = RootView.findViewById(R.id.voiceLogo);
        recyclerView = RootView.findViewById(R.id.voiceRecycler);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("voice1");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        voiceMember = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        joinButton = RootView.findViewById(R.id.join);



        //2
        voiceLogoView2 = RootView.findViewById(R.id.voiceLogo2);
        recyclerView2 = RootView.findViewById(R.id.voiceRecycler2);
        mDatabaseReference2 = FirebaseDatabase.getInstance().getReference("voice2");
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        voiceMember2 = new ArrayList<>();
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        joinButton2 = RootView.findViewById(R.id.join2);


        //3
        voiceLogoView3 = RootView.findViewById(R.id.voiceLogo3);
        recyclerView3 = RootView.findViewById(R.id.voiceRecycler3);
        mDatabaseReference3 = FirebaseDatabase.getInstance().getReference("voice3");
        recyclerView3.setHasFixedSize(true);
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
        voiceMember3 = new ArrayList<>();
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));
        joinButton3 = RootView.findViewById(R.id.join3);



        //permission
        String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA
        };

        ActivityCompat.requestPermissions(getActivity(),PERMISSIONS,1);






        //read voice member 1
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                voiceMember.clear();
                for(DataSnapshot postSnap : snapshot.getChildren()){
                    User member = postSnap.getValue(User.class);
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

        //read voice member 2
        mDatabaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                voiceMember2.clear();
                for(DataSnapshot postSnap : snapshot.getChildren()){
                    User member = postSnap.getValue(User.class);
                    voiceMember2.add(member);
                }

                voiceAdapter2 = new VoiceAdapter(getContext(),voiceMember2);
                recyclerView2.setAdapter(voiceAdapter2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

        //end


//read voice member 3
        mDatabaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                voiceMember3.clear();
                for(DataSnapshot postSnap : snapshot.getChildren()){
                    User member = postSnap.getValue(User.class);
                    voiceMember3.add(member);
                }

                voiceAdapter3 = new VoiceAdapter(getContext(),voiceMember3);
                recyclerView3.setAdapter(voiceAdapter3);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

        //end



        //mod check
        mDatabaseReferenceMod = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mDatabaseReferenceMod.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User thisUser = snapshot.getValue(User.class);

                if(thisUser.getRole().contains("vcMute")){

                    Toast.makeText(getContext(),"You are temporarily muted by mods!!",Toast.LENGTH_LONG).show();
                    joinButton.setEnabled(false);
                    joinButton2.setEnabled(false);
                    joinButton3.setEnabled(false);
                }


                else{

                    joinButton.setEnabled(true);
                    joinButton2.setEnabled(true);
                }


                //mod only channel
                if(thisUser.getRole().contains("mod") || thisUser.getRole().contains("admin")){

                    joinButton3.setEnabled(true);
                }
                else{

                    joinButton3.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

        //end


        //squad check!!
        mDatabaseReferenceSquad = FirebaseDatabase.getInstance().getReference("voice2");
        mDatabaseReferenceSquad.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count = ((int) snapshot.getChildrenCount());

                if(count>=2){
                    joinButton2.setEnabled(false);
                }
                else{
                    joinButton2.setEnabled(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });
//end




        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Intent intent = new Intent(getActivity(), JitsiActivity.class);
                intent.putExtra("channel", "1");
                startActivity(intent);
            }
        });

        joinButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JitsiActivity.class);
                intent.putExtra("channel", "2");
                startActivity(intent);
            }
        });

        joinButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JitsiActivity.class);
                intent.putExtra("channel", "3");
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

       // deleteUserFirebase();
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