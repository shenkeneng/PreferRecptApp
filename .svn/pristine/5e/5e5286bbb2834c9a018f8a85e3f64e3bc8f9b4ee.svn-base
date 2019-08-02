package com.frxs.wmsrecpt;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import com.allenliu.versionchecklib.core.http.HttpParams;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.frxs.common_base.base.BaseActivity;
import com.frxs.common_base.base.BaseApplication;
import com.frxs.common_base.utils.SerializableUtil;
import com.frxs.common_base.utils.SharedPreferencesHelper;
import com.frxs.common_base.utils.SystemUtils;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.wmsrecpt.comms.Config;
import com.frxs.wmsrecpt.comms.GlobelDefines;
import com.frxs.wmsrecpt.model.AppVersionInfo;
import com.frxs.wmsrecpt.model.UserInfo;
import com.frxs.wmsrecpt.rest.RestClient;
import com.frxs.wmsrecpt.rest.model.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/08/15
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class MyApplication extends BaseApplication {

    private static SparseArray<RestClient> restClientSparseArray = new SparseArray<RestClient>();
    private UserInfo mUserInfo;// 用户信息
    private boolean needCheckUpgrade = true; // 是否需要检测更新
    private Activity mActivity;
    private DownloadBuilder builder;
    private BarcodeReader barcodeReader;

    public static MyApplication getInstance() {
        return (MyApplication)BaseApplication.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initData();

        initRestClient();

        AidcManager.create(this, new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                barcodeReader = aidcManager.createBarcodeReader();
                if (barcodeReader != null) {
                    Map<String, Object> properties = new HashMap<String, Object>();
                    properties.put(BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED, true);
                    barcodeReader.setProperties(properties);
                }
            }
        });
    }

    public BarcodeReader getBarcodeReader() {
        return barcodeReader;
    }

    public static RestClient getRestClient(int clientType) {
        return restClientSparseArray.get(clientType);
    }

    private void initRestClient() {
        int env = getEnvironment();
        restClientSparseArray.put(Config.TYPE_BASE, new RestClient(Config.getBaseUrl(Config.TYPE_BASE, env)));
        restClientSparseArray.put(Config.TYPE_UPDATE, new RestClient(Config.getBaseUrl(Config.TYPE_UPDATE, env)));
    }

    public void resetRestClient() {
        restClientSparseArray.clear();
        initRestClient();
    }

    private void initData() {
        // Get the user Info
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        String userStr = helper.getString(Config.KEY_USER, "");
        if (!TextUtils.isEmpty(userStr)) {
            Object object = null;
            try {
                object = SerializableUtil.str2Obj(userStr);
                if (null != object) {
                    mUserInfo = (UserInfo) object;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setEnvironment(int environmentId) {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        helper.putValue(GlobelDefines.KEY_ENVIRONMENT, environmentId);
    }

    public int getEnvironment() {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        return helper.getInt(GlobelDefines.KEY_ENVIRONMENT, Config.networkEnv);
    }

    public void setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;

        String userStr = "";
        try {
            userStr = SerializableUtil.obj2Str(userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        helper.putValue(Config.KEY_USER, userStr);
    }

    public UserInfo getUserInfo() {
        if (null == mUserInfo) {
            initData();
        }

        return mUserInfo;
    }

    public UserInfo.SubWarehousesBean getSubWarehouses() {
        UserInfo.SubWarehousesBean subWarehousesBean = null;
        for (UserInfo.SubWarehousesBean bean : mUserInfo.getSubWarehouses()) {
            if(bean.getSubType() == 1) {
                subWarehousesBean = bean;
            }
        }
        return subWarehousesBean;
    }

    public String getUserAccount() {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        return helper.getString(GlobelDefines.KEY_USER_ACCOUNT, "");
    }

    public void setUserAccount(String userAccount) {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        helper.putValue(GlobelDefines.KEY_USER_ACCOUNT, userAccount);
    }

    public void logout() {
        // 清空用户信息
        setUserInfo(null);
    }

    public void exitApp(int code) {
        System.exit(code);
    }

    public boolean isNeedCheckUpgrade() {
        return needCheckUpgrade;
    }

    /**
     * 更新版本的网路请求
     *
     * @param activity
     */
    public void prepare4Update(final Activity activity, final boolean isShow) {
        if (!SystemUtils.checkNet(this) || !SystemUtils.isNetworkAvailable(this)) {
            ToastUtils.show(this, "网络不可用");
            return;
        }
        mActivity = activity;
        ((BaseActivity) mActivity).showProgressDialog();
        //开始检测了升级之后，设置标志位为不再检测升级
        if (needCheckUpgrade) {
            needCheckUpgrade = false;
        }

        String url = Config.getBaseUrl(Config.TYPE_UPDATE, getEnvironment()) + "AppVersion/AppVersionUpdateGet";
        HttpParams httpParams = new HttpParams();
        httpParams.put("SysType", 0); // 0:android;1:ios
        httpParams.put("AppType", 2); //  0: 复核APP  1: 拣货APP  2: 收货APP
        builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(url)
                .setRequestMethod(HttpRequestMethod.POSTJSON)
                .setRequestParams(httpParams)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        ((BaseActivity) mActivity).dismissProgressDialog();
                        Type type = new TypeToken<ApiResponse<AppVersionInfo>>() {
                        }.getType();
                        ApiResponse<AppVersionInfo> respData = new Gson().fromJson(result, type);
                        int versionCode = Integer.valueOf(SystemUtils.getVersionCode(getApplicationContext()));
                        if (respData.getData() == null) {
                            ToastUtils.show(activity, "更新接口无数据");
                            return null;
                        }
                        if (versionCode >= respData.getData().getCurCode()) {
                            ToastUtils.show(activity, "已是最新版本");
                            return null;
                        }
                        if (respData.getData().getUpdateFlag() == 0) {
                            return null;
                        }
                        if (respData.getData().getUpdateFlag() == 2) {
                            builder.setForceUpdateListener(new ForceUpdateListener() {
                                @Override
                                public void onShouldForceUpdate() {
                                    forceUpdate();
                                }
                            });
                        }
                        return crateUIData(respData.getData().getDownUrl(), respData.getData().getUpdateRemark());
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {
                        ((BaseActivity) mActivity).dismissProgressDialog();
                        ToastUtils.show(activity, "request failed");

                    }
                });
        builder.setShowNotification(true);
        builder.setShowDownloadingDialog(true);
        builder.setShowDownloadFailDialog(true);
        builder.setForceRedownload(true);
        builder.excuteMission(activity);
    }

    /**
     * @return
     * @important 使用请求版本功能，可以在这里设置downloadUrl
     * 这里可以构造UI需要显示的数据
     * UIData 内部是一个Bundle
     */
    private UIData crateUIData(String downloadUrl, String updateRemark) {
        UIData uiData = UIData.create();
        uiData.setTitle(getString(R.string.update_title));
        uiData.setDownloadUrl(downloadUrl);
        uiData.setContent(updateRemark);
        return uiData;
    }

    /**
     * 强制更新操作
     */
    private void forceUpdate() {
        mActivity.finish();
    }
}
