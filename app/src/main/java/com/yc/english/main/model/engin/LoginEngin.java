package com.yc.english.main.model.engin;

import android.content.Context;

import com.kk.securityhttp.domain.ResultInfo;
import com.yc.english.base.model.BaseEngin;
import com.yc.english.group.model.bean.TokenInfo;
import com.yc.english.base.helper.EnginHelper;
import com.yc.english.main.model.domain.UserInfo;
import com.yc.english.main.model.domain.UserInfoWrapper;

import rx.Observable;

/**
 * Created by zhangkai on 2017/7/25.
 */

public class LoginEngin extends BaseEngin {
    public LoginEngin(Context context) {
        super(context);
    }

    public Observable<ResultInfo<UserInfoWrapper>> login(String username, String pwd){
        return EnginHelper.login(mContext, username, pwd);
    }

    public Observable<ResultInfo<TokenInfo>> getTokenInfo(String userId) {
        return EnginHelper.getTokenInfo(mContext, userId);
    }

}
