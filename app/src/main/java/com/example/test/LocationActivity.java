package com.example.test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.test.Databse.DataAddressChild;
import com.example.test.Databse.DataHelper;
import com.example.test.Databse.WorkerOrderHelper;
import com.example.test.databinding.ActivityLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityLocationBinding binding;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 199;
    private GoogleMap mMap;
    private Marker userMarker;
    private DataAddressChild mDataAddressChild;
    private EditText SearchLocationTextLoc;
    private TextView nextBtn,backBtn;
    private ImageButton SearchLocationBarLoc;
    private View mapView;
    private String stringAddress;
    private String emailHome, userLocation,workerLocation;
    private Double  totalValue,taxValue,roundTotalEachValue,DELIVERYVALUE;
    private WorkerOrderHelper mWorkerOrderHelper;
    private DataHelper mDataHelper;
    private Handler mHandler;
    private Runnable mRunnable;
    private Polyline routePolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();

        if (savedInstanceState != null) {
            userLocation = savedInstanceState.getString("userLocation");
            workerLocation = savedInstanceState.getString("workerLocation");

            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyC3pIsDb8pt_36cAEb8iN15Nn8S_Lvng8U")
                    .build();
            DirectionsResult directionsResult;
            try {
                directionsResult = DirectionsApi.newRequest(context)
                        .mode(TravelMode.DRIVING)
                        .origin(userLocation)
                        .destination(workerLocation)
                        .await();

                List<LatLng> path = new PolylineOptions().getPoints();
                for (com.google.maps.model.LatLng latLng : directionsResult.routes[0].overviewPolyline.decodePath()) {
                    path.add(new LatLng(latLng.lat, latLng.lng));
                }

                mMap.addMarker(new MarkerOptions().position(path.get(0)).title(userLocation));
                mMap.addMarker(new MarkerOptions().position(path.get(path.size() - 1)).title(workerLocation));
                routePolyline =  mMap.addPolyline(new PolylineOptions().addAll(path));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(path.get(0), 15));
            } catch (InterruptedException | IOException | ApiException e) {
                e.printStackTrace();
            }


    }}

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of the map and the route
        outState.putString("userLocation", userLocation);
        outState.putString("workerLocation", workerLocation);
        if (routePolyline != null) {
            outState.putParcelableArrayList("path", new ArrayList<>(routePolyline.getPoints()));
        }
    }

    private void initView() {

        SearchLocationTextLoc = findViewById(R.id.SearchLocationTxtLoc);
        SearchLocationBarLoc = findViewById(R.id.SearchLocationBarLoc);
        nextBtn = findViewById(R.id.nextBtn);
        backBtn = findViewById(R.id.backBtn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapLoc);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mDataAddressChild = new DataAddressChild();

        emailHome = getIntent().getStringExtra("mail");
        roundTotalEachValue = getIntent().getDoubleExtra("roundTotalEachValue",0.0);
        DELIVERYVALUE = getIntent().getDoubleExtra("DELIVERYVALUE",0.0);
        taxValue = getIntent().getDoubleExtra("taxValue",0.0);
        totalValue = getIntent().getDoubleExtra("totalValue",0.0);

        mWorkerOrderHelper = new WorkerOrderHelper();
        mDataHelper = new DataHelper();
    }




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        handleCurrentLocBtn();
        handleDraggingMarker();
        handleDataLoc();
        handleSearchLocation();

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this,SummaryActivity.class);
            intent.putExtra("mail",emailHome);
            startActivity(intent);
        });



        nextBtn.setOnClickListener(view -> {
            SearchLocationTextLoc.setEnabled(false);
            SearchLocationBarLoc.setEnabled(false);

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false); // Prevents user from canceling the dialog
            progressDialog.show();

            userLocation = SearchLocationTextLoc.getText().toString();
            mWorkerOrderHelper.addUserPlaceOrderPos(emailHome,userLocation);

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String encodedEmail = emailHome.replace(".", ",").replace("+", "%2B");

            mDatabase.child(encodedEmail).child("foodInCart").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        mWorkerOrderHelper.addOrderListData(
                                emailHome,
                                android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", new java.util.Date()).toString(),
                                childSnapshot.child("foodName").getValue(String.class),
                                childSnapshot.child("foodQuantity").getValue(String.class),
                                childSnapshot.child("each_food_total").getValue(String.class),
                                childSnapshot.child("foodImage").getValue(String.class),
                                Double.toString(roundTotalEachValue),
                                Double.toString(DELIVERYVALUE),
                                Double.toString(taxValue),
                                Double.toString(totalValue));
                    }
                    mDataHelper.deleteAllData(emailHome);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    mWorkerOrderHelper.getPlaceOrderStatus(emailHome, data -> {
                        if (data != null && !data.equals("")) {
                            if (data.equals("Order Confirmed")) {
                                progressDialog.dismiss();
                                Toast.makeText(LocationActivity.this, "Your order has been confirmed.", Toast.LENGTH_LONG).show();
                                mHandler.removeCallbacks(mRunnable);
                            }
                        } else {
                            mHandler.postDelayed(this, 2000);
                        }
                    });
                }
            }; mHandler.postDelayed(mRunnable, 2000);

            progressDialog.setOnDismissListener(dialog -> mWorkerOrderHelper.getWorkerLiveLocation(emailHome, data -> {
                workerLocation = data;
                if (userMarker != null) userMarker.remove();
                mMap.setOnMapLongClickListener(null);

                GeoApiContext context = new GeoApiContext.Builder()
                        .apiKey("AIzaSyCt0I6k3QLaB3XQrNZE9uAae6kXKnoLg4M")
                        .build();
                DirectionsResult directionsResult;
                try {
                    directionsResult = DirectionsApi.newRequest(context)
                            .mode(TravelMode.DRIVING)
                            .origin(userLocation)
                            .destination(workerLocation)
                            .await();

                    List<LatLng> path = new PolylineOptions().getPoints();
                    for (com.google.maps.model.LatLng latLng : directionsResult.routes[0].overviewPolyline.decodePath()) {
                        path.add(new LatLng(latLng.lat, latLng.lng));
                    }

                    mMap.addMarker(new MarkerOptions().position(path.get(path.size() - 1)).title(workerLocation));
                    routePolyline =  mMap.addPolyline(new PolylineOptions().addAll(path));

                    mWorkerOrderHelper.addUserPlaceOrderPos(emailHome, userLocation);

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(path.get(0), 15));
                } catch (InterruptedException | IOException | ApiException e) {
                    e.printStackTrace();
                }
            }));


        });


    }



    private void handleCurrentLocBtn() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        mMap.setMyLocationEnabled(true);
        LocationListener locationListener = location -> {
            // Update onStartLocation variable
            Location onStartLocation = location;
            LatLng latLng1 = new LatLng(onStartLocation.getLatitude(), onStartLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,20f));
        };

        mMap.setOnMyLocationButtonClickListener(() -> {
            Location myLocation = mMap.getMyLocation();
            LatLng latLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20f));
            return true;
        });

        View locationBtn = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationBtn.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
    }

    private void handleDraggingMarker(){

        mMap.setOnMapLongClickListener(latLng -> {
            if (userMarker != null) userMarker.remove();

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
            userMarker = mMap.addMarker(markerOptions);

            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null || addresses.size()>0){
                Address address = addresses.get(0);
                stringAddress = address.getAddressLine(0);
                SearchLocationTextLoc.setText(stringAddress);
            }

        });
    }

    private void handleDataLoc(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

        mDataAddressChild.getAddress(emailHome,data -> {
            if(data != null){
                SearchLocationTextLoc.setText(data);
                Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(SearchLocationTextLoc.getText().toString(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        if (userMarker != null) {
                            userMarker.remove();
                        }
                        userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                    } else {
                        Toast.makeText(LocationActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                SearchLocationTextLoc.setText("Enter Your Location!");
            }
        });
}
    private void handleSearchLocation(){

        SearchLocationBarLoc.setOnClickListener(view -> {
            userLocation =  SearchLocationTextLoc.getText().toString();
            if(!TextUtils.isEmpty(userLocation)){
                Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(userLocation, 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        if (userMarker != null) {
                            userMarker.remove();
                        }
                        userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                    } else {
                        Toast.makeText(LocationActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}