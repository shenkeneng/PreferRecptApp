package com.frxs.wmsrecpt.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.frxs.wmsrecpt.R;
import com.frxs.wmsrecpt.comms.DataGenerator;
import com.frxs.wmsrecpt.fragment.MineFragment;
import com.frxs.wmsrecpt.fragment.ReceivedListFragment;
import com.frxs.wmsrecpt.fragment.StartReceiveFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/10/31
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class HomeActivity extends BaseScanActivity {

    @BindView(R.id.home_container_layout)
    FrameLayout homeContainerLayout;
    @BindView(R.id.bottom_tab_layout)
    TabLayout bottomTabLayout;
    private StartReceiveFragment startReceiveFragment;
    private ReceivedListFragment receivedListFragment;
    private MineFragment mineFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabItemSelected(tab.getPosition());

                for (int i = 0; i < bottomTabLayout.getTabCount(); i++) {
                    View view = bottomTabLayout.getTabAt(i).getCustomView();
                    ImageView icon = (ImageView) view.findViewById(R.id.tab_content_image);
                    TextView text = (TextView) view.findViewById(R.id.tab_content_text);
                    if (i == tab.getPosition()) {
                        icon.setImageResource(DataGenerator.mTabResPressed[i]);
                        text.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
                    } else {
                        icon.setImageResource(DataGenerator.mTabRes[i]);
                        text.setTextColor(ContextCompat.getColor(HomeActivity.this, android.R.color.darker_gray));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 提供自定义的布局添加Tab
        for (int i = 0; i < DataGenerator.mTabTitle.length; i++) {
            View tabView =  DataGenerator.getTabView(this, i);
            bottomTabLayout.addTab(bottomTabLayout.newTab().setCustomView(tabView));
        }

    }

    @Override
    protected void initData() {

    }

    private void onTabItemSelected(int position) {
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        switch (position) {
            case 0:
                if (null == startReceiveFragment) {
                    startReceiveFragment = StartReceiveFragment.newInstance("CustomTabView Tab");
                }
                fragment = startReceiveFragment;
                break;
            case 1:
                if (null == receivedListFragment) {
                    receivedListFragment = ReceivedListFragment.newInstance("CustomTabView Tab");
                }
                fragment = receivedListFragment;
                break;
            case 2:
                if (null == mineFragment) {
                    mineFragment = MineFragment.newInstance("CustomTabView Tab");
                }
                fragment = mineFragment;
                break;
            default:
                break;
        }

        if (fragment != null) {
            hideFragments(beginTransaction);

            if (!fragment.isAdded()) {
                beginTransaction.add(R.id.home_container_layout, fragment, String.valueOf(position));
            } else {
                beginTransaction.show(fragment);
            }

            beginTransaction.commitAllowingStateLoss();
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (null != startReceiveFragment && startReceiveFragment.isAdded()) {
            transaction.hide(startReceiveFragment);
        }

        if (null != receivedListFragment && receivedListFragment.isAdded()) {
            transaction.hide(receivedListFragment);
        }

        if (null != mineFragment && mineFragment.isAdded()) {
            transaction.hide(mineFragment);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof StartReceiveFragment) {
            startReceiveFragment = (StartReceiveFragment) fragment;
        } else if (fragment instanceof ReceivedListFragment) {
            receivedListFragment = (ReceivedListFragment) fragment;
        }else if (fragment instanceof MineFragment) {
            mineFragment = (MineFragment) fragment;
        }
    }

    @Override
    public void onSuccessEvent(String barcodeData) {
        if (!TextUtils.isEmpty(barcodeData)) {
            int currentPosition = bottomTabLayout.getSelectedTabPosition();
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(currentPosition));
            if (currentFragment instanceof StartReceiveFragment) {
                ((StartReceiveFragment)currentFragment).onSuccessEvent(barcodeData);
            }
        }
    }
}
