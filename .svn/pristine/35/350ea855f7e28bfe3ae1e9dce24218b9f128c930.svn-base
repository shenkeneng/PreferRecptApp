package com.frxs.wmsrecpt.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.frxs.common_base.base.BaseActivity;
import com.frxs.common_base.utils.EasyPermissionsEx;
import com.frxs.common_base.utils.SystemUtils;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.wmsrecpt.MyApplication;
import com.frxs.wmsrecpt.comms.Config;
import com.frxs.wmsrecpt.model.Product;
import com.frxs.wmsrecpt.model.ReceivedOrderDetalise;
import com.frxs.wmsrecpt.model.UserInfo;
import com.frxs.wmsrecpt.rest.model.ApiRequest;
import com.frxs.wmsrecpt.rest.model.ApiResponse;
import com.frxs.wmsrecpt.rest.service.ApiService;
import com.frxs.wmsrecpt.rest.service.RequestListener;
import com.frxs.wmsrecpt.rest.service.SimpleCallback;
import java.util.List;
import retrofit2.Call;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/08/15
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public abstract class MyBaseActivity extends BaseActivity{

    private static final int MY_PERMISSIONS_REQUEST_WES = 1001;// 请求文件存储权限的标识码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(this instanceof SplashActivity)) {
            // 判断当前用户是否允许存储权限
            if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE})) {
                // 允许 - 执行更新方法
                if (MyApplication.getInstance().isNeedCheckUpgrade()) {
                    MyApplication.getInstance().prepare4Update(this, false);
                }
            } else {
                // 不允许 - 弹窗提示用户是否允许放开权限
                EasyPermissionsEx.executePermissionsRequest(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WES);
            }
        }
    }

    public ApiService getService() {
        return MyApplication.getRestClient(Config.TYPE_BASE).getApiService();
    }

    public void reLogin() {
        MyApplication.getInstance().logout();
        gotoActivity(LoginActivity.class, true);
    }

    public String getToken() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        return userInfo != null ? userInfo.getToken() : "";
    }

    public int getUserID() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        return userInfo != null ? userInfo.getEmpID() : 0;
    }

    public String getUserAccount() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        return userInfo != null ? userInfo.getUserAccount() : "";
    }

    public String getUserName() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        return userInfo != null ? userInfo.getEmpName() : "";
    }

    public int getWID() {
        UserInfo.SubWarehousesBean subWarehouses = MyApplication.getInstance().getSubWarehouses();
        return subWarehouses != null ? subWarehouses.getWID() : -1;
    }

    public int getSubWID() {
        UserInfo.SubWarehousesBean subWarehouses = MyApplication.getInstance().getSubWarehouses();
        return subWarehouses != null ? subWarehouses.getSubWID() : -1;
    }

    public String getSubWName() {
        UserInfo.SubWarehousesBean subWarehouses = MyApplication.getInstance().getSubWarehouses();
        return subWarehouses != null ? subWarehouses.getSubWName() : "";
    }

    public int getOpAreaID() {
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();
        return userInfo != null ? userInfo.getOpAreaID() : -1;
    }

    public boolean isHoneywell() {
        String brand = Build.BRAND;//SUNMI   Honeywell
        if (!TextUtils.isEmpty(brand) && brand.equals("Honeywell")) {
            return true;
        }else {
            return false;
        }
    }

    public void getProductInfo(String barcodeData, double vendorId, final RequestListener listener) {
        if (!SystemUtils.checkNet(this) || !SystemUtils.isNetworkAvailable(this)) {
            ToastUtils.show(this, "网络不可用");
            return;
        }
        showProgressDialog();
        ApiRequest params = new ApiRequest(this);
        params.put("FindKey", barcodeData);
        if (vendorId > -1) {
            params.put("VendorID", vendorId);
        }
        params.put("UserID", getUserID());
        params.put("UserName", getUserName());
        params.put("WID", getWID());
        params.put("SubWID", getSubWID());
        params.put("OpAreaID", getOpAreaID());
        getService().GetProductInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(ApiResponse<List<Product>> result, int code, String msg) {
                if (listener != null) {
                    listener.handleRequestResponse(result);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(MyBaseActivity.this, t.getMessage());
            }
        });
    }

    public void getReceivedOrderDetalis(String stockId, final RequestListener listener) {
        if (!SystemUtils.checkNet(this) || !SystemUtils.isNetworkAvailable(this)) {
            ToastUtils.show(this, "网络不可用");
            return;
        }
        showProgressDialog();
        ApiRequest params = new ApiRequest(this);
        params.put("StockInID", stockId);
        params.put("UserID", getUserID());
        params.put("UserName", getUserName());
        params.put("WID", getWID());
        params.put("OpAreaID", getOpAreaID());
        getService().getReceivedOrderDeatlis(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<ReceivedOrderDetalise>>() {
            @Override
            public void onResponse(ApiResponse<ReceivedOrderDetalise> result, int code, String msg) {
                if (listener != null) {
                    listener.handleRequestResponse(result);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReceivedOrderDetalise>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(MyBaseActivity.this, t.getMessage());
            }
        });
    }

    /**
     * 请求用户是否放开权限的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WES: {// 版本更新存储权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 已获取权限 继续运行应用
                    if (MyApplication.getInstance().isNeedCheckUpgrade()) {
                        MyApplication.getInstance().prepare4Update(this, false);
                    }
                } else {
                    // 不允许放开权限后，提示用户可在去设置中跳转应用设置页面放开权限。
                    if (!EasyPermissionsEx.somePermissionPermanentlyDenied(this, permissions)) {
                        EasyPermissionsEx.goSettings2PermissionsDialog(this, "需要文件存储权限来下载更新的内容,但是该权限被禁止,你可以到设置中更改");
                    }
                }
                break;
            }
        }
    }
}
