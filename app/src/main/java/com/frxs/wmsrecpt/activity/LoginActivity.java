package com.frxs.wmsrecpt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.frxs.common_base.utils.SystemUtils;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.wmsrecpt.MyApplication;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.comms.Config;
import com.frxs.wmsrecpt.model.UserInfo;
import com.frxs.wmsrecpt.rest.model.AjaxParams;
import com.frxs.wmsrecpt.rest.model.ApiResponse;
import com.frxs.wmsrecpt.rest.service.SimpleCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
public class LoginActivity extends MyBaseActivity {
    @BindView(R.id.user_name_et)
    EditText userNameEt;
    @BindView(R.id.user_name_layout)
    TextInputLayout userNameLayout;
    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    int keyDownNum = 0;
    private String[] environments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        String account = MyApplication.getInstance().getUserAccount();
        if (!TextUtils.isEmpty(account)) {
            userNameEt.setText(account);
            userNameEt.setSelection(userNameEt.length());
        }
    }

    @Override
    protected void initData() {
        initEnvironment();
    }

    private void initEnvironment() {
        environments = getResources().getStringArray(R.array.run_environments);
        for (int i = 0; i < environments.length; i++) {
            environments[i] = String.format(environments[i], Config.getBaseUrl(Config.TYPE_BASE, i));
        }
    }

    @OnClick({R.id.login_btn, R.id.select_environment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                userLogin();
            break;
            case R.id.select_environment:
                selectorEnvironment();
            break;
            default:
                break;
        }

    }

    private void userLogin() {
        String userAccount = userNameEt.getText().toString();
        String password = passwordEt.getText().toString();
        if (TextUtils.isEmpty(userAccount)) {
            passwordLayout.setError("");
            userNameLayout.setError(getString(R.string.tips_null_account));
            userNameEt.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            userNameLayout.setError("");
            passwordLayout.setError(getString(R.string.tips_null_password));
            passwordEt.requestFocus();
        } else {
            userNameLayout.setError("");
            passwordLayout.setError("");
            reqLogin(userAccount, password);
        }
    }

    private void selectorEnvironment() {
        keyDownNum++;
        if (keyDownNum == 9) {
            ToastUtils.showLongToast(LoginActivity.this, "再点击1次进入环境选择模式");
        }
        if (keyDownNum == 10) {
            ToastUtils.showLongToast(LoginActivity.this, "已进入环境选择模式");
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            final int spEnv = MyApplication.getInstance().getEnvironment();
            String env = spEnv < environments.length ? environments[spEnv] : "";
            dialog.setTitle(getResources().getString(R.string.tips_environment, env));
            dialog.setCancelable(false);
            dialog.setItems(environments, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, final int which) {
                    if (spEnv == which) {
                        return;
                    }
                    if (which != 0) {
                        final AlertDialog verifyMasterDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_evironments, null);
                        final EditText pswEt = contentView.findViewById(R.id.password_et);
                        contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TextUtils.isEmpty(pswEt.getText().toString().trim())) {
                                    ToastUtils.show(LoginActivity.this, "密码不能为空！");
                                    return;
                                }

                                if (!pswEt.getText().toString().trim().equals(getString(R.string.str_psw))) {
                                    ToastUtils.show(LoginActivity.this, "密码错误！");
                                    return;
                                }
                                MyApplication.getInstance().setEnvironment(which);//存储所选择环境
                                MyApplication.getInstance().resetRestClient();
                                verifyMasterDialog.dismiss();
                            }
                        });

                        contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                verifyMasterDialog.dismiss();
                            }
                        });
                        verifyMasterDialog.setView(contentView);
                        verifyMasterDialog.show();

                    } else {
                        MyApplication.getInstance().setEnvironment(which);//存储所选择环境
                        MyApplication.getInstance().resetRestClient();
                    }

                }
            });
            dialog.setNegativeButton(getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
            keyDownNum = 0;
        }
    }

    private void reqLogin(final String userAccount, String password) {
        if (!SystemUtils.checkNet(this) || !SystemUtils.isNetworkAvailable(this)) {
            ToastUtils.show(this, "网络不可用");
            return;
        }
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("UserAccount", userAccount);
        params.put("Password", password);
        params.put("UserType", 5);//value="1">拣货员; value="2">配送员; value="5">收货员; value="3">复核员;
        getService().UserLogin(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(ApiResponse<UserInfo> result, int code, String msg) {
                dismissProgressDialog();
                if (result.isSuccessful() && result.getData() != null) {
                    MyApplication application = MyApplication.getInstance();
                    application.setUserAccount(userAccount);

                    UserInfo userInfo = result.getData();
                    if (null != userInfo) {
                        application.setUserAccount(userInfo.getUserAccount());
                        application.setUserInfo(userInfo);
                        gotoActivity(HomeActivity.class, true);
                    }
                } else {
                    ToastUtils.show(LoginActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(LoginActivity.this, t.getMessage());
            }
        });
    }
}
