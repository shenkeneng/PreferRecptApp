package com.frxs.common_base.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.frxs.common_base.widget.progress_dialog.MyProgressDialog;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/08/15
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public abstract class BaseActivity extends AppCompatActivity {

    private MyProgressDialog progressDialog;
    private int dialogCount = 0; //等待框计数， 大于0时显示，小于等于0不显示

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        progressDialog = new MyProgressDialog(this);
        progressDialog.setCancelable(false); //设置等待按钮不能手动取消
    }

    /**
     * 初始化数据
     */
    protected abstract void initView();

    protected abstract void initData();

    public void gotoActivity(Class<?> clz) {
        gotoActivity(clz, false, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity) {
        gotoActivity(clz, isCloseCurrentActivity, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity, Bundle ex) {
        Intent intent = new Intent(this, clz);
        if (ex != null) {
            intent.putExtras(ex);
        }
        startActivity(intent);
        if (isCloseCurrentActivity) {
            finish();
        }
    }

    public void gotoActivityForResult(Class<?> clz, boolean isCloseCurrentActivity, Bundle ex, int requestCode) {
        Intent intent = new Intent(this, clz);
        if (ex != null) {
            intent.putExtras(ex);
        }
        startActivityForResult(intent, requestCode);
        if (isCloseCurrentActivity) {
            finish();
        }
    }

    public boolean isShowingProgressDialog() {
        return progressDialog.isShowing();
    }

    public void showProgressDialog() {
        dialogCount++;
        if (!progressDialog.isShowing()) {
            progressDialog.showProgress();
        }
    }

    public void dismissProgressDialog() {
        dialogCount--;
        if (dialogCount <=  0) {
            dialogCount = 0;
            progressDialog.dismissProgress();
        }
    }
}
