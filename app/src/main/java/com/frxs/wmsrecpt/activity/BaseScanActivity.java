package com.frxs.wmsrecpt.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.frxs.common_base.utils.ToastUtils;
import com.frxs.wmsrecpt.MyApplication;
import com.frxs.wmsrecpt.comms.GlobelDefines;
import com.frxs.wmsrecpt.listener.ScanListener;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerUnavailableException;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/09/28
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public abstract class BaseScanActivity extends MyBaseActivity implements BarcodeReader.BarcodeListener, ScanListener {

    protected BarcodeReader barcodeReader;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == GlobelDefines.ACTION_DATA_CODE_RECEIVED) {
                String barcodeData = intent.getStringExtra("data");
                onSuccessEvent(barcodeData);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcodeReader = MyApplication.getInstance().getBarcodeReader();
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BaseScanActivity.this.onSuccessEvent(barcodeReadEvent.getBarcodeData());
            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isHoneywell()) {
            registerReceiver(mBroadcastReceiver, new IntentFilter(GlobelDefines.ACTION_DATA_CODE_RECEIVED));
        } else {
            if (null != barcodeReader) {
                barcodeReader.addBarcodeListener(this);
                try {
                    barcodeReader.claim();
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                    ToastUtils.show(this, "Scanner unavailable");
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isHoneywell()) {
            unregisterReceiver(mBroadcastReceiver);
        } else {
            if (null != barcodeReader) {
                barcodeReader.release();
                barcodeReader.removeBarcodeListener(this);
            }
        }
    }
}
