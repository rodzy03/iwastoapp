package com.example.iwasto;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AllowAccess extends AppCompatActivity {
    Button allow_btn;
    Loading loading;
    private Double lati, longi;
    String lastknowlat,lastknowlong;
    Boolean is_granted = false, is_granted_again = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_access);

        loading = new Loading(AllowAccess.this, "Checking GPS");

        loading.loadDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.loadDialog.dismiss();

            }

        }, 1000);
        allow_btn = findViewById(R.id.allow_btn);


        allow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(AllowAccess.this, "is granted"+is_granted.toString(), Toast.LENGTH_SHORT).show();

                loading.loadDialog.show();
                if(is_granted_again) {
                    startActivity(new Intent(AllowAccess.this,MainNavigation.class));
                } else {
                    checkAndAskForLocationPermission();
                    allow_btn.setText("Please continue to access the app");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.loadDialog.dismiss();
                    }

                }, 1000);

            }
        });



        checkAndAskForLocationPermission();
        if(is_granted) {
            startActivity(new Intent(AllowAccess.this,MainNavigation.class));
        }

        try {

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            android.location.Location gpsLoc = (Location) locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            double lat = gpsLoc.getLatitude();
            double lng = gpsLoc.getLongitude();
            lastknowlat = String.valueOf(lat) ;
            lastknowlong = String.valueOf(lng);
//            Toast.makeText(AllowAccess.this, "lat and long :"+lastknowlat+ " "+lastknowlong, Toast.LENGTH_SHORT).show();
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // TODO Auto-generated method stub
                    Toast.makeText(AllowAccess.this, "status chabged", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // TODO Auto-generated method stub
                    Toast.makeText(AllowAccess.this, "enabled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // TODO Auto-generated method stub
                    Toast.makeText(AllowAccess.this, "disabled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLocationChanged(Location location) {
                    // TODO Auto-generated method stub
                    lati = location.getLatitude();
                    longi = location.getLongitude();

                    Toast.makeText(AllowAccess.this, "lat and long :"+lati+ " "+longi, Toast.LENGTH_SHORT).show();
                    lastknowlat = lati.toString();
                    lastknowlong = longi.toString();
                    double speed = location.getSpeed(); //spedd in meter/minute
                    speed = (speed * 3600) / 1000;      // speed in km/minute               Toast.makeText(GraphViews.this, "Current speed:" + location.getSpeed(),Toast.LENGTH_SHORT).show();


                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                checkNoGPS();
            }


        } catch (Exception ex) {
            PermissionHandler permissionHandler = new PermissionHandler();
            permissionHandler.requestStoragePermission((AllowAccess.this), "Location", "nearby");
        }


    }

    public void checkNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AllowAccess.this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public boolean checkAndAskForLocationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
            ) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(AllowAccess.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    Toast.makeText(this, "Permitted.", Toast.LENGTH_SHORT).show();
                    is_granted_again = true;

                } else {
                    Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            200);
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            200);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.


                }
                return false;
            } else {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                is_granted = true;
                is_granted_again = true;
                return true;
                // Permission has already been granted
            }
        } else
            return true;

    }
}