package com.chatchat.chatchat.Adapter;

/**
 * Created by Khushvinders on 15-Nov-16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatchat.chatchat.Model.Request;
import com.chatchat.chatchat.R;

import java.util.ArrayList;

public class RequestAdapter extends BaseAdapter {

    private static ArrayList<Request> forumList;
    private Context mContext;
    private int mLayout;

    public RequestAdapter(Context context, int layout, ArrayList<Request> ev){
        mContext = context;
        mLayout = layout;
        forumList = ev;
    }

    @Override
    public int getCount() {
        return forumList.size();
    }

    @Override
    public Object getItem(int position) {
        return forumList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return forumList.indexOf(forumList.get(position));
    }

    @Override
    public View getView(int position, View vi, ViewGroup parent) {
        Request forum = (Request) forumList.get(position);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(mLayout, null);

        LinearLayout ln = (LinearLayout) vi.findViewById(R.id.request_layout);

        TextView frm = (TextView) vi.findViewById(R.id.textViewRequestText);
        frm.setText(forum.fo_text);

        TextView time = (TextView) vi.findViewById(R.id.request_time);
        time.setText(forum.fo_date);

        TextView answer = (TextView) vi.findViewById(R.id.request_answer);
//        Log.e("N answer","" + forum.n_answers);
        if(forum.n_answers < 2){
            answer.setText(forum.n_answers + " answer");
        }else{
            answer.setText(forum.n_answers + " answers");
        }

        if (position % 2 == 0) {
            ln.setBackgroundResource(R.drawable.bkg_forum1);
        }else{
            ln.setBackgroundResource(R.drawable.bkg_forum2);
        }
        return vi;
    }
}