package com.example.locationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public  static final int DEFAULT_INTERVAL = 30;
    public static final int FAST_INTERVAL = 5;
    private Button button_getLocation;
    private EditText editText_lastPos, editText_currPos, editText_distance;
    LocationRequest locationRequest;
    private int count = 0;

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


        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        button_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }


            }
        });

    }
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
                            count += 1;
                        }else{
                            String tempStr = editText_currPos.getText().toString();
                            editText_lastPos.setText(tempStr);
                            editText_currPos.setText(Html.fromHtml(
                                    "<font color = '#6200EE'><b> Latitude and Longitude: <b><br></font>"
                                            + addresses.get(0).getLatitude() + addresses.get(0).getLongitude()));
                            int distance = 0;
                            editText_distance.setText("0");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}