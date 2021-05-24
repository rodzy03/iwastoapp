package com.example.iwasto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class ForgotPassword extends AppCompatActivity {
    Button reset_btn;
    String social_email;
    EditText email;
    Loading loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forgot_password);
        loading = new Loading(ForgotPassword.this, "Please wait");

        reset_btn = findViewById(R.id.reset_btn);
        email = findViewById(R.id.email);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.loadDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        social_email = email.getText().toString();
                        Toast.makeText(ForgotPassword.this, social_email, Toast.LENGTH_SHORT).show();
                        new forgot_pass(getApplicationContext()).execute(social_email);
                        loading.loadDialog.dismiss();

                    }
                }, 1000);


            }
        });
    }

    public class forgot_pass extends AsyncTask<String, Void, String> {

        Context ctx;

        forgot_pass (Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            String reference = EndPoints.base_url + EndPoints.forgot_password;



            String data = "";
            try {
                URL url = new URL(reference);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("social_email", "UTF-8") + "=" + URLEncoder.encode("duterterodelb@gmail.com", "UTF-8");


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
                    JSONArray feedvalues = feedcontentvalues.getJSONArray("Results");
                    JSONObject feedarray = feedvalues.getJSONObject(0);
                    String response = feedarray.getString("response");
                    if (response != "false") {
                        Toast.makeText(ctx, "Please check your email.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ctx, "Account does not exists.", Toast.LENGTH_SHORT).show();
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


