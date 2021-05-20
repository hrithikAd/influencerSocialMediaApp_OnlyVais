package com.hrithik.hrithikadhikary.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hrithik.hrithikadhikary.Post_item;
import com.hrithik.hrithikadhikary.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.View.GONE;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<Post_item> mPosts;

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

        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        Post_item postCurrent = mPosts.get(position);
        holder.timeView.setText(postCurrent.getdate());
        if(postCurrent.gettype()==1){
            //tweet

            holder.photoView.setVisibility(GONE);
        }
        else if(postCurrent.gettype()==2) {
            //photo

            if(postCurrent.gettweet().equalsIgnoreCase("")){
                holder.tweetView.setVisibility(GONE);
            }
            else{
                holder.tweetView.setText(postCurrent.gettweet());
            }
            holder.photoView.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(postCurrent.getpicture())
                    .fit()
                    .centerCrop()
                    .into(holder.photoView);
        }
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

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            like_btm = itemView.findViewById(R.id.like_heart);
            like_numberView = itemView.findViewById(R.id.like_number);
            timeView = itemView.findViewById(R.id.time);
            tweetView = itemView.findViewById(R.id.tweet);
            photoView = itemView.findViewById(R.id.photo);

        }
    }


}
