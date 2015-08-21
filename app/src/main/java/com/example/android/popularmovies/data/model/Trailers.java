
package com.example.android.popularmovies.data.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;


public class Trailers {

    @Expose
    private List<Object> quicktime = new ArrayList<Object>();
    @Expose
    private List<Youtube> youtube = new ArrayList<Youtube>();


    public List<Object> getQuicktime() {
        return quicktime;
    }

    public void setQuicktime(List<Object> quicktime) {
        this.quicktime = quicktime;
    }

    public List<Youtube> getYoutube() {
        return youtube;
    }

    public void setYoutube(List<Youtube> youtube) {
        this.youtube = youtube;
    }

}
