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
import com.hrithik.hrithikadhikary.ui.utils.MemberAdapter;
import com.hrithik.hrithikadhikary.ui.utils.VoiceAdapter;

import java.util.ArrayList;

public class MembersFragment extends Fragment {



    private DatabaseReference mDatabaseReference;
    private ArrayList<User> AdminList;
    private MemberAdapter memberAdapter;
    private RecyclerView recyclerViewAdmin;

    private RecyclerView recyclerViewMod;
    private ArrayList<User> ModList;

    private RecyclerView recyclerViewMember;
    private ArrayList<User> MemberList;

    private RecyclerView recyclerViewMute;
    private ArrayList<User> MuteList;


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

        //mute
        recyclerViewMute = RootView.findViewById(R.id.muteRecycler);
        recyclerViewMute.setHasFixedSize(true);
        recyclerViewMute.setLayoutManager(new LinearLayoutManager(getContext()));
        MuteList = new ArrayList<>();
        recyclerViewMute.setLayoutManager(new LinearLayoutManager(getContext()));



        //admin recycler

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                AdminList.clear();
                ModList.clear();
                MemberList.clear();
                MuteList.clear();

                for(DataSnapshot postSnap : snapshot.getChildren()){
                    User member = postSnap.getValue(User.class);
                    if(member.getRole().equalsIgnoreCase("admin")) {
                        AdminList.add(member);
                    }
                    else if(member.getRole().equalsIgnoreCase("mod")){
                        ModList.add(member);
                    }
                    else if(member.getRole().equalsIgnoreCase("member")){
                        MemberList.add(member);
                    }
                    else{
                        MuteList.add(member);
                    }
                }

                //admin
                memberAdapter = new MemberAdapter(getContext(),AdminList);
                recyclerViewAdmin.setAdapter(memberAdapter);


                //mod
                memberAdapter = new MemberAdapter(getContext(),ModList);
                recyclerViewMod.setAdapter(memberAdapter);

                //member
                memberAdapter = new MemberAdapter(getContext(),MemberList);
                recyclerViewMember.setAdapter(memberAdapter);

                //mute
                memberAdapter = new MemberAdapter(getContext(),MuteList);
                recyclerViewMute.setAdapter(memberAdapter);
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