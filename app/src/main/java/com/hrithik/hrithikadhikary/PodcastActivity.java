package com.hrithik.hrithikadhikary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class PodcastActivity extends AppCompatActivity {
    private WebView webView;
    private VideoView videoview;
    private InterstitialAd mInterstitialAd;
    private String podcastLink;

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
            }
        });
        creatPersonalizedAd();
        //end
        //end


        Intent intent = getIntent();
        podcastLink = intent.getExtras().getString("podcastLink");

        //background video podcastBackgoundVideo
        videoview = (VideoView) findViewById(R.id.podcastBackgoundVideo);


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


        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.podcastbackground);
        videoview.setVideoURI(uri);
        videoview.start();




    }

    private void showAd() {


        if (mInterstitialAd != null) {
            mInterstitialAd.show(PodcastActivity.this);

        } else {

            progressBar.setVisibility(View.GONE);


            videoview.setVisibility(View.VISIBLE);

            webView.setVisibility(View.VISIBLE);

        }
    }


    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        videoview.start();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        creatPersonalizedAd();
        super.onBackPressed();
    }

    private void creatPersonalizedAd() {

        AdRequest adRequest = new AdRequest.Builder().build();

        createIntertitialAds(adRequest);
    }


    private void createIntertitialAds(AdRequest adRequest){


        //sample - ca-app-pub-3940256099942544/1033173712
        //my ad unit - ca-app-pub-7056810959104454/5312492420
        InterstitialAd.load(this,"ca-app-pub-7056810959104454/5312492420", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;


                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {


                        // Called when fullscreen content is dismissed.


                        progressBar.setVisibility(View.GONE);


                        videoview.setVisibility(View.VISIBLE);

                        webView.setVisibility(View.VISIBLE);

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

}