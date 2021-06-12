package com.hrithik.hrithikadhikary.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hrithik.hrithikadhikary.FriendlyMessage;
import com.hrithik.hrithikadhikary.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
                .placeholder(mContext.getResources().getDrawable(R.drawable.darkbackground))
                .error(mContext.getResources().getDrawable(R.drawable.darkbackground))
                .into(holder.mPhoto);

        //name color
        //end



        //on click name
        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentName = postCurrent.getName();
                //Toast.makeText(mContext,currentName,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("custom-message");
                //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                intent.putExtra("Chatname",currentName);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });


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
