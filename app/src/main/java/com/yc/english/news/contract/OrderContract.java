package com.yc.english.news.contract;

import com.yc.english.base.presenter.IPresenter;
import com.yc.english.base.view.ILoading;
import com.yc.english.base.view.INoData;
import com.yc.english.base.view.INoNet;
import com.yc.english.base.view.IView;
import com.yc.english.news.model.domain.OrderParams;
import com.yc.english.pay.alipay.OrderInfo;

/**
 * 创建订单
 */

public interface OrderContract {
    interface View extends IView, ILoading, INoData, INoNet {
        void showOrderInfo(OrderInfo orderInfo);
    }

    interface Presenter extends IPresenter {
        void createOrder(OrderParams orderParams);
    }
}
