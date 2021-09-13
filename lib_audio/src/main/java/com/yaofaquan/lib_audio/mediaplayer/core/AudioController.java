package com.yaofaquan.lib_audio.mediaplayer.core;

import com.yaofaquan.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

/**
 * 控制播放逻辑
 */
public class AudioController {

    public enum PlayMode {
        LOOP,
        RANDOM,
        REPEAT
    }

    private AudioPlayer mAudioPlayer;
    private ArrayList<AudioBean> mQueue;
    private PlayMode mPlayMode;
    private int mQueueIndex;

    private static class SingletonHolder {
        private static AudioController sInstance = new AudioController();
    }

    public static AudioController getInstance() {
        return SingletonHolder.sInstance;
    }

    private AudioController() {
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        mPlayMode = PlayMode.LOOP;
    }

    public ArrayList<AudioBean> getQueue() {
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    public void setQueue(ArrayList<AudioBean> queue) {
        mQueue = queue;
    }

    public void setQueue(ArrayList<AudioBean> queue, int queueIndex) {
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
    }

    public void addAudio(AudioBean bean, int index){
        if (mQueue == null) {
            return;
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            addAudio(bean, index);
            setPlayIndex(index);
        } else {
            AudioBean b = getNextPlaying();
            if (!b.id.equals(bean.id)) {
                setPlayIndex(query);
            }
        }
    }

    private void setPlayIndex(int query) {
        mQueueIndex = query;
        mAudioPlayer.load(getNextPlaying());
    }

    private int queryAudio(AudioBean bean) {
        return mQueue.indexOf(bean);
    }

    public void addAudio(AudioBean bean) {
        addAudio(bean, 0);
    }

    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(PlayMode mode) {
        mPlayMode = mode;
    }

    public int getQueueIndex() {
        return mQueueIndex;
    }

    public void setQueueIndex(int index) throws Exception {
        if (mQueue == null) {
            throw new Exception("播放队列为空");
        }
        mQueueIndex = index;
        play();
    }

    public void play() {
        AudioBean bean = getNowPlaying();
        mAudioPlayer.load(bean);
    }

    private AudioBean getNowPlaying() {
        if (mQueue != null && !mQueue.isEmpty() && mQueueIndex >= 0 && mQueueIndex < mQueue.size()) {
            return mQueue.get(mQueueIndex);
        } else {
            return null;
        }
    }

    public void pause() {
        mAudioPlayer.pause();
    }

    public void resume() {
        mAudioPlayer.resume();
    }

    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    public void next() {
        AudioBean bean = getNextPlaying();
        mAudioPlayer.load(bean);
    }

    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getNowPlaying();
    }

    public void previous() {
        AudioBean bean = getPreviousPlaying();
        mAudioPlayer.load(bean);
    }

    private AudioBean getPreviousPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex - 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getNowPlaying();
    }

    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }

    private boolean isPauseState() {
        return false;
    }

    private boolean isStartState() {
        return false;
    }
}