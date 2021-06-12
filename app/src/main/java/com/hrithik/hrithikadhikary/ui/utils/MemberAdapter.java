package com.hrithik.hrithikadhikary.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ModActivity;
import com.hrithik.hrithikadhikary.Post_item;
import com.hrithik.hrithikadhikary.R;
import com.hrithik.hrithikadhikary.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.VoiceViewHolder>{

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private Context mContext;
    private ArrayList<User> mPosts;

    public MemberAdapter(Context mContext, ArrayList<User> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public VoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.voice_item,parent,false);

        return new VoiceViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull VoiceViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        User user = mPosts.get(position);
        holder.name.setText(user.getDisplayName());
        Picasso.get()
                .load(user.getPhotoUrl())
                .into(holder.profileDP);


        //get role of the user
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User thisUser = snapshot.getValue(User.class);

                holder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if((thisUser.getRole().equalsIgnoreCase("mod") || thisUser.getRole().equalsIgnoreCase("admin") && !user.getRole().equalsIgnoreCase("admin") && !user.getRole().equalsIgnoreCase("mod"))){
                            modActivity(user);
                        }

                    }
                });


                holder.profileDP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if((thisUser.getRole().equalsIgnoreCase("mod") || thisUser.getRole().equalsIgnoreCase("admin") && !user.getRole().equalsIgnoreCase("admin") && !user.getRole().equalsIgnoreCase("mod"))){
                            modActivity(user);
                        }

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

        //end




    }



    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public static class VoiceViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView profileDP;

        public VoiceViewHolder(@NonNull View itemView) {

            super(itemView);
            name=itemView.findViewById(R.id.nameTextViewVoice);
            profileDP = itemView.findViewById(R.id.photoImageViewVoice);
        }
    }


    private void modActivity(User user) {
        Intent i = new Intent(mContext, ModActivity.class);

        //Create the bundle
        Bundle bundle = new Bundle();

        //Add your data to bundle
        bundle.putString("userId",user.getUserId());

        //Add the bundle to the intent
        i.putExtras(bundle);

        //Fire that second activity
        mContext.startActivity(i);
    }
}
