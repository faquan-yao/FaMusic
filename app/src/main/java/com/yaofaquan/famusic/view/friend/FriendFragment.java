package com.yaofaquan.famusic.view.friend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yaofaquan.famusic.R;
import com.yaofaquan.famusic.api.RequestCenter;
import com.yaofaquan.famusic.model.friend.BaseFriendModel;
import com.yaofaquan.famusic.model.friend.FriendBodyValue;
import com.yaofaquan.famusic.view.friend.adpater.FriendRecyclerAdapter;
import com.yaofaquan.lib_common_ui.recyclerview.wrapper.LoadMoreWrapper;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataListener;
import com.yaofaquan.lib_network.okhttp.utils.ResponseEntityToModule;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        LoadMoreWrapper.OnLoadMoreListener {

    private Context mContext;
    /*
     * UI
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private FriendRecyclerAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;
    /*
     * data
     */
    private BaseFriendModel mRecommandData;
    private List<FriendBodyValue> mDatas = new ArrayList<>();

    public static Fragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    private FriendFragment() {

    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_layout, null);
        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_red_light));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        requestData();
    }

    @Override
    public void onRefresh() {
        requestData();
    }


    @Override
    public void onLoadMoreRequested() {

    }

    private void requestData() {
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mRecommandData = (BaseFriendModel) responseObj;
                updateView();
            }

            @Override
            public void onFailure(Object reasonObj) {
//                onSuccess(ResponseEntityToModule.parseJsonToModule(MockData.FRIEND_DATA,
//                        BaseFriendModel.class));
            }
        });
    }

    private void loadMore() {
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseFriendModel moreData = (BaseFriendModel) responseObj;
                mDatas.addAll(moreData.data.list);
                mLoadMoreWrapper.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Object reasonObj) {
//                onSuccess(
//                        ResponseEntityToModule.parseJsonToModule(MockData.FRIEND_DATA, BaseFriendModel.class));
            }
        });
    }

    private void updateView() {
        mSwipeRefreshLayout.setRefreshing(false); //停止刷新
        mDatas = mRecommandData.data.list;
        mAdapter = new FriendRecyclerAdapter(mContext, mDatas);
        mLoadMoreWrapper = new LoadMoreWrapper(mAdapter);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(this);
        mRecyclerView.setAdapter(mLoadMoreWrapper);
    }
}
