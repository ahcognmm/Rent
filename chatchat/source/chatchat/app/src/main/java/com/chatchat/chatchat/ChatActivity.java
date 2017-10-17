package com.chatchat.chatchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatchat.chatchat.Adapter.ChatAdapter;
import com.chatchat.chatchat.DB.DBHandle;
import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.ChatMessage;
import com.chatchat.chatchat.Model.Hotel;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Thong Pham on 24/08/2017.
 */

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private Context context;
    private Hotel hotel;
    private Intent intentService;

    private Button hotels;
    private Button profile;

    private TextView tvName;
    private TextView tvTime;
    private ImageView ivImage;

    private MyService mService;
    private boolean mBounded;
    private DBHandle db;

    private EditText msg_edittext;
    private String user1 = "";
    public  String user2 = "";
    private Random random;
    public static HashMap<String,ArrayList<ChatMessage>> hashmap;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;

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
        setContentView(R.layout.activity_chat);
        //set list
        context = getApplicationContext();
        SharedPreferences sh = getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        String us = sh.getString("Username","");
        String us2 = sh.getString("subscribe","");
        String pass = sh.getString("Password","");
        user1 = us.replace("@","__") + "@localhost";
        Intent it = getIntent();
        String name = it.getStringExtra("name");
        String imglink = it.getStringExtra("link");
        String from = it.getStringExtra("from");
        String to = it.getStringExtra("to");
        String city = it.getStringExtra("city");
        String country = it.getStringExtra("country");
        String username = it.getStringExtra("username");
        user2 = username + "@localhost";
        if(!user2.equals(us2)){
            SharedPreferences.Editor editor = sh.edit();
            editor.putString("subscribe", user2);
            editor.commit();
        }
        db = new DBHandle(context);
        hotel = new Hotel(username,name,city,country,from,to,imglink);
        profile = (Button) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle(context.getResources().getString(R.string.logout_prompt));
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
                        sharedPreferences.edit().clear().commit();
                        doUnbindService();
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

        tvName = (TextView)findViewById(R.id.textViewHotelName);
        tvName.setText(name);
        tvTime = (TextView)findViewById(R.id.textViewtime);
        tvTime.setText(from + " - " + to);
        ivImage = (ImageView)findViewById(R.id.imageViewHotel);
        Picasso.with(context).load(imglink).into(ivImage);

        hotels = (Button)findViewById(R.id.hotelbutton);
        hotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                doUnbindService();
                onBackPressed();
            }
        });
        profile = (Button)findViewById(R.id.profile);

        random = new Random();
//        user1 = getArguments().getString("username");
        msg_edittext = (EditText)findViewById(R.id.messageEditText);
        msgListView = (ListView) findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton)findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Defined.checkInternetConnection(context)) {
                    sendTextMessage(v);
                }
            }
        });

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
        hashmap = new HashMap<>();
        chatlist = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(ChatActivity.this, chatlist);
        msgListView.setAdapter(chatAdapter);
        if(Defined.checkInternetConnection(context)){
            new API_DoGetChatHistory(ChatActivity.this, us, pass ,username).execute();
//            doBindService(user2);
        }else{
            chatlist = db.getAllMessage(user2);
            chatAdapter.setArrayList(chatlist);
            chatAdapter.notifyDataSetChanged();
//            onBackPressed();
        }
    }

    public void sendTextMessage(View v) {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
//            Log.e("Sender and receiver: ",user1 + " " + user2);
            final ChatMessage chatMessage = new ChatMessage(user1, user2,
                    message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = CommonMethods.getCurrentDate();
            chatMessage.Time = CommonMethods.getCurrentTime();
            msg_edittext.setText("");
            db.addMessage(chatMessage,user1,CommonMethods.getCurrentDateTime(),user2);
//            chatAdapter.add(chatMessage, user2);
            chatlist.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
            this.getmService().xmpp.sendMessage(chatMessage);
        }
    }

//    @Override
//    protected void onDestroy() {
//        if(Defined.checkInternetConnection(context)){
//            doUnbindService();
//        }
//        super.onDestroy();
//    }

    @Override
    public void onBackPressed(){
        if(Defined.checkInternetConnection(context)){
            doUnbindService();
        }
        super.onBackPressed();
    }


    void doBindService(String to) {
        intentService = new Intent(getApplicationContext(),MyService.class);
        intentService.putExtra("receiver",to);
        startService(intentService);
        bindService(intentService, mConnection,
                Context.BIND_AUTO_CREATE);

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

    class API_DoGetChatHistory extends AsyncTask<String, Void, Void> {
        private Activity context;
        private ProgressDialog pDialog;
        private String jsonstr;
        private String us;
        private String us2;
        private String pa;

        API_DoGetChatHistory(Activity context, String us, String pa, String us2)
        {
            this.context = context;
            this.us = us;
            this.pa = pa;
            this.us2 = us2;
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
            String fromdate = db.getMaxSentDate(us2);
            JSONObject json = new JSONObject();
            try {
                json.put("ApiKey", Defined.API_KEY);
                json.put("Login", us);
                json.put("Password", pa);
                json.put("Hotel", us2);
                json.put("FromDate", fromdate);
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
                url = new URL(Defined.API_getChatHistory);
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
                String num1 = response.getString("num");
                int numrow = Integer.parseInt(num1);
                if (status.equals("ok")) {
                    if(numrow > 0){
                        String str1 = response.getString("chat");
                        db.loadChatFromServer(str1,chatlist);
                    }else{
                        chatlist = db.getAllMessage(user2);
                    }
                    chatAdapter.setArrayList(chatlist);
                    chatAdapter.notifyDataSetChanged();
                    doBindService(user2);
//                    Toast.makeText(context, "Get history ok!", Toast.LENGTH_SHORT).show();
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
