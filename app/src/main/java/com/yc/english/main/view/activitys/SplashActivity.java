package com.yc.english.main.view.activitys;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.UIUitls;
import com.bumptech.glide.Glide;
import com.yc.english.R;
import com.yc.english.base.view.BaseActivity;
import com.yc.english.base.utils.StatusBarCompat;
import com.yc.english.main.contract.SplashContract;
import com.yc.english.main.presenter.SplashPresenter;

import butterknife.BindView;

/**
 * Created by zhangkai on 2017/8/1.
 */

public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {


    @BindView(R.id.status_bar)
    View mStatusBar;

    private static final int Time = 1000;

    @Override
    public void init() {
        StatusBarCompat.light(this);
        StatusBarCompat.compat(this, mStatusBar);
        gotToMain();
    }

    @Override
    public int getLayoutId() {
        return R.layout.main_activity_splash;
    }

    @Override
    public void gotToMain() {
        UIUitls.postDelayed(Time, new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
