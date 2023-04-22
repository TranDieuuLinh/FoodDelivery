package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.test.Adapter.OrderDetailAdapter;
import com.example.test.Adapter.OrderNoAdapter;
import com.example.test.Databse.WorkerOrderHelper;
import com.example.test.Domain.CartDomain;
import com.example.test.Domain.OrderDetailDomain;
import com.example.test.Domain.OrderNoDomain;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkerOrderDetailActivity extends AppCompatActivity {

    private String uniqueOrderNo;
    private RecyclerView workerOrderDetailRecycler;
    private WorkerOrderHelper mWorkerOrderHelper;
    private OrderDetailAdapter adapter;
    private String emailOrderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_order_detail);

        workerOrderDetailRecycler = findViewById(R.id.workerOrderDetailRecycler);

        uniqueOrderNo = getIntent().getStringExtra("uniqueOrderNo");
        emailOrderList = getIntent().getStringExtra("emailOrderList");

        ArrayList<OrderDetailDomain> orderDetailDomains = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        workerOrderDetailRecycler.setLayoutManager(linearLayoutManager);

        mWorkerOrderHelper = new WorkerOrderHelper();


        adapter = new OrderDetailAdapter(this,orderDetailDomains);
        workerOrderDetailRecycler.setAdapter(adapter);

        mWorkerOrderHelper.getOrderDetail(emailOrderList, uniqueOrderNo, mOrderDetailDomain -> {
            orderDetailDomains.addAll(mOrderDetailDomain);
            adapter.notifyDataSetChanged();
        });

    }
}