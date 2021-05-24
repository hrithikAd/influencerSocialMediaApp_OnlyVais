package com.hrithik.hrithikadhikary.ui.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.Comment;
import com.hrithik.hrithikadhikary.CommentsActivity;
import com.hrithik.hrithikadhikary.FriendlyMessage;
import com.hrithik.hrithikadhikary.Post_item;
import com.hrithik.hrithikadhikary.R;
import com.hrithik.hrithikadhikary.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<FriendlyMessage> mPosts;
    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context,ArrayList<FriendlyMessage> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_message,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FriendlyMessage postCurrent = mPosts.get(position);

        holder.mName.setText(postCurrent.getName());
        holder.mMessage.setText(postCurrent.getText());
        Picasso.get()
                .load(postCurrent.getPhotoUrl())
                .into(holder.mPhoto);


    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView mName;
        public ImageView mPhoto;
        public TextView mMessage;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.nameTextView);
            mMessage = itemView.findViewById(R.id.messageTextView);
            mPhoto = itemView.findViewById(R.id.photoImageView);

        }
    }
}
