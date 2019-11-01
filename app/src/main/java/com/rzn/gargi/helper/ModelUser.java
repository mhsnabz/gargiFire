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
    String userId;
    public ModelUser() {
    }

    public ModelUser(String about, long age, String burc, int chatCount, long click, String email, String gender, String job, double lat, double longLat, String name, String profileImage, String school, String thumb_image, long count, long totalRate, double rate, String userId) {
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
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(long totalRate) {
        this.totalRate = totalRate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getBurc() {
        return burc;
    }

    public void setBurc(String burc) {
        this.burc = burc;
    }

    public int getChatCount() {
        return chatCount;
    }

    public void setChatCount(int chatCount) {
        this.chatCount = chatCount;
    }

    public long getClick() {
        return click;
    }

    public void setClick(long click) {
        this.click = click;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongLat() {
        return longLat;
    }

    public void setLongLat(double longLat) {
        this.longLat = longLat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }





}
