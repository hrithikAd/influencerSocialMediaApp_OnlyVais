package com.hrithik.hrithikadhikary;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


import static android.view.View.GONE;

public class FeedFragment extends Fragment implements BillingProcessor.IBillingHandler {
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private DatabaseReference mDatabaseReference;

    private DatabaseReference mDatabaseReference2;
    private DatabaseReference mDatabaseReference3;

    private ArrayList<Post_item> mPosts;
    private FirebaseAuth mAuth;
    private FriendlyMessage msg;
    private ViewPager viewPager;

    private CircleImageView mStory;

    private ProgressBar mProgressbar;

    private InterstitialAd mInterstitialAd;

    private int DELAY = 5000; // Delay time in milliseconds

    private Post_item post;

    private BillingProcessor bp;
    private Button mprime;
    private TransactionDetails transactionDetails = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("onlyভাই");

        View RootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mProgressbar = RootView.findViewById(R.id.progressbarFeed);

        mRecyclerView = (RecyclerView) RootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);


        //cache
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setNestedScrollingEnabled(false);
        //end
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setVisibility(GONE);

        mPosts = new ArrayList<>();

        mStory = RootView.findViewById(R.id.story);

        mprime = RootView.findViewById(R.id.primeBtn);

        //chat notification
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        //ad
        //ads
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        creatPersonalizedAd();
        //end
        //end

        //story
        mStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (post != null) {

                    if (mInterstitialAd != null && getRandomBoolean() == true) {
                        mInterstitialAd.show(getActivity());

                    } else {

                        Intent i = new Intent(getContext(), StoryActivity.class);
                        startActivity(i);

                    }

                    //border width 4
                    mStory.setBorderWidth(0);
                }
                else{
                    Toast.makeText(getContext(),"No Story Available",Toast.LENGTH_SHORT).show();
                }

            }
        });










        //prime
        bp = new BillingProcessor(getContext(), getResources().getString(R.string.subscription_license), this);
        bp.initialize();
        //end















        //Banner
        RelativeLayout banner = RootView.findViewById(R.id.banner);
        TextView bannerText = RootView.findViewById(R.id.bannerText);
        TextView bannerName = RootView.findViewById(R.id.bannerName);
        ImageView bannerDP = RootView.findViewById(R.id.bannerDP);

        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        ((BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.action_chat);

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


        //story thumbnail
        mDatabaseReference3 = FirebaseDatabase.getInstance().getReference("Story");
        mDatabaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    post = postSnap.getValue(Post_item.class);

                }

                if (post != null) {
                    Picasso.get()
                            .load(post.getpicture())
                            .into(mStory);
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
                mPosts.clear();
                for(DataSnapshot postSnap : snapshot.getChildren()){
                    Post_item post = postSnap.getValue(Post_item.class);
                    mPosts.add(post);
                }
                Collections.reverse(mPosts);

                mImageAdapter = new ImageAdapter(getContext(),mPosts);
                mRecyclerView.setAdapter(mImageAdapter);

                mStory.setVisibility(View.VISIBLE);
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


    private void creatPersonalizedAd() {

        AdRequest adRequest = new AdRequest.Builder().build();

        createIntertitialAds(adRequest);
    }
    private void createIntertitialAds(AdRequest adRequest){


        //sample - ca-app-pub-3940256099942544/1033173712
        //my story ad unit - ca-app-pub-7056810959104454/8146193841
        InterstitialAd.load(getContext(),"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;


                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {


                        // Called when fullscreen content is dismissed.
                        Intent i = new Intent(getContext(), StoryActivity.class);
                        startActivity(i);

                        creatPersonalizedAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                    }
                });


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                mInterstitialAd = null;
            }
        });
    }

    public boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

private boolean hasSubscription() {
    if (transactionDetails != null) {
        return transactionDetails.purchaseInfo != null;
    }
    else{
        return false;
    }
}

    //prime methods
    @Override
    public void onBillingInitialized() {


        //id
        transactionDetails = bp.getSubscriptionTransactionDetails(getResources().getString(R.string.product_id));

        mprime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bp.isSubscriptionUpdateSupported()) {
                    bp.subscribe(getActivity(), getResources().getString(R.string.product_id));
                }
            }
        });
        if(hasSubscription()){
        mprime.setText("PRIME");
}
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

}