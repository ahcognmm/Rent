package com.chatchat.chatchat.Model;

/**
 * Created by Thong Pham on 23/08/2017.
 */

public class Request {

    public String fo_code;

    public String fo_cu_username;

    public String fo_ho_username;

    public String fo_text;

    public String fo_id;

    public String fo_date;

    public int fo_days;

    public int n_answers;

    public Request(String fo_id, String fo_code, String fo_cu_username, String fo_ho_username,
                   String fo_text, String fo_date, int fo_days, int n_answers){
        this.fo_id = fo_id;
        this.fo_code = fo_code;
        this.fo_cu_username = fo_cu_username;
        this.fo_ho_username = fo_ho_username;
        this.fo_text = fo_text;
        this.fo_date = fo_date;
        this.fo_days = fo_days;
        this.n_answers = n_answers;
    }
}
