package com.chatchat.chatchat.Model;

/**
 * Created by Thong Pham on 23/08/2017.
 */

public class Hotel {

    public String name;

    public String city;

    public String country;

    public String startdate;

    public String enddate;

    public String username;

    public String imagelink;

    public String imagelink_large;

    public String startdate1;

    public String enddate1;

    public String startday;

    public String endday;

    public int numberday;

    public String zip = "";

    public Hotel(String username, String name, String city, String country, String startdate, String enddate, String imagelink){
        this.username = username;
        this.name = name;
        this.city = city;
        this.country = country;
        this.startdate = startdate;
        this.enddate = enddate;
        this.imagelink = imagelink;
    }

}
