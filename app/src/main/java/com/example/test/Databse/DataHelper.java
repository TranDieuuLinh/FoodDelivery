package com.example.test.Databse;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test.Interface.CompletionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataHelper implements CompletionListener {

    public DataHelper() {

    }


    //done
    public boolean addData(String name, String quantity, String each_total, int image, double each_price, String email){

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String encodedEmail = email.replace(".", ",").replace("+", "%2B");

            Map<String, Object> food = new HashMap<>();
            food.put("foodName", name);
            food.put("foodQuantity", quantity);
            food.put("each_food_total", each_total);
            food.put("foodImage", String.valueOf(image));
            food.put("each_food_price_tag", each_price);

            mDatabase.child(encodedEmail).child("foodInCart").push().setValue(food);

            return true;
    }

    public void checkNameExist(String email,String name,CompletionListener listener){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("foodInCart");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean foodExists = false;
                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    if (Objects.equals(foodSnapshot.child("foodName").getValue(String.class), name)) {
                        foodExists = true;
                        break;
                    }
                }
                listener.onComplete(foodExists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    //done
    public void updateData(String name, String new_value1, String new_value2,String email) {

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("foodInCart");
        Query query = databaseReference.orderByChild("foodName").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    childSnapshot.getRef().child("each_food_total").setValue(new_value2);
                    childSnapshot.getRef().child("foodQuantity").setValue(new_value1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //done
    public void deleteAllData(String email){

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("foodInCart");

        databaseReference.removeValue();
    }






    //done
    public void deleteData(String name, String email) {

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("foodInCart");
        Query query = databaseReference.orderByChild("foodName").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    childSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public void onComplete(boolean result) {

    }
}
