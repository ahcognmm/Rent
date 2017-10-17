package com.chatchat.chatchat.Adapter;

/**
 * Created by Khushvinders on 15-Nov-16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chatchat.chatchat.Model.Forum;
import com.chatchat.chatchat.R;

import java.util.ArrayList;

public class ForumAdapter extends BaseAdapter {

    private static ArrayList<Forum> forumList;
    private Context mContext;
    private int mLayout;

    public ForumAdapter(Context context, int layout, ArrayList<Forum> ev){
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
        Forum forum = (Forum) forumList.get(position);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(mLayout, null);

        TextView frm = (TextView) vi.findViewById(R.id.txtnameforum);
        frm.setText(forum.name);

        if (position % 2 == 0) {
            frm.setBackgroundResource(R.drawable.bkg_forum1);
        }else{
            frm.setBackgroundResource(R.drawable.bkg_forum2);
        }
        return vi;
    }
}