package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.Databse.DataSignUpHelper;

public class SignInActivity extends AppCompatActivity {
    private TextView toRegisterBtn,mailEditTxt,passEditTxt;
    private DataSignUpHelper mDataSignUpHelper;
    private SharedPreferences sharedPreferences;
    private Button loginBtn;
    private String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        intitView();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white,this.getTheme()));

        toRegisterActivity();
        checkMailPass();

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", "");
        if (!userEmail.equals("")){

            mDataSignUpHelper.getUserTypeData(userEmail,data->{
                Intent intent;
                if (data.equals("User")){
                    intent = new Intent(this, HomeActivity.class);
                }
                else{
                    intent = new Intent(this, WorkerMainActivity.class);
                }
                startActivity(intent);
            });
        }


    }

    private void intitView(){
        toRegisterBtn  = findViewById(R.id.toLoginBtn);
        mailEditTxt = findViewById(R.id.mailEditTxt);
        passEditTxt = findViewById(R.id.passEditTxt);
        loginBtn = findViewById(R.id.loginBtn);
        mDataSignUpHelper = new DataSignUpHelper();
    }

    void checkMailPass(){
        loginBtn.setOnClickListener(view -> {
            String mailGetTxt = mailEditTxt.getText().toString().trim();
            if(mailGetTxt.equalsIgnoreCase("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(mailGetTxt).matches())
            {
                mailEditTxt.setError("Invalid Email Address");
            } else if (passEditTxt.getText().toString().equalsIgnoreCase(""))
            {
                passEditTxt.setError("Please fill in Password");
            } else{
               mDataSignUpHelper.checkUsernamePassword(mailEditTxt.getText().toString(),passEditTxt.getText().toString(),result -> {
                    if (result){
                        sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_email", mailGetTxt);
                        editor.putString("password" ,passEditTxt.getText().toString());
                        editor.apply();
                        mDataSignUpHelper.getUserTypeData(mailGetTxt,data -> {
                            Intent intent;
                            if(data.equals("User")) {
                                intent = new Intent(this, HomeActivity.class);
                            }
                            else{
                                intent = new Intent(this, WorkerMainActivity.class);
                            }
                            startActivity(intent);
                        });

                    }
                    else{
                        Toast.makeText(this, "Please check your Email or Password!", Toast.LENGTH_SHORT).show();
                    }
                });

             }
        });
    }


    void toRegisterActivity(){
        toRegisterBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this,SignUpActivity.class);
            startActivity(intent);
        });
    }
}