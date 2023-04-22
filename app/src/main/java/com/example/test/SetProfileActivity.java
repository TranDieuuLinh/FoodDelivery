package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.request.RequestOptions;
import com.example.test.Databse.DataAddressChild;
import com.example.test.Databse.DataSignUpHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SetProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private Button nextBtn;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private DataSignUpHelper mDataSignUpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white,this.getTheme()));

        profileImage = findViewById(R.id.profileImage);
        nextBtn = findViewById(R.id.nextBtn);

        mDataSignUpHelper = new DataSignUpHelper();

        profileImage.setOnClickListener(view -> selectImage());

        Intent intent = getIntent();
        String email = intent.getStringExtra("Email");
        String pass = intent.getStringExtra("Pass");
        String name = intent.getStringExtra("Name");
        String mobile = intent.getStringExtra("Mobile");
        String userType;
        String[] splitted = email.split("@");
        if (splitted[splitted.length - 1].equals("hoachatdailongbinh.com")) userType = "Worker";
        else userType = "User";

        nextBtn.setOnClickListener(view -> {


            if (mDataSignUpHelper.addData(email, pass, name, mobile, profileImage,userType)) {
                Intent signInIntent = new Intent(this, SignInActivity.class);
                startActivity(signInIntent);
            } else {
                Toast.makeText(this, "not successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    // Request camera permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);}
                }

            else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start camera
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                // Permission is denied, show a message and redirect to app settings
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle image from gallery
            Glide.with(this)
                    .load(data.getData())
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle image from camera
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Glide.with(this)
                        .load(imageBitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImage);


            }
        }
    }






}
