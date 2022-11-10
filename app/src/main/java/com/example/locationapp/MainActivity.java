package com.example.locationapp;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PatternMatcher;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public  static final int DEFAULT_INTERVAL = 30;
    public static final int FAST_INTERVAL = 5;
    private Button button_getLocation;
    private EditText editText_lastPos, editText_currPos, editText_distance;
    private LocationRequest locationRequest;
    private int count = 0;
    private double lastPositionLa, lastPositionLong;

    boolean updateOn = false;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_getLocation = findViewById(R.id.button_getLocation);
        editText_lastPos = findViewById(R.id.editText_lastPos);
        editText_currPos = findViewById(R.id.editText_currPos);
        editText_distance = findViewById(R.id.editText_distance);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        button_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    turnGPS();
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest,
                            new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() >0){
                                        int index = locationResult.getLocations().size() -1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        if (count == 0){
                                            editText_lastPos.setText(Html.fromHtml(
                                                    "<font color = '#6200EE'><b> Latitude and Longitude: <b><br></font>"
                                                            + latitude + longitude));
                                            editText_currPos.setText(Html.fromHtml(
                                                    "<font color = '#6200EE'><b> Latitude and Longitude: <b><br></font>"
                                                            + latitude + longitude));
                                            editText_distance.setText("0ft");
                                            lastPositionLa = latitude;
                                            lastPositionLong = longitude;
                                            count += 1;
                                        }else{
                                            String tempStr = editText_currPos.getText().toString();
                                            editText_lastPos.setText(tempStr);
                                            editText_currPos.setText(Html.fromHtml(
                                                    "<font color = '#6200EE'><b> Latitude and Longitude: <b><br></font>"
                                                            + latitude + longitude));
                                            double distance = pow(abs(latitude-lastPositionLa)*364000, 2) + pow(abs(longitude-lastPositionLong) *288200, 2);
                                            distance = sqrt(distance);
                                            editText_distance.setText(Double.toString(distance) + "ft");
                                            lastPositionLa = latitude;
                                            lastPositionLong = longitude;

                                        }

                                    }
                                }
                            }, Looper.getMainLooper());
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }


            }
        });

    }
    //not used function
    private void getLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null){
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);
                        if (count == 0){
                            editText_lastPos.setText(Html.fromHtml(
                                    "<font color = '#6200EE'><b> Latitude and Longitude: <b><br></font>"
                                            + addresses.get(0).getLatitude() + addresses.get(0).getLongitude()));
                            editText_currPos.setText(Html.fromHtml(
                                    "<font color = '#6200EE'><b> Latitude and Longitude: <b><br></font>"
                                            + addresses.get(0).getLatitude() + addresses.get(0).getLongitude()));
                            editText_distance.setText("0");
                            lastPositionLa = addresses.get(0).getLatitude();
                            lastPositionLong = addresses.get(0).getLongitude();
                            count += 1;
                        }else{
                            String tempStr = editText_currPos.getText().toString();
                            editText_lastPos.setText(tempStr);
                            editText_currPos.setText(Html.fromHtml(
                                    "<font color = '#6200EE'><b> Latitude and Longitude: <b><br></font>"
                                            + addresses.get(0).getLatitude() + addresses.get(0).getLongitude()));
                            double distance = pow(abs(addresses.get(0).getLatitude()-lastPositionLa), 2) + pow(abs(addresses.get(0).getLongitude()-lastPositionLong), 2);
                            distance = sqrt(distance);
                            editText_distance.setText(Double.toString(distance));
                            lastPositionLa = addresses.get(0).getLatitude();
                            lastPositionLong = addresses.get(0).getLongitude();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void turnGPS(){

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(MainActivity.this,2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

}