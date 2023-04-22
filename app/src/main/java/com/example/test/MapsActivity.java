package com.example.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.test.Databse.DataAddressChild;
import com.example.test.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ActivityMapsBinding binding;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ImageButton imageButton;
    private Marker userMarker;
    private EditText SearchLocationText;
    private Button doneButton;
    private DataAddressChild mDataAddressChild;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private View mapView;
    private String emailHome, stringAddress, fromProfAdd;

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, this.getTheme()));

        emailHome = getIntent().getStringExtra("mail");
        fromProfAdd = getIntent().getStringExtra("2ndAddress");

        handleDoneBtn();
    }

    private void initView() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mDataAddressChild = new DataAddressChild();

        SearchLocationText = findViewById(R.id.SearchLocationText);
        imageButton = findViewById(R.id.imageButton);
        doneButton = findViewById(R.id.doneButton);
    }

    private void handleDoneBtn() {
        doneButton.setOnClickListener(view -> mDataAddressChild.checkAddAvailable(emailHome, result -> {
            if (result) {
                mDataAddressChild.updateAddress(emailHome, stringAddress);
            } else {
                mDataAddressChild.addAddressData(emailHome, stringAddress);
            }
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("mail", emailHome);
            intent.putExtra("1stAddress", stringAddress);
            startActivity(intent);
        }));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        handleLocAndMarker();
        handleCurrentLocBtn();
        handleDraggingMarker();
        handleSearchLocation();
    }

    private void handleSearchLocation() {

        imageButton.setOnClickListener(view -> {
            String location = SearchLocationText.getText().toString();
            if (!TextUtils.isEmpty(location)) {
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(location, 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        stringAddress = address.getAddressLine(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        if (userMarker != null) {
                            userMarker.remove();
                        }
                        userMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
                    } else {
                        Toast.makeText(MapsActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleDraggingMarker() {
        mMap.setOnMapLongClickListener(latLng -> {

            if (userMarker != null) {
                userMarker.remove();
            }

            // Add new marker at long-clicked location
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
            userMarker = mMap.addMarker(markerOptions);

            // Get address from location
            Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                stringAddress = address.getAddressLine(0);
                SearchLocationText.setText(stringAddress);
            }
        });
    }

    private void handleLocAndMarker() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }


        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLocation = location;
                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                userMarker = mMap.addMarker(new MarkerOptions().position(latLng));

                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    stringAddress = address.getAddressLine(0);
                    if (fromProfAdd.equals("")) {
                        SearchLocationText.setText(stringAddress);
                    } else {
                        SearchLocationText.setText(fromProfAdd);
                        Geocoder geocoder1 = new Geocoder(MapsActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses1 = geocoder1.getFromLocationName(SearchLocationText.getText().toString(), 1);
                            Address address1 = addresses1.get(0);
                            stringAddress = address1.getAddressLine(0);
                            LatLng latLng1 = new LatLng(address1.getLatitude(), address1.getLongitude());

                            if (userMarker != null) {
                                userMarker.remove();
                            }

                            userMarker = mMap.addMarker(new MarkerOptions().position(latLng1).title(address.getAddressLine(0)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 20));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));
            }
        });


    }

    private void handleCurrentLocBtn(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener((GoogleMap.OnMyLocationButtonClickListener) () -> {
            Location myLocation = mMap.getMyLocation();

            LatLng latLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f));
            return false;
        });

        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
    }
}
