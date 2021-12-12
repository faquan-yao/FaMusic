package com.yaofaquan.famusic.view.discory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.yaofaquan.famusic.R;

public class DiscoryFunctionView extends RelativeLayout {
    private Context mContext;

    public DiscoryFunctionView(Context context) {
        this(context, null);
    }

    public DiscoryFunctionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        View rootView =
                LayoutInflater.from(mContext).inflate(R.layout.item_discory_head_function_layout, this);
    }
}

