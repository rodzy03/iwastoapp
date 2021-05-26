package com.example.iwasto;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class fragment_map extends Fragment {

    private PermissionsManager permissionsManager;
    private MapView mapView;
    private static final String TAG = "fragment_dashboard";
    Context context;
    CardView cardholder;
    private Double lati, longi;
    public String saveuser;
    private Loading loading;
    String SelectedCase="recovered",casetype;
    TextView tcases,tdeaths, trecoveries, tfatal, treceovrate, tlastupdate;
    Button select_routes_btn;
    CardView routes_card;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.access_token));
        final View rootView = inflater.inflate(R.layout.layout_fragment_dashboard, container, false);
        mapView = rootView.findViewById(R.id.mapView);
        loading = new Loading((Activity)getContext(),"Please wait");
        loading.loadDialog.show();
        context = getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        casetype = preferences.getString("casetype", "");
        SelectedCase = casetype;
//        tcases = rootView.findViewById(R.id.cases);
//        tdeaths = rootView.findViewById(R.id.deaths);
//        treceovrate = rootView.findViewById(R.id.recovery_rate);
//        trecoveries = rootView.findViewById(R.id.recoveries);
//        tfatal = rootView.findViewById(R.id.fatality_rate);
//        tlastupdate = rootView.findViewById(R.id.last_update);
        select_routes_btn = rootView.findViewById(R.id.select_routes_btn);
        routes_card = rootView.findViewById(R.id.routes_card);

        select_routes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(routes_card.getVisibility()==View.GONE) {
                    routes_card.setVisibility(View.VISIBLE);
                }
                else {
                    routes_card.setVisibility(View.GONE);
                }

            }
        });
        new getCases(context).execute();




        return rootView;

    }


    public class getCases extends AsyncTask<String, Void, String> {
        AlertDialog alertDialog;
        Context ctx;

        getCases(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            String reference = "https://tech4good.ph/"+EndPoints.getPHCases;

            String data = "";
            try {
                URL url = new URL(reference);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));


                data  = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("Died", "UTF-8");


                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("casesfromscrap", result);
            JSONObject feedcontentvalues = null;

            try {
                feedcontentvalues = new JSONObject(result);

                try {
                    final JSONArray feedvalues = feedcontentvalues.getJSONArray("PHCases");
                    final Integer length = feedvalues.length();

                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final MapboxMap mapboxMap) {
                            mapboxMap.setMaxZoomPreference(18);
                            for (int i = 0; i < length; i++) {
                                try {
                                    JSONObject feedarray = feedvalues.getJSONObject(i);

                                    final String rml_lat = feedarray.getString("latitude");
                                    final String rml_long = feedarray.getString("longhitude");
                                    final String case_no = feedarray.getString("caseno");
                                    final String sex = feedarray.getString("sex");
                                    final String age = feedarray.getString("age");
                                    final String date_reported = feedarray.getString("date_reported");
                                    String status = feedarray.getString("removal_type");
                                    if(!rml_lat.equals("null")&&!rml_long.equals("null")) {
//                                        MarkerOptions options = new MarkerOptions();
//                                        options.title("CASE NO: " + case_no.toUpperCase());
//                                        IconFactory iconFactory =  IconFactory.getInstance(getContext());
//                                        Icon icon = iconFactory.fromResource(R.drawable.icon_death);
//                                        options.position(new LatLng(Double.parseDouble(rml_lat), Double.parseDouble(rml_long)));
//                                        options.icon(icon);
//                                        options.setSnippet("Gender: "+sex+"\nAge: "+age+"\nDate Reported: "+date_reported);

//                                        mapboxMap.addMarker(options);
                                        if(i==feedarray.length()-1) {
                                            Point originPosition = Point.fromLngLat(121.00808889312924, 14.655575708308534);
                                            Point dstPosition = Point.fromLngLat(121.09597951394174, 14.641292686431356);
//
//                                            options = new MarkerOptions();
//                                            options.title("Source");
//                                            options.position(new LatLng(Double.parseDouble(rml_lat), Double.parseDouble(rml_long)));

//                                            MarkerOptions options1 = new MarkerOptions();
//                                            options1.title("Destination");
//                                            options1.position(new LatLng(14.71132057676174, 121.09342923771496));

                                            NavigationRoute.builder().alternatives(true);
                                            NavigationRoute.builder()
                                                    .accessToken(Mapbox.getAccessToken())
                                                    .origin(originPosition)
                                                    .destination(dstPosition)
                                                    .build()
                                                    .getRoute(new Callback<DirectionsResponse>() {
                                                        @Override
                                                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse>
                                                                response) {
                                                            if (response.body() == null) {
                                                                return;
                                                            } else if (response.body().routes().size() == 0) {
                                                                return;
                                                            }


                                                            NavigationMapRoute navigationMapRoute;
                                                            DirectionsRoute currentRoute = response.body().routes().get(0);
                                                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                                                            navigationMapRoute.showAlternativeRoutes(true);
                                                            navigationMapRoute.addRoute(currentRoute);


                                                        }

                                                        @Override
                                                        public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                                                            Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                } catch (JSONException ex) {
                                    Log.d("json-exep", ex.toString());
                                }

                            }
                            loading.loadDialog.dismiss();
                            checkAndAskForLocationPermission();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            saveuser = preferences.getString("session", "");
                            String lastknowlat = preferences.getString("lastknowlat", "");
                            String lastknowlong = preferences.getString("lastknowlong", "");
                            if (!lastknowlat.equals("") && !lastknowlong.equals("")) {

                                final LatLng location = new LatLng(Double.parseDouble(lastknowlat), Double.parseDouble(lastknowlong));
                                mapboxMap.addPolyline(new PolylineOptions()
                                        .add(location)
                                        .color(Color.parseColor("#38afea"))
                                        .width(5));
                                mapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(MapboxMap mapboxMap) {
                                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                                .target(location)
                                                .zoom(15)
                                                .build());
                                    }
                                });
                            }

                            try {
                                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("lastknowlat", String.valueOf(lati));
                                        editor.putString("lastknowlong", String.valueOf(longi));
                                        editor.apply();
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
                                permissionHandler.requestStoragePermission(((Activity) context), "Location", "nearby");
                            }


                        }

                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }


    public void checkNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    Toast.makeText(getContext(), "Permitted.", Toast.LENGTH_SHORT).show();

                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            200);
                    ActivityCompat.requestPermissions(getActivity(),
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




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();

        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();

    }


}


