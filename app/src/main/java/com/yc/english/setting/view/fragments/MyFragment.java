package com.yc.english.setting.view.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding.view.RxView;
import com.yc.english.R;
import com.yc.english.base.helper.GlideHelper;
import com.yc.english.base.helper.TipsHelper;
import com.yc.english.base.utils.QQUtils;
import com.yc.english.base.utils.StatusBarCompat;
import com.yc.english.base.view.BaseActivity;
import com.yc.english.base.view.BaseFragment;
import com.yc.english.base.view.HonourAbilityView;
import com.yc.english.base.view.QQqunDialog;
import com.yc.english.base.view.SharePopupWindow;
import com.yc.english.main.hepler.UserInfoHelper;
import com.yc.english.main.model.domain.Constant;
import com.yc.english.main.model.domain.UserInfo;
import com.yc.english.setting.contract.MyContract;
import com.yc.english.setting.model.bean.MyOrderInfo;
import com.yc.english.setting.model.bean.ScoreInfo;
import com.yc.english.setting.presenter.MyPresenter;
import com.yc.english.setting.view.activitys.CameraTaskActivity;
import com.yc.english.setting.view.activitys.FeedbackActivity;
import com.yc.english.setting.view.activitys.MyOrderActivity;
import com.yc.english.setting.view.activitys.PersonCenterActivity;
import com.yc.english.setting.view.activitys.SettingActivity;
import com.yc.english.setting.view.activitys.VipEquitiesActivity;
import com.yc.english.setting.view.popupwindows.FollowWeiXinPopupWindow;
import com.yc.english.setting.view.widgets.MenuItemView;
import com.yc.english.vip.views.activity.VipScoreTutorshipActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.functions.Action1;

/**
 * Created by zhangkai on 2017/7/24.
 */

public class MyFragment extends BaseFragment<MyPresenter> implements MyContract.View, QQqunDialog.QQqunClick {

    @BindView(R.id.iv_avatar)
    ImageView mAvatarImageView;

    @BindView(R.id.tv_nickname)
    TextView mNickNameTextView;

    @BindView(R.id.tv_school)
    TextView mSchoolTextView;

    @BindView(R.id.miv_buy_vip)
    MenuItemView mBuyVipMenuItemView;

    @BindView(R.id.miv_to_market)
    MenuItemView mMarketMenuItemView;

    @BindView(R.id.miv_weixin)
    MenuItemView mWeixinMenuItemView;

    @BindView(R.id.miv_qq)
    MenuItemView mQQMenuItemView;

    @BindView(R.id.miv_feedback)
    MenuItemView mFeedbackMenuItemView;

    @BindView(R.id.miv_share)
    MenuItemView mShareMenuItemView;

    @BindView(R.id.miv_setting)
    MenuItemView mSettingMenuItemView;

