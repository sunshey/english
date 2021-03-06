package com.yc.english.setting.contract;

import com.yc.english.base.presenter.IPresenter;
import com.yc.english.base.view.IDialog;
import com.yc.english.base.view.ILoading;
import com.yc.english.base.view.INoData;
import com.yc.english.base.view.INoNet;
import com.yc.english.base.view.IView;
import com.yc.english.pay.alipay.OrderInfo;
import com.yc.english.setting.model.bean.GoodInfo;
import com.yc.english.pay.PayWayInfo;

import java.util.List;

/**
 * Created by wanglin  on 2017/11/6 15:56.
 */

public interface GoodsListContract {

    interface View extends IView,ILoading,INoData,INoNet,IDialog{
        void showGoodVipList(List<GoodInfo> list);

        void showOrderInfo(OrderInfo data, String s, String money);
    }

    interface Presenter extends IPresenter{}
}
