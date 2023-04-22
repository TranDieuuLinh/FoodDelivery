package com.example.test.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.Domain.OrderDetailDomain;
import com.example.test.Domain.OrderNoDomain;
import com.example.test.R;

import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private Context context;
    ArrayList <OrderDetailDomain> orderDetailDomains;

    public OrderDetailAdapter(Context context, ArrayList<OrderDetailDomain> orderDetailDomains){
        this.context = context;
        this.orderDetailDomains = orderDetailDomains;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(context).inflate(R.layout.order_list,parent,false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ViewHolder holder, int position) {
        holder.foodNameOrder.setText(orderDetailDomains.get(position).getFoodNAme());
        holder.foodQuantityOrder.setText(orderDetailDomains.get(position).getFoodQUantity());
        holder.totalFoodPriceOrder.setText(orderDetailDomains.get(position).getEachFoodTOtal());

        int images = Integer.parseInt(orderDetailDomains.get(position).getFoodIMage());
        holder.foodPicOrder.setImageResource(images);
    }

    @Override
    public int getItemCount() {

        return orderDetailDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodPicOrder;
        private TextView foodNameOrder,foodQuantityOrder,totalFoodPriceOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodPicOrder = itemView.findViewById(R.id.foodPicOrder);
            foodNameOrder = itemView.findViewById(R.id.foodNameOrder);
            foodQuantityOrder = itemView.findViewById(R.id.foodQuantityOrder);
            totalFoodPriceOrder = itemView.findViewById(R.id.totalFoodPriceOrder);


        }
    }
}
