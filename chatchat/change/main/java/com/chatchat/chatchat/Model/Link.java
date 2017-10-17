package com.chatchat.chatchat.Model;

/**
 * Created by Thong Pham on 14/09/2017.
 */

public class Link {
    public String title;

    public String url;

    public String description;

    public int position;

    public Link(String title, String url, String description, int position){
        this.title = title;
        this.url = url;
        this.description = description;
        this.position = position;
    }

}
