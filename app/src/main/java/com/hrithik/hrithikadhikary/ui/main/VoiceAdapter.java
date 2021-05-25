package com.hrithik.hrithikadhikary.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hrithik.hrithikadhikary.FriendlyMessage;
import com.hrithik.hrithikadhikary.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>{

    private Context mContext;
    private ArrayList<FriendlyMessage> mPosts;
    private FirebaseUser firebaseUser;
    private LayoutInflater mInflater;

    public VoiceAdapter(Context context,ArrayList<FriendlyMessage> posts){
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public VoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.item_message,parent,false);
        return new VoiceViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull VoiceViewHolder holder, int position) {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FriendlyMessage postCurrent = mPosts.get(position);

        holder.mName.setText(postCurrent.getName());
        holder.mMessage.setText(postCurrent.getText());
        Picasso.get()
                .load(postCurrent.getPhotoUrl())
                .placeholder(mContext.getResources().getDrawable(R.drawable.darkbackground))
                .error(mContext.getResources().getDrawable(R.drawable.darkbackground))
                .into(holder.mPhoto);

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class VoiceViewHolder extends RecyclerView.ViewHolder {

        public TextView mName;
        public ImageView mPhoto;
        public TextView mMessage;


        public VoiceViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.nameTextView);
            mMessage = itemView.findViewById(R.id.messageTextView);
            mPhoto = itemView.findViewById(R.id.photoImageView);
        }
    }

}
