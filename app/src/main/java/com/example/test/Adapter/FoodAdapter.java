package com.example.test.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.Domain.FoodDomain;
import com.example.test.R;
import com.example.test.infoActivity;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private final String emailHome;
    ArrayList<FoodDomain> foodDomains;
    Context context;
    public FoodAdapter(Context context, ArrayList<FoodDomain> foodDomains, String emailHome) {
        this.context = context;
        this.foodDomains = foodDomains;
        this.emailHome = emailHome;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(context).inflate(R.layout.viewholder_food,parent,false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.foodTitle.setText(foodDomains.get(position).getTitle());
        holder.foodPrice.setText(String.valueOf(foodDomains.get(position).getFee()));


        int images = foodDomains.get(position).getPic();
        holder.foodPic.setImageResource(images);

        holder.addBtn.setOnClickListener(view -> {
                Intent intent = new Intent(context, infoActivity.class);
                intent.putExtra( "object", foodDomains.get(position));
                intent.putExtra("mail",emailHome);
                context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodDomains.size();
    }

    public void filteredList(ArrayList<FoodDomain> filteredList){
        foodDomains = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView foodPic;
        TextView foodTitle, foodPrice,addBtn;

        public ViewHolder(View itemView){
            super(itemView);
            foodPic = itemView.findViewById(R.id.foodPic);
            foodTitle = itemView.findViewById(R.id.foodTitle);
            foodPrice = itemView.findViewById(R.id.foodPrice);

            addBtn = itemView.findViewById(R.id.addBtn);

        }

    }

}
