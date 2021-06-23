package com.hrithik.hrithikadhikary;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ui.utils.ImageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import static android.view.View.GONE;

public class FeedFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private DatabaseReference mDatabaseReference;

    private DatabaseReference mDatabaseReference2;
    private ArrayList<Post_item> mPosts;
    private FirebaseAuth mAuth;
    private FriendlyMessage msg;
    private ViewPager viewPager;

    private ProgressBar mProgressbar;

    private int DELAY = 5000; // Delay time in milliseconds

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mProgressbar = RootView.findViewById(R.id.progressbarFeed);

        mRecyclerView = (RecyclerView) RootView.findViewById(R.id.recycler_view);
        mRecyclerView.hasFixedSize();


        //cache
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //end
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setVisibility(GONE);

        mPosts = new ArrayList<>();


        //chat notification
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        //Banner
        RelativeLayout banner = RootView.findViewById(R.id.banner);
        TextView bannerText = RootView.findViewById(R.id.bannerText);
        TextView bannerName = RootView.findViewById(R.id.bannerName);
        ImageView bannerDP = RootView.findViewById(R.id.bannerDP);

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewPager = (ViewPager) getActivity().findViewById(
                        R.id.view_pager);
                viewPager.setCurrentItem(1);

            }
        });

        mDatabaseReference2 = FirebaseDatabase.getInstance().getReference("messages");
        mDatabaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot postSnap2 : snapshot.getChildren()) {
                    msg = postSnap2.getValue(FriendlyMessage.class);
                }

                if (msg!=null && !msg.getName().equalsIgnoreCase(currentUser.getDisplayName())) {



                    //SNACK VIEW!!!
                    banner.setVisibility(View.VISIBLE);
                    bannerText.setText(msg.getText());
                    bannerName.setText(msg.getName());

                    Picasso.get()
                            .load(msg.getPhotoUrl())
                            .into(bannerDP);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           banner.setVisibility(GONE);
                        }
                    }, DELAY);

                }




           }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });




        //end






        mDatabaseReference = FirebaseDatabase.getInstance().getReference("posts");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot postSnap : snapshot.getChildren()){
                    Post_item post = postSnap.getValue(Post_item.class);
                    mPosts.add(post);
                }
                Collections.reverse(mPosts);

                mImageAdapter = new ImageAdapter(getContext(),mPosts);

                mRecyclerView.setAdapter(mImageAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressbar.setVisibility(GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

        // Inflate the layout for this fragment

        return RootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}