package com.frxs.wmsrecpt.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import com.frxs.wmsrecpt.MyApplication;
import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.model.UserInfo;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/06/07
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class SplashActivity extends MyBaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        boolean flag = hasShortcut(this);//如果已经创建,则不需要在创建
        if(flag == false){
            CreateShotCut(this, SplashActivity.class, getString(R.string.app_name));
        }
        initView();
        initData();
    }

    @Override
    protected void initView() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void initData() {
        new CountDownTimer(3000, 1500) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                UserInfo userInfo = MyApplication.getInstance().getUserInfo();
                if (null != userInfo) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                SplashActivity.this.finish();
                overridePendingTransition(R.anim.just_fade_in, R.anim.just_fade_out);
            }
        }.start();
    }

    public void CreateShotCut(final Context context, final Class<?> clazz,
                              final String name) {

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        // 加入action,和category之后，程序卸载的时候才会主动将该快捷方式也卸载
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClass(context, clazz);
        // 创建快捷方式的Intent
        Intent shortcut = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        // 不允许重复创建
        shortcut.putExtra("duplicate", false);
        // 点击快捷图片，运行的程序主入口
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        // 需要现实的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                getApplicationContext(), R.mipmap.icon_launcher);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(shortcut);
    }

    public boolean hasShortcut(Context context) {
        boolean isInstallShortcut = false ;
        final ContentResolver cr = context.getContentResolver();
        final String AUTHORITY = "com.android.launcher2.settings";
        final Uri CONTENT_URI = Uri.parse("content://" +
                AUTHORITY + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI,
                new String[] {"title","iconResource" },
                "title=?",
                new String[] {getString(R.string.app_name ) }, null);//XXX表示应用名称。
        if(c!=null && c.getCount()>0){
            isInstallShortcut = true ;
            System.out.println("已创建");
        }
        return isInstallShortcut ;
    }
}
