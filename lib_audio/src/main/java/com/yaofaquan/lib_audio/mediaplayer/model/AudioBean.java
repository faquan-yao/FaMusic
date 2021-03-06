package com.yaofaquan.lib_audio.mediaplayer.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

@Entity
public class AudioBean implements Serializable {

    private static final long serialVersionUID = -8849228294348905620L;

    @Generated(hash = 1701787808)
    public AudioBean(String id, @NotNull String mUrl, @NotNull String name, @NotNull String author,
                     @NotNull String album, @NotNull String albumInfo, @NotNull String albumPic,
                     @NotNull String totalTime) {
        this.id = id;
        this.mUrl = mUrl;
        this.name = name;
        this.author = author;
        this.album = album;
        this.albumInfo = albumInfo;
        this.albumPic = albumPic;
        this.totalTime = totalTime;
    }

    @Generated(hash = 1628963493) public AudioBean() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMUrl() {
        return this.mUrl;
    }

    public void setMUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumPic() {
        return this.albumPic;
    }

    public void setAlbumPic(String albumPic) {
        this.albumPic = albumPic;
    }

    public String getAlbumInfo() {
        return this.albumInfo;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getTotalTime() {
        return this.totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    @Id
    public String id;
    //??????
    @NotNull @Unique
    public String mUrl;

    //??????
    @NotNull public String name;

    //??????
    @NotNull public String author;

    //????????????
    @NotNull public String album;

    @NotNull public String albumInfo;

    //????????????
    @NotNull public String albumPic;

    //??????
    @NotNull public String totalTime;

    @Override public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof AudioBean)) {
            return false;
        }
        return ((AudioBean) other).id.equals(this.id);
    }

    @Override
    public String toString() {
        return "AudioBean{" +
                "id='" + id + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", album='" + album + '\'' +
                ", albumInfo='" + albumInfo + '\'' +
                ", albumPic='" + albumPic + '\'' +
                ", totalTime='" + totalTime + '\'' +
                '}';
    }
}
