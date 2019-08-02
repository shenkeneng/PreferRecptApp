package com.frxs.wmsrecpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.model.ReceivedOrderDetalise;
import com.frxs.wmsrecpt.rest.model.ApiResponse;
import com.frxs.wmsrecpt.rest.service.RequestListener;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceivedOrderDetalisActivity extends MyBaseActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.tv_order_id)
    TextView TvOrderID;
    @BindView(R.id.tv_vendor_name)
    TextView tvVendorName;
    @BindView(R.id.lv_detalis)
    ListView lvDetalis;
    private Adapter<ReceivedOrderDetalise.ItemsBean> detalisAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detalis);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        titleTv.setText(R.string.title_order_detalis);
        detalisAdapter = new Adapter<ReceivedOrderDetalise.ItemsBean>(this, R.layout.item_product) {
            @Override
            protected void convert(AdapterHelper helper, ReceivedOrderDetalise.ItemsBean item) {
                helper.setText(R.id.product_name_tv, item.getProductName());
                helper.setText(R.id.product_package_num_tv, String.format(getString(R.string.product_package_num), item.getPackingQty()));
                helper.setText(R.id.received_num_tv, item.getQty() + item.getUnit());
                helper.setText(R.id.product_barcode_tv, String.format(getString(R.string.product_barcode), item.getBarCode()));
                helper.setText(R.id.product_code_tv, String.format(getString(R.string.product_code), item.getSKU()));
            }
        };
        lvDetalis.setAdapter(detalisAdapter);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String stockId = intent.getExtras().getString("stock_id");
            if (TextUtils.isEmpty(stockId)) {
                ToastUtils.show(this, "收货单号为空！");
                return;
            }
            getReceivedOrderDetalis(stockId, new RequestListener<ReceivedOrderDetalise>() {
                @Override
                public void handleRequestResponse(ApiResponse<ReceivedOrderDetalise> result) {
                    dismissProgressDialog();
                    if (result.isSuccessful() && result.getData() != null) {
                        ReceivedOrderDetalise detalise = result.getData();
                        TvOrderID.setText("单号：" + detalise.getStockInID());
                        tvVendorName.setText(detalise.getVendorCode() + "-" + detalise.getVendorName());
                        detalisAdapter.addAll(detalise.getItems());
                    } else {
                        if (result.isAuthenticationFailed()) {
                            ToastUtils.show(ReceivedOrderDetalisActivity.this, getString(R.string.authentication_failed));
                            reLogin();
                        } else {
                            ToastUtils.show(ReceivedOrderDetalisActivity.this, result.getInfo());
                        }
                    }
                }

                @Override
                public void handleExceptionResponse(String errMsg) {

                }
            });
        } else {
            ToastUtils.show(this, "收货单号为空！");
        }
    }

    @OnClick(R.id.action_back_tv)
    public void onClick(View view) {
        finish();
    }
}
