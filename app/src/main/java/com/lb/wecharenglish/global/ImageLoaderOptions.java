package com.lb.wecharenglish.global;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * ImageLoader配置，主要是加载动画
 */
public class ImageLoaderOptions {
    /**
     * 不带动画的options
     */
    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            // .showImageOnLoading(R.drawable.loading)
            // .showImageForEmptyUri(R.drawable.loading)
            // .showImageOnFail(R.drawable.loading)
            .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
            .displayer(new SimpleBitmapDisplayer()).build();

    /**
     * 带动画的options
     */
    public static DisplayImageOptions fadein_options = new DisplayImageOptions.Builder()
            // .showImageOnLoading(R.drawable.loading)
            // .showImageForEmptyUri(R.drawable.loading)
            // .showImageOnFail(R.drawable.loading)
            .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(1000)).build();


}
