package com.frxs.common_base.utils;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.frxs.common_base.R;


/**
 * Android大小单位转换工具类
 * 
 * @author wader
 * 
 */
public class DisplayUtil {
	
	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;  
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;  
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
		return (int) (spValue * fontScale + 0.5f);
	}
	
	public static int getScreenWidth(Context context)
	{
		DisplayMetrics metric = new DisplayMetrics();
	    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
	    
	    return metric.widthPixels;
	}
	
	public static int getScreenHeight(Context context)
	{
		DisplayMetrics metric = new DisplayMetrics();
	    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
	    
	    return metric.heightPixels;
	}

	/**
	 * 获取屏幕尺寸
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static Point getScreenSize(Context context){
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
			return new Point(display.getWidth(), display.getHeight());
		}else{
			Point point = new Point();
			display.getSize(point);
			return point;
		}
	}

	/**
	 * 抖动窗口
	 * 
	 * @param context
	 * @param view
	 */
	public static void shakeView(Context context, View view)
	{
		if (view != null)
		{
			Animation shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake);
			view.startAnimation(shakeAnimation);
		}
	}

	/**
	 * 设置添加屏幕的背景透明度
	 *
	 * @param alpha 0~1f
	 */
	public static void setBackgroundAlpha(Activity activity, float alpha) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = alpha;
		activity.getWindow().setAttributes(lp);
	}
}