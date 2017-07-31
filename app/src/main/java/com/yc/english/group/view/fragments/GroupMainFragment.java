package com.yc.english.group.view.fragments;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.yc.english.R;
import com.yc.english.base.view.BaseToolBar;
import com.yc.english.base.view.ToolbarFragment;
import com.yc.english.group.contract.GroupListContract;
import com.yc.english.group.model.bean.ClassInfo;
import com.yc.english.group.presenter.GroupListPresenter;
import com.yc.english.group.view.activitys.GroupCreateActivity;
import com.yc.english.group.view.activitys.GroupJoinActivity;
import com.yc.english.group.view.activitys.GroupVerifyActivity;
import com.yc.english.group.view.adapter.GroupGroupAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wanglin  on 2017/7/24 17:59.
 */

public class GroupMainFragment extends ToolbarFragment implements GroupListContract.View {
    private static final String TAG = "GroupMainFragment";

    @BindView(R.id.btn_create_class)
    Button btnCreateClass;
    @BindView(R.id.btn_join_class)
    Button btnJoinClass;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private List<ClassInfo> mlist;
    private GroupGroupAdapter adapter;


    @Override
    public void init() {
        super.init();
        LogUtils.e(TAG, "init: " );
        mPresenter = new GroupListPresenter(getActivity(), this);
        mToolbar.setTitle(getString(R.string.group));

        mToolbar.setMenuIcon(R.mipmap.group57);
        mToolbar.setOnItemClickLisener(new BaseToolBar.OnItemClickLisener() {
            @Override
            public void onClick() {
                startActivity(new Intent(getActivity(), GroupVerifyActivity.class));
            }
        });


        if (mlist != null) {

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            adapter = new GroupGroupAdapter(getContext(), mlist);
            recyclerView.setAdapter(adapter);
            initData();

        } else {

        }


    }

    @Override
    public boolean isInstallToolbar() {
        return true;
    }

    private void initData() {
        if (mlist == null) {
            mlist.add(new ClassInfo("", "武汉街道口小学5年级一班", 20, 456666));
            mlist.add(new ClassInfo("", "武汉江夏区小学2年级三班", 15, 457777));
            mlist.add(new ClassInfo("", "武汉南湖小学3年级五班", 25, 458888));
            mlist.add(new ClassInfo("", "武汉洪山区小学6年级四班", 10, 45999));
            adapter.setData(mlist);
        }

    }

    @Override
    public int getLayoutId() {
        LogUtils.e(TAG, "getLayoutId: " );
        if (mlist == null) {
            return R.layout.group_fragment_class_start;
        } else {
            return R.layout.group_fragment_class_list;
        }
    }

    @OnClick({R.id.btn_create_class, R.id.btn_join_class})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_class:
                startActivity(new Intent(getActivity(), GroupCreateActivity.class));
                break;
            case R.id.btn_join_class:
                startActivity(new Intent(getActivity(), GroupJoinActivity.class));
                break;

        }

    }


    @Override
    public void showGroupList(List<ClassInfo> classInfos) {
        LogUtils.e(TAG, "showGroupList: " );
        mlist = classInfos;

        LogUtils.e(classInfos.size());
    }
}
