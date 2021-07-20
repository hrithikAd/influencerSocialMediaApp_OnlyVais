package com.hrithik.hrithikadhikary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ui.utils.MemberAdapter;
import com.hrithik.hrithikadhikary.ui.utils.VoiceAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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


    private ProgressBar progressBar;
    private TextView t1,t2,t3,t4;

    private FirebaseAuth mAuth;

    private User currentMember;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_members, container, false);

        getActivity().setTitle("লোকজোন");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        progressBar = RootView.findViewById(R.id.memberProgressBar);

        t1 = RootView.findViewById(R.id.adminText);

        t2 = RootView.findViewById(R.id.modText);

        t3 = RootView.findViewById(R.id.memberText);



        t4 = RootView.findViewById(R.id.muteText);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");

        //admin
        recyclerViewAdmin = RootView.findViewById(R.id.adminRecycler);
        recyclerViewAdmin.setHasFixedSize(true);
        recyclerViewAdmin.setItemViewCacheSize(20);
        recyclerViewAdmin.setDrawingCacheEnabled(true);
        recyclerViewAdmin.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        recyclerViewAdmin.setLayoutManager(new LinearLayoutManager(getContext()));
        AdminList = new ArrayList<>();
        recyclerViewAdmin.setLayoutManager(new LinearLayoutManager(getContext()));

        //mod
        recyclerViewMod = RootView.findViewById(R.id.modRecycler);
        recyclerViewMod.setHasFixedSize(true);
        recyclerViewMod.setItemViewCacheSize(20);
        recyclerViewMod.setDrawingCacheEnabled(true);
        recyclerViewMod.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewMod.setLayoutManager(new LinearLayoutManager(getContext()));
        ModList = new ArrayList<>();
        recyclerViewMod.setLayoutManager(new LinearLayoutManager(getContext()));

        //member
        recyclerViewMember = RootView.findViewById(R.id.memberRecycler);
        recyclerViewMember.setHasFixedSize(true);
        recyclerViewMember.setItemViewCacheSize(20);
        recyclerViewMember.setDrawingCacheEnabled(true);
        recyclerViewMember.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerViewMember.setLayoutManager(new LinearLayoutManager(getContext()));
        MemberList = new ArrayList<>();
        recyclerViewMember.setLayoutManager(new LinearLayoutManager(getContext()));


        //mute
        recyclerViewMute = RootView.findViewById(R.id.muteRecycler);
        recyclerViewMute.setHasFixedSize(true);
        recyclerViewMute.setItemViewCacheSize(20);
        recyclerViewMute.setDrawingCacheEnabled(true);
        recyclerViewMute.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
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

                    if(member.getUserId().equalsIgnoreCase(currentUser.getUid())){
                        currentMember = member;
                    }
                }


                //admin
                memberAdapter = new MemberAdapter(getContext(),AdminList);
                recyclerViewAdmin.setAdapter(memberAdapter);


                //mod
                memberAdapter = new MemberAdapter(getContext(),ModList);
                recyclerViewMod.setAdapter(memberAdapter);





                if(currentMember.getRole().equalsIgnoreCase("Mod") || currentMember.getRole().equalsIgnoreCase("admin")) {

                    //member
                    if (MemberList.size() > 0) {
                        Collections.sort(MemberList, new Comparator<User>() {
                            @Override
                            public int compare(final User object1, final User object2) {
                                return object1.getDisplayName().compareTo(object2.getDisplayName());
                            }
                        });
                    }

                    memberAdapter = new MemberAdapter(getContext(),MemberList);
                    recyclerViewMember.setAdapter(memberAdapter);
                    t3.setVisibility(View.VISIBLE);

                    //mute
                    memberAdapter = new MemberAdapter(getContext(),MuteList);
                    recyclerViewMute.setAdapter(memberAdapter);
                    t4.setVisibility(View.VISIBLE);
                }
                else{
                    recyclerViewMember.setVisibility(View.GONE);
                    t3.setVisibility(View.GONE);
                    recyclerViewMute.setVisibility(View.GONE);
                    t4.setVisibility(View.GONE);
                }


                progressBar.setVisibility(View.GONE);
                t1.setVisibility(View.VISIBLE);
                t2.setVisibility(View.VISIBLE);

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