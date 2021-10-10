package com.yaofaquan.famusic.model.friend;

import com.yaofaquan.famusic.model.BaseModel;
import com.yaofaquan.lib_audio.mediaplayer.model.AudioBean;

import java.util.ArrayList;

public class FriendBodyValue extends BaseModel {

    public int type;
    public String avatr;
    public String name;
    public String fans;
    public String text;
    public ArrayList<String> pics;
    public String videoUrl;
    public String zan;
    public String msg;
    public AudioBean audioBean;
}
