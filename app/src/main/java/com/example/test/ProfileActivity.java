package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.Databse.DataAddressChild;
import com.example.test.Databse.DataSignUpHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.concurrent.atomic.AtomicReference;


public class ProfileActivity extends AppCompatActivity {
    private ImageView profilePicProf;
    private String emailHome;
    private LinearLayout homeBtnProf,orderBtnProf,locationBtnProf;
    private TextView FullNameProf,editMailProf,phoneEditProf,logOutBtn,addLocBtn,showAddProf,changePass,nameEditProf,editPicBtn;
    private DataSignUpHelper mDataSignUpHelper;
    private ImageButton phoneEditBtn,nameEditBtn;
    private DataAddressChild mDataAddressChild;
    BottomSheetDialog dialogPhone,dialogPass,dialogName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        getWindow().setStatusBarColor(getResources().getColor(R.color.yellow,this.getTheme()));

        phoneEditBtn.setOnClickListener(v -> dialogPhone.show());
        changePass.setOnClickListener(view -> dialogPass.show());
        nameEditBtn.setOnClickListener(view -> dialogName.show());

        handleNameDialog();
        handlePassDialog();
        bottomNavigation();
        createDialog();
        handleLogOut();
        handleDoneBtn();
        handleShowAddBox();
        setPicProf();
        handleSetTxtProf();
    }



    private void initView(){
        profilePicProf =findViewById(R.id.profilePicProf);
        homeBtnProf = findViewById(R.id.homeBtnProf);
        orderBtnProf = findViewById(R.id.orderBtnProf);
        FullNameProf = findViewById(R.id.FullNameProf);
        editMailProf = findViewById(R.id.editMailProf);
        phoneEditProf = findViewById(R.id.phoneEditProf);
        logOutBtn = findViewById(R.id.logOutBtn);
        addLocBtn = findViewById(R.id.addLocBtn);
        showAddProf = findViewById(R.id.showAddProf);
        phoneEditBtn = findViewById(R.id.phoneEditBtn);
        changePass = findViewById(R.id.changePass);
        nameEditProf = findViewById(R.id.nameEditProf);
        nameEditBtn = findViewById(R.id.nameEditBtn);
        editPicBtn = findViewById(R.id.editPicBtn);
        locationBtnProf = findViewById(R.id.locationBtnProf);

        mDataSignUpHelper = new DataSignUpHelper();
        mDataAddressChild = new DataAddressChild();

        dialogPhone = new BottomSheetDialog(this);
        dialogPass = new BottomSheetDialog(this);
        dialogName = new BottomSheetDialog(this);

        emailHome = getIntent().getStringExtra("mail");
    }
    private void handleSetTxtProf(){

        mDataSignUpHelper.getNameData(emailHome,data -> {
            FullNameProf.setText(data);
            nameEditProf.setText(data);
        });

        editMailProf.setText(emailHome);
        mDataSignUpHelper.getPhoneData(emailHome,data -> phoneEditProf.setText(data));
    }
    private void setPicProf(){
        mDataSignUpHelper.getImageData(emailHome,imageData -> {
            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                profilePicProf.setImageBitmap(bitmap);
            } else {
                profilePicProf.setImageResource(R.drawable.default_profile_image);
            }
        });

    }
    private void handleShowAddBox(){

        mDataAddressChild.checkAddAvailable(emailHome,result -> {
            if(result){
                mDataAddressChild.getAddress(emailHome,data -> {
                    showAddProf.setText(data);
                });

            }
            else {  showAddProf.setText("");}
        });


    }

    private void handleDoneBtn(){
        addLocBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this,MapsActivity.class);
            intent.putExtra("mail",emailHome);
            intent.putExtra("2ndAddress",showAddProf.getText().toString());
            startActivity(intent);
        });
    }

    private void handleLogOut(){
        logOutBtn.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_email", "");
            editor.putString("password" ,"");
            editor.apply();

            Intent intent = new Intent(this,SignInActivity.class);
            startActivity(intent);
        });

    }
    private void handleNameDialog() {
        View view = getLayoutInflater().inflate(R.layout.name_dialog,  null,  false);
        Button submitNameBtn = view.findViewById(R.id.submitNameBtn);
        EditText editTextName = view.findViewById(R.id.editTextName);

        submitNameBtn.setOnClickListener(v->{
            dialogName.dismiss();

            String newName = editTextName.getText().toString();
            mDataSignUpHelper.updateName(emailHome,newName);
            nameEditProf.setText(newName);
            FullNameProf.setText(newName);
            editTextName.setText("");
        });

        dialogName.setContentView(view);
    }

    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.phone_dialog,  null,  false);
        Button submitPhoneBtn = view.findViewById(R.id.submitPhoneBtn);
        EditText editTextPhone = view.findViewById(R.id.editTextPhone);

        submitPhoneBtn.setOnClickListener(v->{
            dialogPhone.dismiss();
            mDataSignUpHelper.updatePhone(emailHome,editTextPhone.getText().toString());
            phoneEditProf.setText(editTextPhone.getText().toString());
            editTextPhone.setText("");
        });

        dialogPhone.setContentView(view);
    }

    private void handlePassDialog() {
        View view = getLayoutInflater().inflate(R.layout.pass_dialog,  null,  false);
        Button submitPassBtn = view.findViewById(R.id.submitPassBtn);
        EditText editNewPass = view.findViewById(R.id.editNewPass);
        EditText editOldPass = view.findViewById(R.id.editOldPass);

        submitPassBtn.setOnClickListener(v->{
            dialogPass.dismiss();

            mDataSignUpHelper.getPassData(emailHome,data -> {
                if (editOldPass.getText().toString().equals(data) && !editNewPass.getText().toString().equals(data))
                {
                    mDataSignUpHelper.updatePassword(emailHome, editNewPass.getText().toString());
                    Toast.makeText(this, "New Pass Has Set", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Unable To Change PassWord", Toast.LENGTH_SHORT).show();
                }

                editNewPass.setText("");
                editOldPass.setText("");
            });


        });
        dialogPass.setContentView(view);


    }


    private void bottomNavigation() {

        orderBtnProf.setOnClickListener(view -> {
            Intent intent = new Intent(this,SummaryActivity.class);
            intent.putExtra("mail",emailHome);
            startActivity(intent);
        });

        homeBtnProf.setOnClickListener(view -> {
            Intent intent = new Intent(this,HomeActivity.class);
            intent.putExtra("mail",emailHome);
            startActivity(intent);
        });

    }
}