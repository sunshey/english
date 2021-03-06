package com.yc.english.group.contract;

import android.content.Context;

import com.yc.english.base.presenter.IPresenter;
import com.yc.english.base.view.IDialog;
import com.yc.english.base.view.IFinish;
import com.yc.english.base.view.ILoading;
import com.yc.english.base.view.INoData;
import com.yc.english.base.view.INoNet;
import com.yc.english.base.view.IView;
import com.yc.english.group.model.bean.StudentInfo;

import java.util.List;

/**
 * Created by wanglin  on 2017/8/3 16:48.
 */

public interface GroupMyMemberListContract {
    interface View extends IView, ILoading, INoNet, INoData,IDialog,IFinish {
        void showMemberList(List<StudentInfo> list);

    }

    interface Presenter extends IPresenter {
        void getMemberList(Context context, String class_id,int page,int page_size, String status, String master_id,String flag);

        void exitGroup(String class_id, String master_id, String members);

    }

}
