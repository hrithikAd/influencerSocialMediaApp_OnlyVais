package com.hrithik.hrithikadhikary;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ui.main.GlobalSnackClass;
import com.hrithik.hrithikadhikary.ui.main.ImageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import static android.view.View.GONE;
import static android.view.View.ROTATION;
import static org.webrtc.ContextUtils.getApplicationContext;

public class FeedFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private DatabaseReference mDatabaseReference;

    private DatabaseReference mDatabaseReference2;
    private ArrayList<Post_item> mPosts;
    private FirebaseAuth mAuth;
    private FriendlyMessage msg;
    private ViewPager viewPager;

    private int DELAY = 5000; // Delay time in milliseconds
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mRecyclerView = (RecyclerView) RootView.findViewById(R.id.recycler_view);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
                            .placeholder(getContext().getResources().getDrawable(R.drawable.darkbackground))
                            .error(getContext().getResources().getDrawable(R.drawable.darkbackground))
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