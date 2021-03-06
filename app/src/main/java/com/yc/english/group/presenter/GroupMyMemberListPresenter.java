package com.yc.english.group.presenter;

import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.UIUitls;
import com.hwangjr.rxbus.RxBus;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.english.base.helper.ResultInfoHelper;
import com.yc.english.base.helper.TipsHelper;
import com.yc.english.base.model.BaseEngin;
import com.yc.english.base.presenter.BasePresenter;
import com.yc.english.group.constant.BusAction;
import com.yc.english.group.contract.GroupMyMemberListContract;
import com.yc.english.group.model.bean.StudentInfoWrapper;
import com.yc.english.group.model.bean.StudentRemoveInfo;
import com.yc.english.group.utils.EngineUtils;
import com.yc.english.main.hepler.UserInfoHelper;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by wanglin  on 2017/8/3 16:46.
 */

public class GroupMyMemberListPresenter extends BasePresenter<BaseEngin, GroupMyMemberListContract.View> implements GroupMyMemberListContract.Presenter {
    public GroupMyMemberListPresenter(Context context, GroupMyMemberListContract.View view) {
        super(context, view);
    }

    @Override
    public void loadData(boolean forceUpdate, boolean showLoadingUI) {

    }

    @Override
    public void getMemberList(Context context, String class_id, int page, int page_size, String status, String master_id, String type) {
        mView.showLoading();
        Subscription subscription = EngineUtils.getMemberList(context, class_id, page, page_size, status, master_id, type).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResultInfo<StudentInfoWrapper>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showNoNet();
            }

            @Override
            public void onNext(final ResultInfo<StudentInfoWrapper> studentInfoWrapperResultInfo) {
                ResultInfoHelper.handleResultInfo(studentInfoWrapperResultInfo, new ResultInfoHelper.Callback() {
                    @Override
                    public void resultInfoEmpty(String message) {
                        mView.showNoNet();
                    }

                    @Override
                    public void resultInfoNotOk(String message) {
                        mView.showNoData();
                    }

                    @Override
                    public void reulstInfoOk() {

                        StudentInfoWrapper data = studentInfoWrapperResultInfo.data;
                        if (data != null && data.getList() != null && data.getList().size() > 0) {
                            mView.showMemberList(data.getList());
                            mView.hideStateView();
                        } else {
                            mView.showNoData();
                        }
                    }
                });

            }
        });

        mSubscriptions.add(subscription);
    }

    @Override
    public void exitGroup(final String class_id, final String master_id, String members) {
        mView.showLoadingDialog("正在退出，请稍候...");
        Subscription subscription = EngineUtils.deleteMember(mContext, class_id, master_id, members).subscribe(new Subscriber<ResultInfo<StudentRemoveInfo>>() {
            @Override
            public void onCompleted() {
                mView.dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                mView.dismissLoadingDialog();
            }

            @Override
            public void onNext(ResultInfo<StudentRemoveInfo> studentRemoveInfoResultInfo) {
                ResultInfoHelper.handleResultInfo(studentRemoveInfoResultInfo, new ResultInfoHelper.Callback() {
                    @Override
                    public void resultInfoEmpty(final String message) {
                        UIUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                TipsHelper.tips(mContext, message);
                            }
                        });
                        mView.dismissLoadingDialog();
                    }

                    @Override
                    public void resultInfoNotOk(final String message) {
                        UIUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                TipsHelper.tips(mContext, message);
                            }
                        });
                        mView.dismissLoadingDialog();
                    }

                    @Override
                    public void reulstInfoOk() {
                        RxBus.get().post(BusAction.FINISH, BusAction.REMOVE_GROUP);
                        RxBus.get().post(BusAction.GROUP_LIST, "exit group");
                        SPUtils.getInstance().remove(class_id + UserInfoHelper.getUserInfo().getUid());
                        mView.finish();
                    }
                });

            }
        });
        mSubscriptions.add(subscription);
    }


}
