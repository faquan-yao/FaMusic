package com.yaofaquan.famusic.view.home.adpater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yaofaquan.famusic.model.CHANNEL;
import com.yaofaquan.famusic.view.discory.DiscoryFragment;
import com.yaofaquan.famusic.view.friend.FriendFragment;
import com.yaofaquan.famusic.view.mine.MineFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private CHANNEL[] mList;

    public HomePagerAdapter(FragmentManager fm, CHANNEL[] datas) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mList = datas;
    }

    @Override
    public Fragment getItem(int position) {
        int type = mList[position].getValue();
        switch (type) {
            case CHANNEL.MINE_ID:
                return MineFragment.newInstance();
            case CHANNEL.DISCORY_ID:
                return DiscoryFragment.newInstance();
            case CHANNEL.FRIEND_ID:
                return FriendFragment.newInstance();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
