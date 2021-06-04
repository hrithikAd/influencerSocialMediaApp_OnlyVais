package com.hrithik.onlydogs.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
import com.hrithik.onlydogs.Comment;
import com.hrithik.onlydogs.CommentsActivity;
import com.hrithik.onlydogs.Post_item;
import com.hrithik.onlydogs.R;
import com.hrithik.onlydogs.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import static android.view.View.GONE;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<Post_item> mPosts;
    private FirebaseUser firebaseUser;
    private ArrayList<Comment> commentList;


    public ImageAdapter(Context context,ArrayList<Post_item> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new ImageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post_item postCurrent = mPosts.get(position);
        holder.timeView.setText(postCurrent.getdate());

        //post name and pic

        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Users").child(postCurrent.getuser());

        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userProf = dataSnapshot.getValue(User.class);
                Picasso.get()
                        .load(userProf.getPhotoUrl())
                        .fit()
                        .centerCrop()
                        .into(holder.profilePic);

                holder.profileName.setText(userProf.getDisplayName());
                //blue tick

                if(userProf.getUserId().equals("N1YgunucCQTDtRmFRgGgFc7oGEs1")){
                    holder.blueTick.setVisibility(View.VISIBLE);
                }
                else{
                    holder.blueTick.setVisibility(GONE);
                }

                //end
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //..





            //photo
            holder.play.setVisibility(GONE);
            if(postCurrent.gettweet().equalsIgnoreCase("")){
                holder.tweetView.setVisibility(GONE);
            }
            else{
                holder.tweetView.setText(postCurrent.gettweet());
            }
            holder.photoView.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(postCurrent.getpicture())
                    .placeholder(mContext.getResources().getDrawable(R.drawable.darkbackground))
                    .error(mContext.getResources().getDrawable(R.drawable.darkbackground))
                    .into(holder.photoView);







        //feed Comment section

        commentList = new ArrayList<Comment>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postCurrent.getpost_id());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                String size = String.valueOf(commentList.size());
                holder.commentCount.setText(size);

                Collections.reverse(commentList);
                if( !commentList.isEmpty()) {

                    holder.feedCommentName.setVisibility(View.VISIBLE);
                    holder.feedCommentDp.setVisibility(View.VISIBLE);
                    holder.feedCommentComment.setVisibility(View.VISIBLE);

                    Comment lastestComment = commentList.get(0);
                    //name
                    holder.feedCommentComment.setText(lastestComment.getComment());

                    //get Name and DP
                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users").child(lastestComment.getPublisher());

                    reference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User userComment = dataSnapshot.getValue(User.class);
                            Picasso.get()
                                    .load(userComment.getPhotoUrl())
                                    .fit()
                                    .centerCrop()
                                    .into(holder.feedCommentDp);

                            holder.feedCommentName.setText(userComment.getDisplayName());
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });



                    //end name and dp
                }
                else{
                    holder.feedCommentComment.setVisibility(GONE);
                    holder.feedCommentName.setVisibility(GONE);
                    holder.feedCommentDp.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //end

        isLiked(postCurrent.getpost_id(), holder.like_btm);
        nrLikes(holder.like_numberView, postCurrent.getpost_id());


        //FeedComment On click
        holder.feedCommentComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", postCurrent.getpost_id());


                //publisher



                intent.putExtra("publisherid", firebaseUser.getUid());
                mContext.startActivity(intent);
            }
        });

        holder.feedCommentDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", postCurrent.getpost_id());


                //publisher



                intent.putExtra("publisherid", firebaseUser.getUid());
                mContext.startActivity(intent);
            }
        });

        holder.feedCommentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", postCurrent.getpost_id());


                //publisher



                intent.putExtra("publisherid", firebaseUser.getUid());
                mContext.startActivity(intent);
            }
        });



        holder.like_btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like_btm.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postCurrent.getpost_id())
                            .child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postCurrent.getpost_id())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", postCurrent.getpost_id());


                //publisher



                intent.putExtra("publisherid", firebaseUser.getUid());
                mContext.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView tweetView;
        public ImageView photoView;
        public ImageView like_btm;
        public TextView like_numberView;
        public TextView timeView;
        public ImageView comment;
        public ImageView feedCommentDp;
        public TextView feedCommentName;
        public TextView feedCommentComment;
        public TextView commentCount;
        public TemplateView templateView;
        public ImageView play;
        public ImageView profilePic;
        public TextView profileName;
        public ImageView blueTick;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            like_btm = itemView.findViewById(R.id.like_heart);
            like_numberView = itemView.findViewById(R.id.like_number);
            timeView = itemView.findViewById(R.id.time);
            tweetView = itemView.findViewById(R.id.tweet);
            photoView = itemView.findViewById(R.id.photo);
            comment = itemView.findViewById(R.id.commentView);
            feedCommentDp = itemView.findViewById(R.id.feedcomment_image_profile);
            feedCommentComment = itemView.findViewById(R.id.feedcomment_comment);
            feedCommentName = itemView.findViewById(R.id.feedcomment_username);
            commentCount = itemView.findViewById(R.id.commentCount);
            templateView = itemView.findViewById(R.id.my_template);
            play = itemView.findViewById(R.id.play);
            profileName=itemView.findViewById(R.id.profileName);
            profilePic=itemView.findViewById(R.id.profile_image);
            blueTick=itemView.findViewById(R.id.blueTick);


        }
    }

    private void nrLikes(final TextView likes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void isLiked(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                    imageView.setTag("liked");
                } else{
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
