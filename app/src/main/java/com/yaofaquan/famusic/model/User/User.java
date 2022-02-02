package com.yaofaquan.famusic.model.User;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class User {
    @Unique
    public String username;
    @Unique
    public String email;
    @Unique
    public String phone;
    public String avatar;
    public String intro;

    @Generated(hash = 2090442887)
    public User(String username, String email, String phone, String avatar,
            String intro) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.intro = intro;
    }

    @Generated(hash = 586692638)
    public User() {
    }

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

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIntro() {
        return this.intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
