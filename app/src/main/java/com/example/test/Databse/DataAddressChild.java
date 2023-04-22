package com.example.test.Databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test.Interface.CompletionListener;
import com.example.test.Interface.getDataType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DataAddressChild implements CompletionListener, getDataType {


    public DataAddressChild() {

    }

//done
    public boolean addAddressData(String email, String address){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");

        Map<String, Object> userAddress = new HashMap<>();
        userAddress.put("Address",address);

        mDatabase.child(encodedEmail).child("userAddress").setValue(userAddress);

        return true;

    }


    //done
    public void updateAddress(String email,  String newAddress) {
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userAddress").child("Address");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().setValue(newAddress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //done
    public void checkAddAvailable(String email, CompletionListener listener){

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userAddress").child("Address");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onComplete(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //done
    public void getAddress(String email, getDataType dataType){

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userAddress");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataType.onGetData(snapshot.child("Address").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataType.onGetData(null);
            }
        });

    }


    @Override
    public void onComplete(boolean result) {

    }

    @Override
    public void onGetData(String data) {

    }
}
