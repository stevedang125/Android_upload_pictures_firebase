package com.steve.imageuploads;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

// Part 1:
// ======================= Firebase set up: ===========================
// Tools/Firebase
// Click on realtime database
// Log into the google firebase console
// Choose/CreateNew project
// Connected?
// Choose Storage: Add Cloud Storage to your app

// Manifest
// <uses-permission android:name="android.permission.INTERNET"/>

// Set up the layout.xml

// Part 2:
// ======================= Get the files:  =============================
// MainActivity.java

//private static final int PICK_IMAGE_REQUEST = 1;
//private Button mButtonChooseImage;
//private Button mButtonUpload;
//private TextView mTextViewShowUploads;
//private EditText mEditTextFilename;
//private ImageView mImageView;
//private ProgressBar mProgressBar;
//
//private Uri mImageUri;

//mButtonChooseImage = findViewById(R.id.button_choose_image);
//mButtonUpload = findViewById((R.id.button_upload));
//mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
//mEditTextFilename = findViewById(R.id.edit_text_file_name);
//mImageView = findViewById(R.id.image_view);
//mProgressBar = findViewById(R.id.progress_bar);

// Set on click listener for:
// mButtonChooseImage, mButtonUpload, mTextViewShowUploads

// create openFileChoose(); inside mButtonChooseImage

// Ctrl + O => look for onActivityResult()
//    if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
//    && data != null && data.getData() != null)
//
//    {
//    mImageUri = data.getData();
//    //            Picasso.with(this).load(mImageUri).into(mImageView);
//    mImageView.setImageURI(mImageUri);
//    System.out.println("**************** mImageUri: "+mImageUri);
//    }

// Part 3:
// ======================= Upload the files:  =============================
// Create a class with 2 attributes and constructor

// Tool/Firebase:
// Add the RealTime Database

//private StorageReference mStorageReference;
//private DatabaseReference mDatabaseReference;

// Save in the folder calls "uploads" in the Storage
//mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
//mDatabaseReference = FirebaseDatabase.getInstance().getReference("uploads");

// Button Upload

// Part 4:
// ======================= Set up show uploaded files:  =====================
// Create the new Activity
// Create an item layout for the recycler view

// Open this new Activity by the Show Uploads TextView

// Part 5:
// ======================= Adapter for uploaded files:  ======================
// Create a new adapter class:
// ImageAdapter
// android.support.v7.widget.RecyclerView.Adapter

// Part 6:
// ======================= Show uploaded files:  ======================
// ImageActivity



public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFilename;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById((R.id.button_upload));
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextFilename = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        // Save in the folder calls "uploads" in the Storage and Database
        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoose();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });


    }


    private void openFileChoose(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)

        {
            mImageUri = data.getData();
//            Picasso.with(this).load(mImageUri).into(mImageView);
            mImageView.setImageURI(mImageUri);
            System.out.println("**************** mImageUri: "+mImageUri);
        }
    }

    // Get file extension from the image: jpg or png
    private String getFileExtension(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile()
    {
        if(mImageUri != null)
        {
            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
            + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            mProgressBar.setProgress(0);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(MainActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                            Upload upload = new Upload(mEditTextFilename.getText().toString().trim(),
                                    taskSnapshot.getDownloadUrl().toString());

                            System.out.println("****** Upload Link: "+upload);

                            String uploadId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(uploadId).setValue(upload);

                            System.out.println("****** Upload ID: "+uploadId);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int)progress);
                            System.out.println("****** URLLL: "+taskSnapshot.getDownloadUrl());
//                            System.out.println("****** URLLL22222: "+taskSnapshot.getDownloadUrl().toString());
                        }
                    });
        }else
        {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    private  void openImagesActivity()
    {
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

}
