package com.chatchat.chatchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.Hotel;
import com.chatchat.chatchat.fragment.ForumFragment;
import com.chatchat.chatchat.fragment.HomeFragment;
import com.chatchat.chatchat.fragment.InfoFragment;
import com.chatchat.chatchat.fragment.LinksFragment;
import com.chatchat.chatchat.fragment.MessageFragment;

/**
 * Created by Thong Pham on 24/08/2017.
 */

public class HotelDetailActivity extends AppCompatActivity {

    private ImageButton home;
    private ImageButton mess;
    private ImageButton links;
    private ImageButton forum;
    private ImageButton info;

    private TextView text_home;
    private TextView text_mess;
    private TextView text_links;
    private TextView text_forum;
    private TextView text_info;

    private RelativeLayout rl_home;
    private RelativeLayout rl_mess;
    private RelativeLayout rl_links;
    private RelativeLayout rl_forum;
    private RelativeLayout rl_info;

    private HomeFragment homeFragment;
    private ForumFragment forumFragment;
    private MessageFragment messageFragment;
    private LinksFragment linksFragment;
    private InfoFragment infoFragment;

    private int current_choosing = 1;

    private TextView tv_hotel_name;
    private ImageButton btn_back;
    private ImageButton btn_acc;

    private Activity context;
    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);
        ActionBar ac = getSupportActionBar();
        ac.hide();

        //set list
        context = this;
        Intent it = getIntent();
        String name = it.getStringExtra("name");
        String imglink = it.getStringExtra("link");
        String imglinklarge = it.getStringExtra("link_large");
        String from = it.getStringExtra("from");
        String to = it.getStringExtra("to");
        String city = it.getStringExtra("city");
        String country = it.getStringExtra("country");
        String username = it.getStringExtra("username");
        String zip = it.getStringExtra("zip");
        hotel = new Hotel(username,name,city,country,from,to,imglink);
        hotel.zip = zip;
        hotel.imagelink_large = imglinklarge;

        String startdate1 = it.getStringExtra("startdate");
        String enddate1 = it.getStringExtra("enddate");
        String startday = it.getStringExtra("startday");
        String endday = it.getStringExtra("endday");
        String ndays = it.getStringExtra("ndays");
        hotel.startdate1 = startdate1;
        hotel.enddate1 = enddate1;
        hotel.startday = startday;
        hotel.endday = endday;
        hotel.numberday = Integer.parseInt(ndays);

        home = (ImageButton) findViewById(R.id.btn_home);
        mess = (ImageButton) findViewById(R.id.btn_message);
        links = (ImageButton) findViewById(R.id.btn_links);
        forum = (ImageButton) findViewById(R.id.btn_forum);
        info = (ImageButton) findViewById(R.id.btn_info);

        text_home = (TextView) findViewById(R.id.btn_home_text);
        text_mess = (TextView) findViewById(R.id.btn_message_text);
        text_links =(TextView) findViewById(R.id.btn_links_text);
        text_forum =(TextView) findViewById(R.id.btn_forum_text);
        text_info = (TextView) findViewById(R.id.btn_info_text);

        rl_home = (RelativeLayout) findViewById(R.id.rl_btn_home);
        rl_mess = (RelativeLayout) findViewById(R.id.rl_btn_message);
        rl_links =(RelativeLayout) findViewById(R.id.rl_btn_links);
        rl_forum =(RelativeLayout) findViewById(R.id.rl_btn_forum);
        rl_info = (RelativeLayout) findViewById(R.id.rl_btn_info);

        tv_hotel_name = (TextView) findViewById(R.id.menu_hotel_name);
        tv_hotel_name.setText(hotel.name);

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_acc = (ImageButton) findViewById(R.id.btn_account);
        btn_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get prompts.xml view
                AlertDialog.Builder builder = new AlertDialog.Builder(HotelDetailActivity.this);
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

        homeFragment = new HomeFragment(context, hotel);
        forumFragment = new ForumFragment(context, hotel);
        messageFragment = new MessageFragment(context, hotel);
        linksFragment = new LinksFragment(context, hotel);
        infoFragment = new InfoFragment(context, hotel);

        text_home.setTextColor(Color.parseColor(getResources().getString(R.color.tabmenu_select_color)));
        rl_home.setBackgroundResource(R.color.slideTextColor);
        home.setBackgroundResource(R.drawable.ico_home_click);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,homeFragment).commit();

        View.OnClickListener clickhome = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container,homeFragment).commit();
                if(current_choosing != 1){
                    setAllUnSelect();
                    text_home.setTextColor(Color.parseColor(getResources().getString(R.color.tabmenu_select_color)));
                    rl_home.setBackgroundResource(R.color.slideTextColor);
                    home.setBackgroundResource(R.drawable.ico_home_click);
                    current_choosing = 1;
                }
            }
        };

        View.OnClickListener clickmess = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, messageFragment).commit();
                if (current_choosing != 2) {
                    setAllUnSelect();
                    text_mess.setTextColor(Color.parseColor(getResources().getString(R.color.tabmenu_select_color)));
                    rl_mess.setBackgroundResource(R.color.slideTextColor);
                    mess.setBackgroundResource(R.drawable.ico_message_click);
                    current_choosing = 2;
                }
            }
        };

        View.OnClickListener clickforum = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, forumFragment).commit();
                if(current_choosing != 3){
                    setAllUnSelect();
                    text_forum.setTextColor(Color.parseColor(getResources().getString(R.color.tabmenu_select_color)));
                    rl_forum.setBackgroundResource(R.color.slideTextColor);
                    forum.setBackgroundResource(R.drawable.ico_forum_click);
                    current_choosing = 3;
                }
            }
        };

        View.OnClickListener clicklink = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, linksFragment).commit();
                if(current_choosing != 4){
                    setAllUnSelect();
                    text_links.setTextColor(Color.parseColor(getResources().getString(R.color.tabmenu_select_color)));
                    rl_links.setBackgroundResource(R.color.slideTextColor);
                    links.setBackgroundResource(R.drawable.ico_link_click);
                    current_choosing = 4;
                }
            }
        };

        View.OnClickListener clickinfo = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container,infoFragment).commit();
                if(current_choosing != 5){
                    setAllUnSelect();
                    text_info.setTextColor(Color.parseColor(getResources().getString(R.color.tabmenu_select_color)));
                    rl_info.setBackgroundResource(R.color.slideTextColor);
                    info.setBackgroundResource(R.drawable.ico_info_click);
                    current_choosing = 5;
                }
            }
        };

        rl_home.setOnClickListener(clickhome);
        home.setOnClickListener(clickhome);
        rl_mess.setOnClickListener(clickmess);
        mess.setOnClickListener(clickmess);
        rl_forum.setOnClickListener(clickforum);
        forum.setOnClickListener(clickforum);
        rl_links.setOnClickListener(clicklink);
        links.setOnClickListener(clicklink);
        rl_info.setOnClickListener(clickinfo);
        info.setOnClickListener(clickinfo);

    }

    private void setAllUnSelect(){
        text_home.setTextColor(Color.WHITE);
        text_mess.setTextColor(Color.WHITE);
        text_links.setTextColor(Color.WHITE);
        text_forum.setTextColor(Color.WHITE);
        text_info.setTextColor(Color.WHITE);

        rl_home.setBackgroundResource(R.color.transparent);
        rl_mess.setBackgroundResource(R.color.transparent);
        rl_links.setBackgroundResource(R.color.transparent);
        rl_forum.setBackgroundResource(R.color.transparent);
        rl_info.setBackgroundResource(R.color.transparent);

        home.setBackgroundResource(R.drawable.ico_home);
        mess.setBackgroundResource(R.drawable.ico_message);
        forum.setBackgroundResource(R.drawable.ico_forum);
        info.setBackgroundResource(R.drawable.ico_info);
        links.setBackgroundResource(R.drawable.ico_link);
    }





   /*******************************************************************/
    //    private Button hotels;
