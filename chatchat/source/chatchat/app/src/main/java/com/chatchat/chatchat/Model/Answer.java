package com.chatchat.chatchat.Model;

/**
 * Created by Thong Pham on 23/08/2017.
 */

public class Answer {

    public String fa_id;

    public String fa_cu_username;

    public String fa_fo_id;

    public String fa_text;

    public String fa_date;

    public Answer(String fa_id, String fa_cu_username, String fa_fo_id, String fa_text, String fa_date){
        this.fa_id = fa_id;
        this.fa_cu_username = fa_cu_username;
        this.fa_fo_id = fa_fo_id;
        this.fa_text = fa_text;
        this.fa_date = fa_date;
    }

}
