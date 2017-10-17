package com.chatchat.chatchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.chatchat.chatchat.Defined.Defined;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thong Pham on 20/07/2017.
 */

public class Chatchat extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ActionBar ac = getSupportActionBar();
        ac.hide();
        SharedPreferences sh = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        String us = sh.getString("Username","");
        String pa = sh.getString("Password","");
        if(!us.equals("") && !pa.equals("")) {
            Intent intent = new Intent(context,MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_intro);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark, null));
            }
            context = this.getApplicationContext();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, Defined.TIME_OUT);
        }
    }
}
