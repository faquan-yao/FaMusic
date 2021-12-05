package com.yaofaquan.lib_video.videoplayer.core;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yaofaquan.lib_video.videoplayer.core.view.CustomVideoView;
import com.yaofaquan.lib_video.videoplayer.utils.Utils;

public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener{

    private Context mContext;

    private CustomVideoView mVideoView;
    private ViewGroup mParentView;

    //protected AudioService mAudioService;
    private String mXAdInstance;
    private SDKSlotListener mSlotListener;

    public VideoAdSlot(String adInstance, SDKSlotListener slotListener) {
        mXAdInstance = adInstance;
        mSlotListener = slotListener;
        mParentView = slotListener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView();
    }

    private void initVideoView() {
        mVideoView = new CustomVideoView(mContext);
        if (mXAdInstance != null) {
            mVideoView.setDataSource(mXAdInstance);
            mVideoView.setListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        mContext = null;
        mXAdInstance = null;
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickFullScreenBtn() {
        Bundle bundle = Utils.getViewProperty(mParentView);
        mParentView.removeView(mVideoView);

    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {

    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    @Override
    public void onAdVideoLoadComplete() {

    }

    public interface SDKSlotListener {

        ViewGroup getAdParent();

        void onVideoLoadSuccess();

        void onVideoFailed();

        void onVideoComplete();
    }
}
