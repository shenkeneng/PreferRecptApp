package com.frxs.wmsrecpt.activity;

import android.graphics.Color;
import android.view.ViewGroup;

import com.frxs.wmsrecpt.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/11/02
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public abstract class SwipeRecyclerViewActivity extends BaseScanActivity {

    protected SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {

            int width = getResources().getDimensionPixelSize(R.dimen.dp_100);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;


            // 添加左侧的，如果不添加，则左侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(SwipeRecyclerViewActivity.this)
                        .setBackground(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_delete_white)
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeLeftMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }

        }
    };

//     /**
//     * RecyclerView的Item的Menu点击监听。
//     */
//     protected SwipeMenuItemClickListener menuItemClickListener = new SwipeMenuItemClickListener() {
//        @Override
//        public void onItemClick(SwipeMenuBridge menuBridge) {
//            menuBridge.closeMenu();
//
//            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
//            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
//            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
//
//            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
//
//            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
//
//            }
//        }
//    };
}
