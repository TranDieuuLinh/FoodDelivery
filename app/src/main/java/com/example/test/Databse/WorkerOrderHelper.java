package com.example.test.Databse;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test.Domain.OrderDetailDomain;
import com.example.test.Domain.WorkerDomain;
import com.example.test.Interface.getDataType;
import com.example.test.Interface.getListDataType;
import com.example.test.Interface.getOrderDetailDomainDataType;
import com.example.test.Interface.getWorkerDomainDataType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkerOrderHelper implements getListDataType, getWorkerDomainDataType, getDataType, getOrderDetailDomainDataType {
    public WorkerOrderHelper() {
    }


    public void getPlaceOrderStatus(String email, getDataType dataType){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("placeOrderList");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.child("orderStatus").exists()){
                    dataType.onGetData(snapshot.child("orderStatus").child("status").getValue(String.class));
                }
                else {dataType.onGetData("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void shareLiveLocation(String email, String liveLocation){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        Map<String, Object> live = new HashMap<>();
        live.put("Location",liveLocation);

        FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("placeOrderList").child("workerLiveLocation").setValue(live);

    }

    public void addPlaceOrderListStatus(String email, String statusString){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");

        Map<String, Object> status = new HashMap<>();
        status.put("status", statusString);

        mDatabase.child(encodedEmail).child("placeOrderList").child("orderStatus").setValue(status);
    }

    //done
    public void addOrderListData(String email, String uniqueId, String foodName, String foodQuantity, String eachFoodTotal, String foodImage, String roundTotalEachValue,String DELIVERYVALUE, String taxValue, String totalValue){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");

        Map<String, Object> placeOrder = new HashMap<>();
        placeOrder.put("foodName",foodName);
        placeOrder.put("foodQuantity",foodQuantity);
        placeOrder.put("eachFoodTotal",eachFoodTotal);
        placeOrder.put("foodImage",foodImage);
        placeOrder.put("roundTotalAllFood",roundTotalEachValue);
        placeOrder.put("DeliveryPrice",DELIVERYVALUE);
        placeOrder.put("taxValue",taxValue);
        placeOrder.put("finalTotalValue",totalValue);

        mDatabase.child(encodedEmail).child("placeOrderList").child("orderDetail").child(uniqueId).push().setValue(placeOrder);
    }

    public void getOrderNoForOrderList(String email, getListDataType listDataType){

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("placeOrderList").child("orderDetail");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> orderNo = new ArrayList<>();
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    orderNo.add(childSnapshot.getKey());

                }
                listDataType.onGetListDataType(orderNo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserPlaceOrderPos(String email, getDataType dataType){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("placeOrderList").child("userPlaceOrderPos").child("Address");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataType.onGetData(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getWorkerLiveLocation(String email, getDataType dataType){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("placeOrderList").child("workerLiveLocation").child("Location");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataType.onGetData(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onGetListDataType(List<String> listData) {
    }

    @Override
    public void omGetWorkerDomainDataType(List<WorkerDomain> mWorkerDomain) {

    }


    public void getEmailAndAddressForOrderList( getWorkerDomainDataType getWorkerDomainDataTypelistener){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<WorkerDomain> workerDomains = new ArrayList<>();
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    if (childSnapshot.child("placeOrderList").exists()){
                        String email = childSnapshot.child("userData").child("email").getValue(String.class);
                        String address = childSnapshot.child("placeOrderList").child("userPlaceOrderPos").child("Address").getValue(String.class);
                        WorkerDomain workerDomain = new WorkerDomain(email, address);
                        workerDomains.add(workerDomain);
                    }
                }
                getWorkerDomainDataTypelistener.omGetWorkerDomainDataType(workerDomains);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getOrderDetail(String email, String uniqueOrderNo, getOrderDetailDomainDataType listener) {
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("placeOrderList").child("orderDetail").child(uniqueOrderNo);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<OrderDetailDomain> domains = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {

                    domains.add(new OrderDetailDomain(
                            child.child("foodName").getValue(String.class),
                            child.child("foodQuantity").getValue(String.class),
                            child.child("eachFoodTotal").getValue(String.class),
                            child.child("foodImage").getValue(String.class),
                            child.child("roundTotalAllFood").getValue(String.class),
                            child.child("DeliveryPrice").getValue(String.class),
                            child.child("taxValue").getValue(String.class),
                            child.child("finalTotalValue").getValue(String.class)

                    ));

                }

                listener.ongetOrderDetailDomainDataTypeListner(domains);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("WorkerOrderHelper", "Error retrieving order details from database", error.toException());
            }
        });
    }



    public void addUserPlaceOrderPos(String email, String userPlaceOrderPos){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");

        Map<String, Object> PlaceOrderPos = new HashMap<>();
        PlaceOrderPos.put("Address",userPlaceOrderPos);

        FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("placeOrderList").child("userPlaceOrderPos").setValue(PlaceOrderPos);

    }


    @Override
    public void onGetData(String data) {

    }

    @Override
    public void ongetOrderDetailDomainDataTypeListner(List<OrderDetailDomain> mOrderDetailDomain) {

    }
}
