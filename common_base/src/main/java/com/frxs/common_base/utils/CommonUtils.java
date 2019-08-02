package com.frxs.common_base.utils;

/**
 * Created by ewu on 2016/5/27.
 */
public class CommonUtils {

    private static int FAST_DOUBLE_CLICK_INTERVAL = 1500;
    private static long mLastClickTime;

    public static boolean isFastDoubleClick()
    {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        if (0 < timeD && timeD < FAST_DOUBLE_CLICK_INTERVAL)
        {
            return true;
        }

        mLastClickTime = time;

        return false;
    }
}
