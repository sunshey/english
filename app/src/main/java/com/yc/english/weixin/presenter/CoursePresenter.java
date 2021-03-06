package com.yc.english.weixin.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.blankj.utilcode.util.UIUitls;
import com.kk.securityhttp.domain.ResultInfo;
import com.yc.english.base.helper.ResultInfoHelper;
import com.yc.english.base.helper.RxUtils;
import com.yc.english.base.presenter.BasePresenter;
import com.yc.english.base.utils.SimpleCacheUtils;
import com.yc.english.weixin.contract.CourseContract;
import com.yc.english.weixin.model.domain.CourseInfo;
import com.yc.english.weixin.model.domain.CourseInfoWrapper;
import com.yc.english.weixin.model.engin.WeixinEngin;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by zhangkai on 2017/8/30.
 */

public class CoursePresenter extends BasePresenter<WeixinEngin, CourseContract.View> implements CourseContract.Presenter {
    public static final String NEWSINFO = "newsListInfo";

    public CoursePresenter(Context context, CourseContract.View iView) {
        super(context, iView);
        mEngin = new WeixinEngin(context);
    }

    @Override
    public void loadData(boolean forceUpdate, boolean showLoadingUI) {

    }

    @Override
    public void getWeiXinList(final String type_id, final String page,
                              String page_size) {
        if (page.equals("1")) {
            mView.showLoading();

            SimpleCacheUtils.readCache(mContext, NEWSINFO + type_id, new SimpleCacheUtils.CacheRunnable() {
                @Override
                public void run() {
                    final List<CourseInfo> courseInfos = JSON.parseObject(getJson(), new TypeReference<List<CourseInfo>>() {
                    }
                            .getType());
                    cached = true;
                    UIUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            showNewsListInfo(courseInfos, type_id, page, false);
                        }
                    });
                }
            });
        }


        Subscription subscription = mEngin.getWeixinList(type_id, page, page_size).subscribe(new Subscriber<ResultInfo<CourseInfoWrapper>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (page.equals("1") && !cached) {
                    mView.showNoNet();
                }
            }

            @Override
            public void onNext(final ResultInfo<CourseInfoWrapper> courseInfoResultInfo) {
                ResultInfoHelper.handleResultInfo(courseInfoResultInfo, new ResultInfoHelper.Callback() {
                    @Override
                    public void resultInfoEmpty(String message) {
                        if (page.equals("1") && !cached) {
                            mView.showNoData();
                        }
                        mView.fail();
                    }

                    @Override
                    public void resultInfoNotOk(String message) {
                        if (page.equals("1") && !cached) {
                            mView.showNoData();
                        }
                        mView.fail();
                    }

                    @Override
                    public void reulstInfoOk() {
                        if (courseInfoResultInfo.data != null) {
                            showNewsListInfo(courseInfoResultInfo.data.getList(), type_id, page, true);
                        } else {
                            if(cached){
                                return;
                            }
                            if (page.equals("1")) {
                                mView.showNoData();
                            }
                            mView.end();
                        }
                    }
                });
            }
        });
        mSubscriptions.add(subscription);
    }

    private void showNewsListInfo(final List<CourseInfo> courseInfos,final String type_id, final String page, boolean isCache) {
        if (courseInfos != null && courseInfos.size() > 0) {
            if(isCache) {
                SimpleCacheUtils.writeCache(mContext, NEWSINFO + type_id, JSON.toJSONString(courseInfos));
            }
            mView.showWeixinList(courseInfos);
            if (page.equals("1")) {
                mView.hideStateView();
            }
        } else {
            if (page.equals("1")) {
                mView.showNoData();
            }
            mView.end();
        }
    }
}
