package com.example.tuyucel.activship;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private Button buttonSaveImage;
    private Button buttonSelectImage;
    private ImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 123;
    private Uri filePath;
    private FirebaseAuth mAuth;
    private FirebaseStorage fStorage;
    private ProgressDialog progressDialog;
    private Button signOutBtn;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Picasso.with(ProfileActivity.this).load(filePath).fit().centerCrop().into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();

        buttonSaveImage =  findViewById(R.id.buttonSaveImage);
        buttonSelectImage =  findViewById(R.id.buttonSelectImage);
        imageView =  findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference storageRef = fStorage.getReference().child("users").child(mAuth.getCurrentUser().getUid());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                progressDialog.dismiss();
                Picasso.with(ProfileActivity.this).load(uri).fit().centerCrop().into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSaveImage.invalidate();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), PICK_IMAGE_REQUEST);

            }
        });

        signOutBtn = findViewById(R.id.buttonSignOut);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(intent);

            }
        });

        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            buttonSaveImage.invalidate();
                if(filePath!=null){

                    progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setMessage("Yükleniyor...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    StorageReference storageRef = fStorage.getReference().child("users").child(mAuth.getCurrentUser().getUid());
                    storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Fotoğraf başarılı bir şekilde kaydedildi.", Toast.LENGTH_SHORT).show();
                            imageView.setImageBitmap(null);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        });
    }

    }