package com.example.meteoapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText input;
    private Button btn;
    private Button btn_location;
    private String url_base = "https://www.prevision-meteo.ch/services/json/";
    private String ville = "";
    private String url = "";
    private Intent intent;
    private double latitude, longitude;
    private LocationManager locationManager;

    int FINE_LOCATION_RC = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = findViewById(R.id.input);
        btn = findViewById(R.id.btn_load);
        btn_location = findViewById(R.id.btn_location);
        intent = new Intent(this, WeatherActivity.class);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        input.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!input.getText().toString().equals("")) {
                    btn.setEnabled(true);
                } else {
                    btn.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ville = input.getText().toString();
                url = url_base + ville;
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("gps", "!= 0");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, FINE_LOCATION_RC);
                    return;
                }
                getCurrentLocation();
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_RC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else Log.d("gps", "deny");
        }
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        String provider = locationManager.getAllProviders().get(0);
        Location location = locationManager.getLastKnownLocation(provider);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            ville = addresses.get(0).getLocality();
            Log.d("gps", ville);
        } catch (IOException e) {
            e.printStackTrace();
        }
        url = url_base + ville;

        intent.putExtra("url", url);
        startActivity(intent);
    }
}
