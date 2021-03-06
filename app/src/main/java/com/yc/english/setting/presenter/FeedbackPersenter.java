package com.yc.english.setting.presenter;

import android.content.Context;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.UIUitls;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.english.base.helper.TipsHelper;
import com.yc.english.base.presenter.BasePresenter;
import com.yc.english.base.view.IView;
import com.yc.english.setting.contract.FeedbackContract;
import com.yc.english.setting.model.engin.MyEngin;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by zhangkai on 2017/8/3.
 */

public class FeedbackPersenter extends BasePresenter<MyEngin, FeedbackContract.View> implements FeedbackContract.Presenter {
    public FeedbackPersenter(Context context, FeedbackContract.View iView) {
        super(context, iView);
        mEngin = new MyEngin(context);
    }

    @Override
    public void loadData(boolean forceUpdate, boolean showLoadingUI) {

    }

    @Override
    public void postMessage(final String message) {
        if (StringUtils.isEmpty(message) || message.trim().length() < 5) {
            TipsHelper.tips(mContext, "内容不少于5个字符");
            return;
        }
        mView.showLoadingDialog("正在发送, 请稍后");
        Subscription subscription = mEngin.postMessage(message).subscribe(new Subscriber<ResultInfo<String>>() {
            @Override
            public void onCompleted() {
                mView.dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                mView.dismissLoadingDialog();
            }

            @Override
            public void onNext(ResultInfo<String> resultInfo) {
                handleResultInfo(resultInfo, new Runnable() {
                    @Override
                    public void run() {
                        UIUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                TipsHelper.tips(mContext, "反馈成功，谢谢您的宝贵意见");
                                mView.emptyView();
                            }
                        });
                    }
                });
            }
        });
        mSubscriptions.add(subscription);
    }
}
