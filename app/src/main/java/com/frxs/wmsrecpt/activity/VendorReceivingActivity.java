package com.frxs.wmsrecpt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.frxs.common_base.utils.CommonUtils;
import com.frxs.common_base.utils.ImeUtils;
import com.frxs.common_base.utils.SystemUtils;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.common_base.widget.ClearEditText;
import com.frxs.common_base.widget.CountEditText;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.model.Product;
import com.frxs.wmsrecpt.model.ReceivedOrder;
import com.frxs.wmsrecpt.rest.model.ApiResponse;
import com.frxs.wmsrecpt.rest.service.RequestListener;
import com.frxs.wmsrecpt.rest.service.SimpleCallback;
import com.pacific.adapter.RecyclerAdapter;
import com.pacific.adapter.RecyclerAdapterHelper;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/10/31
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class VendorReceivingActivity extends SwipeRecyclerViewActivity {

    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    @BindView(R.id.vendor_info_tv)
    TextView vendorInfoTv;
    @BindView(R.id.received_product_lv)
    SwipeMenuRecyclerView receivedProductLv;
    @BindView(R.id.total_row_tv)
    TextView totalRowTv;
    @BindView(R.id.total_product_tv)
    TextView totalProductTv;
    @BindView(R.id.search_content_et)
    ClearEditText searchContentEt;
    private RecyclerAdapter<Product> recyclerAdapter;
    private List<Product> receivedProductList = new ArrayList<>();
    private AlertDialog receiveDialog;
    private Product productVendorInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_receiving);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        actionRightTv.setVisibility(View.VISIBLE);
        actionRightTv.setText(R.string.search);

        receivedProductLv.setLayoutManager(new LinearLayoutManager(this));
        receivedProductLv.setHasFixedSize(true);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.h_divider));
        receivedProductLv.addItemDecoration(divider);

        recyclerAdapter = new RecyclerAdapter<Product>(this, receivedProductList, R.layout.item_product) {
            @Override
            protected void convert(RecyclerAdapterHelper helper, final Product item) {
                helper.setText(R.id.product_name_tv, item.getProductName());
                helper.setText(R.id.product_package_num_tv, String.format(getString(R.string.product_package_num),item.getPackingQty()));
                helper.setText(R.id.received_num_tv, item.getQty() + item.getUnit());
                helper.setText(R.id.product_barcode_tv, String.format(getString(R.string.product_barcode), item.getBarCodes()));
                helper.setText(R.id.product_code_tv, String.format(getString(R.string.product_code), item.getSKU()));
                helper.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (CommonUtils.isFastDoubleClick()){
                            return;
                        }
                        showReceiveDialog(item);
                    }
                });
            }
        };
        receivedProductLv.setSwipeMenuCreator(swipeMenuCreator);
        receivedProductLv.setSwipeMenuItemClickListener(new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                receiveDialog = null;
                menuBridge.closeMenu();

                int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
                int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。

                if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {

                } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                    receivedProductList.remove(adapterPosition);
                    recyclerAdapter.replaceAll(receivedProductList);
                    if (receivedProductList.size() < 1) {
                        productVendorInfo = null;
                        vendorInfoTv.setText("");
                    }
                    updateProductStatistics();
                }
            }
        });
        receivedProductLv.setAdapter(recyclerAdapter);

        updateProductStatistics();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null){
                Product product = (Product) intent.getExtras().getSerializable("product");
                showReceiveDialog(product);
            }
        }
    }

    private void updateProductStatistics() {
        totalRowTv.setText(getString(R.string.total_product_row, receivedProductList.size()));
        int totalProduct = 0;
        for (Product item : receivedProductList) {
            totalProduct += item.getQty();
        }
        totalProductTv.setText(getString(R.string.total_product_num, String.valueOf(totalProduct)));
    }

    private void showReceiveDialog(final Product product) {
        if (product.getUpperLimit() > -1 && product.getStockQty() >= product.getUpperLimit()) {
            ToastUtils.show(this, "商品当前库存已达到最大库存量，不能再收货。");
            return;
        }
        if (receiveDialog == null) {
            receiveDialog = new AlertDialog.Builder(this).create();
            //设置对话框铺满屏幕
            Window window = receiveDialog.getWindow();
            final View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_receive, null);
            ((TextView) contentView.findViewById(R.id.product_name_tv)).setText(product.getProductName());
            ((TextView) contentView.findViewById(R.id.product_code_tv)).setText(String.format(getString(R.string.product_code), product.getSKU()));
            // 销售单位（配送单位）
            Product.UnitListBean saleUnit = null;
            // 库存单位
            Product.UnitListBean stockUnit = null;
            if (product.getUnitList() == null || product.getUnitList().size() < 1) {
                ToastUtils.showCenterToast(this, "当前商品没有单位，不能开始收货");
                receiveDialog = null;
                return;
            }
            for (Product.UnitListBean item : product.getUnitList()) {
                if (item.getUnitType().equals("配送单位")) {
                    saleUnit = item;
                }
                if (item.getPackingQty() == 1) {
                    stockUnit = item;
                    product.setStockUnit(item.getUnitName());
                }
            }
            if (stockUnit == null) {
                ToastUtils.showCenterToast(this, "当前商品没有库存单位，不能开始收货");
                receiveDialog = null;
                return;
            }
            // 上次选择单位
            Product.UnitListBean unit = product.getChooseUnit();
            if (unit == null) {
                // 有配送单位则用配送单位，没有用库存单位
                unit = saleUnit != null ? saleUnit : stockUnit;
            }
            if (unit == null) {
                ToastUtils.showCenterToast(this, "该商品不存在库存单位，无法收货!");
                receiveDialog = null;
                return;
            }
            // 设置商品初始化收货单位名称 收货单位包装数 库存单位名名称（用于上传）
            product.setUnit(unit.getUnitName());
            product.setPackingQty(unit.getPackingQty());
            product.setStockUnit(stockUnit.getUnitName());
            product.setBarCodes(unit.getBarCode());
            ((TextView) contentView.findViewById(R.id.product_barcode_tv)).setText(String.format(getString(R.string.product_barcode), product.getBarCodes()));
            // 当前单位当前库存 = (库存单位当前库存数 / 当前单位包装数)
            int stockQty = product.getStockQty() / unit.getPackingQty();
            final CountEditText countEditText = contentView.findViewById(R.id.edit_count_et);
            // 当前可收货数 = (库存单位可收货数 / 当前单位包装数)
            int recivableQty = 0;
            // 有预警库存
            if (product.getUpperLimit() > -1 ) {
                // 库存数小于预警库存 计算出可以收货数量 反之不可收货
                if (product.getStockQty() < product.getUpperLimit()) {
                    recivableQty = (product.getUpperLimit() - product.getStockQty()) / unit.getPackingQty();
                }
                countEditText.setMaxCount(recivableQty);
                ((TextView) contentView.findViewById(R.id.product_receivable_num_tv)).setText(String.format(getString(R.string.product_receivable_num), String.valueOf(recivableQty)) + unit.getUnitName());
            } else {// 没有预警库存可收商品最大数 （默认6位数）
                ((TextView) contentView.findViewById(R.id.product_receivable_num_tv)).setText(String.format(getString(R.string.product_receivable_num), ""));
            }
            product.setTempUnit(unit);
            // 赋值
            ((TextView) contentView.findViewById(R.id.receive_unit_tv)).setText(unit.getUnitName());
            ((TextView) contentView.findViewById(R.id.product_package_num_tv)).setText(String.format(getString(R.string.product_package_num), unit.getPackingQty()));
            ((TextView) contentView.findViewById(R.id.product_stock_num_tv)).setText(String.format(getString(R.string.product_stock_num), stockQty) + unit.getUnitName());
            countEditText.setCount(product.getQty() > 0 ? product.getQty() :recivableQty);//product.getQty() > 0 ? product.getQty() :
            // 切换单位
            contentView.findViewById(R.id.switch_unit_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUnitPop(v, product, contentView);
                }
            });
            // 取消修改商品信息
            contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    receiveDialog.dismiss();
                    receiveDialog = null;
                }
            });
            // 确认修改商品信息
            contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (productVendorInfo == null) {
                        productVendorInfo = product;
                        vendorInfoTv.setText(productVendorInfo.getVendorCode() + "-" + productVendorInfo.getVendorName());
                    }
                    int count = ((CountEditText) contentView.findViewById(R.id.edit_count_et)).getCount();
                    if (count <= 0) {
                        ToastUtils.showCenterToast(VendorReceivingActivity.this, "收货数量不能为0");
                        return;
                    }
                    receiveDialog.dismiss();
                    receiveDialog = null;
                    product.setQty(count);
                    product.setStockUnitQty(count * product.getTempUnit().getPackingQty());
                    product.setUnit(product.getTempUnit().getUnitName());
                    product.setChooseUnit(product.getTempUnit());

                    List<Product> lastReceivedProductList = new ArrayList<>();
                    boolean isExist = false;
                    if (receivedProductList.size() > 0) {
                        for (Product item : receivedProductList) {
                            if (item.getProductId() == product.getProductId()) {
                                lastReceivedProductList.add(product);
                                isExist = true;
                            } else {
                                lastReceivedProductList.add(item);
                            }
                        }
                    }
                    if (!isExist) {
                        lastReceivedProductList.add(0, product);
                    }
                    receivedProductList = lastReceivedProductList;
                    recyclerAdapter.replaceAll(receivedProductList);
                    updateProductStatistics();
                }
            });
            receiveDialog.setView(contentView, 0, 0, 0, 0);
            receiveDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    ImeUtils.popSoftKeyboard(VendorReceivingActivity.this, countEditText.getmEdit(), true);
                }
            });
        }
        if (receiveDialog != null) {
            receiveDialog.show();
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = receiveDialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth()); //设置宽度
            receiveDialog.getWindow().setAttributes(lp);
        }

    }

    private void showUnitPop(View anchor, final Product product, final View contentView) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        Menu menu = popupMenu.getMenu();
        final List<Product.UnitListBean> unitList = product.getUnitList();
        for (int i = 0; i < unitList.size(); i++) {
            menu.add(Menu.NONE, Menu.FIRST + i, i, String.format(getString(R.string.item_unit),
                    unitList.get(i).getUnitName(), unitList.get(i).getPackingQty(), product.getStockUnit()));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                CountEditText countEditText = contentView.findViewById(R.id.edit_count_et);
                int itemId = menuItem.getItemId();
                // 当前单位
                Product.UnitListBean unit = unitList.get(itemId - Menu.FIRST);
                // 当前单位当前库存 = (库存单位当前库存数 / 当前单位包装数)
                int stockQty = product.getStockQty() / unit.getPackingQty();
                // 当前收货数
                int count = countEditText.getCount();
                // 切换相同单位不做处理
                if (product.getTempUnit().getPackingQty() == unit.getPackingQty() && product.getTempUnit().getUnitId() == unit.getUnitId()) {
                    return false;
                }
                if (product.getTempUnit().getPackingQty() > 1 && unit.getPackingQty() == 1) {
                    count = count * product.getTempUnit().getPackingQty();
                } else {
                    count = count * product.getTempUnit().getPackingQty();
                    if (count % unit.getPackingQty() == 0) {
                        count = count / unit.getPackingQty();
                    } else {
                        ToastUtils.showCenterToast(VendorReceivingActivity.this, "不能切换该单位，使收货数量为小数");
                        return false;
                    }
                }
                // 有预警库存商品
                if (product.getUpperLimit() > -1) {
                    // 库存数小于预警库存
                    if (product.getStockQty() < product.getUpperLimit()) {
                        int recivableQty = (product.getUpperLimit() - product.getStockQty()) / unit.getPackingQty();
                        countEditText.setMaxCount(recivableQty);
                        ((TextView) contentView.findViewById(R.id.product_receivable_num_tv)).setText(String.format(getString(R.string.product_receivable_num), String.valueOf(recivableQty)) + unit.getUnitName());
                    } else { // 库存数大于预警库存时不允许收货
                        countEditText.setMaxCount(0);
                        ((TextView) contentView.findViewById(R.id.product_receivable_num_tv)).setText(String.format(getString(R.string.product_receivable_num), String.valueOf(0)) + unit.getUnitName());
                    }
                }
                product.setTempUnit(unit);
                product.setPackingQty(unit.getPackingQty());
                product.setBarCodes(unit.getBarCode());
                // 赋值
                ((TextView) contentView.findViewById(R.id.product_barcode_tv)).setText(String.format(getString(R.string.product_barcode), product.getBarCodes()));
                ((TextView)contentView.findViewById(R.id.receive_unit_tv)).setText(unit.getUnitName());
                ((TextView)contentView.findViewById(R.id.product_package_num_tv)).setText(String.format(getString(R.string.product_package_num), unit.getPackingQty()));
                ((TextView)contentView.findViewById(R.id.product_stock_num_tv)).setText(String.format(getString(R.string.product_stock_num), stockQty) + unit.getUnitName());
                countEditText.setCount(count);
                return false;
            }
        });
        popupMenu.show();
    }

    @OnClick({R.id.action_back_tv, R.id.action_right_tv, R.id.action_done_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action_back_tv:
                confirmExit();
                break;
            case R.id.action_right_tv:
                String content = searchContentEt.getText().toString().trim();
                if (!TextUtils.isEmpty(content)) {
                    reqProductInfo(content, productVendorInfo != null ? productVendorInfo.getVendorID() : -1);
                } else {
                    ToastUtils.show(this, "搜索内容不能为空");
                }
                break;
            case R.id.action_done_btn:
                if (receivedProductList.size() > 0) {
                    finishReceivedOrder();
                } else {
                    ToastUtils.show(this, "没有商品无法完成收货");
                }
                break;
        }
    }

    private void finishReceivedOrder() {
        if (!SystemUtils.checkNet(this) || !SystemUtils.isNetworkAvailable(this)) {
            ToastUtils.show(this, "网络不可用");
            return;
        }
        showProgressDialog();
        ReceivedOrder receivedOrder = new ReceivedOrder();
        receivedOrder.setToken(getToken());
        receivedOrder.setUserAccount(getUserAccount());
        receivedOrder.setVersion(SystemUtils.getAppVersion(this));
        receivedOrder.setReceiverID(getUserID());
        receivedOrder.setReceiverName(getUserName());
        receivedOrder.setUserID(getUserID());
        receivedOrder.setUserName(getUserName());
        receivedOrder.setVendorID(receivedProductList.get(0).getVendorID());
        receivedOrder.setVendorCode(receivedProductList.get(0).getVendorCode());
        receivedOrder.setVendorName(receivedProductList.get(0).getVendorName());
        receivedOrder.setWID(getWID());
        receivedOrder.setSubWID(getSubWID());
        receivedOrder.setSubWName(getSubWName());
        receivedOrder.setOpAreaID(getOpAreaID());
        receivedOrder.setRemark("");
        int productQty = 0;
        List<ReceivedOrder.ReqProduct> list = new ArrayList<>();
        for (int i = 0; i< receivedProductList.size(); i++) {
            ReceivedOrder.ReqProduct item = new ReceivedOrder.ReqProduct();
            item.setSortNo(i+1);
            item.setProductId(receivedProductList.get(i).getProductId());
            item.setProductName(receivedProductList.get(i).getProductName());
            item.setSKU(receivedProductList.get(i).getSKU());
            item.setSKUContent(receivedProductList.get(i).getSKUContent());
            item.setBarCode(receivedProductList.get(i).getBarCodes());
            item.setUnit(receivedProductList.get(i).getUnit());
            item.setQty(receivedProductList.get(i).getQty());
            item.setPackingQty(receivedProductList.get(i).getPackingQty());

            item.setStockUnit(receivedProductList.get(i).getStockUnit());
            item.setStockUnitQty(receivedProductList.get(i).getStockUnitQty());
            item.setStockUnitPrice(receivedProductList.get(i).getUnitPrice());
            item.setStockUnitSupplyPrice(receivedProductList.get(i).getUnitBuyPrice());
            item.setShelfAreaCode(receivedProductList.get(i).getShelfAreaCode());
            item.setShelfAreaName(receivedProductList.get(i).getShelfAreaName());
            item.setSubShelfAreaCode(receivedProductList.get(i).getSubShelfAreaCode());
            item.setSubShelfAreaName(receivedProductList.get(i).getSubShelfAreaName());
            item.setShelfCode(receivedProductList.get(i).getShelfCode());
            list.add(item);
        }
        receivedOrder.setQty(productQty);
        receivedOrder.setItemRequest(list);

        getService().CompletedReceivedOrder(receivedOrder).enqueue(new SimpleCallback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(ApiResponse<Boolean> result, int code, String msg) {
                dismissProgressDialog();
                if (result.isSuccessful() && result.getData()) {
                    ToastUtils.show(VendorReceivingActivity.this, "收货完成");
                    finish();
                } else {
                    if (result.isAuthenticationFailed()) {
                        ToastUtils.show(VendorReceivingActivity.this, getString(R.string.authentication_failed));
                        reLogin();
                    } else {
                        ToastUtils.show(VendorReceivingActivity.this, result.getInfo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(VendorReceivingActivity.this, t.getMessage());
            }
        });
    }

    private void confirmExit() {
        final AlertDialog receiveDialog = new AlertDialog.Builder(this).create();
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialoig_prompt, null);
        ((TextView)contentView.findViewById(R.id.tv_content)).setText(R.string.confirm_exit);
        contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveDialog.dismiss();
            }
        });
        contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() ==  KeyEvent.KEYCODE_BACK) {
            confirmExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSuccessEvent(String barcodeData) {
        if (!TextUtils.isEmpty(barcodeData)) {
            searchContentEt.setText(barcodeData);
            reqProductInfo(barcodeData, productVendorInfo != null ? productVendorInfo.getVendorID() : -1);
        } else {
            ToastUtils.show(this, "请重新扫描！");
        }
    }

    private void reqProductInfo(String content, double vendorId) {
        getProductInfo(content, vendorId, new RequestListener<List<Product>>() {
            @Override
            public void handleRequestResponse(ApiResponse<List<Product>> result) {
                dismissProgressDialog();
                if (result.isSuccessful()) {
                    ArrayList<Product> data = (ArrayList<Product>) result.getData();
                    if (data != null && data.size() > 0) {
                        if (data.size() > 1) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("product_list", data);
                            bundle.putString("from", "list");
                            gotoActivityForResult(ChooseProductActivity.class, false, bundle, 0101);
                        } else{
                            searchContentEt.setText("");
                            if (productVendorInfo != null) {
                                if (data.get(0).getVendorID() != productVendorInfo.getVendorID()) {
                                    ToastUtils.show(VendorReceivingActivity.this, "该供应商下无此商品！");
                                    return;
                                }

                                for (Product item : receivedProductList) {
                                    if (data.get(0).getProductId() == item.getProductId()) {
                                        showReceiveDialog(item);
                                        return;
                                    }
                                }
                            }
                            showReceiveDialog(data.get(0));
                        }
                    }
                } else {
                    if (result.isAuthenticationFailed()) {
                        ToastUtils.show(VendorReceivingActivity.this, getString(R.string.authentication_failed));
                        reLogin();
                    } else {
                        ToastUtils.show(VendorReceivingActivity.this, result.getInfo());
                    }
                }
            }

            @Override
            public void handleExceptionResponse(String errMsg) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0101) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Product product = (Product) extras.getSerializable("product");
                if (product != null) {
                    if (productVendorInfo != null) {
                        if (product.getVendorID() != productVendorInfo.getVendorID()) {
                            ToastUtils.show(VendorReceivingActivity.this, "该供应商下无此商品！");
                            return;
                        } else {
                            for (Product item : receivedProductList) {
                                if (item.getProductId() == product.getProductId()) {
                                    showReceiveDialog(item);
                                    return;
                                }
                            }
                        }
                    }
                    showReceiveDialog(product);
                }
            }
        }
    }

}
