package com.yaofaquan.famusic.model.User;

public class User {
    public String username;
    public String email;
    public String phone;
    public String avatar;
    public String intro;

    @Override
    public String toString() {
        return "User{" +
                "username=\'" + username + '\'' +
                ", email=\'" + email + '\'' +
                ", phone=\'" + phone + '\'' +
                ", avatar=\'" + avatar + '\'' +
                ", intro=\'" + intro + '\'' +
                '}';
    }
}