//    private Button profile;
//    private Button messages;
//    private Button forum;
//    private Button link;
//    private Button info;
//
//    private TextView w_time;
//    private TextView w_city;
//    private TextView [] w_temp;
//    private TextView [] w_tomo;
//    private TextView w_no_info;
//
//    private ImageView [] w_ico;
//
//    private LinearLayout ll_time;
//    private LinearLayout ll_weather;
//
//    private TextView tvName;
//    private TextView tvTime;
//    private ImageView ivImage;
//
//    private Context context;
//    private String username;
//    private String pass;
//
//    private RelativeLayout ll_all_weather;
//    private LinearLayout ll_all;
//    private SimpleDateFormat sdf1;
//
//    private Hotel hotel;


//    protected void onCreate(Bundle savedInstanceState) {
//
//    }
//        super.onCreate(savedInstanceState);
//        ActionBar ac = getSupportActionBar();
//        ac.hide();
//        setContentView(R.layout.activity_hotel_detail);
//
//        SharedPreferences sharedPreferences = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
//        username = sharedPreferences.getString("Username","");
//        pass = sharedPreferences.getString("Password","");
//
//
//        profile = (Button) findViewById(R.id.profile);
//        profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // get prompts.xml view
//                AlertDialog.Builder builder = new AlertDialog.Builder(HotelDetailActivity.this);
//                builder.setTitle(context.getResources().getString(R.string.logout_prompt));
//                // Set up the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences sharedPreferences = context.getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
//                        sharedPreferences.edit().clear().commit();
//                        Intent intent = new Intent(context, IntroActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                builder.show();
//            }
//        });
//
//
//        tvName = (TextView)findViewById(R.id.textViewHotelName);
//        tvName.setText(name);
//        tvTime = (TextView)findViewById(R.id.textViewtime);
//        tvTime.setText(from + " - " + to);
//        ivImage = (ImageView)findViewById(R.id.imageViewHotel);
//        Picasso.with(context).load(imglink).into(ivImage);
//
//        hotels = (Button)findViewById(R.id.hotelbutton);
//        profile = (Button)findViewById(R.id.profile);
//        messages = (Button)findViewById(R.id.btn_messages);
//        forum = (Button)findViewById(R.id.btn_forum);
//        info = (Button)findViewById(R.id.btn_info);
//
//        info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context,InfoActivity.class);
//                intent.putExtra("name",hotel.name);
//                intent.putExtra("link",hotel.imagelink);
//                intent.putExtra("from",hotel.startdate);
//                intent.putExtra("to",hotel.enddate);
//                intent.putExtra("city",hotel.city);
//                intent.putExtra("country",hotel.country);
//                intent.putExtra("username",hotel.username);
//                startActivity(intent);
//            }
//        });
//
//        link = (Button)findViewById(R.id.btn_links);
//        link.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context,LinksActivity.class);
//                intent.putExtra("name",hotel.name);
//                intent.putExtra("link",hotel.imagelink);
//                intent.putExtra("from",hotel.startdate);
//                intent.putExtra("to",hotel.enddate);
//                intent.putExtra("city",hotel.city);
//                intent.putExtra("country",hotel.country);
//                intent.putExtra("username",hotel.username);
//                startActivity(intent);
//            }
//        });
//
//        hotels.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        messages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context,ChatActivity.class);
//                intent.putExtra("name",hotel.name);
//                intent.putExtra("link",hotel.imagelink);
//                intent.putExtra("from",hotel.startdate);
//                intent.putExtra("to",hotel.enddate);
//                intent.putExtra("city",hotel.city);
//                intent.putExtra("country",hotel.country);
//                intent.putExtra("username",hotel.username);
//                startActivity(intent);
////                finish();
//            }
//        });
//
//        forum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context,ForumActivity.class);
//                intent.putExtra("name",hotel.name);
//                intent.putExtra("link",hotel.imagelink);
//                intent.putExtra("from",hotel.startdate);
//                intent.putExtra("to",hotel.enddate);
//                intent.putExtra("city",hotel.city);
//                intent.putExtra("country",hotel.country);
//                intent.putExtra("username",hotel.username);
//                startActivity(intent);
//            }
//        });
//        // For weather
//        w_time = (TextView) findViewById(R.id.weather_time);
//        w_city = (TextView) findViewById(R.id.weather_city);
//        w_temp = new TextView[3];
//        w_temp[0] = (TextView) findViewById(R.id.weather_temp1);
//        w_temp[1] = (TextView) findViewById(R.id.weather_temp2);
//        w_temp[2] = (TextView) findViewById(R.id.weather_temp3);
//        w_tomo = new TextView[2];
//        w_tomo[0] = (TextView) findViewById(R.id.weather_day2);
//        w_tomo[1] = (TextView) findViewById(R.id.weather_day3);
//        w_no_info = (TextView) findViewById(R.id.w_no_infos);
//        w_ico = new ImageView[3];
//        w_ico[0] = (ImageView) findViewById(R.id.weather_icon1);
//        w_ico[1] = (ImageView) findViewById(R.id.weather_icon2);
//        w_ico[2] = (ImageView) findViewById(R.id.weather_icon3);
//
//        ll_time = (LinearLayout) findViewById(R.id.ll_time_city);
//        ll_weather = (LinearLayout) findViewById(R.id.ll_weather);
//        ll_all_weather = (RelativeLayout) findViewById(R.id.ll_all_weather);
//
//        ll_all = (LinearLayout) findViewById(R.id.ll_all);
//
//        sdf1 = new SimpleDateFormat("HH:mm a");
//        w_time.setText(sdf1.format(Calendar.getInstance().getTime()).toString());
//
//        new API_DoGetWeather(HotelDetailActivity.this).execute();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                w_time.setText(sdf1.format(Calendar.getInstance().getTime()).toString());
//            }
//        }, 30000);
//
//    }
//
//    class API_DoGetWeather extends AsyncTask<String, Void, Void> {
//        private Activity context;
//        private ProgressDialog pDialog;
//        private String jsonstr;
//
//        API_DoGetWeather(Activity context)
//        {
//            this.context = context;
//            pDialog= new ProgressDialog(context);
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
//            pDialog.setMessage("Please wait for requesting...");
//            //  pDialog.setCancelable(false);
//            pDialog.show();
//        }
//        @Override
//        protected Void doInBackground(String... args) {
//            JSONObject json = new JSONObject();
//            try {
//                json.put("ApiKey", Defined.API_KEY);
//                json.put("Login", username);
//                json.put("Password", pass);
//                json.put("City", hotel.city);
//                json.put("Zip", hotel.zip);
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
//                url = new URL(Defined.API_getWeather);
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
////            Log.e("Ket qua tra ve",jsonstr);
//            // Dismiss the progress dialog
//            if (pDialog.isShowing()) pDialog.dismiss();
//            try{
//                JSONObject response = new JSONObject(jsonstr);
//                String status = response.getString("result");
//                if (status.equals("ok")) {
//                    String str = response.getString("number");
//                    int num = Integer.parseInt(str);
////                    Log.e("number kq:",str);
//                    if(num == 0){
//                        w_no_info.setVisibility(TextView.VISIBLE);
//                        ll_time.setVisibility(LinearLayout.INVISIBLE);
//                        ll_weather.setVisibility(LinearLayout.INVISIBLE);
//                    }else{
//                        boolean isRain = true;
//                        w_no_info.setVisibility(TextView.INVISIBLE);
//                        ll_time.setVisibility(LinearLayout.VISIBLE);
//                        ll_weather.setVisibility(LinearLayout.VISIBLE);
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        w_city.setText(hotel.city + ", " + hotel.country);
//                        String str1 = response.getString("weather");
//                        JSONArray arr = new JSONArray(str1);
////                        Log.e("List hotels",str1 + " -- " + arr.length());
//                        int end = arr.length() > 3 ? 3 : arr.length();
//                        for (int i = 0;i< end;i++){
//                            JSONObject inner = arr.getJSONObject(i);
//                            String date = inner.getString("w_date");
//                            String min = inner.getString("w_temp_min");
//                            String max = inner.getString("w_temp_max");
//                            String icon = inner.getString("w_icon");
//
//                            int min1 = Math.round(Float.parseFloat(min) - 273);
//                            int max1 = Math.round(Float.parseFloat(max) - 273);
//                            w_temp[i].setText("" + min1 + " ~ " + max1);
//
//                            switch (icon.substring(0,icon.length() - 2)){
//                                case "i01": Picasso.with(context).load(R.drawable.i01d).into(w_ico[i]); isRain = false; break;
//                                case "i02": Picasso.with(context).load(R.drawable.i02d).into(w_ico[i]); isRain = false;break;
//                                case "i03": Picasso.with(context).load(R.drawable.i03d).into(w_ico[i]); isRain = false;break;
//                                case "i04": Picasso.with(context).load(R.drawable.i04d).into(w_ico[i]); isRain = false;break;
//                                case "i09": Picasso.with(context).load(R.drawable.i09d).into(w_ico[i]); isRain = true;break;
//                                case "i10": Picasso.with(context).load(R.drawable.i10d).into(w_ico[i]); isRain = true;break;
//                                case "i11": Picasso.with(context).load(R.drawable.i11d).into(w_ico[i]); isRain = true;break;
//                                case "i13": Picasso.with(context).load(R.drawable.i13d).into(w_ico[i]); isRain = true;break;
//                                case "i50": Picasso.with(context).load(R.drawable.i50d).into(w_ico[i]); isRain = true;break;
//                                default: Picasso.with(context).load(R.drawable.i01d).into(w_ico[i]); isRain = false; break;
//                            }
//                            if(i > 0){
//                                try {
//                                    Date date1 = sdf.parse(date);
//                                    int d = date1.getDay();
//                                    switch (d) {
//                                        case 1: w_tomo[i - 1].setText(getResources().getString(R.string.mon)); break;
//                                        case 2: w_tomo[i - 1].setText(getResources().getString(R.string.tue)); break;
//                                        case 3: w_tomo[i - 1].setText(getResources().getString(R.string.wed)); break;
//                                        case 4: w_tomo[i - 1].setText(getResources().getString(R.string.thu)); break;
//                                        case 5: w_tomo[i - 1].setText(getResources().getString(R.string.fri)); break;
//                                        case 6: w_tomo[i - 1].setText(getResources().getString(R.string.sat)); break;
//                                        case 0: w_tomo[i - 1].setText(getResources().getString(R.string.sun));
//                                    }
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                            }else{
//                                if(!isRain){
//                                    ll_all.setBackgroundResource(R.drawable.weather_cloudy);
//                                }else{
//                                    ll_all.setBackgroundResource(R.drawable.weather_rain);
//                                }
//                            }
//                        }
//
////                        int  hei = ll_time.getHeight() + ll_weather.getHeight();
////                        Log.e("Do cao","" + hei);
//////                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ll_all_weather.getWidth(),hei);
//////                        ll_all_weather.setLayoutParams(lp);
////                        ll_all_weather.setMinimumHeight(hei);
//                    }
//                } else {
//                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show();
//                }
//            }
//            catch (JSONException es){
//                return;
//            }
//        }
//    }

}
