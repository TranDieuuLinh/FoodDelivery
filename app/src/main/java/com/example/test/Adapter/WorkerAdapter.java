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

import com.example.test.Domain.WorkerDomain;
import com.example.test.R;
import com.example.test.WorkerMapActivity;
import com.example.test.WorkerOrderNoActivity;

import java.util.ArrayList;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.ViewHolder>{
    Context context;
    ArrayList<WorkerDomain> workerDomains;
    public WorkerAdapter( Context context, ArrayList<WorkerDomain> workerDomains) {
        this.context = context;
        this.workerDomains = workerDomains;
    }

    @NonNull
    @Override
    public WorkerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(context).inflate(R.layout.order_email_list,parent,false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerAdapter.ViewHolder holder, int position) {
        holder.emailList.setText(workerDomains.get(position).getEmail());
        holder.moreDetailArrows.setOnClickListener(view -> {
            Intent intent = new Intent(context, WorkerOrderNoActivity.class);
            intent.putExtra("emailPosition", workerDomains.get(position).getEmail());
            context.startActivity(intent);
        });
        holder.userCurAdd.setText(workerDomains.get(position).getUserAddress());
        holder.userCurAdd.setOnClickListener(view -> {
            Intent intent = new Intent(context, WorkerMapActivity.class);
            intent.putExtra("emailPosition", workerDomains.get(position).getEmail());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return workerDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton moreDetailArrows;
        TextView emailList,userCurAdd;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            moreDetailArrows = itemView.findViewById(R.id.moreDetailArrows);
            emailList = itemView.findViewById(R.id.emailList);
            userCurAdd = itemView.findViewById(R.id.userCurAdd);



        }
    }
}
