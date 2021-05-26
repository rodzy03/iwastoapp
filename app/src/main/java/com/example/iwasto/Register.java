package com.example.iwasto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;


public class Register extends AppCompatActivity {

    Loading loading;
    Spinner city_spinner, barangay_spinner;
    Button submit_btn;
    EditText firstname, middlename, lastname, email, password;
    String str_firstname, str_middlename, str_lastname, str_email, major_area, barangay, str_password, check_verified, social_email ;
    String social_reg = "false";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loading = new Loading(  Register.this, "Processing");


        city_spinner = findViewById(R.id.city_spinner);
        barangay_spinner = findViewById(R.id.barangay_spinner);
        submit_btn = findViewById(R.id.submit_btn);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        social_email = getIntent().getStringExtra("social_email");

        if(social_email == "") {
            email.setFocusableInTouchMode(true);

        } else {
            social_reg = "true";
            email.setFocusable(false);
            email.setText(social_email);
        }


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.loadDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        firstname = findViewById(R.id.firstname);
                        middlename = findViewById(R.id.middlename);
                        lastname = findViewById(R.id.lastname);
                        email = findViewById(R.id.email);
                        str_firstname = firstname.getText().toString().trim();
                        str_middlename = middlename.getText().toString().trim();
                        str_lastname = lastname.getText().toString().trim();
                        str_email = email.getText().toString().trim();
                        str_password = password.getText().toString().trim();
                        new submit_registration(getApplicationContext()).execute(str_firstname, str_middlename, str_lastname, str_email, major_area, barangay, str_password, social_reg);

                    }
                }, 2000);

            }
        });

        new get_zipcodes(getApplicationContext()).execute();

    }
    public class submit_registration extends AsyncTask<String, Void, String> {

        Context ctx;

        submit_registration (Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            String reference = EndPoints.base_url + EndPoints.register;
            String firstname = params[0];
            String middlename = params[1];
            String lastname = params[2];
            String email = params[3];
            String major_area = params[4];
            String barangay = params[5];
            String password = params[6];
            String social_reg = params[7];
            String data = "";

            try {
                URL url = new URL(reference);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("firstname", "UTF-8") + "=" + URLEncoder.encode(firstname, "UTF-8")
                        + "&" + URLEncoder.encode("middlename", "UTF-8") + "=" + URLEncoder.encode(middlename, "UTF-8")
                        + "&" + URLEncoder.encode("lastname", "UTF-8") + "=" + URLEncoder.encode(lastname, "UTF-8")
                        + "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + URLEncoder.encode("major_area", "UTF-8") + "=" + URLEncoder.encode(major_area, "UTF-8")
                        + "&" + URLEncoder.encode("barangay", "UTF-8") + "=" + URLEncoder.encode(barangay, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
                        + "&" + URLEncoder.encode("social_reg", "UTF-8") + "=" + URLEncoder.encode(social_reg, "UTF-8");


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
        protected void onPostExecute(String result)
        {
            JSONObject feedcontentvalues = null;

            try
            {
                feedcontentvalues = new JSONObject(result);

                JSONArray feedvalues = feedcontentvalues.getJSONArray("Results");
                if(feedvalues.length() > 0) {


                    JSONObject feedarray = feedvalues.getJSONObject(0);
                    String output = feedarray.getString("pubkey");

                    if(output.equals("account exists")) {
                        loading.loadDialog.dismiss();
                        Toast.makeText(ctx, "Account exists", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    }

                }
                else {

                }


//                try
//                {
//
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Register.this,
//                            R.layout.items);
//                    JSONArray feedvalues = feedcontentvalues.getJSONArray("Results");
//                    for(int i =0; i< feedvalues.length(); i++ )
//                    {
//                        JSONObject feedarray = feedvalues.getJSONObject(i);
//                        final String output = feedarray.getString("city");
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class get_barangays extends AsyncTask<String, Void, String> {

        Context ctx;

        get_barangays (Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            String reference = EndPoints.base_url + EndPoints.get_barangays;
            String major_area = params[0];
//            String reliefid = params[1];


            String data = "";
            try {
                URL url = new URL(reference);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("major_area", "UTF-8") + "=" + URLEncoder.encode(major_area, "UTF-8");


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
            JSONObject feedcontentvalues = null;

            try {
                feedcontentvalues = new JSONObject(result);

                try {

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Register.this,
                            R.layout.items);
                    JSONArray feedvalues = feedcontentvalues.getJSONArray("Results");
                    for(int i =0; i< feedvalues.length(); i++ ) {
                        JSONObject feedarray = feedvalues.getJSONObject(i);
                        final String city = feedarray.getString("city");

                        adapter.add(city);
                        barangay_spinner.setAdapter(adapter);
                        barangay_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                barangay = barangay_spinner.getSelectedItem().toString();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // TODO Auto-generated method stub
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class get_zipcodes extends AsyncTask<String, Void, String> {

        Context ctx;

        get_zipcodes (Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            String reference = EndPoints.base_url + EndPoints.get_zipcodes;
//            String censusid = params[0];
//            String reliefid = params[1];


            String data = "";
            try {
                URL url = new URL(reference);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

//                OutputStream outputStream = httpURLConnection.getOutputStream();
//                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
//                data = URLEncoder.encode("ACCOUNT_ID", "UTF-8") + "=" + URLEncoder.encode("2", "UTF-8");
//
//
//                bufferedWriter.write(data);
//                bufferedWriter.flush();
//                bufferedWriter.close();
//                outputStream.close();

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
            JSONObject feedcontentvalues = null;

            try {
                feedcontentvalues = new JSONObject(result);

                try {

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Register.this,
                            R.layout.items);
                    JSONArray feedvalues = feedcontentvalues.getJSONArray("Results");
                    for(int i =0; i< feedvalues.length(); i++ ){
                        JSONObject feedarray = feedvalues.getJSONObject(i);
                        final String city = feedarray.getString("major_area");

                        adapter.add(city);
                        city_spinner.setAdapter(adapter);
                        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                major_area = city_spinner.getSelectedItem().toString();
                                new get_barangays(getApplicationContext()).execute(major_area);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // TODO Auto-generated method stub
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}