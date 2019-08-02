package com.frxs.common_base.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.frxs.common_base.R;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/09/12
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ImageLoader {

    public static int IMG_LOADING = R.mipmap.ic_image_loading;
    public static int IMG_ERROR = R.mipmap.ic_empty_picture;

    public static void loadImage(Context context, ImageView imageView, int placeholderResId) {
        Glide.with(context).load(placeholderResId).into(imageView);
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        loadImage(context, imageUrl, imageView, 0, 0);
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView, int errorResId) {
        loadImage(context, imageUrl, imageView, 0, errorResId);
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView, int placeholderResId , int errorResId) {
        RequestOptions options = new RequestOptions()
                .placeholder(placeholderResId == 0 ? IMG_LOADING : placeholderResId)  //加载中显示的图片
                .centerCrop()            //图像则位于视图的中央
                .error(errorResId == 0 ? IMG_ERROR : errorResId) //加载失败时显示的图片centerCrop().
                .diskCacheStrategy(DiskCacheStrategy.ALL);  //图片缓存
        Glide.with(context).load(imageUrl).apply(options).into(imageView);
    }
}
