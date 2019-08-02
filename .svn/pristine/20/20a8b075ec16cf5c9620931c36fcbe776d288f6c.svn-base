package com.frxs.wmsrecpt.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.common_base.widget.ClearEditText;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.activity.ChooseProductActivity;
import com.frxs.wmsrecpt.activity.MyBaseActivity;
import com.frxs.wmsrecpt.activity.VendorReceivingActivity;
import com.frxs.wmsrecpt.model.Product;
import com.frxs.wmsrecpt.rest.model.ApiResponse;
import com.frxs.wmsrecpt.rest.service.RequestListener;
import java.util.ArrayList;
import java.util.List;
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
public class StartReceiveFragment extends MyBaseFragment{
    public static final String TAG = StartReceiveFragment.class.getSimpleName();
    @BindView(R.id.action_back_tv)
    TextView actionBackTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.search_content_et)
    ClearEditText searchContentEt;
    private Unbinder unbinder;

    public static StartReceiveFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG, argument);
        StartReceiveFragment fragment = new StartReceiveFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_receive, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_start_receive;
    }

    @Override
    protected void initView(View view) {
        actionBackTv.setVisibility(View.GONE);
        actionRightTv.setVisibility(View.VISIBLE);
        actionRightTv.setText("搜索");
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.action_right_tv)
    public void onClick(View view) {
        String s = searchContentEt.getText().toString().trim();
        if (!TextUtils.isEmpty(s)) {
            ((MyBaseActivity)mActivity).getProductInfo(s, -1, new RequestListener<List<Product>>() {
                @Override
                public void handleRequestResponse(ApiResponse<List<Product>> result) {
                    scanResult(result);
                }

                @Override
                public void handleExceptionResponse(String errMsg) {
                }
            });
        } else {
            ToastUtils.show(mActivity, "搜索内容不能为空");
        }
    }

    public void onSuccessEvent(String barcodeData) {
        if (!TextUtils.isEmpty(barcodeData)) {
            searchContentEt.setText(barcodeData);
            ((MyBaseActivity)mActivity).getProductInfo(barcodeData, -1, new RequestListener<List<Product>>() {
                @Override
                public void handleRequestResponse(ApiResponse<List<Product>> result) {
                    scanResult(result);
                }

                @Override
                public void handleExceptionResponse(String errMsg) {
                }
            });
        } else {
            ToastUtils.show(mActivity, "请重新扫描！");
        }
    }

    private void scanResult(ApiResponse<List<Product>> result) {
        mActivity.dismissProgressDialog();
        if (result.isSuccessful()) {
            ArrayList<Product> data = (ArrayList<Product>) result.getData();
            if (data != null && data.size() > 0) {
                searchContentEt.setText("");
                if (data.size() > 1) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("product_list",data);
                    bundle.putString("from", "home");
                    mActivity.gotoActivity(ChooseProductActivity.class, false, bundle);
                } else{
                    if (data.get(0).getUpperLimit() > -1 && data.get(0).getStockQty() >= data.get(0).getUpperLimit()) {
                        ToastUtils.show(mActivity, "商品当前库存已达到最大库存量，不能再收货。");
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("product", data.get(0));
                    mActivity.gotoActivity(VendorReceivingActivity.class, false, bundle);
                }
            }
        } else {
            if (result.isAuthenticationFailed()) {
                ToastUtils.show(mActivity, getString(R.string.authentication_failed));
                ((MyBaseActivity)mActivity).reLogin();
            } else {
                ToastUtils.show(mActivity, result.getInfo());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
