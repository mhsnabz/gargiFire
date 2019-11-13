package com.rzn.gargi.helper;

import java.util.Date;

public class NewMsgList
{
    String uid,currentUser;
    boolean seen;

    public NewMsgList() {
    }

    public NewMsgList(String uid, String currentUser, boolean seen) {
        this.uid = uid;
        this.currentUser = currentUser;
        this.seen = seen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }



    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
