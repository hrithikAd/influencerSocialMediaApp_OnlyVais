package com.hrithik.hrithikadhikary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

public class PodcastActivity extends AppCompatActivity implements OnUserEarnedRewardListener {
    private WebView webView;
    private ImageView backgroundView;
    private String podcastLink;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

        progressBar = findViewById(R.id.podcastLoading);

        //ad
        //ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadAd();
            }

            private void loadAd() {
                // Use the test ad unit ID to load an ad.


                //my ad id:  ca-app-pub-7056810959104454/8774564017
                //test ad id: ca-app-pub-3940256099942544/5354046379

                RewardedInterstitialAd.load(getApplicationContext(), "ca-app-pub-3940256099942544/5354046379",
                        new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                            @Override
                            public void onAdLoaded(RewardedInterstitialAd ad) {
                                rewardedInterstitialAd = ad;

                                rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    /** Called when the ad failed to show full screen content. */
                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {

                                        progressBar.setVisibility(View.GONE);


                                        backgroundView.setVisibility(View.VISIBLE);

                                        webView.setVisibility(View.VISIBLE);

                                    }

                                    /** Called when ad showed the full screen content. */
                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                    }

                                    /** Called when full screen content is dismissed. */
                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        progressBar.setVisibility(View.GONE);

                                        backgroundView.setVisibility(View.VISIBLE);

                                        webView.setVisibility(View.VISIBLE);


                                    }
                                });

                            }
                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                            }
                        });
            }
        });
        //end
        //end


        Intent intent = getIntent();
        podcastLink = intent.getExtras().getString("podcastLink");

        //background video podcastBackgoundVideo
        backgroundView = (ImageView) findViewById(R.id.podcastBackgoundVideo);


        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        //optimize

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //end

        webView.setWebViewClient(new Callback());
        webView.loadData(podcastLink, "text/html", null);

        (new Handler()).postDelayed(this::showAd, 5000);





    }



    private void showAd() {


        if (rewardedInterstitialAd != null) {
            rewardedInterstitialAd.show(/* Activity */ PodcastActivity.this,/*
    OnUserEarnedRewardListener */ PodcastActivity.this);

        } else {

            progressBar.setVisibility(View.GONE);


            backgroundView.setVisibility(View.VISIBLE);

            webView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onUserEarnedReward(@NonNull @NotNull RewardItem rewardItem) {

       progressBar.setVisibility(View.GONE);


       backgroundView.setVisibility(View.VISIBLE);

       webView.setVisibility(View.VISIBLE);
    }


    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }









    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

                        // Called when fullscreen content is dismissed.





}