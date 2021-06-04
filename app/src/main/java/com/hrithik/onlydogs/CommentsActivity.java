package com.hrithik.onlydogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.onlydogs.ui.main.CommentAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private Post_item commentPost;
    private String Noticomment;
    EditText addcomment;
    ImageView image_profile,fullImage;
    TextView post,fullTweet;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;


    //noti
    private FirebaseUser Currentuser;
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =  "key=" + "AAAALfTg9Fk:APA91bEzdRcKp01J1vB8SmgvWZ2RXjsXvi7gMPfjiR55NKN7-0-oxi4NLcZx30OAM0DON3ksx26ikDC-QpMIv5ULTsT6sWnd-zN42KnM2jIKwDSDraUl4QuJsxTZUTETKIlXDmpq_f4Q";
    private String contenttype = "application/json";
    //end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Currentuser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        commentList = new ArrayList<>();


        commentAdapter = new CommentAdapter(this, commentList, postid);
        recyclerView.setAdapter(commentAdapter);


        post = findViewById(R.id.post);
        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    Noticomment=addcomment.getText().toString();
                    addComment(Noticomment);

                }
            }
        });

        getImage();
        readComments();
      //  getFullDisplayImage();

    }




    private void addComment(String noticomment){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);

        reference.child(commentid).setValue(hashMap);



        //noti
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("posts").child(postid);
        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                commentPost = dataSnapshot.getValue(Post_item.class);
                DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("Users").child(commentPost.getuser());
                reference4.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User commentUser = dataSnapshot.getValue(User.class);
                        noti(commentPost.getuser(), commentUser.getDisplayName(), noticomment);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

//end

        addcomment.setText("");



    }



    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(image_profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //notification
    private void noti(String userID,String UserName, String comment) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        String topic = "/topics/".concat(userID);
        try {
            notifcationBody.put("title", UserName.concat(" commented on your post"));
            notifcationBody.put("message", comment)  ; //Enter your notification message
            notification.put("to", topic);
            notification.put("data", notifcationBody);

        } catch (JSONException e) {

        }

        sendNotification(notification);
    }
    private void sendNotification(JSONObject notification) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,FCM_API,notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Toast.makeText(getApplicationContext(), response.toString(),Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contenttype);

                return params;
            }
        };

        queue.add(jsObjRequest);

    }
    //end
}