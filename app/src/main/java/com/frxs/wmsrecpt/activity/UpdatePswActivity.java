package com.frxs.wmsrecpt.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.frxs.common_base.utils.InputUtils;
import com.frxs.common_base.utils.SystemUtils;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.rest.model.ApiRequest;
import com.frxs.wmsrecpt.rest.model.ApiResponse;
import com.frxs.wmsrecpt.rest.service.SimpleCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * 修改用户密码界面
 * Created by Endoon on 2016/3/31.
 */
public class UpdatePswActivity extends MyBaseActivity{
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.old_password_et)
    EditText oldPasswordEt;
    @BindView(R.id.old_password_layout)
    TextInputLayout oldPasswordLayout;
    @BindView(R.id.new_pw_et)
    EditText newPwEt;
    @BindView(R.id.new_pw_layout)
    TextInputLayout newPwLayout;
    @BindView(R.id.repeat_new_pw_et)
    EditText repeatNewPwEt;
    @BindView(R.id.repeat_new_pw_layout)
    TextInputLayout repeatNewPwLayout;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_psw);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        titleTv.setText(R.string.title_update_pwd);
    }

    private void reqUpdatePws(String oldPassword, String newPassword) {
        if (!SystemUtils.checkNet(this) || !SystemUtils.isNetworkAvailable(this)) {
            ToastUtils.show(this, "网络不可用");
            return;
        }
        showProgressDialog();
        ApiRequest params = new ApiRequest(this);
        params.put("EmpID", getUserID());
        params.put("Password",oldPassword);
        params.put("NewPassword", newPassword);
        params.put("UserID", getUserID());
        params.put("UserName", getUserName());

        getService().UserEditPassword(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(ApiResponse<Boolean> result, int code, String msg) {
                dismissProgressDialog();
                if (result.isSuccessful() && result.getData()) {
                    ToastUtils.show(UpdatePswActivity.this, "修改成功");
                    finish();
                } else {
                    if (result.isAuthenticationFailed()) {
                        ToastUtils.show(UpdatePswActivity.this, getString(R.string.authentication_failed));
                        reLogin();
                    } else {
                        ToastUtils.show(UpdatePswActivity.this, result.getInfo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(UpdatePswActivity.this, t.getMessage());
            }
        });
    }

    private void actionSubmitNewPsw() {
        String oldPassword = oldPasswordEt.getText().toString();
        String newPassword = newPwEt.getText().toString();
        String repeatNewPassword = repeatNewPwEt.getText().toString();
        if (TextUtils.isEmpty(oldPassword)) {
            oldPasswordLayout.setError(getString(R.string.tips_null_password));
            oldPasswordEt.requestFocus();
        } else if (TextUtils.isEmpty(newPassword)) {
            newPwLayout.setError(getString(R.string.tips_null_password));
            newPwEt.requestFocus();
        } else {
            if (newPassword.equals(repeatNewPassword)) {
                if (InputUtils.isNumericOrLetter(newPassword)) {
                    oldPasswordLayout.setErrorEnabled(false);
                    newPwLayout.setErrorEnabled(false);
                    reqUpdatePws(oldPassword, newPassword);
                } else {
                    newPwLayout.setError(getString(R.string.tips_input_limit));
                    newPwEt.requestFocus();
                }
            } else {
                ToastUtils.show(this, getString(R.string.tips_new_password_error));
                newPwEt.requestFocus();
            }
        }
    }

    @OnClick({R.id.action_back_tv, R.id.confirm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                finish();
                break;
            case R.id.confirm_btn:
                actionSubmitNewPsw();
                break;
        }
    }
}
