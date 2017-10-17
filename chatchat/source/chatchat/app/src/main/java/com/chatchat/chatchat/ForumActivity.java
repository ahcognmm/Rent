package com.chatchat.chatchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.chatchat.Adapter.ForumAdapter;
import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.Forum;
import com.chatchat.chatchat.Model.Hotel;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by Thong Pham on 25/08/2017.
 */

public class ForumActivity extends AppCompatActivity {
    private Context context;

    private ListView listViewforum;
    private ArrayList<Forum> forumList;
    private TextView emptyforum;

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
        setContentView(R.layout.activity_forum);
        //set list
        context = getApplicationContext();
        SharedPreferences sh = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        username = sh.getString("Username","");
        password = sh.getString("Password","");
        Intent it = getIntent();
        String name = it.getStringExtra("name");
        String imglink = it.getStringExtra("link");
        String from = it.getStringExtra("from");
        String to = it.getStringExtra("to");
        String city = it.getStringExtra("city");
        String country = it.getStringExtra("country");
        String ho_username = it.getStringExtra("username");
        hotel = new Hotel(ho_username,name,city,country,from,to,imglink);

        profile = (Button) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                AlertDialog.Builder builder = new AlertDialog.Builder(ForumActivity.this);
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


        listViewforum = (ListView)findViewById(R.id.forumListView);
        emptyforum = (TextView)findViewById(R.id.emptyforum);
        forumList = new ArrayList<Forum>();
        listViewforum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Forum fo = forumList.get(position);
                Intent intent = new Intent(context,RequestActivity.class);
                intent.putExtra("name",fo.name);
                intent.putExtra("code",fo.code);
                intent.putExtra("order",position);
                intent.putExtra("ho_username",hotel.username);
                intent.putExtra("startdate",hotel.startdate);
                intent.putExtra("enddate",hotel.enddate);
                startActivity(intent);
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
            new API_DoGetListForums(ForumActivity.this).execute();
        }else{
            emptyforum.setVisibility(TextView.VISIBLE);
            listViewforum.setVisibility(ListView.INVISIBLE);
        }
    }

    private void refreshList(){
        ForumAdapter lh = new ForumAdapter(ForumActivity.this, R.layout.forum_list, forumList);
        listViewforum.setAdapter(lh);
    }

    class API_DoGetListForums extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String language;

        API_DoGetListForums(Activity context)
        {
            this.context = context;
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
            language = Locale.getDefault().getLanguage();
//            if(!language.equals("it") && !language.equals("en")){
//                language = "en";
//            }
            try {
                json.put("ApiKey", Defined.API_KEY);
                json.put("Login", username);
                json.put("Password", password);
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
                url = new URL(Defined.API_getlistForum);
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
                    if(num == 0){
                        emptyforum.setVisibility(TextView.VISIBLE);
                        listViewforum.setVisibility(ListView.INVISIBLE);
                    }else{
                        emptyforum.setVisibility(TextView.INVISIBLE);
                        listViewforum.setVisibility(ListView.VISIBLE);
                        String str1 = response.getString("forums");
                        JSONArray arr = new JSONArray(str1);
                        for (int i = 0;i<arr.length();i++){
                            JSONObject inner = arr.getJSONObject(i);
                            String fo_code = inner.getString("fo_code");
                            String fo_text = inner.getString("fo_text");
                            forumList.add(new Forum(fo_text,fo_code,i + 1));
                        }
                        refreshList();
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
