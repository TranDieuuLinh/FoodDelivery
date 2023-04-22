package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.Databse.DataHelper;
import com.example.test.Domain.FoodDomain;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class infoActivity extends AppCompatActivity {

    ImageView minusBtn,plusBtn,picFood;
    TextView numberOrderTxt,priceTxt,titleTxt,backButton;

    int quantity =1;
    double value;
    private FoodDomain foodDomains;
    private DataHelper mDataHelper;
    private String quantityString,valueString,emailHome;
    private Double eachPrice;
    Button addToCartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        getWindow().setStatusBarColor(getResources().getColor(R.color.yellow,this.getTheme()));

        picFood = findViewById(R.id.picFood);
        minusBtn = findViewById(R.id.minusBtn);
        plusBtn = findViewById(R.id.plusBtn);
        numberOrderTxt = findViewById(R.id.numberOrderTxt);
        titleTxt = findViewById(R.id.titleTxt);
        priceTxt = findViewById(R.id.priceTxt);
        addToCartBtn = findViewById(R.id.addToCartBtn);
        backButton = findViewById(R.id.backToOrderBtn);


        foodDomains= (FoodDomain) getIntent().getSerializableExtra( "object");
        emailHome = getIntent().getStringExtra("mail");
        mDataHelper = new DataHelper();

        titleTxt.setText(foodDomains.getTitle());
        priceTxt.setText(String.valueOf(foodDomains.getFee()));
        int images = foodDomains.getPic();
        this.picFood.setImageResource(images);

        eachPrice = foodDomains.getFee();

        quantityString = "1";
        valueString = String.valueOf(foodDomains.getFee());

        plusQuantity();
        minusQuantity();
        backArrowButton();
        addData();


        }


    public void backArrowButton(){
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);});

    }

    public void plusQuantity(){
        plusBtn.setOnClickListener(view -> {
            quantity++;
            numberOrderTxt.setText(String.valueOf(quantity));
            value = Math.round((quantity*foodDomains.getFee())*100.0)/100.0;
            priceTxt.setText(String.valueOf(value));

            quantityString = String.valueOf(quantity);
            valueString = String.valueOf(value);
        });
    }

    public void minusQuantity(){
        minusBtn.setOnClickListener(view -> {
            if(quantity<=1){
                Toast.makeText(this, "Cannot decrease less than 1", Toast.LENGTH_SHORT).show();
            }
            else{
                quantity--;
                numberOrderTxt.setText(String.valueOf(quantity));
                value = Math.round((quantity*foodDomains.getFee())*100.0)/100.0;
                priceTxt.setText(String.valueOf(value));

                quantityString = String.valueOf(quantity);
                valueString = String.valueOf(value);
            }
        });
    }


    public void addData(){
        addToCartBtn.setOnClickListener(view -> mDataHelper.checkNameExist(emailHome,foodDomains.getTitle(), result -> {
            if (result) Toast.makeText(this, "Food already in cart", Toast.LENGTH_SHORT).show();

            else {
                mDataHelper.addData(foodDomains.getTitle(), quantityString, valueString, foodDomains.getPic(), eachPrice, emailHome);
            }
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("mail",emailHome);
                startActivity(intent);
        }));


    }




}