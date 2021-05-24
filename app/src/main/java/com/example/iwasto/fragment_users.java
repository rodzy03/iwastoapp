package com.example.iwasto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class fragment_users extends Fragment {

    Loading loading;
    TextView fullname;
    Button signoutbutton;

    public static List<users_constructor> users_constructors_var = new ArrayList<>();
    public users_adapter users_adapter_var;
    RecyclerView rcy_users;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.layout_users, container, false);

        rcy_users = rootView.findViewById(R.id.users);
        rcy_users.setLayoutManager(new LinearLayoutManager(getContext()));
        new getCencusRegistrants(getContext()).execute();
        return  rootView;


    }

    public class getCencusRegistrants extends AsyncTask<String, Void, String> {
        AlertDialog alertDialog;
        Context ctx;
        getCencusRegistrants(Context ctx){
            this.ctx = ctx;
        }
        @Override
        protected String doInBackground(String... params) {

            String reference = EndPoints.base_url + EndPoints.get_all;

            String data ="";
            try {
                URL url = new URL(reference);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    response+= line;
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
            return  null;
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
            Log.d("cencus",result);
            JSONObject feedcontentvalues = null;

            try {
                feedcontentvalues = new JSONObject(result);

                try {
                    JSONArray feedvalues = feedcontentvalues.getJSONArray("Results");
                    users_constructors_var.clear();
                    Integer length = feedvalues.length();

                    for (int i=0; i <length; i++)
                    {
                        JSONObject feedarray = feedvalues.getJSONObject(i);
                        String rrl_reliefcontent = feedarray.getString("user_id");
                        String rrl_unitcount= feedarray.getString("lastname");
                        String rrl_datecreated = feedarray.getString("major_area");
                        String rrl_image = feedarray.getString("barangay");
                        users_constructor current1 = new users_constructor(rrl_reliefcontent,
                                rrl_unitcount,rrl_datecreated,rrl_image);
                        users_constructors_var.add(current1);

                    }
                    String empty = " ";
                    if(length == 0) {
//                        no_result.setVisibility(View.VISIBLE);
                        rcy_users.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                users_adapter_var = new users_adapter(getContext(), users_constructors_var);
                rcy_users.setAdapter(null);
                rcy_users.setAdapter(users_adapter_var);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

}
