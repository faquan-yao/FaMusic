package com.yaofaquan.famusic.model.User;

public class User {
    public String id;
    public String nick_name;
    public String head_photo;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", head_photo='" + head_photo + '\'' +
                '}';
    }
}
