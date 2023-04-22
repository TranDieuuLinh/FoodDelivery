package com.example.test.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.Domain.OrderNoDomain;
import com.example.test.R;
import com.example.test.WorkerOrderDetailActivity;

import java.util.ArrayList;

public class OrderNoAdapter extends RecyclerView.Adapter<OrderNoAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderNoDomain> orderNoDomains;
    private String emailOrderList;
    public OrderNoAdapter(Context context, ArrayList<OrderNoDomain> orderNoDomains,String emailOrderList){
        this.context = context;
        this.orderNoDomains = orderNoDomains;
        this.emailOrderList = emailOrderList;
    }
    @NonNull
    @Override
    public OrderNoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(context).inflate(R.layout.order_no_list,parent,false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderNoAdapter.ViewHolder holder, int position) {
        holder.orderNoList.setText(orderNoDomains.get(position).getOrderNo());

        holder.moreDetailArrowsOrderNo.setOnClickListener(view -> {
            Intent intent = new Intent(context, WorkerOrderDetailActivity.class);
            intent.putExtra("uniqueOrderNo",orderNoDomains.get(position).getOrderNo());
            intent.putExtra("emailOrderList",emailOrderList);
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return orderNoDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderNoList;
        private ImageButton moreDetailArrowsOrderNo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderNoList = itemView.findViewById(R.id.orderNoList);
            moreDetailArrowsOrderNo = itemView.findViewById(R.id.moreDetailArrowsOrderNo);
        }
    }
}
