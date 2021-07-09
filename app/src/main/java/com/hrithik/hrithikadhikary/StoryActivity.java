package com.hrithik.hrithikadhikary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hrithik.hrithikadhikary.ui.utils.ImageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import jp.shts.android.storiesprogressview.StoriesProgressView;

import static android.view.View.GONE;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {


    private ArrayList<Post_item> mPosts;

    private DatabaseReference mDatabaseReference;
    // on below line we are creating a int array
    // in which we are storing all our image ids.


    // on below line we are creating variable for
    // our press time and time limit to display a story.
    long pressTime = 0L;
    long limit = 500L;

    // on below line we are creating variables for
    // our progress bar view and image view .
    private StoriesProgressView storiesProgressView;
    private ImageView image;

    // on below line we are creating a counter
    // for keeping count of our stories.
    private int counter = 0;

    // on below line we are creating a new method for adding touch listener
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // inside on touch method we are
            // getting action on below line.
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    // on action down when we press our screen
                    // the story will pause for specific time.
                    pressTime = System.currentTimeMillis();

                    // on below line we are pausing our indicator.
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:

                    // in action up case when user do not touches
                    // screen this method will skip to next image.
                    long now = System.currentTimeMillis();

                    // on below line we are resuming our progress bar for status.
                    storiesProgressView.resume();

                    // on below line we are returning if the limit < now - presstime
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //get the images
        mPosts = new ArrayList<>();

        ProgressDialog Dialog = new ProgressDialog(StoryActivity.this);
        Dialog.setMessage("Loading...Jio naki?");
        Dialog.show();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Story");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPosts.clear();
                for(DataSnapshot postSnap : snapshot.getChildren()){
                    Post_item post = postSnap.getValue(Post_item.class);
                    mPosts.add(post);
                }
                Collections.reverse(mPosts);
                Dialog.hide();
                showStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StoryActivity.this,"Firebase Error",Toast.LENGTH_LONG).show();
            }
        });
        //end

     //   Post_item post = new Post_item("-Me23z0kirajUn3VqN_J",0,"Hi girls!!!",null,"https://firebasestorage.googleapis.com/v0/b/onlyvais-391df.appspot.com/o/Story%2F1625696757032.jpg?alt=media&token=2b079424-30a7-48f9-a009-d09eb42f03d2","08-Jul-2021",2);
     //   mPosts.add(post);



    }

    private void showStory() {

        // inside in create method below line is use to make a full screen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story);

        // on below line we are initializing our variables.
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);

        // on below line we are setting the total count for our stories.
        storiesProgressView.setStoriesCount(mPosts.size());

        // on below line we are setting story duration for each story.
        storiesProgressView.setStoryDuration(3000L);

        // on below line we are calling a method for set
        // on story listener and passing context to it.
        storiesProgressView.setStoriesListener(this);

        // below line is use to start stories progress bar.
        storiesProgressView.startStories(counter);

        // initializing our image view.
        image = (ImageView) findViewById(R.id.image);

        // on below line we are setting image to our image view.
        Picasso.get()
                .load(mPosts.get(counter).getpicture())
                .into(image);

        // below is the view for going to the previous story.
        // initializing our previous view.
        View reverse = findViewById(R.id.reverse);

        // adding on click listener for our reverse view.
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inside on click we are
                // reversing our progress view.
                storiesProgressView.reverse();
            }
        });

        // on below line we are calling a set on touch
        // listener method to move towards previous image.
        reverse.setOnTouchListener(onTouchListener);

        // on below line we are initializing
        // view to skip a specific story.
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inside on click we are
                // skipping the story progress view.
                storiesProgressView.skip();
            }
        });
        // on below line we are calling a set on touch
        // listener method to move to next story.
        skip.setOnTouchListener(onTouchListener);


    }

    @Override
    public void onNext() {
        // this method is called when we move
        // to next progress view of story.
        Picasso.get()
                .load(mPosts.get(++counter).getpicture())
                .into(image);
    }

    @Override
    public void onPrev() {

        // this method id called when we move to previous story.
        // on below line we are decreasing our counter
        if ((counter - 1) < 0) return;

        // on below line we are setting image to image view
        Picasso.get()
                .load(mPosts.get(--counter).getpicture())
                .into(image);
    }

    @Override
    public void onComplete() {
        // when the stories are completed this method is called.
        // in this method we are moving back to initial main activity.
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // in on destroy method we are destroying
        // our stories progress view.
        storiesProgressView.destroy();
        super.onDestroy();
    }
}