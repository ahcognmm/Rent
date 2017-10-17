package com.chatchat.chatchat.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.Hotel;
import com.chatchat.chatchat.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.Date;


public class HomeFragment extends Fragment {

    private Activity context;
    private Hotel hotel;

    private ImageView img_large;
    private ImageView img_small;
    private ImageView img_temp;
    private ImageView img_temp1;
    private ImageView img_temp2;

    private TextView tv_name;
    private TextView tv_address;
    private TextView tv_numday;

    private TextView tv_date1;
    private TextView tv_date2;
    private TextView tv_day1;
    private TextView tv_day2;
    private TextView tv_temp_min;
    private TextView tv_temp_max;

    private TextView tv_temp_1;
    private TextView tv_temp_2;
    private TextView tv_temp_day1;
    private TextView tv_temp_day2;

    private RelativeLayout rl;

    private String username;
    private String pass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    public HomeFragment(Activity context, Hotel hotel) {
        this.context = context;
        this.hotel = hotel;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        username = sharedPreferences.getString("Username","");
        pass = sharedPreferences.getString("Password","");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){

        // set image
        img_large = (ImageView) getView().findViewById(R.id.imvHotel);
        img_small = (ImageView) getView().findViewById(R.id.imvIconHotel);
        img_temp = (ImageView) getView().findViewById(R.id.imvTempDay);
        img_temp1 = (ImageView) getView().findViewById(R.id.imvTempDay1);
        img_temp2 = (ImageView) getView().findViewById(R.id.imvTempDay2);

        // set text view
        tv_name = (TextView) getView().findViewById(R.id.tvHotelName);
        tv_address = (TextView) getView().findViewById(R.id.tvCity);
        tv_numday = (TextView) getView().findViewById(R.id.tvTime);
        tv_date1 = (TextView) getView().findViewById(R.id.tvStartDate);
        tv_date2 = (TextView) getView().findViewById(R.id.tvEndDate);
        tv_day1 = (TextView) getView().findViewById(R.id.tvStartDay);
        tv_day2 = (TextView) getView().findViewById(R.id.tvEndDAy);
        tv_temp_min = (TextView) getView().findViewById(R.id.tempMinDay);
        tv_temp_max = (TextView) getView().findViewById(R.id.tempMaxDay);
        tv_temp_1 = (TextView) getView().findViewById(R.id.tvTemp1);
        tv_temp_2 = (TextView) getView().findViewById(R.id.tvTemp2);
        tv_temp_day1 = (TextView) getView().findViewById(R.id.tvTempDay1);
        tv_temp_day2 = (TextView) getView().findViewById(R.id.tvTempDay2);

        //set element
//        rl = (RelativeLayout) getView().findViewById(R.id.ll_weather);
        tv_name.setText(hotel.name);
        tv_address.setText(hotel.city + ", " + hotel.country);
        tv_numday.setText(hotel.numberday + " notti");
        tv_date1.setText(hotel.startdate1);
        tv_day1.setText(hotel.startday);
        tv_date2.setText(hotel.enddate1);
        tv_day2.setText(hotel.endday);

        Picasso.with(context).load(hotel.imagelink_large).into(img_large);
        Picasso.with(context).load(hotel.imagelink).into(img_small);

        new API_DoGetWeather(context).execute();

    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment HomeFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static HomeFragment newInstance(String param1, String param2) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false);
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
    class API_DoGetWeather extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;

