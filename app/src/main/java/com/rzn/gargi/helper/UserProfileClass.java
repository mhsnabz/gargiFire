package com.rzn.gargi.helper;

public class UserProfileClass {
    String  about;
    long age;
    long click;
    String gender;
    String job;
    String name ;
    String profileImage;
    String school;
    String thumb_image;
    int count;
    int totalRate;
    double rate;

    public UserProfileClass() {
    }

    public UserProfileClass(String about, long age, long click, String gender, String job, String name, String profileImage, String school, String thumb_image, int count, int totalRate, double rate) {
        this.about = about;
        this.age = age;
        this.click = click;
        this.gender = gender;
        this.job = job;
        this.name = name;
        this.profileImage = profileImage;
        this.school = school;
        this.thumb_image = thumb_image;
        this.count = count;
        this.totalRate = totalRate;
        this.rate = rate;
    }

    public String getAbout() {
        return about;
    }

    public long getAge() {
        return age;
    }

    public long getClick() {
        return click;
    }

    public String getGender() {
        return gender;
    }

    public String getJob() {
        return job;
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

    public int getCount() {
        return count;
    }

    public int getTotalRate() {
        return totalRate;
    }

    public double getRate() {
        return rate;
    }
}
