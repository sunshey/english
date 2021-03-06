package com.yc.english.weixin.views.activitys;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.umeng.analytics.MobclickAgent;
import com.yc.english.R;
import com.yc.english.base.view.FullScreenActivity;
import com.yc.english.weixin.views.utils.TabsUtils;

import butterknife.BindView;

/**
 * Created by admin on 2018/1/17.
 */

public class TeacherAnswerTypeActivity extends FullScreenActivity {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.fiv_indicator)
    FixedIndicatorView mFixedIndicatorView;

    private final String[] titles = new String[]{"词汇", "语法", "句型", "作文", "听力"};
    private int index = 0;

    @Override
    public void init() {

        MobclickAgent.onEvent(this, "teacher_answer_click", "教材答案点击详情");

        Intent intent = getIntent();
        if (intent != null) {
            index = intent.getIntExtra("index", 0);
        }
        mToolbar.showNavigationIcon();

        mToolbar.setTitle("教材答案");
        mFixedIndicatorView.setAdapter(new TabsUtils.MyAdapter(this, titles));
        mFixedIndicatorView.setScrollBar(new ColorBar(this, ContextCompat.getColor(this, R.color
                .primary), 6));
        float unSelectSize = 15;
        float selectSize = 15;
        int selectColor = ContextCompat.getColor(this, R.color.primary);
        int unSelectColor = ContextCompat.getColor(this, R.color.black_333);
        mFixedIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));
        mFixedIndicatorView.setOnIndicatorItemClickListener(new Indicator.OnIndicatorItemClickListener() {
            @Override
            public boolean onItemClick(View clickItemView, int position) {
                mViewPager.setCurrentItem(position);
                return false;
            }
        });
        mFixedIndicatorView.setCurrentItem(index, true);

        TabsUtils.AnswerFragmentAdapter mFragmentAdapter = new TabsUtils.AnswerFragmentAdapter(getSupportFragmentManager(), new String[]{"9", "10", "11", "12", "13"});
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(index);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mFixedIndicatorView.setCurrentItem(i, true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_teacher_answer_type;
    }

}