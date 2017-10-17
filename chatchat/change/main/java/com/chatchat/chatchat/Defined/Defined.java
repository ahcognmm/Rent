package com.chatchat.chatchat.Defined;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.chatchat.chatchat.R;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by Thong Pham on 20/07/2017.
 */

public class Defined {
    public static final String SHP_NAME = "Chatchat";
    public static String MODEL_DEVICE = android.os.Build.MODEL;
    public static final String SERVER = "52.90.171.87";
    public static final String SERVERHTTP = "52.90.171.87";
    public static final String SERVERAPI = "http://52.90.171.87/api/";
    public static final String PHOTOURL = "http://52.90.171.87/uploads/images/";
//    public static final String SERVER = "10.8.75.133";
    public static final String API_KEY = "CHATCHATAPI";
    public static final String API_Login = SERVERAPI + "login";
    //    public static final String API_getAppUserKey = SERVER + "api/getAppUserKey";
//    public static final String API_checkAppUserKey = SERVER + "api/checkAppUserKey";
    public static final String API_getlistHotels = SERVERAPI + "getlisthotels";
    public static final String API_getlistForum = SERVERAPI + "getlistforums";
    public static final String API_getlistRequest = SERVERAPI + "getlistrequests";
    public static final String API_sendRequest = SERVERAPI + "sendrequest";
    public static final String API_getlistAnswer = SERVERAPI + "getlistanswers";
    public static final String API_sendAnswer = SERVERAPI + "sendanswer";
    public static final String API_registerDevice = SERVERAPI + "registerDevice";
    public static final String API_register = SERVERAPI + "register";
    public static final String API_getChatHistory = SERVERAPI + "getchathistory";
    public static final String API_getAllChatHistory = SERVERAPI + "getallchathistory";
    public static final String API_getlistLink = SERVERAPI + "getlistinterestinglinks";
    public static final String API_getHotelInfo = SERVERAPI + "gethotelinfos";
    public static final String API_getWeather = SERVERAPI + "getweather";


    public static final int API_NUM_HISTORY = 50;
    public static final int TIME_OUT = 3000;

    // for store chat on sql;
    public static final String CHAT_DB_NAME = "chatchat";
    public static final int DATABASE_VERSION = 1;
    public static final String CHAT_TABLE_NAME = "message";
    public static final String [] CHAT_ATT = {"id","sentdate","fromJID","toJID","body","time","date"};


    public static boolean checkInternetConnection(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity == null) return false;

        if(Build.VERSION.SDK_INT >= 21){
            Network[] info = connectivity.getAllNetworks();
            if(info != null){
                for (int i = 0; i < info.length; i++) {
                    if(info[i] != null && connectivity.getNetworkInfo(info[i]).isConnected())
                        return true;
                }
            }
        }else{
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info != null){
                for (int i = 0; i < info.length; i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
            final NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnected())
                return true;
        }
        Toast.makeText(context, R.string.no_network,Toast.LENGTH_SHORT).show();
        return false;
    }

    public static String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    public static String getDate(int date, String day) {
        switch (date) {
            case 1:
                return day + " " + Resources.getSystem().getString(R.string.jan);

            case 2:
                return day + " " + Resources.getSystem().getString(R.string.feb);

            case 3:
                return day + " " + Resources.getSystem().getString(R.string.mar);

            case 4:
                return day + " " + Resources.getSystem().getString(R.string.apr);

            case 5:
                return day + " " + Resources.getSystem().getString(R.string.may);

            case 6:
                return day + " " + Resources.getSystem().getString(R.string.jun);

            case 7:
                return day + " " + Resources.getSystem().getString(R.string.jul);

            case 8:
                return day + " " + Resources.getSystem().getString(R.string.aug);

            case 9:
                return day + " " + Resources.getSystem().getString(R.string.sep);

            case 10:
                return day + " " + Resources.getSystem().getString(R.string.oct);

            case 11:
                return day + " " + Resources.getSystem().getString(R.string.nov);

            case 12:
                return day + " " + Resources.getSystem().getString(R.string.dec);

            default:
                return "";
        }
    }
    public static String getDay(String day){
        switch (day){
            case "Sunday": return Resources.getSystem().getString(R.string.Sunday);

            case "Monday": return Resources.getSystem().getString(R.string.Monday);

            case "Tuesday": return Resources.getSystem().getString(R.string.Tuesday);

            case "Wednesday": return Resources.getSystem().getString(R.string.Wednesday);

            case "Thursday": return Resources.getSystem().getString(R.string.Thursday);

            case "Friday": return Resources.getSystem().getString(R.string.Friday);

            case "Saturday": return Resources.getSystem().getString(R.string.Saturday);

            default: return "";
        }
    }

}
