package com.frxs.wmsrecpt.rest.model;


import android.content.Context;

import com.frxs.common_base.utils.SystemUtils;
import com.frxs.wmsrecpt.MyApplication;
import com.frxs.wmsrecpt.model.UserInfo;

/**
 * Created by Endoon on 2016/4/12.
 */
public class ApiRequest extends AjaxParams {

    public ApiRequest(Context context) {
        super();

        UserInfo userInfo =  MyApplication.getInstance().getUserInfo();
        if (null != userInfo) {
            put("Token", userInfo.getToken());
            put("UserAccount", userInfo.getUserAccount());
            put("Version", SystemUtils.getAppVersion(context));
        }
    }
}