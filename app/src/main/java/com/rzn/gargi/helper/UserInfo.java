package com.rzn.gargi.helper;

public class UserInfo {
    String about,
    face,
    insta,
    job,name,
    school,
    snap,burc,
    twitter;
    double lat,longLat,rate;
    long age, totalRate,count;

    public UserInfo(String about, String face, String insta, String job, String name, String school, String snap, String burc, String twitter, double lat, double longLat, double rate, long age, long totolRate, long count) {
        this.about = about;
        this.face = face;
        this.insta = insta;
        this.job = job;
        this.name = name;
        this.school = school;
        this.snap = snap;
        this.burc = burc;
        this.twitter = twitter;
        this.lat = lat;
        this.longLat = longLat;
        this.rate = rate;
        this.age = age;
        this.totalRate = totolRate;
        this.count = count;
    }

    public UserInfo() {
    }

    public double getRate() {
        return rate;
    }

    public long getTotalRate() {
        return totalRate;
    }

    public long getCount() {
        return count;
    }

    public double getLat() {
        return lat;
    }

    public double getLongLat() {
        return longLat;
    }

    public String getName() {
        return name;
    }

    public String getBurc() {
        return burc;
    }

    public long getAge() {
        return age;
    }

    public String getAbout() {
        return about;
    }

    public String getFace() {
        return face;
    }

    public String getInsta() {
        return insta;
    }

    public String getJob() {
        return job;
    }

    public String getSchool() {
        return school;
    }

    public String getSnap() {
        return snap;
    }

    public String getTwitter() {
        return twitter;
    }
}
