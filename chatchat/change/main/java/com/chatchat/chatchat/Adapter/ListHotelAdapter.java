package com.chatchat.chatchat.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatchat.chatchat.Defined.Defined;
import com.chatchat.chatchat.Model.Hotel;
import com.chatchat.chatchat.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Thong Pham on 23/08/2017.
 */

public class ListHotelAdapter extends BaseAdapter {

    private Context mContext;
    private int mLayout;
    private List<Hotel> mev;
    private String language = "";
    private int height;

    public ListHotelAdapter(Context context, int layout, List<Hotel> ev, int height) {
        mContext = context;
        mLayout = layout;
        mev = ev;
        this.height = height;
    }

    @Override
    public int getCount() {
        return this.mev.size();
    }

    @Override
    public Object getItem(int i) {
        return this.mev.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.mev.indexOf(this.mev.get(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(mLayout, null);

        Hotel ho = mev.get(position);
        Date endTime = null, startTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            endTime = sdf.parse(ho.enddate);
            startTime = sdf.parse(ho.startdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long day = (endTime.getTime() - startTime.getTime()) / (24 * 60 * 60 * 1000);
        String[] str = ho.startdate.split("/");
        int startmonth = 1;
        startmonth = Integer.parseInt(str[1]);
        String startday = str[0];
        str = ho.enddate.split("/");
        int endmonth = 1;
        endmonth = Integer.parseInt(str[1]);
        String endday = str[0];

        //Days: stay at hotel
        TextView time = (TextView) convertView.findViewById(R.id.tvTime);
        time.setText(String.valueOf((int) day));

        //Hotel Name
        TextView name = (TextView) convertView.findViewById(R.id.tvHotelName);
        name.setText(ho.name);

        //Start Date

        TextView startDate = (TextView) convertView.findViewById(R.id.tvStartDate);
        startDate.setText(getDate(startmonth, startday));

        //End Date
        TextView endDate = (TextView) convertView.findViewById(R.id.tvEndDate);
        endDate.setText(getDate(endmonth, endday));

        //Start Day
        TextView startDay = (TextView) convertView.findViewById(R.id.tvStartDay);
        startDay.setText(getDay((String) DateFormat.format("EEEE", startTime)));

        //End Day
        TextView endDay = (TextView) convertView.findViewById(R.id.tvEndDAy);
        endDay.setText(getDay((String) DateFormat.format("EEEE", endTime)));

        //Image of hotel
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imvHotel);

        //image size, new version
        imageView.setMaxHeight(height);

        Picasso.with(mContext).load(ho.imagelink).into(imageView);
        return convertView;
    }

    public String getDate(int date, String day) {
        switch (date) {
            case 1:
                return day + " " + mContext.getString(R.string.jan);

            case 2:
                return day + " " + mContext.getString(R.string.feb);

            case 3:
                return day + " " + mContext.getString(R.string.mar);

            case 4:
                return day + " " + mContext.getString(R.string.apr);

            case 5:
                return day + " " + mContext.getString(R.string.may);

            case 6:
                return day + " " + mContext.getString(R.string.jun);

            case 7:
                return day + " " + mContext.getString(R.string.jul);

            case 8:
                return day + " " + mContext.getString(R.string.aug);

            case 9:
                return day + " " + mContext.getString(R.string.sep);

            case 10:
                return day + " " + mContext.getString(R.string.oct);

            case 11:
                return day + " " + mContext.getString(R.string.nov);

            case 12:
                return day + " " + mContext.getString(R.string.dec);

            default:
                return "";
        }
    }
    public String getDay(String day){
        switch (day){
            case "Sunday": return mContext.getString(R.string.Sunday);

            case "Monday": return mContext.getString(R.string.Monday);

            case "Tuesday": return mContext.getString(R.string.Tuesday);

            case "Wednesday": return mContext.getString(R.string.Wednesday);

            case "Thursday": return mContext.getString(R.string.Thursday);

            case "Friday": return mContext.getString(R.string.Friday);

            case "Saturday": return mContext.getString(R.string.Saturday);

            default: return "";
        }
    }


}
