package com.hrithik.hrithikadhikary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ModActivity extends AppCompatActivity {


    private ImageView userPic;
    private TextView userName;
    private TextView userStatus;

    private Button commentBtn;
    private Button chatBtn;
    private Button vcBtn;
    private Button unmuteBtn;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod);

        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the data…
        String userID = bundle.getString("userId");


        userPic = findViewById(R.id.modUserPic);
        userName = findViewById(R.id.modUserName);
        userStatus = findViewById(R.id.modUserStatus);

        commentBtn = findViewById(R.id.modCommentMute);
        chatBtn = findViewById(R.id.modChatMute);
        vcBtn = findViewById(R.id.modVoiceMute);

        unmuteBtn = findViewById(R.id.modUnmute);


        //get role of the user
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User thisUser = snapshot.getValue(User.class);

                if(thisUser!=null) {


                    userName.setText(thisUser.getDisplayName());
                    Picasso.get()
                            .load(thisUser.getPhotoUrl())
                            .fit()
                            .centerCrop()
                            .into(userPic);

                    userStatus.setText(thisUser.getRole());
                    commentBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(userID).child("role");


                            if(!thisUser.getRole().contains("commentMute")){
                                myRef.setValue(thisUser.getRole()+" commentMute");

                                Toast.makeText(getApplicationContext(),"Muted!!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Already Muted!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    chatBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(userID).child("role");


                            if(!thisUser.getRole().contains("chatMute")){
                                myRef.setValue(thisUser.getRole()+" chatMute");

                                Toast.makeText(getApplicationContext(),"Muted!!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Already Muted!!",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    vcBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(userID).child("role");



                            if(!thisUser.getRole().contains("vcMute")){
                                myRef.setValue(thisUser.getRole()+" vcMute");
                                Toast.makeText(getApplicationContext(),"Muted!!",Toast.LENGTH_SHORT).show();
                            }

                            else{
                                Toast.makeText(getApplicationContext(),"Already Muted!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    unmuteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users").child(userID).child("role");
                            myRef.setValue("member");
                            Toast.makeText(getApplicationContext(),"unMute",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ModActivity.this,"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });

        //end





    }
}