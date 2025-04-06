package com.example.restaurants_3;


public class Users {
    public String getUseraddress() {
        return useraddress;
    }

    public
    String  useraddress;
    double userLat;
    double userLon;
    String username;
    String userpassword;
    String email;
    String phone;

    public Users(double userLat, double userLon, String username,
                 String userpassword, String email) {

        this.userLat = userLat;
        this.userLon = userLon;
        this.username = username;
        this.userpassword = userpassword;
        this.email = email;
        this.phone = phone;
    }

    public double getUserLat() {
        return userLat;
    }

    public void setUserLat(double userLat) {
        this.userLat = userLat;
    }

    public double getUserLon() {
        return userLon;
    }

    public void setUserLon(double userLon) {
        this.userLon = userLon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
