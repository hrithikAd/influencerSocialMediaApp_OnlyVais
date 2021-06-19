package com.hrithik.hrithikadhikary;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.modules.core.PermissionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class JitsiActivity extends AppCompatActivity implements JitsiMeetActivityInterface, JitsiMeetViewListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private JitsiMeetView view;
    private String channel;

    public JitsiActivity() {
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult(
                this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        Intent intent = getIntent();
        channel = intent.getExtras().getString("channel");



        view = new JitsiMeetView(this);
        JitsiMeetConferenceOptions options = getOptions();
        view.join(options);

        setContentView(view);
        view.setListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        view.dispose();
        view = null;

        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        JitsiMeetActivityDelegate.onHostPause(this);
    }















    //permission



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {

        String[] PERMISSIONS = {
                Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA
        };

        this.requestPermissions(permissions,requestCode);

        if(!hasPermissions( PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

    }
    public boolean hasPermissions(String... permissions) {
        if ( permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



//end


















    protected JitsiMeetConferenceOptions getOptions(){
        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }



        //jitsi info
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        userInfo.setDisplayName(currentUser.getDisplayName());


        try {
            userInfo.setAvatar(new URL(currentUser.getPhotoUrl().toString()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//end


        return  new JitsiMeetConferenceOptions.Builder()
                .setRoom("onlyvais"+channel)
                .setVideoMuted(true)
                .setAudioMuted(true)
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

    }

    @Override
    public void onConferenceJoined(Map<String, Object> map) {
        insertFirebase();
    }

    @Override
    public void onConferenceTerminated(Map<String, Object> map) {
        deleteUserFirebase();
        finish();
    }

    @Override
    public void onConferenceWillJoin(Map<String, Object> map) {

    }




    private void insertFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("voice"+channel);
        User voiceProfile = new User(currentUser.getUid(),currentUser.getDisplayName(),currentUser.getPhotoUrl().toString(),"");
        myRef.child(currentUser.getUid()).setValue(voiceProfile);
    }

    private void deleteUserFirebase() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        DatabaseReference ref;

        ref = FirebaseDatabase.getInstance().getReference()
                .child("voice"+channel).child(currentUser.getUid());
        ref.removeValue();


    }
}