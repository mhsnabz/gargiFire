package com.rzn.gargi.helper;

public class ModelUser  {
    String  about;

    long age;
    String burc;
    int chatCount;
    long click;
    String email;
    String gender;
    String job;
    double lat;
    double longLat;
    String name ;
    String profileImage;
    String school;
    String thumb_image;
     long count;
    long totalRate;
    double rate;

    String id;

    public ModelUser() {
    }

    public ModelUser(String about, long age, String burc, int chatCount, long click, String email, String gender, String job, double lat, double longLat, String name, String profileImage, String school, String thumb_image, long count, long totalRate, double rate, String id) {
        this.about = about;
        this.age = age;
        this.burc = burc;
        this.chatCount = chatCount;
        this.click = click;
        this.email = email;
        this.gender = gender;
        this.job = job;
        this.lat = lat;
        this.longLat = longLat;
        this.name = name;
        this.profileImage = profileImage;
        this.school = school;
        this.thumb_image = thumb_image;
        this.count = count;
        this.totalRate = totalRate;
        this.rate = rate;

        this.id = id;
    }

    public String getAbout() {
        return about;
    }

    public long getAge() {
        return age;
    }

    public String getBurc() {
        return burc;
    }

    public int getChatCount() {
        return chatCount;
    }

    public long getClick() {
        return click;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getJob() {
        return job;
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

    public String getProfileImage() {
        return profileImage;
    }

    public String getSchool() {
        return school;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public long getCount() {
        return count;
    }

    public long getTotalRate() {
        return totalRate;
    }

    public double getRate() {
        return rate;
    }

    public String getId() {
        return id;
    }
}
