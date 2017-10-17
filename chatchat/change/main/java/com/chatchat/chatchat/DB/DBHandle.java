package com.chatchat.chatchat.DB;

/**
 * Created by Thong Pham on 13/09/2017.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chatchat.chatchat.ChatActivity;
import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.ChatMessage;

import org.jivesoftware.smack.chat.Chat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by THONG_PHAM on 13/9/2017.
 */
public class DBHandle extends SQLiteOpenHelper {

    private Context context;
    private String user;
    private Random random;

    public DBHandle(Context context) {
        super(context, Defined.CHAT_DB_NAME, null, Defined.DATABASE_VERSION);
        this.context = context;
        SharedPreferences sh = context.getSharedPreferences(Defined.SHP_NAME, Activity.MODE_PRIVATE);
        String us = sh.getString("Username","");
        user = us.replace("@","__") + "@localhost";
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        int i;
        random = new Random();
        // CREATE TABLE IF NOT EXISTS Chat
        String sql = "CREATE TABLE IF NOT EXISTS " + Defined.CHAT_TABLE_NAME + " (";
        sql += Defined.CHAT_ATT[0] + " INTEGER primary key autoincrement,";
        for (i = 1; i < Defined.CHAT_ATT.length - 1; i++) {
            sql += Defined.CHAT_ATT[i] + " TEXT,";
        }
        sql += Defined.CHAT_ATT[i] + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + Defined.CHAT_TABLE_NAME);
        onCreate(db);
    }

    public void dropTable(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, null, null);
        db.close();
    }

    public boolean checkIsLastSentDate(String sentdate){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + Defined.CHAT_TABLE_NAME + " WHERE " +
                Defined.CHAT_ATT[1] + " = '" + sentdate + "'";
        Cursor cursor = db.rawQuery(sql, null) ;
        int number = cursor.getCount();
        if(number > 0){
            return true;
        }
        return false;
    }

    public void loadChatFromServer(String data, ArrayList<ChatMessage> ar){
        try {
            JSONArray arr = new JSONArray(data);
            if(arr.length() > 0) {
                if(ar == null) {
                    ar = new ArrayList<ChatMessage>();
                }
                boolean ismine = true;
                String from, to;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject inner = arr.getJSONObject(i);
                    from = inner.getString("fromJID");
                    to = inner.getString("toJID");
                    String date1 = inner.getString("date");
                    String time1 = inner.getString("time");
                    String sentdate = inner.getString("sentDate");
                    String body = inner.getString("body");

                    SQLiteDatabase db = this.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Defined.CHAT_ATT[1],sentdate);
                    values.put(Defined.CHAT_ATT[2],from);
                    values.put(Defined.CHAT_ATT[3],to);
                    values.put(Defined.CHAT_ATT[4],body);
                    values.put(Defined.CHAT_ATT[5],date1);
                    values.put(Defined.CHAT_ATT[6],time1);

                    db.insert(Defined.CHAT_TABLE_NAME, null, values);
                    db.close();

                    if(from.equals(user)){
                        ismine = true;
                    }else{
                        ismine = false;
                    }
                    ChatMessage art = new ChatMessage(from, to, body, "1", ismine);
                    art.setDate(date1);
                    art.setTime(time1);
                    art.setMsgID();
                    ar.add(art);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadAllChatsFromServer(String data){
        try {
            JSONArray arr = new JSONArray(data);
            if(arr.length() > 0) {
                boolean ismine = true;
                String from, to;
                String slave = "";
                SQLiteDatabase db = this.getWritableDatabase();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject inner = arr.getJSONObject(i);
                    from = inner.getString("fromJID");
                    to = inner.getString("toJID");
                    String date1 = inner.getString("date");
                    String time1 = inner.getString("time");
                    String sentdate = inner.getString("sentDate");
                    String body = inner.getString("body");


                    ContentValues values = new ContentValues();
                    values.put(Defined.CHAT_ATT[1],sentdate);
                    values.put(Defined.CHAT_ATT[2],from);
                    values.put(Defined.CHAT_ATT[3],to);
                    values.put(Defined.CHAT_ATT[4],body);
                    values.put(Defined.CHAT_ATT[5],date1);
                    values.put(Defined.CHAT_ATT[6],time1);

                    db.insert(Defined.CHAT_TABLE_NAME, null, values);

                    if(from.equals(user)){
                        ismine = true;
                        slave = to;
                    }else{
                        ismine = false;
                        slave = from;
                    }
                    ChatMessage art = new ChatMessage(from, to, body, "1", ismine);
                    art.setDate(date1);
                    art.setTime(time1);
                    art.setMsgID();

                    if(ChatActivity.hashmap.containsKey(slave)){
                        ChatActivity.hashmap.get(slave).add(art);
                    }else{
                        ArrayList<ChatMessage> ar = new ArrayList<ChatMessage>();
                        ar.add(art);
                        ChatActivity.hashmap.put(slave,ar);
                    }
                }
                db.close();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ChatMessage> loadAllChatFromServer(String data){
        dropTable(Defined.CHAT_TABLE_NAME);
        ArrayList<ChatMessage> ar = null;
        try {
            JSONArray arr = new JSONArray(data);
            if(arr.length() > 0) {
                ar = new ArrayList<ChatMessage>();
                boolean ismine = true;
                String from, to;

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject inner = arr.getJSONObject(i);
                    from = inner.getString("fromJID");
                    to = inner.getString("toJID");
                    String date1 = inner.getString("date");
                    String time1 = inner.getString("time");
                    String sentdate = inner.getString("sentDate");
                    String body = inner.getString("body");

                    ContentValues values = new ContentValues();
                    values.put(Defined.CHAT_ATT[1],sentdate);
                    values.put(Defined.CHAT_ATT[2],from);
                    values.put(Defined.CHAT_ATT[3],to);
                    values.put(Defined.CHAT_ATT[4],body);
                    values.put(Defined.CHAT_ATT[5],date1);
                    values.put(Defined.CHAT_ATT[6],time1);
                    SQLiteDatabase db = this.getWritableDatabase();
                    db.insert(Defined.CHAT_TABLE_NAME, null, values);
                    db.close();

                    if(from.equals(user)){
                        ismine = true;
                    }else{
                        ismine = false;
                    }
                    ChatMessage art = new ChatMessage(from, to, body, "1", ismine);
                    art.setDate(date1);
                    art.setTime(time1);
                    art.setMsgID();
                    ar.add(art);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ar;
    }

    public String getMaxSentDate(String us2){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MAX(id)," + Defined.CHAT_ATT[1]+ " FROM " + Defined.CHAT_TABLE_NAME;
        sql += " where (fromJID = '" + user + "' AND toJID = '" + us2 + "')";
        sql += " OR (fromJID = '" + us2 + "' AND toJID = '" + user + "')";
        Cursor cursor = db.rawQuery(sql, null) ;
        int number = cursor.getCount();
        if(number > 0){
            String sentdate = "";
            if (cursor.moveToFirst()) {
                sentdate = cursor.getString(1);
            }
            return sentdate;
        }else{
            return "";
        }
    }

    public String getMaximunSentDate(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MAX(id)," + Defined.CHAT_ATT[1]+ " FROM " + Defined.CHAT_TABLE_NAME;
        sql += " where fromJID = '" + user + "' OR toJID = '" + user + "'";
        Cursor cursor = db.rawQuery(sql, null) ;
        int number = cursor.getCount();
        if(number > 0){
            String sentdate = "";
            if (cursor.moveToFirst()) {
                sentdate = cursor.getString(1);
            }
            return sentdate;
        }else{
            return "";
        }
    }

    public void addMessage(ChatMessage cm, String sender, long sentDate, String to){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Defined.CHAT_ATT[1],sentDate);
        values.put(Defined.CHAT_ATT[2],sender);
        values.put(Defined.CHAT_ATT[3],to);
        values.put(Defined.CHAT_ATT[4],cm.body);
        values.put(Defined.CHAT_ATT[5],cm.getDate());
        values.put(Defined.CHAT_ATT[6],cm.getTime());

        db.insert(Defined.CHAT_TABLE_NAME, null, values);
        db.close();
    }



    public ArrayList<ChatMessage> getAllMessage(String ho_jid){
        ArrayList<ChatMessage> ar = null;
        ar = new ArrayList<ChatMessage>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + Defined.CHAT_TABLE_NAME;
        sql += " where (fromJID = '" + user + "' AND toJID = '" + ho_jid + "')";
        sql += " OR (fromJID = '" + ho_jid + "' AND toJID = '" + user + "')";
        Cursor cursor = db.rawQuery(sql, null) ;
        int number = cursor.getCount();
        boolean ismine = false;
        if(number > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(0);
                    String sentdate = cursor.getString(1);
                    String from = cursor.getString(2);
                    String to = cursor.getString(3);
                    String body = cursor.getString(4);
                    String time1 = cursor.getString(5);
                    String date1 = cursor.getString(6);
                    if(from.equals(user)) {
                        ismine = true;
                    }else {
                        ismine = false;
                    }
                    ChatMessage art = new ChatMessage(from, to, body,"1",ismine);
                    art.setDate(date1);
                    art.setTime(time1);
                    art.setMsgID();
                    ar.add(art);
                }while(cursor.moveToNext());
            }
        }
        return ar;
    }
}