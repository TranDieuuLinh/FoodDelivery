package com.example.test;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.Databse.DataAddressChild;
import com.example.test.Databse.WorkerOrderHelper;
import com.example.test.databinding.ActivityWorkerMapBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WorkerMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 199;

    private GoogleMap mMap;
    private Marker userMarker;
    private TextView nextBtnWorkerMap,backBtnWorkerMap;
    private Handler mHandler;
    private Runnable mRunnable;
    private View mapView;
    private String emailPosition, userAddressString,workerAddressString;
    private WorkerOrderHelper mWorkerOrderHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.test.databinding.ActivityWorkerMapBinding binding = ActivityWorkerMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        handleUserPlaceOrderPos();


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        backBtnWorkerMap.setOnClickListener(view -> {
            Intent intent = new Intent(this,WorkerMainActivity.class);
            startActivity(intent);
        });


    }

    private void initView() {
        nextBtnWorkerMap = findViewById(R.id.nextBtnWorkerMap);
        backBtnWorkerMap = findViewById(R.id.backBtnWorkerMap);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapWorkerMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mWorkerOrderHelper = new WorkerOrderHelper();

        emailPosition = getIntent().getStringExtra("emailPosition");

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        handleNextBtn();
        handleCurrentLocBtn();

    }

    private void handleNextBtn(){
        nextBtnWorkerMap.setOnClickListener(view -> {

            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {

                    mWorkerOrderHelper.addPlaceOrderListStatus(emailPosition,"Order Confirmed");

                    if (userMarker != null) userMarker.remove();
                    mMap.setOnMapLongClickListener(null);

                    Location myLocation = mMap.getMyLocation();
                    LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    Geocoder geocoder = new Geocoder(WorkerMapActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        if(addresses!= null && addresses.size()>0){
                            Address address = addresses.get(0);
                            workerAddressString = address.getAddressLine(0);
                            mWorkerOrderHelper.shareLiveLocation(emailPosition, workerAddressString);


                            GeoApiContext context = new GeoApiContext.Builder()
                                    .apiKey("AIzaSyCt0I6k3QLaB3XQrNZE9uAae6kXKnoLg4M")
                                    .build();
                            DirectionsResult directionsResult = DirectionsApi.newRequest(context)
                                    .mode(TravelMode.DRIVING)
                                    .origin(workerAddressString)
                                    .destination(userAddressString)
                                    .await();

                            List<LatLng> path = new PolylineOptions().getPoints();
                            for (com.google.maps.model.LatLng latLng1 : directionsResult.routes[0].overviewPolyline.decodePath()) {
                                path.add(new LatLng(latLng1.lat, latLng1.lng));
                            }


                            mMap.addMarker(new MarkerOptions().position(path.get(path.size() - 1)).title(userAddressString));
                            mMap.addPolyline(new PolylineOptions().addAll(path));

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(path.get(0), 20));

                        }
                    } catch (InterruptedException | IOException |
                             com.google.maps.errors.ApiException e) {
                        e.printStackTrace();
                    }


                    mHandler.postDelayed(this, 2000);
                }
            };
            mHandler.postDelayed(mRunnable, 2000);


        });
    }

    private void handleUserPlaceOrderPos() {
        mWorkerOrderHelper.getUserPlaceOrderPos(emailPosition,data -> {
            userAddressString = data;
            Geocoder geocoder = new Geocoder(WorkerMapActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(userAddressString, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    if (userMarker != null) {
                        userMarker.remove();
                    }
                    userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                } else {
                    Toast.makeText(WorkerMapActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void handleCurrentLocBtn() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        mMap.setMyLocationEnabled(true);
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












}