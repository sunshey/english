package com.yc.junior.english.main.view.activitys;

import android.content.Intent;
import android.view.View;

import com.kk.utils.UIUitls;
import com.yc.junior.english.R;
import com.yc.junior.english.base.view.BaseActivity;
import com.yc.junior.english.base.utils.StatusBarCompat;
import com.yc.junior.english.main.contract.SplashContract;
import com.yc.junior.english.main.presenter.SplashPresenter;

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