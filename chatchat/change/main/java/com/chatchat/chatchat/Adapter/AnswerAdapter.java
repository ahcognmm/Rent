package com.chatchat.chatchat.Adapter;

/**
 * Created by Khushvinders on 15-Nov-16.
 */

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chatchat.chatchat.Model.Answer;
import com.chatchat.chatchat.R;

import java.util.ArrayList;

public class AnswerAdapter extends BaseAdapter {

    private static ArrayList<Answer> answerList;
    private Context mContext;
    private int mLayout;
    private String username;

    public AnswerAdapter(Context context, int layout, ArrayList<Answer> ev, String us){
        mContext = context;
        mLayout = layout;
        answerList = ev;
        username = us;
    }

    @Override
    public int getCount() {
        return answerList.size();
    }

    @Override
    public Object getItem(int position) {
        return answerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return answerList.indexOf(answerList.get(position));
    }

    @Override
    public View getView(int position, View vi, ViewGroup parent) {
        Answer an = (Answer) answerList.get(position);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi = inflater.inflate(mLayout, null);

        LinearLayout ln = (LinearLayout) vi.findViewById(R.id.answer_layout);
        TextView frm = (TextView) vi.findViewById(R.id.textViewAnswerText);
        frm.setText(an.fa_text);
        if(an.fa_cu_username.equals(username)){
            ln.setBackgroundResource(R.drawable.bkg_answer1);
            ln.setGravity(Gravity.RIGHT);
        }else{
            ln.setBackgroundResource(R.drawable.bkg_answer2);
            ln.setGravity(Gravity.LEFT);
        }
        return vi;
    }
}