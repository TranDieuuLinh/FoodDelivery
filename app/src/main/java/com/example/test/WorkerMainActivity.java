package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.test.Adapter.WorkerAdapter;
import com.example.test.Databse.WorkerOrderHelper;
import com.example.test.Domain.WorkerDomain;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WorkerMainActivity extends AppCompatActivity {

    private TextView logOutBtnWorker;
    private RecyclerView.Adapter adapter;
    private RecyclerView orderListWorkerMain;
    private WorkerOrderHelper mWorkerOrderHelper;

    private final ArrayList<WorkerDomain> workerDomains = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_worker);


        logOutBtnWorker = findViewById(R.id.logOutBtnWorker);
        orderListWorkerMain = findViewById(R.id.orderListWorkerMain);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        orderListWorkerMain.setLayoutManager(linearLayoutManager);





        mWorkerOrderHelper = new WorkerOrderHelper();

        adapter = new WorkerAdapter(this,workerDomains);
        orderListWorkerMain.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() ->   mWorkerOrderHelper.getEmailAndAddressForOrderList(mWorkerDomain -> {
            workerDomains.clear();
            workerDomains.addAll(mWorkerDomain);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }));




        mWorkerOrderHelper.getEmailAndAddressForOrderList(mWorkerDomain -> {
            workerDomains.addAll(mWorkerDomain);
            adapter.notifyDataSetChanged();
        });


        handleLogOut();
    }

    private void handleLogOut(){
        logOutBtnWorker.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_email", "");
            editor.putString("password" ,"");
            editor.apply();

            Intent intent = new Intent(this,SignInActivity.class);
            startActivity(intent);
        });

    }

}