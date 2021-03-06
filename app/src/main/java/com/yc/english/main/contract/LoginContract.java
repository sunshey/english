package com.yc.english.main.contract;

import com.yc.english.base.presenter.IPresenter;
import com.yc.english.base.view.IDialog;
import com.yc.english.base.view.IFinish;
import com.yc.english.base.view.IView;

/**
 * Created by zhangkai on 2017/7/25.
 */

public interface LoginContract {
    interface View extends IView, IDialog , IFinish {
        void showPhone(String phone);
    }

    interface Presenter extends IPresenter {
        void login(String username, String pwd);
        void getPhone();
    }
}
