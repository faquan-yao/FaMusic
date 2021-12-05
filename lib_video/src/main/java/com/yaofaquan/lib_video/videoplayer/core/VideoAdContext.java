package com.yaofaquan.lib_video.videoplayer.core;

import android.view.ViewGroup;

import com.yaofaquan.lib_video.videoplayer.VideoContextInterface;

public class VideoAdContext implements VideoAdSlot.SDKSlotListener {

    private ViewGroup mParentView;

    private VideoAdSlot mAdSlot;
    private String mInstance;

    private VideoContextInterface mListener;

    public VideoAdContext(ViewGroup parentView, String instance) {
        mParentView = parentView;
        mInstance = instance;
        load();
    }

    public void load() {
        if (mInstance != null) {
            mAdSlot = new VideoAdSlot(mInstance, this);
        } else {
            mAdSlot = new VideoAdSlot(null, this);
            if (mListener != null) {
                mListener.onVideoFailed();
            }
        }
    }

    public void destroy() {
        mAdSlot.destroy();
    }

    public void setAdResultListener(VideoContextInterface listener) {
        mListener = listener;
    }

    @Override
    public ViewGroup getAdParent() {
        return mParentView;
    }

    @Override
    public void onVideoLoadSuccess() {
        if (mListener != null) {
            mListener.onVideoSuccess();
        }
    }

    @Override
    public void onVideoFailed() {
        if (mListener != null) {
            mListener.onVideoFailed();
        }
    }

    @Override
    public void onVideoComplete() {
        if (mListener != null) {
            mListener.onVieoComplete();
        }
    }
}
