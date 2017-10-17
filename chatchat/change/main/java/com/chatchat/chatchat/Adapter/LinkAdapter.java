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

    public LinkAdapter(Context context, int layout, ArrayList<Link> ev){
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
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(mLayout, null);

        TextView frm = (TextView) vi.findViewById(R.id.textViewLinkTitle);
        frm.setText(link.title);

        TextView des = (TextView) vi.findViewById(R.id.link_des);
        des.setText(link.description);

        TextView url = (TextView) vi.findViewById(R.id.link_link);
        String text = "<a href=\"" + link.url + "\">" + link.url +"</a>";
        url.setText(Html.fromHtml(text));
        url.setMovementMethod(LinkMovementMethod.getInstance());

//        if (position % 2 == 0) {
//            frm.setBackgroundResource(R.drawable.bkg_forum1);
//        }else{
//            frm.setBackgroundResource(R.drawable.bkg_forum2);
//        }
        return vi;
    }
}