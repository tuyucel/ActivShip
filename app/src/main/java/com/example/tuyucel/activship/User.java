package com.example.tuyucel.activship;


public class User {
    private String userName;
    private String uId;

    public User(String userName, String uId) {
        this.userName = userName;
        this.uId = uId;

    }
    public User(){

    }
    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}