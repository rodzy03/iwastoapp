package com.example.iwasto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuNavigation extends AppCompatActivity {

    public static BottomNavigationView navigation;
    TextView navigation_title;
    private Double lati, longi;
    String lastknowlat,lastknowlong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_navigation2);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pubkey = preferences.getString("pubkey", "");

        //navigation = findViewById(R.id.navigation);
        navigation_title = findViewById(R.id.navigation_title);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(1);
        MenuItem item = navigation.getMenu().findItem(R.id.profile);
        item.setCheckable(true);
        item.setChecked(true);


        checkAndAskForLocationPermission();


        try {
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProviderEnabled(String provider) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProviderDisabled(String provider) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onLocationChanged(Location location) {
                    // TODO Auto-generated method stub
                    lati = location.getLatitude();
                    longi = location.getLongitude();
//                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString("lastknowlat", String.valueOf(lati));
//                    editor.putString("lastknowlong", String.valueOf(longi));
//                    editor.apply();
//
                    Toast.makeText(MenuNavigation.this, lati+ " "+longi, Toast.LENGTH_SHORT).show();
                    lastknowlat = lati.toString();
                    lastknowlong = longi.toString();
                    double speed = location.getSpeed(); //spedd in meter/minute
                    speed = (speed * 3600) / 1000;      // speed in km/minute               Toast.makeText(GraphViews.this, "Current speed:" + location.getSpeed(),Toast.LENGTH_SHORT).show();

                    Toast.makeText(MenuNavigation.this, lastknowlat, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MenuNavigation.this, lastknowlong, Toast.LENGTH_SHORT).show();
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                checkNoGPS();
            }


        } catch (Exception ex) {
            PermissionHandler permissionHandler = new PermissionHandler();
            permissionHandler.requestStoragePermission((MenuNavigation.this), "Location", "nearby");
        }

    }

    public void checkNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MenuNavigation.this);
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(MenuNavigation.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    Toast.makeText(getApplicationContext(), "Permitted.", Toast.LENGTH_SHORT).show();

                } else {
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
                return true;
                // Permission has already been granted
            }
        } else return true;

    }
    int count = 0;
    @Override
    public void onBackPressed() {

        count = count + 1;
        fragment_profile fragment_profile  = new fragment_profile();
        loadFragment(fragment_profile);
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        if(count == 2) {
            finish();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
//            switch (item.getItemId()) {
//                case R.id.hom:
//                    fragment_profile fragment_resident  = new fragment_profile();
//                    loadFragment(fragment_resident);
//                    navigation_title.setText("IWasto — Profile");
//                    return true;
//                case R.id.users:
//                    //fragment_users fragment_users  = new fragment_users();
//                    //loadFragment(fragment_users);
////                    navigation_title.setText("Ayuda — Residents");
//                    return true;
//
//
//
//            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack("my_fragment").commit();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);

    }
}