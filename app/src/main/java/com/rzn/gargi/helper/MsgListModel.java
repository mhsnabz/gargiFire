package com.rzn.gargi.helper;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class MsgListModel
{
    String getterUid,senderUid;
    boolean seen;
    long timer;
    Timestamp time;

    public MsgListModel() {
    }

    public MsgListModel(String getterUid, String senderUid, boolean seen, long timer, Timestamp time) {
        this.getterUid = getterUid;
        this.senderUid = senderUid;
        this.seen = seen;
        this.timer = timer;
        this.time = time;
    }

    public String getGetterUid() {
        return getterUid;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public boolean isSeen() {
        return seen;
    }

    public long getTimer() {
        return timer;
    }

    public Timestamp getTime() {
        return time;
    }
}
