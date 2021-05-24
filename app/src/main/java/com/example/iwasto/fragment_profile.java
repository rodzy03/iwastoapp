package com.example.iwasto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

public class fragment_profile extends Fragment {

    Loading loading;
    TextView fullname, major_area, barangay;
    GoogleSignInClient mGoogleSignInClient;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final View rootView = inflater.inflate(R.layout.layout_profile, container, false);
        Button logout = rootView.findViewById(R.id.loginout_btn);
        loading = new Loading(getContext(), "Logging out..");
        fullname = rootView.findViewById(R.id.fullname);
        major_area = rootView.findViewById(R.id.major_area);
        barangay = rootView.findViewById(R.id.barangay);

        String pubkey = preferences.getString("pubkey", "");
        new fetchProfile(getContext()).execute(pubkey);

        // Events
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading.loadDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("pubkey", null);
                        editor.putString("userid", null);
                        editor.putString("fullname", null);
                        editor.putString("major_area", null);
                        editor.putString("barangay", null);
                        editor.apply();
                        loading.loadDialog.dismiss();
                        startActivity(new Intent(getContext(),Login.class));

                    }
                }, 2000);



            }
        });

        return  rootView;

    }


    public class fetchProfile extends AsyncTask<String, Void, String> {

        Context ctx;

        fetchProfile(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            String reference = EndPoints.base_url + EndPoints.get_profile;
            String pubkey = params[0];


            String data = "";
            try {
                URL url = new URL(reference);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("pubkey", "UTF-8") + "=" + URLEncoder.encode(pubkey, "UTF-8") ;


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

            if(result == "" || result == null) {
                Toast.makeText(ctx, "Error getting profile", Toast.LENGTH_LONG).show();
            }
            else {

                JSONObject reports = null;

                try {

                    try {
                        //Succcess
                        reports = new JSONObject(result);
                        JSONArray feedvalues = reports.getJSONArray("Results");
                        JSONObject feedarray = feedvalues.getJSONObject(0);

                        final String str_fullname = feedarray.getString("fullname");
                        final String str_major_area = feedarray.getString("major_area");
                        final String str_barangay = feedarray.getString("barangay");
                        fullname.setText(str_fullname);
                        major_area.setText(str_major_area);
                        barangay.setText(str_barangay);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception ex) {
                    //handle errors here , show user invalid input etc
                }
            }
        }


    }

}
