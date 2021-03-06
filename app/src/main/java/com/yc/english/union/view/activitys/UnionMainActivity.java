package com.yc.english.union.view.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kk.guide.GuideCallback;
import com.kk.guide.GuidePopupWindow;
import com.kk.securityhttp.net.contains.HttpConfig;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.yc.english.R;
import com.yc.english.base.view.BaseToolBar;
import com.yc.english.base.view.FullScreenActivity;
import com.yc.english.base.view.StateView;
import com.yc.english.group.constant.BusAction;
import com.yc.english.group.model.bean.ClassInfo;
import com.yc.english.group.model.bean.StudentInfo;
import com.yc.english.group.view.activitys.teacher.GroupVerifyActivity;
import com.yc.english.main.hepler.UserInfoHelper;
import com.yc.english.main.model.domain.UserInfo;
import com.yc.english.union.contract.UnionCommonListContract;
import com.yc.english.union.presenter.UnionCommonListPresenter;
import com.yc.english.union.view.activitys.student.UnionJoinActivity;
import com.yc.english.union.view.activitys.teacher.UnionCreateActivity;
import com.yc.english.union.view.fragment.UnionAllFragment;
import com.yc.english.union.view.fragment.UnionFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wanglin  on 2017/7/24 17:59.
 */

public class UnionMainActivity extends FullScreenActivity<UnionCommonListPresenter> implements UnionCommonListContract.View {
    private static final String TAG = "UnionMainActivity";
    @BindView(R.id.sView_loading)
    StateView sViewLoading;
    @BindView(R.id.btn_create_class)
    Button btnCreateClass;
    @BindView(R.id.ll_empty_container)
    LinearLayout llEmptyContainer;
    @BindView(R.id.fiv_indicator)
    FixedIndicatorView fivIndicator;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.ll_data_container)
    LinearLayout llDataContainer;
    @BindView(R.id.content_view)
    FrameLayout contentView;
    @BindView(R.id.rootView)
    LinearLayout rootView;


    private GuidePopupWindow guidePopupWindow;

    private final String[] titles = new String[]{"所有的", "我创建的", "我参与的"};

    @Override
    public void init() {

        mPresenter = new UnionCommonListPresenter(this, this);
        mToolbar.setTitle(getString(R.string.english_union));

        mToolbar.showNavigationIcon();

        getData(true);
    }


    private void showCreateGuide() {
        GuidePopupWindow.Builder builder = new GuidePopupWindow.Builder();
        guidePopupWindow = builder.setDelay(1f).setTargetView(btnCreateClass).setCorner(5).setGuideCallback(new GuideCallback() {
            @Override
            public void onClick(GuidePopupWindow guidePopupWindow) {
                goToActivity(UnionCreateActivity.class);
                guidePopupWindow.dismiss();
            }
        }).build(this);
        guidePopupWindow.addCustomView(R.layout.guide_create_union_view, R.id.btn_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                guidePopupWindow.dismiss();
            }
        });

        guidePopupWindow.setDebug(true);
        guidePopupWindow.show(rootView, "create_union");

    }


    @Override
    public int getLayoutId() {
        return R.layout.group_union_list_main;
    }

    @OnClick({R.id.btn_create_class, R.id.btn_create_class1, R.id.btn_join_class, R.id.btn_join_class1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_class:
            case R.id.btn_create_class1:
                goToActivity(UnionCreateActivity.class);

                break;
            case R.id.btn_join_class:
            case R.id.btn_join_class1:
                goToActivity(UnionJoinActivity.class);
                break;
        }

    }

    private void goToActivity(Class activity) {
        if (!UserInfoHelper.isGotoLogin(this)) {
            startActivity(new Intent(this, activity));
        }

    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(BusAction.GROUP_LIST)
            }
    )
    public void getList(String group) {
        getData(false);
    }

    private ArrayList<ClassInfo> mClassInfos;

    @Override
    public void showUnionList(List<ClassInfo> classInfos, int page, boolean isFitst) {
        if (classInfos != null && classInfos.size() > 0) {
            if (guidePopupWindow != null && guidePopupWindow.isShowing()) {
                guidePopupWindow.dismiss();
            }
            mClassInfos = (ArrayList<ClassInfo>) classInfos;
            llDataContainer.setVisibility(View.VISIBLE);
            llEmptyContainer.setVisibility(View.GONE);
        } else {
            llDataContainer.setVisibility(View.GONE);
            llEmptyContainer.setVisibility(View.VISIBLE);
            hideStateView();
            if (ActivityUtils.isValidContext(this)) {
                showCreateGuide();
            }
        }
        if (isFitst) {
            initViewPager();
        }
    }

    @Override
    public void showMemberList(final List<StudentInfo> studentInfoList) {

        if (studentInfoList != null && studentInfoList.size() > 0) {
            mToolbar.setMenuIcon(R.mipmap.group65);
        } else {
            mToolbar.setMenuIcon(R.mipmap.group66);
        }
        invalidateOptionsMenu();

        mToolbar.setOnItemClickLisener(new BaseToolBar.OnItemClickLisener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(UnionMainActivity.this, GroupVerifyActivity.class);
                intent.putExtra("type", "1");
                intent.putParcelableArrayListExtra("studentList", (ArrayList<StudentInfo>) studentInfoList);
                startActivity(intent);
            }
        });
    }


    @Override
    public void showMyGroupList(List<ClassInfo> classInfos) {

    }

    @Override
    public void showCommonClassList(List<ClassInfo> list) {

    }

    @Override
    public void showIsMember(int is_member, ClassInfo classInfo) {

    }


    private void initViewPager() {
        fivIndicator.setAdapter(new MyAdapter());
        fivIndicator.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color
                .primary), 6));

        float unSelectSize = 15;
        float selectSize = 15;
        int selectColor = ContextCompat.getColor(this, R.color.primary);
        int unSelectColor = ContextCompat.getColor(this, R.color.black_333);
        fivIndicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));
        fivIndicator.setOnIndicatorItemClickListener(new Indicator.OnIndicatorItemClickListener() {
            @Override
            public boolean onItemClick(View clickItemView, int position) {
                viewpager.setCurrentItem(position);
                return false;
            }
        });
        fivIndicator.setCurrentItem(0, true);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        viewpager.setAdapter(mFragmentAdapter);
        viewpager.setCurrentItem(0);
        viewpager.setOffscreenPageLimit(3);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                fivIndicator.setCurrentItem(i, true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void hideStateView() {
        sViewLoading.hide();
    }

    @Override
    public void showNoNet() {
        sViewLoading.showNoNet(contentView, HttpConfig.NET_ERROR, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(true);

            }
        });
    }

    @Override
    public void showNoData() {
        hideStateView();
    }

    @Override
    public void showLoading() {
        sViewLoading.showLoading(contentView);
    }


    private UnionFragment unionCreateFragment;
    private UnionFragment unionJoinFragment;
    private UnionAllFragment unionAllFragment;


    private class FragmentAdapter extends FragmentStatePagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Bundle bundle = new Bundle();
                if (unionAllFragment == null) {
                    unionAllFragment = new UnionAllFragment();
                    bundle.putParcelableArrayList("classInfos", mClassInfos);
                    unionAllFragment.setArguments(bundle);
                    unionAllFragment.setType(1);
                }
                return unionAllFragment;
            } else if (position == 1) {
                if (unionCreateFragment == null) {

                    unionCreateFragment = new UnionFragment();
                    unionCreateFragment.setType(2);
                }
                return unionCreateFragment;
            } else if (position == 2) {

                if (unionJoinFragment == null) {
                    unionJoinFragment = new UnionFragment();
                    unionJoinFragment.setType(0);
                }
                return unionJoinFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private class MyAdapter extends Indicator.IndicatorAdapter {


        public MyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.weixin_tab, parent, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(titles[position]);
            return convertView;
        }
    }

    private void getData(boolean isFirst) {
        UserInfo userInfo = UserInfoHelper.getUserInfo();
        if (userInfo != null) {
            String uid = userInfo.getUid();
            mPresenter.getUnionList("1", 1, 10, isFirst);
            mPresenter.getMemberList("", "0", 1, 10, uid, "1");
        } else {
            showUnionList(null, 1, isFirst);
        }
    }

}