package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.example.test.Adapter.OrderNoAdapter;
import com.example.test.Adapter.WorkerAdapter;
import com.example.test.Databse.WorkerOrderHelper;
import com.example.test.Domain.OrderNoDomain;
import com.example.test.Domain.WorkerDomain;

import java.util.ArrayList;

public class WorkerOrderNoActivity extends AppCompatActivity {

    private RecyclerView orderNoRecycler;
    private WorkerOrderHelper mWorkerOrderHelper;
    private OrderNoAdapter adapter;
    private String emailOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_order_no);

        orderNoRecycler = findViewById(R.id.orderNoRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        orderNoRecycler.setLayoutManager(linearLayoutManager);


        mWorkerOrderHelper = new WorkerOrderHelper();
        ArrayList<OrderNoDomain> orderNoDomains = new ArrayList<>();

        emailOrderList = getIntent().getStringExtra("emailPosition");


        mWorkerOrderHelper.getOrderNoForOrderList(emailOrderList,listData -> {

            for (String i : listData){
                orderNoDomains.add(new OrderNoDomain(i));
            }
            adapter.notifyDataSetChanged();

        });


        adapter = new OrderNoAdapter(this,orderNoDomains,emailOrderList);
        orderNoRecycler.setAdapter(adapter);






    }
}