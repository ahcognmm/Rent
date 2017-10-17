package com.chatchat.chatchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class LoginActivity extends AppCompatActivity {

    private EditText user;
    private EditText pass;
    private Button btnLogin;
    private Button btnRegister;
    private Context context;
    private Button btnForgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ac = getSupportActionBar();
        ac.hide();
        setContentView(R.layout.activity_login);
        context = this.getApplicationContext();
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark, null));
        }
        user = (EditText)findViewById(R.id.userlogin);
        pass = (EditText)findViewById(R.id.passwordlogin);
        btnLogin = (Button)findViewById(R.id.btnlogin);
        btnRegister = (Button)findViewById(R.id.btnregister);
        btnForgetPass = (Button)findViewById(R.id.btn_forgetpass);
        btnForgetPass.setPaintFlags(btnForgetPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnRegister.setPaintFlags(btnRegister.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if(Build.VERSION.SDK_INT < 21){
            Drawable dr1 = user.getBackground();
            dr1.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            Drawable dr2 = pass.getBackground();
            dr2.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            if(Build.VERSION.SDK_INT > 16){
                user.setBackground(dr1);
                pass.setBackground(dr2);
            }else{
                user.setBackgroundDrawable(dr1);
                pass.setBackgroundDrawable(dr2);
            }
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context,RegisterActivity.class);
//                startActivity(intent);
            }
        });

        btnForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String us1 = user.getText().toString();
                String pa1 = pass.getText().toString();

                if(us1.equals("")) {
                    Toast.makeText(context,R.string.fillemail,Toast.LENGTH_SHORT).show();
                }else{
                    if(!us1.contains("@")) {
                        Toast.makeText(context, R.string.fillemail1, Toast.LENGTH_SHORT).show();
                    }else{
                        if(pa1.equals("")){
                            Toast.makeText(context,R.string.fillpass,Toast.LENGTH_SHORT).show();
                        }else{
                            if(Defined.checkInternetConnection(context)) {
                                SharedPreferences sharedPreferences = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Username", us1);
                                editor.putString("Password", pa1);
                                editor.commit();
                                new API_DoLogin(LoginActivity.this, us1, pa1).execute();
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }


    class API_DoLogin extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String us;
        private String pa;

        API_DoLogin(Activity context, String us, String pa)
        {
            this.context = context;
            this.us = us;
            this.pa = pa;
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
                json.put("Login", us);
                json.put("Password", pa);
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
                url = new URL(Defined.API_Login);
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
                    editor.putString("Username", us);
                    editor.putString("Password", pa);
                    editor.commit();
                    Intent intent = new Intent(context,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (status.equals("fail")) {
                        String error = response.getString("Error");
                        if(error.equals("Wrong username or password")){
                            error = getString(R.string.wrong_us_pass);
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

}
