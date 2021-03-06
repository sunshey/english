package com.yc.english.main.contract;

import com.yc.english.base.presenter.IPresenter;
import com.yc.english.base.view.IDialog;
import com.yc.english.base.view.IFinish;
import com.yc.english.base.view.IView;

/**
 * Created by zhangkai on 2017/7/26.
 */

public interface RegisterContract {
    interface View extends IView, IDialog, IFinish {
        void codeRefresh();
    }

    interface Presenter extends IPresenter {
        void sendCode(String mobile);
        void register(String mobile, String pwd, String code);
    }
}
