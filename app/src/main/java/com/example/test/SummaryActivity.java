package com.example.test;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.Adapter.CartAdapter;
import com.example.test.Databse.DataHelper;
import com.example.test.Databse.DataSignUpHelper;
import com.example.test.Databse.WorkerOrderHelper;
import com.example.test.Domain.CartDomain;
import com.example.test.Interface.PlusMinusClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SummaryActivity extends AppCompatActivity   {

    private DataHelper mDataHelper;
    private RecyclerView orderSummaryRecyclerViewList;

    private TextView selectAllBtn,deselectAllBtn,nothingText,deliverAddressBtn;
    private RecyclerView.Adapter adapter;
    private Button clearAllOrder, placeOrderBtn;
    private LinearLayout paymentLinear;
    private static TextView itemFeeCheckOut;
    private static TextView deliveryFeeCheckOut;
    private static TextView taxFeeCheckOut;
    private static TextView totalFeeCheckOut;
    private ImageView profilePicSum;

    private DataSignUpHelper mDataSignUpHelper;
    private WorkerOrderHelper mWorkerOrderHelper;

    private LinearLayout homeBtnSummary,profileBtnSummary,locationBtnSummary;
    private String emailHome;
    private static double roundTotalEachValue;
    private static final double DELIVERYVALUE = 10;
    private static double taxValue;
    private static double totalValue;
    ArrayList<CartDomain> cartDomains = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        initView();
        addDataToDomain();
        popUpDeleteAllBtn();
        deleteAll();
        bottomNavigation();
        deselect();

        adapter = new CartAdapter(this,this,cartDomains);
        orderSummaryRecyclerViewList.setAdapter(adapter);

        setProfilePicSummary();
        handlePlaceOrderBtn();
        handleDeliverAddressBtn();

    }

    private void handleDeliverAddressBtn() {
        deliverAddressBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("mail", emailHome);
            startActivity(intent);
        });
    }

    private void handlePlaceOrderBtn(){
        placeOrderBtn.setOnClickListener(view -> {

                    Intent intent = new Intent(SummaryActivity.this,LocationActivity.class);
                    intent.putExtra("mail",emailHome);
                    intent.putExtra("roundTotalEachValue",roundTotalEachValue);
                    intent.putExtra("DELIVERYVALUE",DELIVERYVALUE);
                    intent.putExtra("taxValue",taxValue);
                    intent.putExtra("totalValue",totalValue);
                    startActivity(intent);


        });
    }

    private void initView(){

        homeBtnSummary= findViewById(R.id.homeBtnSummary);
        orderSummaryRecyclerViewList = findViewById(R.id.orderSummaryRecyclerViewList);
        clearAllOrder = findViewById(R.id.clearAllOrder);
        selectAllBtn = findViewById(R.id.selectAllBtn);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        deselectAllBtn = findViewById(R.id.deselectAllBtn);
        nothingText = findViewById(R.id.nothingText);
        itemFeeCheckOut = findViewById(R.id.itemFeeCheckOut);
        deliveryFeeCheckOut = findViewById(R.id.deliveryFeeCheckOut);
        taxFeeCheckOut = findViewById(R.id.taxFeeCheckOut);
        totalFeeCheckOut = findViewById(R.id.totalFeeCheckOut);
        paymentLinear = findViewById(R.id.paymentLinear);
        profilePicSum = findViewById(R.id.profilePicSum);
        profileBtnSummary = findViewById(R.id.profileBtnSummary);
        locationBtnSummary = findViewById(R.id.locationBtnSummary);
        deliverAddressBtn = findViewById(R.id.deliverAddressBtn);

        getWindow().setStatusBarColor(getResources().getColor(R.color.yellow,this.getTheme()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        orderSummaryRecyclerViewList.setLayoutManager(linearLayoutManager);

        mDataHelper = new DataHelper();
        mWorkerOrderHelper = new WorkerOrderHelper();
        mDataSignUpHelper = new DataSignUpHelper();

        Intent emailIntent = getIntent();
        emailHome = emailIntent.getStringExtra("mail");
    }

    private void setProfilePicSummary(){
        mDataSignUpHelper.getImageData(emailHome,imageData -> {
            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                profilePicSum.setImageBitmap(bitmap);
            } else {
                profilePicSum.setImageResource(R.drawable.default_profile_image);
            }
        });

    }


    public double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartDomain cartDomain : cartDomains) {
            totalPrice += cartDomain.getEachFee() * Double.parseDouble(cartDomain.getQuantity());
        }
        return Math.round(totalPrice * 100.0) / 100.0;
    }

    public void addDataToDomain(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String encodedEmail = emailHome.replace(".", ",").replace("+", "%2B");

        mDatabase.child(encodedEmail).child("foodInCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    CartDomain cartDomain = new CartDomain();

                    cartDomain.setTitle(childSnapshot.child("foodName").getValue(String.class));
                    cartDomain.setQuantity(childSnapshot.child("foodQuantity").getValue(String.class));
                    cartDomain.setEach_fee(childSnapshot.child("each_food_total").getValue(String.class));
                    cartDomain.setEachFee(childSnapshot.child("each_food_price_tag").getValue(Double.class));
                    cartDomain.setPic(Integer.parseInt(childSnapshot.child("foodImage").getValue(String.class)));

                    cartDomains.add(cartDomain);
                }

                new ItemTouchHelper(itemTouchHelpCallBack).attachToRecyclerView(orderSummaryRecyclerViewList);
                nothingTextShow();
                calc(calculateTotalPrice());
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }

    ItemTouchHelper.SimpleCallback itemTouchHelpCallBack = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            DataHelper dataHelper = new DataHelper();
            dataHelper.deleteData(cartDomains.get(viewHolder.getAdapterPosition()).getTitle(),emailHome);


            cartDomains.remove(viewHolder.getAdapterPosition());
            nothingTextShow();
            calc(calculateTotalPrice());
            adapter.notifyDataSetChanged();


        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            if(viewHolder!=null){
                View foreGroundView = ((CartAdapter.ViewHolder) viewHolder).layoutForeground;
                getDefaultUIUtil().onSelected(foreGroundView);}
        }

        @Override
        public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View foreGroundView = ((CartAdapter.ViewHolder) viewHolder).layoutForeground;
            getDefaultUIUtil().onDrawOver(c,recyclerView,foreGroundView,dX, dY,actionState,isCurrentlyActive);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View foreGroundView = ((CartAdapter.ViewHolder) viewHolder).layoutForeground;
            getDefaultUIUtil().onDraw(c,recyclerView,foreGroundView,dX, dY,actionState,isCurrentlyActive);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            View foreGroundView = ((CartAdapter.ViewHolder) viewHolder).layoutForeground;
            getDefaultUIUtil().clearView(foreGroundView);
        }
    };


    public void nothingTextShow(){
        if (cartDomains.size() <= 0) nothingText.setVisibility(View.VISIBLE);
        else nothingText.setVisibility(View.GONE);
    }

    public void popUpDeleteAllBtn(){
        selectAllBtn.setOnClickListener(view -> {
            clearAllOrder.setVisibility(View.VISIBLE);
            placeOrderBtn.setVisibility(View.GONE);
            deselectAllBtn.setVisibility(View.VISIBLE);
            selectAllBtn.setVisibility(View.GONE);

        });
    }

    public void deselect(){
        deselectAllBtn.setOnClickListener(view -> {
            clearAllOrder.setVisibility(View.GONE);
            deselectAllBtn.setVisibility(View.GONE);
            placeOrderBtn.setVisibility(View.VISIBLE);
            selectAllBtn.setVisibility(View.VISIBLE);
        });
    }


    public void deleteAll(){
        clearAllOrder.setOnClickListener(view->{
            mDataHelper.deleteAllData(emailHome);
            cartDomains.clear();
            adapter.notifyDataSetChanged();
            Intent intent = new Intent(this,SummaryActivity.class);
            intent.putExtra("mail",emailHome);
            overridePendingTransition(0, 0);
            startActivity(intent);
            emailHome = getIntent().getStringExtra("mail");
            overridePendingTransition(0, 0);

        });
    }








    private void bottomNavigation() {
        homeBtnSummary.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("mail", emailHome);
            startActivity(intent);
        });

        profileBtnSummary.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("mail", emailHome);
            startActivity(intent);
        });




    }

    public void calc(double totalEachValue){
        roundTotalEachValue = Math.round(totalEachValue * 100.0) / 100.0;
        itemFeeCheckOut.setText(String.valueOf(roundTotalEachValue));


        deliveryFeeCheckOut.setText(String.valueOf(DELIVERYVALUE));

        taxValue = Math.round(((10.0f / 100.0f) * totalEachValue) * 100.0) / 100.0;
        taxFeeCheckOut.setText(String.valueOf(taxValue));

        totalValue = Math.round((totalEachValue + DELIVERYVALUE + taxValue) * 100.0) / 100.0;
        totalFeeCheckOut.setText(String.valueOf(totalValue));

    }



}