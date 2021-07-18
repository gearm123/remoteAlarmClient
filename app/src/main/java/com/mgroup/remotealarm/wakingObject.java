package com.mgroup.remotealarm;

public class wakingObject {

    private String status;
    private String waker;

    public wakingObject(String status,String waker){
        this.status = status;
        this.waker = waker;
    }

    public String getStatus(){
        return status;
    }

    public String getWaker(){
        return waker;
    }

}
