package com.hrithik.hrithikadhikary.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
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



        Picasso.get()
                .load(postCurrent.getPhotoUrl())
                .into(holder.mPhoto);



        //name color
        if(postCurrent.getText().contains(firebaseUser.getDisplayName())){
            holder.mMessage.setText(postCurrent.getText());
            holder.mMessage.setTextColor(Color.YELLOW);
        }
        else{
            holder.mMessage.setText(postCurrent.getText());
            holder.mMessage.setTextColor(Color.parseColor("#cccccc"));

        }
        //end

        //admin & mod

        if(postCurrent.getUserId()!=null && firebaseUser.getUid()!=null ) {

            if(postCurrent.getUserId().equalsIgnoreCase("SDe5xSNOYsPMfke8xL2gAMuOQh32")){

                holder.mName.setText(postCurrent.getName());
                holder.mName.setTextColor(Color.RED);

            }

            else if (postCurrent.getUserId().equalsIgnoreCase(firebaseUser.getUid())) {
                holder.mName.setText(postCurrent.getName());
                holder.mName.setTextColor(Color.CYAN);
            } else {
                holder.mName.setText(postCurrent.getName());
                holder.mName.setTextColor(Color.WHITE);

            }

        }
        //end





        //on click name
        holder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentName = postCurrent.getName();
                Intent intent = new Intent("custom-message");
                intent.putExtra("Chatname",currentName);
                intent.putExtra("UserId",postCurrent.getUserId());
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
