package com.chatchat.chatchat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.chatchat.Adapter.LinkAdapter;
import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.Hotel;
import com.chatchat.chatchat.Model.Link;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Thong Pham on 25/08/2017.
 */

public class InfoActivity extends AppCompatActivity {
    private Context context;
    private TextView hotel_name;
    private TextView hotel_city;
    private TextView hotel_country;
    private TextView hotel_address;
    private TextView hotel_address2;
    private TextView hotel_description;
    private TextView hotel_link;
    private TextView hotel_tel;
    private ImageView hotel_image;
    private ImageView hotel_logo;

    private String gmap_coodinate = "";
    private String telephone = "";

    private Button menu;
    private Button profile;

    private Hotel hotel;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ac = getSupportActionBar();
        ac.hide();
        setContentView(R.layout.activity_infos);
        //set list
        context = getApplicationContext();
        SharedPreferences sh = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        username = sh.getString("Username", "");
        password = sh.getString("Password", "");
        Intent it = getIntent();
        String name = it.getStringExtra("name");
        String imglink = it.getStringExtra("link");
        String from = it.getStringExtra("from");
        String to = it.getStringExtra("to");
        String city = it.getStringExtra("city");
        String country = it.getStringExtra("country");
        String ho_username = it.getStringExtra("username");
        hotel = new Hotel(ho_username, name, city, country, from, to, imglink);

        hotel_name = (TextView) findViewById(R.id.textViewHotelName);
        hotel_city = (TextView) findViewById(R.id.textviewHotelCity);
        hotel_country = (TextView) findViewById(R.id.textviewHotelCountry);
        hotel_address = (TextView) findViewById(R.id.hotel_address);
        hotel_address2 = (TextView) findViewById(R.id.hotel_address2);
        hotel_description = (TextView) findViewById(R.id.hotel_description);
        hotel_link = (TextView) findViewById(R.id.hotel_link);
        hotel_tel = (TextView) findViewById(R.id.hotel_tel);
        hotel_image = (ImageView) findViewById(R.id.hotel_image);
        hotel_logo = (ImageView) findViewById(R.id.imageViewHotel);

        hotel_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!telephone.equals("")) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ActivityCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }
            }
        });

        hotel_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gmap_coodinate.equals("")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gmap_coodinate));
                    startActivity(intent);
                }
            }
        });

        Picasso.with(context).load(imglink).into(hotel_logo);
        if(imglink.contains("_logo.jpg")){
            imglink = imglink.replace("_logo.jpg",".jpg");
        }
        Picasso.with(context).load(imglink).into(hotel_image);
        hotel_name.setText(name);
        hotel_city.setText(city);
        hotel_country.setText(country);        

        profile = (Button) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                builder.setTitle(context.getResources().getString(R.string.logout_prompt));
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
                        sharedPreferences.edit().clear().commit();
                        Intent intent = new Intent(context, IntroActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        menu = (Button)findViewById(R.id.hotelbutton);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(Defined.checkInternetConnection(context)) {
            new API_DoGetHotelDetails(InfoActivity.this).execute();
        }
    }

    class API_DoGetHotelDetails extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String language;

        API_DoGetHotelDetails(Activity context)
        {
            this.context = context;
            pDialog= new ProgressDialog(context);
            language = Locale.getDefault().getLanguage();
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
                json.put("Password", password);
                json.put("Hotel", hotel.username);
                json.put("Language", language);
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
                url = new URL(Defined.API_getHotelInfo);
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
//            Log.e("Ket qua tra ve",jsonstr);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) pDialog.dismiss();
            try{
                JSONObject response = new JSONObject(jsonstr);
                String status = response.getString("result");
                if (status.equals("ok")) {
                    String str = response.getString("number");
                    int num = Integer.parseInt(str);
                    if(num > 0){
                        JSONObject ob = new JSONObject(response.getString("hotel"));
                        String lat = ob.getString("ho_latitude");
                        String lon = ob.getString("ho_longitude");
                        String add = ob.getString("ho_address");
                        String add2 = ob.getString("ho_address2");
                        String tel = ob.getString("ho_tel");
                        String link1 = ob.getString("hd_link");
                        String des = ob.getString("hd_description");
                        String city1 = ob.getString("ho_city");
                        String state1 = ob.getString("ho_state");
                        String zip1 = ob.getString("ho_zip");
                        String country1 = ob.getString("ho_country");

                        if(!link1.equals("")){
                            String text = "<a href=\"" + link1 + "\">" + link1 +"</a>";
                            hotel_link.setText(Html.fromHtml(text));
                            hotel_link.setMovementMethod(LinkMovementMethod.getInstance());
                        }

                        if(!add.equals("")){
                            if(!lat.equals("") && !lon.equals("")){
//                                gmap_coodinate = "http://maps.google.com/maps?daddr=" + lat + "," + lon;
                                gmap_coodinate = "geo:0,0?q=" + lat + "," + lon + "(" + hotel.name + ")";
                                if(!city1.equals("")) city1 = ", " + city1;
                                if(!zip1.equals("")) zip1 = ", " + zip1;
                                if(!state1.equals("")) state1 = ", " + state1;
                                if(!country1.equals("")) country1 = ", " + country1;
                                add += city1 + zip1 + state1 + country1;
                                String text = "<a href=\"#\">" + add +"</a>";
                                hotel_address.setText(Html.fromHtml(text));
                            }else{
                                hotel_address.setText(add);
                            }
                        }

                        if(!add2.equals("")){
                            hotel_address2.setText(add2);
                        }

                        if(!des.equals("")){
                            hotel_description.setText(des);
                        }

                        if(!tel.equals("") && !tel.equals("null")){
                            telephone = tel;
                            String text = "<a href=\"#\">" + tel +"</a>";
                            hotel_tel.setText(Html.fromHtml(text));
                        }
                    }
                } else {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException es){
                return;
            }
        }
    }


}
