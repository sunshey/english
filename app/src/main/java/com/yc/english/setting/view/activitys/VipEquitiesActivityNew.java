package com.yc.english.setting.view.activitys;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yc.english.R;
import com.yc.english.base.utils.StatusBarCompat;
import com.yc.english.base.view.BaseActivity;

import butterknife.BindView;

/**
 * Created by wanglin  on 2017/11/8 11:19.
 */

public class VipEquitiesActivityNew extends BaseActivity {


    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_school)
    TextView tvSchool;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void init() {
        toolbar.setNavigationIcon(R.mipmap.vip_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        StatusBarCompat.compat(this, appbarLayout, toolbar);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_vip_equity;
    }


}
