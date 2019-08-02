package com.frxs.wmsrecpt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.model.Product;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseProductActivity extends MyBaseActivity {

    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.lv_order)
    ListView lvOrder;
    private Adapter<Product> productAdapter;
    private String form;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_product);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        titleTv.setText("选择商品");
        productAdapter = new Adapter<Product>(this, R.layout.item_choose_prodct) {
            @Override
            protected void convert(AdapterHelper helper, final Product item) {
                helper.setText(R.id.tv_num, (helper.getPosition()+1) + ".");
                helper.setText(R.id.tv_vendor_name, item.getVendorName());
                helper.setText(R.id.tv_product_name, item.getProductName());
                String barCode = "";
                if (item.getUnitList() != null) {
                    if (item.getUnitList().size() > 0) {
                        barCode = item.getUnitList().get(0) != null ? item.getUnitList().get(0).getBarCode() : "";
                    }
                }
                helper.setText(R.id.tv_sku_content, String.format(getString(R.string.product_barcode), barCode));
                helper.setText(R.id.tv_barcode, String.format(getString(R.string.product_code), item.getSKU()));
                helper.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("product", item);
                        if (form.equals("home")) {
                            gotoActivity(VendorReceivingActivity.class, true, bundle);
                        } else {
                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            setResult(0101, intent);
                            finish();
                        }
                    }
                });
            }
        };
        lvOrder.setAdapter(productAdapter);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null){
                ArrayList<Product> list = (ArrayList<Product>) intent.getExtras().getSerializable("product_list");
                form = intent.getExtras().getString("from", "");
                if (list != null && list.size() > 1) {
                    productAdapter.addAll(list);
                    productAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @OnClick(R.id.action_back_tv)
    public void onClick(View view) {
        finish();
    }
}
