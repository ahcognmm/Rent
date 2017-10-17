package com.chatchat.chatchat.Adapter;

/**
 * Created by Khushvinders on 15-Nov-16.
 */

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chatchat.chatchat.Model.Link;
import com.chatchat.chatchat.R;

import java.util.ArrayList;

public class LinkAdapter extends BaseAdapter {

    private static ArrayList<Link> linkList;
    private Context mContext;
    private int mLayout;

    public LinkAdapter(Context context, int layout, ArrayList<Link> ev) {
        mContext = context;
        mLayout = layout;
        linkList = ev;
    }

    @Override
    public int getCount() {
        return linkList.size();
    }

    @Override
    public Object getItem(int position) {
        return linkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return linkList.indexOf(linkList.get(position));
    }

    @Override
    public View getView(int position, View vi, ViewGroup parent) {

        Link link = (Link) linkList.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(mLayout, null);

        // set Title
        TextView title = (TextView) vi.findViewById(R.id.tvLinkTitle);
        title.setText(link.title);

        return vi;
    }
}