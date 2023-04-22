package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.test.Databse.DataAddressChild;
import com.example.test.Databse.DataSignUpHelper;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditTxt,passwordEditTxt,nameEditTxt,mobileEditTxt;
    private DataSignUpHelper mDataSignUpHelper;
    private Button continueBtn;
    private TextView toLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white,this.getTheme()));

        initView();
        handleContinue();
        handleToLogInBtn();



    }

    public void handleToLogInBtn(){
        toLoginBtn.setOnClickListener(view ->{
            Intent intent = new Intent(this,SignInActivity.class);
            startActivity(intent);
        });
    }

    public void handleContinue(){

        continueBtn.setOnClickListener(view -> {
                String emailGetText = emailEditTxt.getText().toString().trim();
                String passGetText = passwordEditTxt.getText().toString();
                String nameGetText = nameEditTxt.getText().toString();
                String mobileGetText = mobileEditTxt.getText().toString();

            if(emailGetText.equalsIgnoreCase("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailGetText).matches())
            {
                emailEditTxt.setError("Invalid Email Address");
            }
            else if (passGetText.equalsIgnoreCase(""))
            {
                passwordEditTxt.setError("please enter password");
            }
            else if (nameGetText.equalsIgnoreCase("")) {
                nameEditTxt.setError("please enter full name");
            }
            else if (mobileGetText.equalsIgnoreCase("")) {
                mobileEditTxt.setError("please enter mobile number");
            } else{
                mDataSignUpHelper.getProfileData(emailGetText, result -> {
                    if (result) {
                        Intent intent = new Intent(SignUpActivity.this,SetProfileActivity.class);
                        intent.putExtra("Email",emailGetText);
                        intent.putExtra("Pass",passGetText);
                        intent.putExtra("Name",nameGetText);
                        intent.putExtra("Mobile",mobileGetText);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    void initView(){
        mDataSignUpHelper = new DataSignUpHelper();

        emailEditTxt = findViewById(R.id.emailEditTxt);
        passwordEditTxt = findViewById(R.id.passwordEditTxt);
        nameEditTxt = findViewById(R.id.nameEditTxt);
        mobileEditTxt = findViewById(R.id.mobileEditTxt);
        continueBtn = findViewById(R.id.continueBtn);
        toLoginBtn = findViewById(R.id.toLoginBtn);
    }





}