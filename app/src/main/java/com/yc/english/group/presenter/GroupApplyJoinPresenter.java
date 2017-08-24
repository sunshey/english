package com.yc.english.group.presenter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hwangjr.rxbus.RxBus;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.english.base.presenter.BasePresenter;
import com.yc.english.group.constant.BusAction;
import com.yc.english.group.constant.GroupConstant;
import com.yc.english.group.contract.GroupApplyJoinContract;
import com.yc.english.group.model.bean.ClassInfoWarpper;
import com.yc.english.group.model.bean.GroupApplyInfo;
import com.yc.english.group.model.bean.StudentInfo;
import com.yc.english.group.model.bean.StudentInfoWrapper;
import com.yc.english.group.model.engin.GroupApplyJoinEngine;
import com.yc.english.group.utils.EngineUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.UserInfo;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanglin  on 2017/8/2 16:40.
 */

public class GroupApplyJoinPresenter extends BasePresenter<GroupApplyJoinEngine, GroupApplyJoinContract.View> implements GroupApplyJoinContract.Presenter {
    public GroupApplyJoinPresenter(Context context, GroupApplyJoinContract.View view) {
        super(context, view);
        mEngin = new GroupApplyJoinEngine(context);
    }

    @Override
    public void loadData(boolean forceUpdate, boolean showLoadingUI) {

    }

    /**
     * 申请加入班群
     *
     * @param user_id
     * @param sn
     */
    @Override
    public void applyJoinGroup(String user_id, String sn) {
        if (TextUtils.isEmpty(sn)) {
            ToastUtils.showShort("请输入班群号");
            return;
        }
        mView.showLoadingDialog("正在申请加入班级，请稍候");
        Subscription subscription = EngineUtils.applyJoinGroup(mContext, user_id, sn).subscribe(new Subscriber<ResultInfo<GroupApplyInfo>>() {
            @Override
            public void onCompleted() {
                mView.dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                mView.dismissLoadingDialog();
                LogUtils.e("onError: " + e.getMessage());
            }

            @Override
            public void onNext(final ResultInfo<GroupApplyInfo> stringResultInfo) {
                handleResultInfo(stringResultInfo, new Runnable() {
                    @Override
                    public void run() {
                        GroupApplyInfo applyInfo = stringResultInfo.data;
                        int vali_type = Integer.parseInt(applyInfo.getVali_type());

                        if (vali_type == GroupConstant.CONDITION_ALL_ALLOW) {
                            mView.apply(vali_type);
                            RxBus.get().post(BusAction.GROUP_LIST, "from groupjoin");
                        } else {
                            mView.apply(vali_type);
                        }
                    }
                });
            }
        });
        mSubscriptions.add(subscription);
    }

    /**
     * 根据群号查找群
     *
     * @param sn
     */

    @Override
    public void queryGroupById(Context context, String id, String sn) {

        Subscription subscription = EngineUtils.queryGroupById(context, id, sn).subscribe(new Subscriber<ResultInfo<ClassInfoWarpper>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(final ResultInfo<ClassInfoWarpper> stringResultInfo) {
                handleResultInfo(stringResultInfo, new Runnable() {
                    @Override
                    public void run() {
                        mView.showGroup(stringResultInfo.data.getInfo());
                    }
                });
            }
        });
        mSubscriptions.add(subscription);
    }

    @Override
    public void getMemberList(String sn, String status, String master_id, String flag) {
        Subscription subscription = EngineUtils.getMemberList(mContext, sn, status, master_id, flag).subscribe(new Subscriber<ResultInfo<StudentInfoWrapper>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(final ResultInfo<StudentInfoWrapper> studentInfoWrapperResultInfo) {
                handleResultInfo(studentInfoWrapperResultInfo, new Runnable() {
                    @Override
                    public void run() {
                        StudentInfoWrapper data = studentInfoWrapperResultInfo.data;
                        if (data != null && data.getList() != null && data.getList().size() > 0) {
                            List<UserInfo> list = new ArrayList<>();
                            for (StudentInfo studentInfo : data.getList()) {
                                list.add(new UserInfo(studentInfo.getUser_id(), studentInfo.getNick_name(), Uri.parse(studentInfo.getFace())));
                            }
                            mView.showMemberList(list);
                        }
                    }
                });

            }
        });
        mSubscriptions.add(subscription);
    }


}
