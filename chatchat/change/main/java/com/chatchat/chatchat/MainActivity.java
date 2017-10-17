package com.chatchat.chatchat;
/**
 * Created by Khushvinders on 15-Nov-16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.chatchat.Adapter.ListHotelAdapter;
import com.chatchat.chatchat.DB.DBHandle;
import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.ChatMessage;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private Context context;
    private String username;
    private String pass;

    private Button profile;

    private List<Hotel> listhotels;
    private TextView emptylistView;
    private ListView listViewHotels;
    private RelativeLayout rl;
    private ListHotelAdapter lh;

    private MyService mService;
    private boolean mBounded;
    private Intent intentService;
    private DBHandle db;

    private int heightImage;

    private final ServiceConnection mConnection = new ServiceConnection() {

        @SuppressWarnings("unchecked")
        @Override
        public void onServiceConnected(final ComponentName name,
                                       final IBinder service) {
            mService = ((LocalBinder<MyService>) service).getService();
            mBounded = true;
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            mBounded = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ac = getSupportActionBar();
        ac.hide();
        setContentView(R.layout.activity_main);
        //set list
        context = getApplicationContext();
        db = new DBHandle(context);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark, null));
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("Username","");
        pass = sharedPreferences.getString("Password","");
        emptylistView = (TextView)findViewById(R.id.textempty);
        listViewHotels = (ListView)findViewById(R.id.listHotels);
        rl = (RelativeLayout)findViewById(R.id.rl_loading);
        listhotels = new ArrayList<Hotel>();

        int density = (int) (getResources().getDisplayMetrics().density * 160f);
        int x_co = getResources().getDisplayMetrics().widthPixels;
        int x = x_co - 20 * density;
        heightImage = x * 393 / 710;

        lh = new ListHotelAdapter(
                MainActivity.this,
                R.layout.hotel_list,
                listhotels,
                heightImage
        );
        listViewHotels.setAdapter(lh);

//        ChatActivity.hashmap = new HashMap<String, ArrayList<ChatMessage>>();
        profile = (Button) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

        listViewHotels.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Hotel ho = listhotels.get(position);
                Intent intent = new Intent(context,HotelDetailActivity.class);
                intent.putExtra("name",ho.name);
                intent.putExtra("link",ho.imagelink);
                intent.putExtra("link_large",ho.imagelink_large);
                intent.putExtra("from",ho.startdate);
                intent.putExtra("to",ho.enddate);
                intent.putExtra("city",ho.city);
                intent.putExtra("country",ho.country);
                intent.putExtra("username",ho.username);
                intent.putExtra("zip",ho.zip);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date endTime = null, startTime = null;
                try {
                    endTime = sdf.parse(ho.enddate);
                    startTime = sdf.parse(ho.startdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                long day = (endTime.getTime() - startTime.getTime()) / (24 * 60 * 60 * 1000);
                String[] str = ho.startdate.split("/");
                int startmonth = Integer.parseInt(str[1]);
                String startday = str[0];
                str = ho.enddate.split("/");
                int endmonth = Integer.parseInt(str[1]);
                String endday = str[0];
                intent.putExtra("startdate",getDate(startmonth, startday));
                intent.putExtra("enddate",getDate(endmonth, endday));
                intent.putExtra("startday",getDay((String) DateFormat.format("EEEE", startTime)));
                intent.putExtra("endday",getDay((String) DateFormat.format("EEEE", endTime)));
                intent.putExtra("ndays",""+day);
                startActivity(intent);
            }
        });
//        lha = new ListHotelAdapter(MainActivity.this, R.layout.hotel_list,listhotels);
        if(Defined.checkInternetConnection(context)){
            new API_DoGetListHotels(MainActivity.this).execute();
        }else{
            emptylistView.setVisibility(TextView.VISIBLE);
            listViewHotels.setVisibility(ListView.GONE);
            rl.setVisibility(RelativeLayout.GONE);
        }
    }

    private void refreshList(){
        Log.e("Here","abd");
        lh.notifyDataSetChanged();
    }

    class API_DoGetListHotels extends AsyncTask<String, Void, Void> {
        private Activity context;
        //        private AlertDialog pDialog;
        private String jsonstr;

        API_DoGetListHotels(Activity context)
        {
            this.context = context;
//            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View dialogview = inflater.inflate(R.layout.progess_dialog, null);
//            dialog.setView(dialogview);
//            dialog.setCancelable(false);
//            pDialog = dialog.create();
//            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            pDialog.setMessage("Please wait for requesting...");
            //  pDialog.setCancelable(false);
//            pDialog.show();
        }
        @Override
        protected Void doInBackground(String... args) {
            JSONObject json = new JSONObject();
            try {
                json.put("ApiKey", Defined.API_KEY);
                json.put("Login", username);
                json.put("Password", pass);
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
                url = new URL(Defined.API_getlistHotels);
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
//            if (pDialog.isShowing()) pDialog.dismiss();
            rl.setVisibility(RelativeLayout.INVISIBLE);
            try{
                JSONObject response = new JSONObject(jsonstr);
                String status = response.getString("result");
                if (status.equals("ok")) {
                    String str = response.getString("number");
                    int num = Integer.parseInt(str);
//                    Log.e("number kq:",str);
                    if(num == 0){
                        emptylistView.setVisibility(TextView.VISIBLE);
                        listViewHotels.setVisibility(ListView.INVISIBLE);
                    }else{
                        emptylistView.setVisibility(TextView.GONE);
                        listViewHotels.setVisibility(ListView.VISIBLE);
                        String str1 = response.getString("hotels");
                        JSONArray arr = new JSONArray(str1);
                        for (int i = 0;i<arr.length();i++){
                            JSONObject inner = arr.getJSONObject(i);
                            String name = inner.getString("ho_name");
                            String usname = inner.getString("ho_username");
                            String city = inner.getString("ho_city");
                            String country = inner.getString("ho_country");
                            String from = inner.getString("re_fromDate");
                            String to = inner.getString("re_toDate");
                            String zip = inner.getString("ho_zip");
                            String link = Defined.PHOTOURL + usname + "_logo.jpg";
                            String link_large = Defined.PHOTOURL + usname + ".jpg";
                            Hotel ho = new Hotel(usname,name,city,country,from,to,link);
                            ho.zip = zip;
                            ho.imagelink_large = link_large;
                            listhotels.add(ho);
//                            new API_DoGetChatHistory(context,username,pass,ho.username);
                        }
                        refreshList();
                        doBindService();
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

    @Override
    public void onBackPressed(){
        if(Defined.checkInternetConnection(context)){
            doUnbindService();
        }
        super.onBackPressed();
    }

    void doBindService() {
        if(listhotels.size() > 0) {
            intentService = new Intent(MainActivity.this,MyService.class);
            SharedPreferences sharedPreferences = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("numhotels", "" + listhotels.size());
            for (int i = 0; i < listhotels.size(); i++) {
                edit.putString("hotel" + i, listhotels.get(i).username);
            }
            edit.commit();
            this.startService(intentService);
            bindService(intentService, mConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    void doUnbindService() {
        if (mConnection != null) {
            if(intentService != null){
                stopService(intentService);
                unbindService(mConnection);
            }
        }
    }

    public MyService getmService() {
        return mService;
    }

//    class API_DoGetChatHistory extends AsyncTask<String, Void, Void> {
//        private Activity context;
//        private AlertDialog pDialog;
//        private String jsonstr;
//        private String us;
//        private String pa;
//
//        API_DoGetChatHistory(Activity context, String us, String pa, String us2)
//        {
//            this.context = context;
//            this.us = us;
//            this.pa = pa;
////            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
////            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////            View dialogview = inflater.inflate(R.layout.progess_dialog, null);
////            dialog.setView(dialogview);
////            dialog.setCancelable(false);
////            pDialog = dialog.create();
////            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
////            pDialog.setMessage("Please wait for requesting...");
//            //  pDialog.setCancelable(false);
////            pDialog.show();
//        }
//        @Override
//        protected Void doInBackground(String... args) {
//            String fromdate = db.getMaximunSentDate();
//            JSONObject json = new JSONObject();
//            try {
//                json.put("ApiKey", Defined.API_KEY);
//                json.put("Login", us);
//                json.put("Password", pa);
//                json.put("FromDate", fromdate);
//            } catch (JSONException e1) {
//                e1.printStackTrace();
//            }
//
//            String requestBody = null;
//            try {
//                requestBody = Defined.getPostDataString(json);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            URL url = null;
//            try {
//                url = new URL(Defined.API_getAllChatHistory);
//            } catch (MalformedURLException e1) {
//                e1.printStackTrace();
//            }
//
//            HttpURLConnection urlConnection = null;
//            try {
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoOutput(true);
//                urlConnection.setDoInput(true);
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("Content-Type",
//                        "application/x-www-form-urlencoded");
//                urlConnection.setRequestProperty("Content-Length", "" +
//                        Integer.toString(requestBody.getBytes().length));
//                urlConnection.setRequestProperty("Content-Language", "en-US");
//                urlConnection.setUseCaches (false);
//
//                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
//                try {
//                    writer.write(requestBody);
//                    writer.flush();
//                    writer.close();
//                    outputStream.close();
//                    InputStream inputStream;
//
//                    // get stream
//                    if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
//                        inputStream = urlConnection.getInputStream();
//                    } else {
//                        inputStream = urlConnection.getErrorStream();
//                    }
//                    // parse stream
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    String temp, response = "";
//                    while ((temp = bufferedReader.readLine()) != null) {
//                        response += temp;
//                    }
//                    jsonstr = response;
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            // Dismiss the progress dialog
////            if (pDialog.isShowing()) pDialog.dismiss();
//            try{
//                JSONObject response = new JSONObject(jsonstr);
//                String status = response.getString("result");
//                String num1 = response.getString("num");
//                int numrow = Integer.parseInt(num1);
//                if (status.equals("ok")) {
//                    ArrayList<ChatMessage> chatlist = null;
//
//                    if(numrow > 0){
//                        String str1 = response.getString("chat");
//                        db.loadAllChatFromServer(str1);
//                    }else{
//                        db.getAllMessages();
//                    }
//                    if(ChatActivity.hashmap.containsKey(user2)){
//                        ChatActivity.hashmap.remove(user2);
//                    }
//                    ChatActivity.hashmap.put(user2, chatlist);
////                    Toast.makeText(context, "Get history ok!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
//                }
//            }
//            catch (JSONException es){
//                return;
//            }
//        }
//    }

    public String getDate(int date, String day) {
        switch (date) {
            case 1:
                return day + " " + context.getString(R.string.jan);

            case 2:
                return day + " " + context.getString(R.string.feb);

            case 3:
                return day + " " + context.getString(R.string.mar);

            case 4:
                return day + " " + context.getString(R.string.apr);

            case 5:
                return day + " " + context.getString(R.string.may);

            case 6:
                return day + " " + context.getString(R.string.jun);

            case 7:
                return day + " " + context.getString(R.string.jul);

            case 8:
                return day + " " + context.getString(R.string.aug);

            case 9:
                return day + " " + context.getString(R.string.sep);

            case 10:
                return day + " " + context.getString(R.string.oct);

            case 11:
                return day + " " + context.getString(R.string.nov);

            case 12:
                return day + " " + context.getString(R.string.dec);

            default:
                return "";
        }
    }
    public String getDay(String day){
        switch (day){
            case "Sunday": return context.getString(R.string.Sunday);

            case "Monday": return context.getString(R.string.Monday);

            case "Tuesday": return context.getString(R.string.Tuesday);

            case "Wednesday": return context.getString(R.string.Wednesday);

            case "Thursday": return context.getString(R.string.Thursday);

            case "Friday": return context.getString(R.string.Friday);

            case "Saturday": return context.getString(R.string.Saturday);

            default: return "";
        }
    }

}