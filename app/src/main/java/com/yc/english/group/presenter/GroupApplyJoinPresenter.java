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
import com.yc.english.group.model.engin.GroupApplyJoinEngine;
import com.yc.english.group.rong.ImUtils;
import com.yc.english.group.rong.models.CodeSuccessResult;
import com.yc.english.group.rong.models.GroupUser;
import com.yc.english.group.rong.models.GroupUserQueryResult;
import com.yc.english.group.utils.EngineUtils;
import com.yc.english.main.hepler.UserInfoHelper;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by wanglin  on 2017/8/2 16:40.
 */

public class GroupApplyJoinPresenter extends BasePresenter<GroupApplyJoinEngine, GroupApplyJoinContract.View> implements GroupApplyJoinContract.Presenter {
    public GroupApplyJoinPresenter(Context context, GroupApplyJoinContract.View view) {
        super(view);
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
        Subscription subscription = mEngin.applyJoinGroup(user_id, sn).subscribe(new Subscriber<ResultInfo<GroupApplyInfo>>() {
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
                            joinGroup(applyInfo.getUser_id(), applyInfo.getClass_id(), applyInfo.getClass_name(), vali_type);
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
                LogUtils.e("queryGroupById", e.getMessage());
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

    /**
     * 加群
     *
     * @param
     * @param usre_id
     * @param groupId
     * @param groupName
     */
    private void joinGroup(String usre_id, final String groupId, final String groupName, final int vali_type) {
        final String[] userIds = new String[]{usre_id};
        ImUtils.queryGroup(groupId).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<GroupUserQueryResult>() {
            @Override
            public void call(GroupUserQueryResult groupUserQueryResult) {
                if (groupUserQueryResult.getCode() == 200) {
                    final List<GroupUser> users = groupUserQueryResult.getUsers();
                    ImUtils.joinGroup(userIds, groupId, groupName).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<CodeSuccessResult>() {
                        @Override
                        public void call(CodeSuccessResult codeSuccessResult) {
                            if (codeSuccessResult.getCode() == 200) {//加入成功
//                                ToastUtils.showShort("加入成功");
                                mView.apply(vali_type);


//                                mView.startGroupChat(groupId, groupName);
//                                ClassInfo info = new ClassInfo("", groupName, users.size() + "", Integer.parseInt(groupId));
//                                classInfoDao.insert(info);
                                RxBus.get().post(BusAction.GROUPLIST, "from groupjoin");
                            }
                        }
                    });
                } else {
                    ToastUtils.showShort("没有该群组，请重新输入");
                }
            }
        });

    }

    private UserInfo findUserById(String userId, String userName, Uri uri) {


        return new UserInfo(userId, userName, uri);

    }
}