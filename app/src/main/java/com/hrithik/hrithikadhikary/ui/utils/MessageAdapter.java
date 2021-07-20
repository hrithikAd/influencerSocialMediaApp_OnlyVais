package com.hrithik.hrithikadhikary.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.FriendlyMessage;
import com.hrithik.hrithikadhikary.R;
import com.hrithik.hrithikadhikary.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<FriendlyMessage> mPosts;
    private FirebaseUser firebaseUser;
    private User currentMember;

    private DatabaseReference mDatabaseReference;
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


        //mod check
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                    currentMember = snapshot.getValue(User.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,"Firebase Error",Toast.LENGTH_SHORT).show();
            }
        });

        //end



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

        if(!postCurrent.getText().contains(firebaseUser.getDisplayName())) {
            //mentions
            String mention = null;
            String msg = postCurrent.getText();
            int size = 0;
            for (String st : msg.split(" ")) {
                if (st.startsWith("@")) {

                    mention = st;
                    size = st.length();


                    String mentionColor = "<font color='#83b3c8'>" + mention + "</font>";
                    holder.mMessage.setText(Html.fromHtml(mentionColor + postCurrent.getText().substring(size)));
                    break;
                }

            }
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



        //delete msg
        holder.mMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {



                onLongclickMethod(position);


                return true;
            }
        });
        holder.mPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                onLongclickMethod(position);


                return true;
            }
        });
        holder.mName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                onLongclickMethod(position);

                return true;
            }
        });




    }

    private void onLongclickMethod(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Delete Message");
        builder.setMessage("Are You Sure To Delete This Messgae");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMsg(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void deleteMsg(int position) {

        final String myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msgtimestmp = mPosts.get(position).getTimestamp();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("messages");
        Query query = dbref.orderByChild("timestamp").equalTo(msgtimestmp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {



                    if (dataSnapshot1.child("userId").getValue().equals(myuid) || currentMember.getRole().equalsIgnoreCase("mod") || currentMember.getRole().equalsIgnoreCase("Admin")) {
                        // any two of below can be used
                        dataSnapshot1.getRef().removeValue();
                       /* HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "This Message Was Deleted");
                        dataSnapshot1.getRef().updateChildren(hashMap);
                        Toast.makeText(context,"Message Deleted.....",Toast.LENGTH_LONG).show();
*/
                    } else {
                        Toast.makeText(mContext, "you can delete only your msg....", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        public RelativeLayout mMsgLayout;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            mMsgLayout = itemView.findViewById(R.id.msgLayout);
            mName = itemView.findViewById(R.id.nameTextView);
            mMessage = itemView.findViewById(R.id.messageTextView);
            mPhoto = itemView.findViewById(R.id.photoImageView);
        }
    }
}
