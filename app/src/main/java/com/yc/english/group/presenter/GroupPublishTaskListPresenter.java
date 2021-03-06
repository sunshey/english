package com.yc.english.group.presenter;

import android.content.Context;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.english.base.helper.ResultInfoHelper;
import com.yc.english.base.model.BaseEngin;
import com.yc.english.base.presenter.BasePresenter;
import com.yc.english.group.contract.GroupPublishTaskListContract;
import com.yc.english.group.model.bean.TaskAllInfoWrapper;
import com.yc.english.group.utils.EngineUtils;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanglin  on 2017/8/8 15:52.
 */

public class GroupPublishTaskListPresenter extends BasePresenter<BaseEngin, GroupPublishTaskListContract.View> implements GroupPublishTaskListContract.Presenter {
    public GroupPublishTaskListPresenter(Context context, GroupPublishTaskListContract.View view) {
        super(context, view);
    }


    @Override
    public void loadData(boolean forceUpdate, boolean showLoadingUI) {

    }

    @Override
    public void getPublishTaskList(String publisher, String class_id) {
        mView.showLoading();
        Subscription subscription = EngineUtils.getPublishTaskList(mContext, publisher, class_id).subscribe(new Subscriber<ResultInfo<TaskAllInfoWrapper>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mView.showNoNet();
            }

            @Override
            public void onNext(final ResultInfo<TaskAllInfoWrapper> stringResultInfo) {
                ResultInfoHelper.handleResultInfo(stringResultInfo, new ResultInfoHelper.Callback() {
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
                        if (stringResultInfo.data != null) {

                            List<TaskAllInfoWrapper.TaskAllInfo> list = stringResultInfo.data.getList();
                            if (list != null && list.size() > 0) {
                                mView.showPublishTaskList(list);
                                mView.hideStateView();
                            } else {
                                mView.showNoData();
                            }
                        } else {
                            mView.showNoData();
                        }
                    }
                });
            }
        });
        mSubscriptions.add(subscription);
    }
}
