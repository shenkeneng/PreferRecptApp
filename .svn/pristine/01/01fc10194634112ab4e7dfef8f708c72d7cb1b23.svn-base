package com.frxs.wmsrecpt.activity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.frxs.common_base.utils.SharedPreferencesHelper;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.common_base.widget.EmptyView;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.bluetooth.BluetoothService;
import com.frxs.wmsrecpt.bluetooth.OnBluetoothLisenter;
import com.frxs.wmsrecpt.comms.Config;
import com.frxs.wmsrecpt.comms.GlobelDefines;
import com.pacific.adapter.Adapter;
import com.pacific.adapter.AdapterHelper;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.Nullable;


/**
 * Created by Chentie on 2017/2/23.
 */

public class SearchBluetoothActivity extends MyBaseActivity implements OnBluetoothLisenter {

    @BindView(R.id.tvBluetooth)
    TextView bluetoothTv;
    @BindView(R.id.title_tv)
    TextView titleLeftTv;
    @BindView(R.id.lvBluetooth)
    ListView bluetoothLv;
    @BindView(R.id.emptyview)
    EmptyView emptyView;
    @BindView(R.id.llBluetoothList)
    LinearLayout bluetoothLl;
    @BindView(R.id.action_back_tv)
    TextView actionBackTv;
    @BindView(R.id.action_right_tv)
    TextView actionRightTv;
    private String selectedBtAddress;
    private BluetoothService bluetoothService;
    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>(); // 用于存放蓝牙设备
    private Adapter<BluetoothDevice> adapter;
    private boolean isSearch = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchbluetooth);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        titleLeftTv.setText("设置蓝牙");
        TextView headrView = new TextView(this);
        headrView.setText("我的设备");
        headrView.setTextColor(getResources().getColor(R.color.frxs_black));
        headrView.setTextSize(23);
        headrView.setPadding(10, 10, 10, 10);
        bluetoothLv.addHeaderView(headrView);
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothService.isDiscovering()) {
            bluetoothService.cancelDevices();
        }
        bluetoothService.unregisterReceiver();
    }

    protected void initData() {
        SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(SearchBluetoothActivity.this, GlobelDefines.PREFS_NAME);
        selectedBtAddress = sp.getString(Config.KEY_BT_MAC, "");

        bluetoothService = new BluetoothService(this, this);
        bluetoothService.setBluetoothLisenter(this);
        bluetoothService.registerReceiver();
        adapter = new Adapter<BluetoothDevice>(this, R.layout.item_bluetooth) {
            @Override
            protected void convert(AdapterHelper helper, BluetoothDevice item) {
                if (!TextUtils.isEmpty(selectedBtAddress)) {
                    if (item.getAddress().equals(selectedBtAddress)){
                        helper.setVisible(R.id.iv_bluetooth_state, View.VISIBLE);
                        helper.setBackgroundRes(R.id.ll_bluetooth, R.color.frxs_gray_dark);
                    }else{
                        helper.setVisible(R.id.iv_bluetooth_state, View.INVISIBLE);
                        helper.setBackgroundRes(R.id.ll_bluetooth, R.color.transparent);
                    }
                }else{
                    helper.setVisible(R.id.iv_bluetooth_state, View.INVISIBLE);
                    helper.setBackgroundRes(R.id.ll_bluetooth, R.color.transparent);
                }

                helper.setText(R.id.tv_bluetooth_name, item.getName());
            }
        };
        adapter.addAll(bluetoothDevices);
        bluetoothLv.setAdapter(adapter);

        initBluetoothViews();
    }

    private void initBluetoothViews() {
        if (this.bluetoothService.isOpen()) {
            bluetoothDevices.clear();
            bluetoothTv.setText("搜索蓝牙");
            if (bluetoothDevices != null && bluetoothDevices.size() > 0){   // 有蓝牙设备
                emptyView.setVisibility(View.GONE);
                bluetoothLl.setVisibility(View.VISIBLE);
                adapter.replaceAll(bluetoothDevices);
            }else{                                                          // 没有蓝牙设备
                bluetoothLl.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("尚未有成功连接蓝牙，请搜索！");
                emptyView.setImageResource(R.mipmap.icon_start_bluetooth);

                doSearchBTDevices();
            }
        } else {
            bluetoothLl.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("请尚未开启蓝牙，请开启！");
            emptyView.setImageResource(R.mipmap.icon_start_bluetooth);
            bluetoothTv.setText("开启蓝牙");
        }
    }

    protected void initEvent() {
        bluetoothLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice item = (BluetoothDevice)parent.getAdapter().getItem(position);
                if (item != null) {
                    selectedBtAddress = item.getAddress();
                    SharedPreferencesHelper sp = SharedPreferencesHelper.getInstance(SearchBluetoothActivity.this, GlobelDefines.PREFS_NAME);
                    sp.putValue(Config.KEY_BT_MAC, selectedBtAddress);
                    adapter.notifyDataSetChanged();
                    ToastUtils.show(SearchBluetoothActivity.this, "已保存");
                }
            }
        });
    }

    @Override
    public void onBluetoothStateChanged(boolean isOpened) {
        initBluetoothViews();
    }

    @Override
    public void onBluetoothDiscoveryStarted() {
        ToastUtils.show(SearchBluetoothActivity.this, "正在搜索蓝牙");
        emptyView.setVisibility(View.GONE);
        bluetoothLl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBluetoothDiscoveryFound(List<BluetoothDevice> bondDevices, List<BluetoothDevice> unbondDevices) {
        this.bluetoothDevices.clear();

        if (bondDevices != null) {
            for (BluetoothDevice bluetoothDevices : bondDevices) {
                this.bluetoothDevices.add(bluetoothDevices);
            }
        }
        this.bluetoothDevices.addAll(unbondDevices);
        if (adapter != null) {
            adapter.replaceAll(bluetoothDevices);
        }
    }

    @Override
    public void onBluetoothDiscoveryFinished(List<BluetoothDevice> bondDevices, List<BluetoothDevice> unbondDevices) {
        this.bluetoothDevices.clear();

        if (bondDevices != null) {
            for (BluetoothDevice bluetoothDevices : bondDevices) {
                this.bluetoothDevices.add(bluetoothDevices);
            }
        }

        this.bluetoothDevices.addAll(unbondDevices);
        if (adapter != null) {
            adapter.replaceAll(bluetoothDevices);
        }

        ToastUtils.show(SearchBluetoothActivity.this, "搜索蓝牙完毕");
        bluetoothTv.setText("搜索蓝牙");
        isSearch = true;
    }

    @Override
    public void onBluetoothBondStateChanged(BluetoothDevice device, boolean isSuccess) {
    }

    @OnClick({R.id.tvBluetooth, R.id.action_back_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvBluetooth:
                if (adapter != null){
                    adapter.clear();
                }
                if (bluetoothService.isOpen()) {
                    doSearchBTDevices();
                } else {
                    bluetoothService.openBluetooth(SearchBluetoothActivity.this);
                }
                break;

            case R.id.action_back_tv:
                finish();
                break;
        }
    }

    private void doSearchBTDevices() {
        if (isSearch) {
            bluetoothService.searchDevices();
            bluetoothTv.setText("停止搜索");
            isSearch = false;
        } else {
            bluetoothService.cancelDevices();
            isSearch = true;
            bluetoothTv.setText("搜索蓝牙");
        }
    }
}
