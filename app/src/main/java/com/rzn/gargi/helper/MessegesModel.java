package com.rzn.gargi.helper;

public class MessegesModel {
    String data,getter,sender,msg;
    long time;

    public MessegesModel() {
    }

    public MessegesModel(String data, String getter, String sender, String msg, long time) {
        this.data = data;
        this.getter = getter;
        this.sender = sender;
        this.msg = msg;
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
