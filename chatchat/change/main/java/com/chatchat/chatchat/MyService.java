package com.chatchat.chatchat;

/**
 * Created by Khushvinders on 15-Nov-16.
 */

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.chatchat.chatchat.Defined.Defined;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;

public class MyService extends Service {

    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    public static MyService context;

    @Override
    public IBinder onBind(final Intent intent) {
        return new LocalBinder<MyService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        context = MyService.this;
        SharedPreferences sharedPreferences = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        String us = sharedPreferences.getString("Username","");
        String pa = sharedPreferences.getString("Password","");
        String str = sharedPreferences.getString("numhotels","");
        if(!str.equals("")) {
            int number = Integer.parseInt(str);
            ArrayList<String> us2 = new ArrayList<String>();
            for (int i = 0; i < number; i++) {
                str = sharedPreferences.getString("hotel" + i,"");
                if(!str.equals("")){
                    us2.add(str);
                }
            }

            if (!us.equals("") && !pa.equals("")) {
                us = us.replace("@", "__");
                xmpp = MyXMPP.getInstance(MyService.this, Defined.SERVER, us, pa, us2);
//            xmpp = MyXMPP.getInstance(MyService.this, Defined.SERVER, "ht3__chatchat.com", "123");
                xmpp.connect("onCreate");
            }
        }
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(Defined.checkInternetConnection(context)) {
            if(xmpp.connection.isConnected()) {
                xmpp.connection.disconnect();
            }
        }
    }
}