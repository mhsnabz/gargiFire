package com.rzn.gargi.helper;

import java.util.Map;

public class getRate {
    double rate;
    Map<String,Object> map ;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public getRate(double rate, Map<String, Object> map) {
        this.rate = rate;
        this.map = map;
    }

    public getRate() {
    }


    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
