package com.frxs.wmsrecpt.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.frxs.common_base.utils.SystemUtils;
import com.frxs.wmsrecpt.MyApplication;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.activity.LoginActivity;
import com.frxs.wmsrecpt.activity.MyBaseActivity;
import com.frxs.wmsrecpt.activity.SearchBluetoothActivity;
import com.frxs.wmsrecpt.activity.UpdatePswActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/10/31
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class MineFragment extends MyBaseFragment{
    public static final String TAG = MineFragment.class.getSimpleName();
    @BindView(R.id.action_back_tv)
    TextView actionBackTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_version_number)
    TextView tvVersionNumber;
    @BindView(R.id.tv_sign_out)
    TextView tvSignOut;
    private Unbinder bind;

    public static MineFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, argument);
        MineFragment mineFragment = new MineFragment();
        mineFragment.setArguments(bundle);
        return mineFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View view) {
        actionBackTv.setVisibility(View.GONE);
        titleTv.setText(R.string.title_mine);
        tvVersionNumber.setText(SystemUtils.getAppVersion(mActivity));
        tvUserName.setText(((MyBaseActivity)mActivity).getUserName());
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.ll_modify_psw, R.id.ll_setting_bluetooth, R.id.ll_update_version, R.id.tv_sign_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_modify_psw:
                mActivity.gotoActivity(UpdatePswActivity.class);
                break;
            case R.id.ll_setting_bluetooth:
                mActivity.gotoActivity(SearchBluetoothActivity.class);
                break;
            case  R.id.ll_update_version:
                break;
            case R.id.tv_sign_out:
                loginOut();
                break;
                default:
                    break;
        }
    }

    private void loginOut() {
        final AlertDialog receiveDialog = new AlertDialog.Builder(mActivity).create();
        View contentView = LayoutInflater.from(mActivity).inflate(R.layout.dialoig_prompt, null);
        ((TextView)contentView.findViewById(R.id.tv_content)).setText(R.string.exit_query);
        contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveDialog.dismiss();
            }
        });
        contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.getInstance().logout();
                mActivity.finish();
                Intent intent = new Intent(mActivity, LoginActivity.class);
                startActivity(intent);
                receiveDialog.dismiss();
            }
        });
        receiveDialog.setView(contentView, 0, 0, 0, 0);
        receiveDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
            }
        });
        receiveDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }
}