    @BindView(R.id.sv_content)
    NestedScrollView mContentScrollView;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.miv_my_order)
    MenuItemView mOrderMenuItemView;
    @BindView(R.id.iv_share)
    ImageView mIvShare;


    QQqunDialog qqunDialog;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.tv_support)
    TextView mTvSupport;

    @BindView(R.id.credit_view)
    HonourAbilityView abilityView;

    @BindView(R.id.toolbarWarpper)
    FrameLayout mToolbarWarpper;
    @BindView(R.id.ll_carmer_search)
    LinearLayout mLlCarmerSearch;
    @BindView(R.id.ll_add_score_tutorship)
    LinearLayout mLlAddScoreTutorship;
    @BindView(R.id.iv_tutorship_main_bg)
    ImageView ivTutorshipMainBg;


    @Override
    public void init() {
        mPresenter = new MyPresenter(getActivity(), this);

        qqunDialog = new QQqunDialog(getActivity());
        qqunDialog.setQqunClick(this);

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -appBarLayout.getHeight() + SizeUtils.dp2px(80)) {
                    mCollapsingToolbarLayout.setTitle(getString(R.string.main_tab_my) + "  ");
                    mAvatarImageView.setVisibility(View.GONE);
                    mNickNameTextView.setVisibility(View.GONE);
                } else {
                    mCollapsingToolbarLayout.setTitle("");
                    mAvatarImageView.setVisibility(View.VISIBLE);
                    mNickNameTextView.setVisibility(View.VISIBLE);
                }
            }
        });
        restoreScoreData();
        abilityView.setTitles(new String[]{"词汇", "口语", "听力", "语法", "阅读", "写作"}).setTitleColors(new int[]{Color
                .parseColor("#0cacfe"), Color.parseColor("#ff8b01"), Color.parseColor("#fdbb12"),
                Color.parseColor("#ff5252"), Color.parseColor("#97d107"), Color.parseColor("#b0eb02")});

        RxView.clicks(mAvatarImageView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!UserInfoHelper.isGotoLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), PersonCenterActivity.class);
                    startActivity(intent);
                }
            }
        });

        RxView.clicks(mNickNameTextView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!UserInfoHelper.isGotoLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), PersonCenterActivity.class);
                    startActivity(intent);
                }
            }
        });

        //TODO
        RxView.clicks(mBuyVipMenuItemView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!UserInfoHelper.isGotoLogin(getActivity())) {

                    Intent intent = new Intent(getActivity(), VipEquitiesActivity.class);

                    startActivity(intent);
                }
            }
        });

        RxView.clicks(mMarketMenuItemView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                try {
                    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    TipsHelper.tips(getActivity(), "你手机安装的应用市场没有上线该应用，请前往其他应用市场进行点评");
                }
            }
        });

        RxView.clicks(mWeixinMenuItemView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                FollowWeiXinPopupWindow followWeiXinPopupWindow = new FollowWeiXinPopupWindow(getActivity());
                followWeiXinPopupWindow.show(mRootView);
            }
        });

        RxView.clicks(mQQMenuItemView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                qqunDialog.show();
            }
        });


        RxView.clicks(mFeedbackMenuItemView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (!UserInfoHelper.isGotoLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                    startActivity(intent);
                }
            }
        });

        RxView.clicks(mShareMenuItemView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                SharePopupWindow sharePopupWindow = new SharePopupWindow(getActivity());
                sharePopupWindow.show(mContentScrollView);

            }
        });


        RxView.clicks(mSettingMenuItemView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
        RxView.clicks(mOrderMenuItemView).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), MyOrderActivity.class);

                startActivity(intent);
            }
        });

        StatusBarCompat.compat((BaseActivity) getActivity(), mToolbarWarpper, toolbar, R.mipmap.setting_head_bg2);
        RxView.clicks(mLlCarmerSearch).throttleFirst(200, TimeUnit.MICROSECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), CameraTaskActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(ivTutorshipMainBg).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = new Intent(getActivity(), VipScoreTutorshipActivity.class);
                startActivity(intent);
            }
        });

        RxView.clicks(mIvShare).throttleFirst(200, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                final SharePopupWindow sharePopupWindow = new SharePopupWindow(getActivity());
                sharePopupWindow.show();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if (UserInfoHelper.getUserInfo() != null && (UserInfoHelper.getUserInfo().getIsVip() == 1 || UserInfoHelper.getUserInfo().getIsVip() == 2 || UserInfoHelper.getUserInfo().getIsVip() == 3 || UserInfoHelper.getUserInfo().getIsVip() == 4)) {
            mBuyVipMenuItemView.setTitle("会员信息");

        } else {
            mBuyVipMenuItemView.setTitle("开通会员");
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.setting_fragment_my;
    }


    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.USER_INFO)
            }
    )
    @Override
    public void showUserInfo(UserInfo userInfo) {
        mPresenter.getAbilityScore(userInfo.getUid());

        GlideHelper.circleBorderImageView(getActivity(), mAvatarImageView, userInfo.getAvatar(), R.mipmap
                .default_avatar, 0.5f, Color.WHITE);

        if (!StringUtils.isEmpty(userInfo.getSchool())) {
            mSchoolTextView.setText(userInfo.getSchool());
        }

        if (!StringUtils.isEmpty(userInfo.getNickname())) {
            mNickNameTextView.setText(userInfo.getNickname());
        }


    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(Constant.NO_LOGIN)
            }
    )
    @Override
    public void showNoLogin(Boolean flag) {
        if (ActivityUtils.isValidContext(getActivity())) {
            mAvatarImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.mipmap.default_big_avatar));
            mNickNameTextView.setText("还没有登录，点击立即登录");
            mSchoolTextView.setText("");
            restoreScoreData();
        }

    }


    @Override
    public void showMyOrderInfoList(List<MyOrderInfo> list) {

    }

    @Override
    public void hideStateView() {

    }

    @Override
    public void showLoadingDialog(String msg) {

    }

    @Override
    public void dismissLoadingDialog() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void showNoNet() {

    }

    @Override
    public void showNoData() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void xiaoxueClick() {//  C9GzeOgLm4zrKerAk3Hr8gUiWsOhMzR7
        QQUtils.joinQQGroup(getActivity(), "mgo5d9WztUiBuZhK4JvmwMY11F_YIrmw");
    }

    @Override
    public void zhongxueClick() {
        QQUtils.joinQQZhongXueGroup(getActivity(), "njur_1bUpbe7G0-UcKu0ZZgx2r5CrnFV");
    }

    @Override
    public void showScoreResult(ScoreInfo data) {
        abilityView.setDatas(new float[]{data.getVocabulary() / 100f, data.getOracy() / 100f, data.getHearing() / 100f,
                data.getGrammar() / 100f, data.getRead() / 100f, data.getWriting() / 100f});
    }


    private void restoreScoreData() {
        abilityView.setDatas(new float[]{0f, 0f, 0f, 0f, 0f, 0f});
    }


}
