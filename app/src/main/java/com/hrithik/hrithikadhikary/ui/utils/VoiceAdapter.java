package com.hrithik.hrithikadhikary.ui.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hrithik.hrithikadhikary.R;
import com.hrithik.hrithikadhikary.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>{

    private Context mContext;
    private ArrayList<User> mPosts;

    public VoiceAdapter(Context mContext, ArrayList<User> mPosts) {
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

        User user = mPosts.get(position);
        holder.name.setText(user.getDisplayName());
        Picasso.get()
                .load(user.getPhotoUrl())
                .into(holder.profileDP);

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

}
