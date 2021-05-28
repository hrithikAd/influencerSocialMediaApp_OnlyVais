package com.hrithik.hrithikadhikary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.modules.core.PermissionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ui.main.GlobalSnackClass;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetOngoingConferenceService;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class JitsiActivity extends AppCompatActivity implements JitsiMeetActivityInterface, JitsiMeetViewListener {

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

        //snack
        if (GlobalSnackClass.snack != null ){
            GlobalSnackClass.snack.dismiss();
        }
        //end

        try {
            userInfo.setAvatar(new URL(currentUser.getPhotoUrl().toString()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setRoom("HrithikAdhikaryGeneral")
                .setVideoMuted(true)
                .setAudioMuted(false)
                .setAudioOnly(false)
                .setFeatureFlag("meeting-password.enabled", false)
                .setFeatureFlag("live-streaming.enabled", false)
                .setFeatureFlag("tile-view.enabled", true)
                .setFeatureFlag("help.enabled", false)
                .setFeatureFlag("invite.enabled", false)
                .setFeatureFlag("ios.recording.enabled", false)
                .setFeatureFlag("meeting-name.enabled", false)
                .setFeatureFlag("recording.enabled",false)
                .setFeatureFlag("audio-mute.enabled",false)
                .setUserInfo(userInfo)
                .setWelcomePageEnabled(false)
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
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

    }

    @Override
    public void onConferenceJoined(Map<String, Object> map) {
        JitsiMeetLogger.i("Conference joined: " + map);
    }

    @Override
    public void onConferenceTerminated(Map<String, Object> map) {
        JitsiMeetLogger.i("Conference terminated: " + map);
    }

    @Override
    public void onConferenceWillJoin(Map<String, Object> map) {
        JitsiMeetLogger.i("Conference will join: " + map);

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