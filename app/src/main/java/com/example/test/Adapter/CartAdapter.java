package com.example.test.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.Databse.DataHelper;
import com.example.test.Domain.CartDomain;
import com.example.test.R;
import com.example.test.SummaryActivity;

import java.util.ArrayList;

public class CartAdapter extends  RecyclerView.Adapter<CartAdapter.ViewHolder>{

    ArrayList<CartDomain> cartDomains;
    Context context;
    private SummaryActivity summaryActivity;
    private String emailHome;





    public CartAdapter(SummaryActivity summaryActivity,Context context, ArrayList<CartDomain> cartDomains) {
        this.summaryActivity = summaryActivity;
        this.context = context;
        this.cartDomains = cartDomains;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(context).inflate(R.layout.cart_list,parent,false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {


        holder.foodTitleCart.setText(cartDomains.get(position).getTitle());
        holder.foodQuantityCart.setText(cartDomains.get(position).getQuantity());
        holder.foodPriceCart.setText(cartDomains.get(position).getEach_fee());

        int images = cartDomains.get(position).getPic();
        holder.foodPicCart.setImageResource(images);

        holder.plusBtnCart.setOnClickListener(view -> {



            emailHome = summaryActivity.getIntent().getStringExtra("mail");

            int newQuantity = Integer.parseInt(cartDomains.get(position).getQuantity())+1;

            cartDomains.get(position).setQuantity(String.valueOf(newQuantity));
            holder.foodQuantityCart.setText(String.valueOf(newQuantity));

            Double newValue  = Math.round((cartDomains.get(position).getEachFee() * newQuantity)*100.0)/100.0;
            cartDomains.get(position).setEach_fee(String.valueOf(newValue));
            holder.foodPriceCart.setText(String.valueOf(newValue));

            DataHelper dataHelper = new DataHelper();
            dataHelper.updateData(cartDomains.get(position).getTitle(),cartDomains.get(position).getQuantity(),cartDomains.get(position).getEach_fee(),emailHome);

            summaryActivity.calc( summaryActivity.calculateTotalPrice());
            notifyDataSetChanged();





        });
        if (Integer.parseInt(cartDomains.get(position).getQuantity())>1){
        holder.minusBtnCart.setOnClickListener(view -> {

            emailHome = summaryActivity.getIntent().getStringExtra("mail");

            int newQuantity = Integer.parseInt(cartDomains.get(position).getQuantity())-1;

            cartDomains.get(position).setQuantity(String.valueOf(newQuantity));
            holder.foodQuantityCart.setText(String.valueOf(newQuantity));

            Double newValue  = Math.round((cartDomains.get(position).getEachFee() * newQuantity)*100.0)/100.0;
            cartDomains.get(position).setEach_fee(String.valueOf(newValue));
            holder.foodPriceCart.setText(String.valueOf(newValue));


            DataHelper dataHelper = new DataHelper();
            dataHelper.updateData(cartDomains.get(position).getTitle(),cartDomains.get(position).getQuantity(),cartDomains.get(position).getEach_fee(),emailHome);


            summaryActivity.calc( summaryActivity.calculateTotalPrice());


            notifyDataSetChanged();

        });}


    }


    @Override
    public int getItemCount() {
        return cartDomains.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public View layoutForeground;
        ImageView foodPicCart;
        TextView foodQuantityCart, foodPriceCart, foodTitleCart;
        ImageView plusBtnCart, minusBtnCart;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodPicCart = itemView.findViewById(R.id.foodPicCheckOut);
            foodQuantityCart = itemView.findViewById(R.id.foodQuantityCart);
            foodPriceCart = itemView.findViewById(R.id.foodPriceCheckOut);
            foodTitleCart = itemView.findViewById(R.id.foodTitleCheckOut);
            plusBtnCart = itemView.findViewById(R.id.plusBtnCart);
            minusBtnCart = itemView.findViewById(R.id.minusBtnCart);
            layoutForeground = itemView.findViewById(R.id.layoutForeground);


        }
    }


}
