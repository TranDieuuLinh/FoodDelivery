package com.example.test;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.Adapter.FoodAdapter;
import com.example.test.Databse.DataSignUpHelper;
import com.example.test.Domain.FoodDomain;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFoodList;
    private RecyclerView.Adapter adapter;
    private LinearLayout homeBtn,orderBtn,profileBtn,locationBtn;
    private EditText SearchBar;
    private TextView hiUser;
    private ImageView mainProfilePic;
    private DataSignUpHelper mDataSignUpHelper;
    ArrayList<FoodDomain> foodDomains = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();


        getWindow().setStatusBarColor(getResources().getColor(R.color.yellow,this.getTheme()));

        handleStayLogIn();

        recyclerViewFood();
        bottomNavigation();
        setSearchBar();

    }

    private void handleStayLogIn(){


        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", "");

        mDataSignUpHelper.getNameData(userEmail,data -> {
            String[] splitted = data.split(" ");
            hiUser.setText("Hi " + splitted[splitted.length-1]);
        });


        mDataSignUpHelper.getImageData(userEmail,imageData -> {
            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                mainProfilePic.setImageBitmap(bitmap);
            } else {
                mainProfilePic.setImageResource(R.drawable.default_profile_image);

            }
        });


    }

    private void initView(){

        homeBtn = findViewById(R.id.homeBtn);
        orderBtn = findViewById(R.id.orderBtn);
        SearchBar = findViewById(R.id.SearchBar);
        mainProfilePic = findViewById(R.id.mainProfilePic);
        hiUser = findViewById(R.id.hiUser);
        profileBtn = findViewById(R.id.profileBtn);
        locationBtn = findViewById(R.id.locationBtn);

        mDataSignUpHelper = new DataSignUpHelper();
    }
    private void setSearchBar(){
        SearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void filter(String text){
        ArrayList<FoodDomain> filteredList = new ArrayList<>();
        for(FoodDomain i: foodDomains){
            if(i.getTitle().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(i);
            }
        }
        ((FoodAdapter) adapter).filteredList(filteredList);
    }

    private void recyclerViewFood() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false);
        recyclerViewFoodList = findViewById(R.id.orderSummaryRecyclerViewList);
        recyclerViewFoodList.setLayoutManager(gridLayoutManager);


        foodDomains.add(new FoodDomain(R.drawable.pop_1,"Pepperoni Pizza",9.8));
        foodDomains.add(new FoodDomain(R.drawable.pop_2,"Beef Burger",9.5));
        foodDomains.add(new FoodDomain(R.drawable.pop_3,"Cheese Pizza",8.2));
        foodDomains.add(new FoodDomain(R.drawable.pop_4,"Bam Bam Potato",9.2));
        foodDomains.add(new FoodDomain(R.drawable.pop_5,"Vegan Pizza",8.4));
        foodDomains.add(new FoodDomain(R.drawable.pop_6,"Chicken Salad",7.8));
        foodDomains.add(new FoodDomain(R.drawable.pop_7,"French Fries",7.5));
        foodDomains.add(new FoodDomain(R.drawable.pop_8,"Beef Burrito",9.5));
        adapter = new FoodAdapter(this,foodDomains,userEmail);
        recyclerViewFoodList.setAdapter(adapter);

    }

    private void bottomNavigation() {

        orderBtn.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this,SummaryActivity.class);
            intent.putExtra("mail",userEmail);
            startActivity(intent);
        });

        profileBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this,ProfileActivity.class);
            intent.putExtra("mail",userEmail);
            startActivity(intent);
        });


    }


}