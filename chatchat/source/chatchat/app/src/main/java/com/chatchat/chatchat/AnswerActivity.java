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
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.chatchat.Adapter.AnswerAdapter;
import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.Answer;
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

public class AnswerActivity extends AppCompatActivity {
    private Context context;

    private ListView listViewforum;
    private ArrayList<Answer> answerList;
    private TextView emptyforum;

    private Button menu;
    private Button profile;
    private int nday = 1;

    private Button btn[];
    private ImageButton sendRequestButton;
    private TextView requestEditText;
    private String requestText = "";
    private TextView requestBody;

    private Request req;
    private String username;
    private String password;
    private String ho_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ac = getSupportActionBar();
        ac.hide();
        setContentView(R.layout.activity_answer);
        //set list
        context = getApplicationContext();
        SharedPreferences sh = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        username = sh.getString("Username","");
        password = sh.getString("Password","");
        Intent it = getIntent();
        String fo_id = it.getStringExtra("fo_id");
        String fo_code = it.getStringExtra("fo_code");
        ho_username = it.getStringExtra("ho_username");
        String fo_text = it.getStringExtra("fo_text");
        String fo_date = it.getStringExtra("fo_date");
        String fo_days = it.getStringExtra("fo_days");
        String n_answers = it.getStringExtra("n_answers");

        req = new Request(fo_id,fo_code,username,ho_username,fo_text,fo_date,
                Integer.parseInt(fo_days),Integer.parseInt(n_answers));

        requestBody = (TextView)findViewById(R.id.textrequesttile);
        requestBody.setText(fo_text);

        listViewforum = (ListView)findViewById(R.id.requestListView);
        emptyforum = (TextView)findViewById(R.id.emptyrequest);
        answerList = new ArrayList<Answer>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        RelativeLayout layo1 = (RelativeLayout)findViewById(R.id.requestheader);
        LinearLayout layo2 = (LinearLayout)findViewById(R.id.requesttitle);
        int hei = height - layo1.getHeight() - layo2.getHeight();
        ViewGroup.LayoutParams lp = listViewforum.getLayoutParams();
        lp.height = hei;
        listViewforum.setLayoutParams(lp);

        profile = (Button) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
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
        requestEditText = (TextView)findViewById(R.id.requestEditText);
        sendRequestButton = (ImageButton)findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = requestEditText.getText().toString();
                if(!str.trim().equals("")){
                    requestText = str;
                    new API_DoSendAnswer(AnswerActivity.this).execute();
                }
            }
        });
        refreshData();
    }

    private void refreshData(){
        answerList.clear();
        new API_DoGetListAnswers(AnswerActivity.this).execute();
    }

    private void refreshList(){
        String str = username.replace("@","__");
        AnswerAdapter lh = new AnswerAdapter(AnswerActivity.this, R.layout.answer_list, answerList, str);
        listViewforum.setAdapter(lh);
    }

    class API_DoGetListAnswers extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String language;

        API_DoGetListAnswers(Activity context)
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
                json.put("fo_id", req.fo_id);
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
                url = new URL(Defined.API_getlistAnswer);
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
                            String fa_id = inner.getString("fa_id");
                            String fa_fo_id = inner.getString("fa_fo_id");
                            String fa_date = inner.getString("fa_date");
                            String fa_text = inner.getString("fa_text");
                            String fa_cu_username = inner.getString("fa_cu_username");
                            answerList.add(new Answer(fa_id, fa_cu_username, fa_fo_id, fa_text, fa_date));
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

    class API_DoSendAnswer extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String language;

        API_DoSendAnswer(Activity context)
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
                json.put("fo_id", req.fo_id);
                json.put("requesttext",requestText);
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
                url = new URL(Defined.API_sendAnswer);
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
                    Toast.makeText(context, "Send answer ok!", Toast.LENGTH_SHORT).show();
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
