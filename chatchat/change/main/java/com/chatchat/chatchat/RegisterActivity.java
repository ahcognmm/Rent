package com.chatchat.chatchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chatchat.chatchat.Defined.Defined;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
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
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by Thong Pham on 20/07/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText user;
    private EditText pass;
    private EditText firstname;
    private EditText lastname;
    private Button btnLogin;
    private Button btnRegister;

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ac = getSupportActionBar();
        ac.hide();
        context = this.getApplicationContext();
        setContentView(R.layout.activity_register);
        firstname = (EditText)findViewById(R.id.firstname);
        lastname = (EditText)findViewById(R.id.lastname);
        user = (EditText)findViewById(R.id.userlogin);
        pass = (EditText)findViewById(R.id.passwordlogin);
        btnRegister = (Button)findViewById(R.id.btnsignup);
        btnLogin = (Button)findViewById(R.id.btnsignin);

        if(Build.VERSION.SDK_INT < 21){
            Drawable dr1 = firstname.getBackground();
            dr1.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            Drawable dr2 = lastname.getBackground();
            dr2.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            Drawable dr3 = user.getBackground();
            dr3.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            Drawable dr4 = pass.getBackground();
            dr4.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            if(Build.VERSION.SDK_INT > 16){
                firstname.setBackground(dr1);
                lastname.setBackground(dr2);
                user.setBackground(dr3);
                pass.setBackground(dr4);
            }else{
                user.setBackgroundDrawable(dr1);
                pass.setBackgroundDrawable(dr2);
                firstname.setBackgroundDrawable(dr3);
                lastname.setBackgroundDrawable(dr4);
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname1 = firstname.getText().toString();
                String lastname1 = lastname.getText().toString();
                String us1 = user.getText().toString();
                String pa1 = pass.getText().toString();
                if(firstname1.equals("")) {
                    Toast.makeText(context,R.string.fillfirstname,Toast.LENGTH_SHORT).show();
                }else{
                    if(us1.equals("")){
                        Toast.makeText(context,R.string.fillemail,Toast.LENGTH_SHORT).show();
                    }else{
                        if(pa1.equals("")){
                            Toast.makeText(context,R.string.fillpass,Toast.LENGTH_SHORT).show();
                        }else{
                            if(Defined.checkInternetConnection(context)) {
                                new API_DoRegister(RegisterActivity.this, us1, pa1, firstname1, lastname1).execute();
                            }
                        }
                    }
                }
            }
        });
    }

    public void runService(){
        Intent intent = new Intent(getApplicationContext(),MyService.class);
        startService(intent);
    }

    class API_DoRegister extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String username;
        private String firstname;
        private String lastname;
        private String pass;
        private String language;

        API_DoRegister(Activity context, String username, String pass, String fname, String lname)
        {
            this.context = context;
            this.username = username;
            this.pass = pass;
            this.firstname = fname;
            this.lastname = lname;
            language = Locale.getDefault().getLanguage();
//            if(!language.equals("it") && !language.equals("en")){
//                language = "en";
//            }
            pDialog= new ProgressDialog(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog.setMessage("Please wait for requesting...");
            //  pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Void doInBackground(String... args) {
            JSONObject json = new JSONObject();
            try {
                json.put("ApiKey", Defined.API_KEY);
                json.put("Login", username);
                json.put("Password", pass);
                json.put("firstname", firstname);
                json.put("lastname", lastname);
                json.put("language", language);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            String requestBody = null;
            try {
                requestBody = Defined.getPostDataString(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            URL url = null;
            try {
                url = new URL(Defined.API_register);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" +
                        Integer.toString(requestBody.getBytes().length));
                urlConnection.setRequestProperty("Content-Language", "en-US");
                urlConnection.setUseCaches (false);

                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                try {
                    writer.write(requestBody);
                    writer.flush();
                    writer.close();
                    outputStream.close();
                    InputStream inputStream;

                    // get stream
                    if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                        inputStream = urlConnection.getInputStream();
                    } else {
                        inputStream = urlConnection.getErrorStream();
                    }
                    // parse stream
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String temp, response = "";
                    while ((temp = bufferedReader.readLine()) != null) {
                        response += temp;
                    }
                    jsonstr = response;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) pDialog.dismiss();
            try{
                JSONObject response = new JSONObject(jsonstr);
                String status = response.getString("result");
                if (status.equals("ok")) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Username", username);
                    editor.putString("Password", pass);
                    editor.commit();
                    Intent intent = new Intent(context,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (status.equals("fail")) {
                        String error = response.getString("Error");
                        if(error.equals("Username is existed")){
                            error = getString(R.string.username_existed);
                        }
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (JSONException es){
                return;
            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(context,IntroActivity.class);
        startActivity(intent);
        finish();
    }

}
