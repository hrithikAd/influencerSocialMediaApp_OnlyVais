package com.hrithik.onlydogs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    private Button choose_btn;
    private Button submit_btn;
    private EditText caption;
    private ImageView imageView;
    public Uri imageURI;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String imageUrl;
    private FirebaseUser Currentuser;
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey =  "key=" + "AAAALfTg9Fk:APA91bEzdRcKp01J1vB8SmgvWZ2RXjsXvi7gMPfjiR55NKN7-0-oxi4NLcZx30OAM0DON3ksx26ikDC-QpMIv5ULTsT6sWnd-zN42KnM2jIKwDSDraUl4QuJsxTZUTETKIlXDmpq_f4Q";
    private String contenttype = "application/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        submit_btn = findViewById(R.id.img_submit);
        caption = findViewById(R.id.caption);
        imageView = findViewById(R.id.displayImage);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Currentuser = FirebaseAuth.getInstance().getCurrentUser();




        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                chooseImage();
            }
        });


    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageURI = data.getData();
            imageView.setImageURI(imageURI);

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    checkImage(caption.getText().toString());

                }
            });



        }
    }

    private void uploadImage(String caption) throws IOException {

        String mcaption = caption;
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageURI != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageURI));

            //img size reduce
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            //end


            StorageTask uploadtask = filePath.putBytes(data);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();

                    imageUrl = downloadUri.toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
                    String postId = ref.push().getKey();

                    //date
                    Date c = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(c);

                    Post_item post = new Post_item(postId,Currentuser.getUid(),mcaption,null,imageUrl,formattedDate,0);


                    ref.child(postId).setValue(post);


                    noti();


                    pd.dismiss();
                    startActivity(new Intent(UploadActivity.this , MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show();
        }

    }

    private void noti() {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        String topic = "/topics/HrithikAdhikary";
        try {
            notifcationBody.put("title", "A CUTE DOG is waiting for you!!");
            notifcationBody.put("message", Currentuser.getDisplayName()+" added a new pic")  ; //Enter your notification message
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

    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    private void checkImage(String cap){

        String Caption;
        Caption = cap;

        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(getApplicationContext(), imageURI);

            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getOnDeviceImageLabeler();


            labeler.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            // Task completed successfully

                            Boolean dogPic = false;

                            for (FirebaseVisionImageLabel label: labels) {
                                String text = label.getText();
                                String entityId = label.getEntityId();
                                float confidence = label.getConfidence();
                                if(text.equalsIgnoreCase("dog") || text.equalsIgnoreCase("dog") || text.equalsIgnoreCase("puppy") || text.equalsIgnoreCase("puppies")){
                                    dogPic = true;
                                }
                            }

                            if(dogPic==true){


                                try {
                                    uploadImage(Caption);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                            else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!isFinishing()){
                                            new AlertDialog.Builder(UploadActivity.this)
                                                    .setTitle("Image Issue")
                                                    .setMessage("Please select a picture with a DOG!!")
                                                    .setCancelable(false)
                                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            startActivity(new Intent(UploadActivity.this,MainActivity.class));
                                                            finish();
                                                        }
                                                    }).show();
                                        }
                                    }
                                });

                                //  Toast toast=Toast.makeText(getApplicationContext(),"Please Select A Picture With A DOG!!",Toast.LENGTH_LONG);
                                //  toast.setMargin(50,50);
                                //  toast.show();

                            }


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            Toast toast=Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG);
                            toast.setMargin(50,50);
                            toast.show();
                        }
                    });



        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    //end


    @Override
    public void onBackPressed() {
        startActivity(new Intent(UploadActivity.this,MainActivity.class));
        finish();
    }
}