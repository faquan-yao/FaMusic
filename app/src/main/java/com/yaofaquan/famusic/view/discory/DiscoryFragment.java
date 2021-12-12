package com.yaofaquan.famusic.view.discory;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yaofaquan.famusic.R;
import com.yaofaquan.famusic.api.MockData;
import com.yaofaquan.famusic.api.RequestCenter;
import com.yaofaquan.famusic.model.discory.BaseRecommandModel;
import com.yaofaquan.famusic.model.discory.BaseRecommandMoreModel;
import com.yaofaquan.famusic.model.discory.RecommandBodyValue;
import com.yaofaquan.lib_common_ui.recyclerview.CommonAdapter;
import com.yaofaquan.lib_common_ui.recyclerview.base.ViewHolder;
import com.yaofaquan.lib_common_ui.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.yaofaquan.lib_common_ui.recyclerview.wrapper.LoadMoreWrapper;
import com.yaofaquan.lib_image_loader.app.ImageLoaderManager;
import com.yaofaquan.lib_network.okhttp.listener.DisposeDataListener;
import com.yaofaquan.lib_network.okhttp.utils.ResponseEntityToModule;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiscoryFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, LoadMoreWrapper.OnLoadMoreListener {

    private Context mContext;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private CommonAdapter mAdapter;
    private HeaderAndFooterWrapper mHeaderWrapper;
    private LoadMoreWrapper mLoadMoreWrapper;

    private BaseRecommandModel mRecommandData;
    private List<RecommandBodyValue> mDatas = new ArrayList<>();

    public static Fragment newInstance() {
        DiscoryFragment fragment = new DiscoryFragment();
        return fragment;
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
        View rootView = inflater.inflate(R.layout.fragment_discory_layout, null);
        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_light));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestData();
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }

    private void requestData() {
        RequestCenter.requestRecommandData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mRecommandData = (BaseRecommandModel) responseObj;
                updateView();
            }

            @Override
            public void onFailure(Object reasonObj) {
                onSuccess(
                        ResponseEntityToModule.parseJsonToModule(MockData.HOME_DATA, BaseRecommandModel.class));
            }
        });
    }

    private void loadMore() {
        RequestCenter.requestRecommandMore(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseRecommandMoreModel moreData = (BaseRecommandMoreModel) responseObj;
                mDatas.addAll(moreData.data.list);
                mLoadMoreWrapper.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Object reasonObj) {
                onSuccess(ResponseEntityToModule.parseJsonToModule(MockData.HOME_MORE_DATA,
                        BaseRecommandMoreModel.class));
            }
        });
    }

    private void updateView() {
        mSwipeRefreshLayout.setRefreshing(false); //停止刷新
        mDatas = mRecommandData.data.list;
        mAdapter = new CommonAdapter<RecommandBodyValue>(mContext, R.layout.item_discory_list_picture_layout, mDatas) {
            @Override
            protected void convert(ViewHolder holder, RecommandBodyValue recommandBodyValue, int position) {
                TextView titleView = holder.getView(R.id.title_view);
                if (TextUtils.isEmpty(recommandBodyValue.title)) {
                    titleView.setVisibility(View.GONE);
                } else {
                    titleView.setVisibility(View.VISIBLE);
                    titleView.setText(recommandBodyValue.title);
                }
                holder.setText(R.id.name_view, recommandBodyValue.text);
                holder.setText(R.id.play_view, recommandBodyValue.play);
                holder.setText(R.id.time_view, recommandBodyValue.time);
                holder.setText(R.id.zan_view, recommandBodyValue.zan);
                holder.setText(R.id.message_view, recommandBodyValue.msg);
                ImageView logo = holder.getView(R.id.logo_view);
                ImageLoaderManager.getInstance().displayImageForView(logo, recommandBodyValue.logo);
                ImageView avatar = holder.getView(R.id.author_view);
                ImageLoaderManager.getInstance().displayImageForCircle(avatar, recommandBodyValue.avatr);
            }
        };
        mHeaderWrapper = new HeaderAndFooterWrapper(mAdapter);
        DiscoryBannerView bannerView = new DiscoryBannerView(mContext, mRecommandData.data.head);
        mHeaderWrapper.addHeaderView(bannerView);
        DiscoryFunctionView functionView = new DiscoryFunctionView(mContext);
        mHeaderWrapper.addHeaderView(functionView);
        DiscoryRecommandView recommandView =
                new DiscoryRecommandView(mContext, mRecommandData.data.head);
        mHeaderWrapper.addHeaderView(recommandView);
        DiscoryNewView newView = new DiscoryNewView(mContext, mRecommandData.data.head);
        mHeaderWrapper.addHeaderView(newView);
        mLoadMoreWrapper = new LoadMoreWrapper(mHeaderWrapper);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(this);
        mRecyclerView.setAdapter(mLoadMoreWrapper);
    }
}
