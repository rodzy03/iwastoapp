package com.example.iwasto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
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

public class Login extends AppCompatActivity {

    Button go_to_register, login_btn, login_gmail_btn ;
    LinearLayout forgot_pass;
    Loading loading;
    EditText email, password;
    String str_email, str_password, social_email;
    Boolean login_gmail = false;
    GoogleSignInClient mGoogleSignInClient;
    private  static int RC_SIGN_IN = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loading = new Loading(Login.this, "Signing in");

        go_to_register = findViewById(R.id.go_to_register);
        login_btn = findViewById(R.id.login_btn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        forgot_pass = findViewById(R.id.forgot_pass);
        login_gmail_btn = findViewById(R.id.login_gmail_btn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        login_gmail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        go_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, Register.class));
            }
        });

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.loadDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        str_email = email.getText().toString();
                        str_password = password.getText().toString();
                        new checkLogin(getApplicationContext()).execute(str_email, str_password,"normal");

                    }
                }, 1000);
            }
        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loading.loadDialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    handleSignInResult(task);

                }
            }, 1000);

        }

    }
    //String personName = acct.getDisplayName();
    //String personGivenName = acct.getGivenName();
    //String personFamilyName = acct.getFamilyName();
    //String personId = acct.getId();
    //Uri personPhoto = acct.getPhotoUrl();

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {

                String personEmail = acct.getEmail();

                login_gmail = true;
                social_email = personEmail;
                new checkLogin(getApplicationContext()).execute(personEmail, "","gmail");
            }
            else {
                Toast.makeText(this, "No account found", Toast.LENGTH_SHORT).show();
                loading.loadDialog.dismiss();

            }


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            loading.loadDialog.dismiss();
            Log.d("Message",e.toString());
        }
    }
    public class checkLogin extends AsyncTask<String, Void, String> {

        Context ctx;

        checkLogin(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            String reference = EndPoints.base_url + EndPoints.login;
            String username = params[0];
            String password = params[1];
            String login_type = params[2];
            String data = "";
            try {
                URL url = new URL(reference);
                HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("login_type", "UTF-8") + "=" + URLEncoder.encode(login_type, "UTF-8");


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

            JSONObject reports = null;

            try {

                try {
                    reports = new JSONObject(result);
                    JSONArray feedvalues = reports.getJSONArray("Results");
                    if(feedvalues.length() != 0) {
                        JSONObject feedarray = feedvalues.getJSONObject(0);
                        String is_verified = feedarray.getString("email_verified_at");
                        final String pubkey = feedarray.getString("pubkey");

                        loading.loadDialog.dismiss();

                        if(is_verified == "null") {
                            Toast.makeText(ctx, "Please verify You're  Account.", Toast.LENGTH_LONG).show();
                            signOut();

                        } else {


                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("pubkey", pubkey);
                            editor.apply();
                            startActivity(new Intent(Login.this, AllowAccess.class));
                            finish();

                        }

                    } else {
                        loading.loadDialog.dismiss();
                        Toast.makeText(ctx, "You're not registered as IWASTO Representative", Toast.LENGTH_LONG).show();
                        if(login_gmail) {
                            checkGmail();
                        }
                    }
//


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx, "here"+e.toString(), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception ex) {
                //handle errors here , show user invalid input etc
                Toast.makeText(ctx, ex.toString(), Toast.LENGTH_SHORT).show();
            }

        }

        public void checkGmail() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage("You dont have an account, do you want to register?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(getApplicationContext(),Register.class).putExtra("social_email",social_email));
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


    }
}

