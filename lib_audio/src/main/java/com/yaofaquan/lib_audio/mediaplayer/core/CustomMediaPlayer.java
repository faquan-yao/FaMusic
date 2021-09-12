package com.yaofaquan.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;

import java.io.IOException;

public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener{

    public enum Status {
        IDEL, INITALIZED, STARTED, PAUSED, STOPPED, COMPLETED;
    }

    private Status mState = Status.IDEL;
    private OnCompletionListener mCompletionListener;
    public CustomMediaPlayer() {
        super();
        mState = Status.IDEL;
        super.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mState = Status.COMPLETED;
    }

    @Override
    public void reset() {
        super.reset();
        mState = Status.IDEL;
    }

    @Override
    public void setDataSource(String path) throws IOException {
        super.setDataSource(path);
        mState = Status.INITALIZED;
    }

    @Override
    public int getCurrentPosition() {
        return super.getCurrentPosition();
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {
        super.setDisplay(sh);
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mState = Status.STARTED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mState = Status.STOPPED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mState = Status.PAUSED;
    }

    public Status getState() {
        return mState;
    }

    public boolean isComplete() {
        return mState == Status.COMPLETED;
    }

    public void setCompletionListener(OnCompletionListener listener) {
        mCompletionListener = listener;
    }
}
