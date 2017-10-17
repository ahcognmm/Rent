package com.chatchat.chatchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.chatchat.Adapter.RequestAdapter;
import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.Forum;
import com.chatchat.chatchat.Model.Request;

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

public class RequestActivity extends AppCompatActivity {
    private Context context;

    private ListView listViewforum;
    private ArrayList<Request> requestList;
    private TextView emptyforum;

    private Button menu;
    private Button profile;
    private int nday = 1;

    private Button btn[];
    private ImageButton sendRequestButton;
    private TextView requestEditText;
    private String requestText = "";

    private Forum forum;
    private String username;
    private String password;
    private String ho_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ac = getSupportActionBar();
        ac.hide();
        setContentView(R.layout.activity_request);
        //set list
        context = getApplicationContext();
        SharedPreferences sh = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        username = sh.getString("Username","");
        password = sh.getString("Password","");
        Intent it = getIntent();
        String name = it.getStringExtra("name");
        String code = it.getStringExtra("code");
        ho_username = it.getStringExtra("ho_username");
        String startdate = it.getStringExtra("startdate");
        String enddate = it.getStringExtra("enddate");
        int order = 1;
        forum = new Forum(name,code,order);
        TextView tv = (TextView)findViewById(R.id.textViewRequesttitle);
        tv.setText(name);
        listViewforum = (ListView)findViewById(R.id.requestListView);
        emptyforum = (TextView)findViewById(R.id.emptyrequest);
        requestList = new ArrayList<Request>();
        listViewforum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request ho = requestList.get(position);
                Intent intent = new Intent(context,AnswerActivity.class);
                intent.putExtra("fo_id",ho.fo_id);
                intent.putExtra("fo_code",ho.fo_code);
                intent.putExtra("fo_text",ho.fo_text);
                intent.putExtra("fo_date",ho.fo_date);
                intent.putExtra("fo_days","" + ho.fo_days);
                intent.putExtra("n_answers","" + ho.n_answers);
                intent.putExtra("ho_username",ho_username);
                startActivity(intent);
            }
        });

        profile = (Button) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
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


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        RelativeLayout layo1 = (RelativeLayout)findViewById(R.id.requestheader);
        LinearLayout layo2 = (LinearLayout)findViewById(R.id.requesttitle);
        int hei = height - layo1.getHeight() - layo2.getHeight();
        ViewGroup.LayoutParams lp = listViewforum.getLayoutParams();
        lp.height = hei;
        listViewforum.setLayoutParams(lp);

        menu = (Button)findViewById(R.id.hotelbutton);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn = new Button[4];
        btn[0] = (Button) findViewById(R.id.btn1);
        btn[1] = (Button) findViewById(R.id.btn2);
        btn[2]= (Button) findViewById(R.id.btn3);
        btn[3]= (Button) findViewById(R.id.btn4);

        btn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nday != 1){
                    btn[nday - 1].setTextColor(Color.BLACK);
                    btn[0].setTextColor(Color.RED);
                    nday = 1;
                }
            }
        });

        btn[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nday != 2){
                    btn[nday - 1].setTextColor(Color.BLACK);
                    btn[1].setTextColor(Color.RED);
                    nday = 2;
                }
            }
        });

        btn[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nday != 3){
                    btn[nday - 1].setTextColor(Color.BLACK);
                    btn[2].setTextColor(Color.RED);
                    nday = 3;
                }
            }
        });

        btn[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nday != 4){
                    btn[nday - 1].setTextColor(Color.BLACK);
                    btn[3].setTextColor(Color.RED);
                    nday = 4;
                }
            }
        });
        requestEditText = (TextView)findViewById(R.id.requestEditText);
        sendRequestButton = (ImageButton)findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = requestEditText.getText().toString();
                if(!str.trim().equals("")){
                    requestText = str;
                    if(Defined.checkInternetConnection(context)) {
                        new API_DoGetSendRequest(RequestActivity.this).execute();
                    }
                }
            }
        });
        refreshData();
    }

    private void refreshData(){
        requestList.clear();
        if(Defined.checkInternetConnection(context)) {
            new API_DoGetListRequests(RequestActivity.this).execute();
        }else{
            emptyforum.setVisibility(TextView.VISIBLE);
            listViewforum.setVisibility(ListView.INVISIBLE);
        }
    }

    private void refreshList(){
        RequestAdapter lh = new RequestAdapter(RequestActivity.this, R.layout.request_list, requestList);
        listViewforum.setAdapter(lh);
    }

    class API_DoGetListRequests extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String language;

        API_DoGetListRequests(Activity context)
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
                json.put("ho_username", ho_username);
                json.put("fo_code", forum.code);
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
                url = new URL(Defined.API_getlistRequest);
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
                            String fo_text = inner.getString("fo_text");
                            String fo_id = inner.getString("fo_id");
                            String fo_date = inner.getString("fo_date");
                            String fo_days = inner.getString("fo_days");
                            String fo_nanswer = inner.getString("n_answers");
                            requestList.add(new Request(fo_id, forum.code, username, ho_username,
                                    fo_text, fo_date, Integer.parseInt(fo_days), Integer.parseInt(fo_nanswer)));
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

    class API_DoGetSendRequest extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String language;

        API_DoGetSendRequest(Activity context)
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
                json.put("ho_username", ho_username);
                json.put("fo_code", forum.code);
                json.put("requesttext",requestText);
                json.put("ndays",nday);
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
                url = new URL(Defined.API_sendRequest);
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
                    Toast.makeText(context, "Send request ok!", Toast.LENGTH_SHORT).show();
                    requestEditText.setText("");
                    refreshData();
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
