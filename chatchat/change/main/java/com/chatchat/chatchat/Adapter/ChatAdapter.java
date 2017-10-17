package com.chatchat.chatchat.Adapter;

/**
 * Created by Khushvinders on 15-Nov-16.
 */

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatchat.chatchat.ChatActivity;
import com.chatchat.chatchat.Model.ChatMessage;
import com.chatchat.chatchat.R;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private static ArrayList<ChatMessage> chatMessageList;

    public ChatAdapter(Activity activity, ArrayList<ChatMessage> list) {
        chatMessageList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setArrayList(ArrayList<ChatMessage> list){
        chatMessageList = list;
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = (ChatMessage) chatMessageList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.chatbubble, null);

        TextView msg = (TextView) vi.findViewById(R.id.message_text);
        TextView mst = (TextView) vi.findViewById(R.id.message_time);
//        Log.e("List : from:",message.getReceiver() + " to:" + message.getSender() + " - body:"+ message.body + " ismine:" + message.isMine);
        msg.setText(message.body);
        mst.setText(message.getTime());
        LinearLayout layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout_parent);

        // if message is mine then align to right
        if (message.isMine) {
            layout.setBackgroundResource(R.drawable.bubble2);
            parent_layout.setGravity(Gravity.RIGHT);
            layout.setGravity(Gravity.RIGHT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mst.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
            }
            mst.setTextColor(Color.WHITE);
            msg.setTextColor(Color.WHITE);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.bubble1);
            layout.setGravity(Gravity.LEFT);
            parent_layout.setGravity(Gravity.LEFT);
            mst.setTextColor(Color.BLACK);
            msg.setTextColor(Color.BLACK);
        }
        return vi;
    }

    public void add(ChatMessage object, String to) {
        if(ChatActivity.hashmap.containsKey(to)){
            if(!chatMessageList.equals(ChatActivity.hashmap.get(to))){
                chatMessageList = ChatActivity.hashmap.get(to);
            }
            chatMessageList.add(object);
        }else{
            chatMessageList = new ArrayList<ChatMessage>();
            chatMessageList.add(object);
            ChatActivity.hashmap.put(to,chatMessageList);
        }
//        Log.e("From: ", object.sender + " to:" +object.receiver + " - " + to + " ismine=" + object.isMine());
    }
}