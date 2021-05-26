package com.example.iwasto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class MainNavigation extends AppCompatActivity {

    private Double lati, longi;
    String lastknowlat,lastknowlong;
    Boolean is_granted = false, is_granted_again = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_navigation);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        checkAndAskForLocationPermission();
        lastknowlat = preferences.getString("lastknowlat", "");
        lastknowlong = preferences.getString("lastknowlong", "");
        Fragment fragment = new fragment_map();
        loadFragment(fragment);
        try {

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // TODO Auto-generated method stub
                    Toast.makeText(MainNavigation.this, "status chabged", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // TODO Auto-generated method stub
                    Toast.makeText(MainNavigation.this, "enabled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // TODO Auto-generated method stub
                    Toast.makeText(MainNavigation.this, "disabled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLocationChanged(Location location) {
                    // TODO Auto-generated method stub
                    lati = location.getLatitude();
                    longi = location.getLongitude();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("lastknowlat", String.valueOf(lati));
                    editor.putString("lastknowlong", String.valueOf(longi));
                    editor.apply();


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
            permissionHandler.requestStoragePermission((MainNavigation.this), "Location", "nearby");
            Toast.makeText(this, "exe "+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void checkNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainNavigation.this);
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


            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
            ) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MainNavigation.this,
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

                is_granted = true;
                is_granted_again = true;
                return true;
                // Permission has already been granted
            }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = new fragment_map();
            switch (item.getItemId()) {
                case R.id.home:

                    loadFragment(fragment);
                    return true;


            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {

        Fragment fragment = new fragment_map();
        loadFragment(fragment);
        super.onBackPressed();
    }

    public void loadFragment(Fragment fragment) {


        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).addToBackStack("my_fragment").commit();

    }

    static class BottomNavigationViewHelper {

        @SuppressLint("RestrictedApi")
        static void removeShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    item.setShifting(false);
                    // set once again checked value, so view will be updated
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
            } catch (IllegalAccessException e) {
                Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
            }
        }
    }

}