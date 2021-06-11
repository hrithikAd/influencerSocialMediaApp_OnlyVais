package com.hrithik.hrithikadhikary;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.net.MalformedURLException;
import java.net.URL;

public class JitsiActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private JitsiMeetView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jitsi);

        insertFirebase();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        userInfo.setDisplayName(currentUser.getDisplayName());


        try {
           userInfo.setAvatar(new URL(currentUser.getPhotoUrl().toString()));
       } catch (MalformedURLException e) {
           e.printStackTrace();
        }

        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setRoom("OnlyVaisGeneral")
                .setAudioMuted(true)
                .setVideoMuted(true)
                .setFeatureFlag("meeting-password.enabled", false)
                .setFeatureFlag("live-streaming.enabled", false)
                .setFeatureFlag("tile-view.enabled", true)
                .setFeatureFlag("help.enabled", false)
                .setFeatureFlag("invite.enabled", false)
                .setFeatureFlag("ios.recording.enabled", false)
                .setFeatureFlag("meeting-name.enabled", false)
                .setFeatureFlag("recording.enabled", false)
                .setFeatureFlag("chat.enabled", false)
                .build();
        JitsiMeetActivity.launch(JitsiActivity.this,options);

    }

    private void insertFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("voice");
        User voiceProfile = new User(currentUser.getUid(),currentUser.getDisplayName(),currentUser.getPhotoUrl().toString());
        myRef.child(currentUser.getUid()).setValue(voiceProfile);
    }


    @Override
    public void onBackPressed() {
      //  Toast.makeText(getApplicationContext(),"End",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}