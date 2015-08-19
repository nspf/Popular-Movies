
package com.example.android.popularmovies.model;


import com.google.gson.annotations.Expose;


public class Genre {

    @Expose
    private Integer id;
    @Expose
    private String name;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
