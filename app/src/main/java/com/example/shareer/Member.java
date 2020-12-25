package com.example.shareer;

public class Member {
    private String name;
    private String Videourl;
    private String search;


    public Member() {
    }

    public Member(String name, String videourl) {
        this.name = name;
        Videourl = videourl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideourl() {
        return Videourl;
    }

    public void setVideourl(String videourl) {
        Videourl = videourl;
    }

}
