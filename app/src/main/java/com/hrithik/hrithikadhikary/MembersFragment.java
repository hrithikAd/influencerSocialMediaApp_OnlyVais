package com.hrithik.hrithikadhikary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ui.utils.VoiceAdapter;

import java.util.ArrayList;

public class MembersFragment extends Fragment {



    private DatabaseReference mDatabaseReference;
    private ArrayList<User> AdminList;
    private VoiceAdapter voiceAdapter;
    private RecyclerView recyclerViewAdmin;

    private RecyclerView recyclerViewMod;
    private ArrayList<User> ModList;

    private RecyclerView recyclerViewMember;
    private ArrayList<User> MemberList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_members, container, false);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        //admin
        recyclerViewAdmin = RootView.findViewById(R.id.adminRecycler);
        recyclerViewAdmin.setHasFixedSize(true);
        recyclerViewAdmin.setLayoutManager(new LinearLayoutManager(getContext()));
        AdminList = new ArrayList<>();
        recyclerViewAdmin.setLayoutManager(new LinearLayoutManager(getContext()));

        //mod
        recyclerViewMod = RootView.findViewById(R.id.modRecycler);
        recyclerViewMod.setHasFixedSize(true);
        recyclerViewMod.setLayoutManager(new LinearLayoutManager(getContext()));
        ModList = new ArrayList<>();
        recyclerViewMod.setLayoutManager(new LinearLayoutManager(getContext()));

        //member
        recyclerViewMember = RootView.findViewById(R.id.memberRecycler);
        recyclerViewMember.setHasFixedSize(true);
        recyclerViewMember.setLayoutManager(new LinearLayoutManager(getContext()));
        MemberList = new ArrayList<>();
        recyclerViewMember.setLayoutManager(new LinearLayoutManager(getContext()));

        //admin recycler

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                AdminList.clear();
                for(DataSnapshot postSnap : snapshot.getChildren()){
                    User member = postSnap.getValue(User.class);
                    if(member.getRole().equalsIgnoreCase("admin")) {
                        AdminList.add(member);
                    }
                    else if(member.getRole().equalsIgnoreCase("mod")){
                        ModList.add(member);
                    }
                    else{
                        MemberList.add(member);
                    }
                }

                //admin
                voiceAdapter = new VoiceAdapter(getContext(),AdminList);
                recyclerViewAdmin.setAdapter(voiceAdapter);


                //mod
                voiceAdapter = new VoiceAdapter(getContext(),ModList);
                recyclerViewMod.setAdapter(voiceAdapter);

                //member
                voiceAdapter = new VoiceAdapter(getContext(),MemberList);
                recyclerViewMember.setAdapter(voiceAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

        //end




        return RootView;
    }









    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}