        API_DoGetWeather(Activity context)
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
            try {
                json.put("ApiKey", Defined.API_KEY);
                json.put("Login", username);
                json.put("Password", pass);
                json.put("City", hotel.city);
                json.put("Zip", hotel.zip);
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
                url = new URL(Defined.API_getWeather);
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
//                    Log.e("number kq:",str);
                    if(num == 0){
                        rl.setVisibility(RelativeLayout.GONE);
                    }else{
                        boolean isRain = true;
                        rl.setVisibility(RelativeLayout.VISIBLE);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String str1 = response.getString("weather");
                        JSONArray arr = new JSONArray(str1);
    //                        Log.e("List hotels",str1 + " -- " + arr.length());
                        if(arr.length() > 2){
                            JSONObject inner = arr.getJSONObject(0);
                            String min1 = inner.getString("w_temp_min");
                            String max1 = inner.getString("w_temp_max");
                            String icon1 = inner.getString("w_icon");
                            int min_temp = Math.round(Float.parseFloat(min1) - 273);
                            int max_temp = Math.round(Float.parseFloat(max1) - 273);

                            tv_temp_min.setText(min_temp + "\u00B0C");
                            tv_temp_max.setText(max_temp + "\u00B0C");
                            isRain = loadImg(icon1,img_temp, true);

                            inner = arr.getJSONObject(1);
                            String date1 = inner.getString("w_date");
                            min1 = inner.getString("w_temp_min");
                            max1 = inner.getString("w_temp_max");
                            icon1 = inner.getString("w_icon");
                            min_temp = Math.round(Float.parseFloat(min1) - 273);
                            max_temp = Math.round(Float.parseFloat(max1) - 273);
                            Date ngay1 = null;
                            try {
                                ngay1 = sdf.parse(date1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            tv_temp_1.setText(min_temp + "\u00B0C/" + max_temp + "\u00B0C");
                            tv_temp_day1.setText(getDay((String) DateFormat.format("EEEE", ngay1)));
                            isRain = loadImg(icon1,img_temp1,false);

                            inner = arr.getJSONObject(2);
                            String date2 = inner.getString("w_date");
                            min1 = inner.getString("w_temp_min");
                            max1 = inner.getString("w_temp_max");
                            icon1 = inner.getString("w_icon");
                            min_temp = Math.round(Float.parseFloat(min1) - 273);
                            max_temp = Math.round(Float.parseFloat(max1) - 273);
                            Date ngay2 = null;
                            try {
                                ngay2 = sdf.parse(date2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            tv_temp_2.setText(min_temp + "\u00B0C/" + max_temp + "\u00B0C");
                            tv_temp_day2.setText(getDay((String) DateFormat.format("EEEE", ngay2)));
                            isRain = loadImg(icon1,img_temp2,false);
                        }else {
                            rl.setVisibility(RelativeLayout.GONE);
                            Toast.makeText(context, "Error occurred when get weather", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Error occurred when get weather", Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException es){
                return;
            }
        }
    }

    private boolean loadImg(String icon, ImageView img, boolean r){
        boolean isRain = false;
        switch (icon.substring(0,icon.length() - 2)){
            case "i01": if(r) Picasso.with(context).load(R.drawable.im01d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im01d_b).into(img);
                        isRain = false;break;
            case "i02": if(r) Picasso.with(context).load(R.drawable.im02d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im02d_b).into(img);
                        isRain = false;break;
            case "i03": if(r) Picasso.with(context).load(R.drawable.im03d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im03d_b).into(img);
                        isRain = false;break;
            case "i04": if(r) Picasso.with(context).load(R.drawable.im04d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im04d_b).into(img);
                        isRain = false;break;
            case "i09": if(r) Picasso.with(context).load(R.drawable.im09d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im09d_b).into(img);
                        isRain = true;break;
            case "i10": if(r) Picasso.with(context).load(R.drawable.im10d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im10d_b).into(img);
                        isRain = true;break;
            case "i11": if(r) Picasso.with(context).load(R.drawable.im11d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im11d_b).into(img);
                        isRain = true;break;
            case "i13": if(r) Picasso.with(context).load(R.drawable.im13d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im13d_b).into(img);
                        isRain = true;break;
            case "i50": if(r) Picasso.with(context).load(R.drawable.im50d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im50d_b).into(img);
                        isRain = true;break;
            default:    if(r) Picasso.with(context).load(R.drawable.im01d_r).into(img);
                        else Picasso.with(context).load(R.drawable.im01d_b).into(img);
                        isRain = false; break;
        }
        return isRain;
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
