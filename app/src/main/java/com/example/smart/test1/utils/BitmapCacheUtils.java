package com.example.smart.test1.utils;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Smart on 2018-03-12.
 */

public class BitmapCacheUtils {
    private String imageUrl;
    private MemoryCacheUtils memoryCacheUtils;
    private LocalCacheUtils localCacheUtils;
    private DownloadAvatarUtils downloadAvatarUtils;
    public BitmapCacheUtils(){
        memoryCacheUtils = new MemoryCacheUtils();
        localCacheUtils = new LocalCacheUtils(memoryCacheUtils);
        downloadAvatarUtils = new DownloadAvatarUtils(memoryCacheUtils,localCacheUtils);
    }


    /**
     * 根据图片的网络地址获取为内存的bitmap对象
     */
    public void setGetBitmapListener(String mImageUrl, final GetBitmapListener listener){
        this.imageUrl = mImageUrl;

        Bitmap bitmap = memoryCacheUtils.getBitmap(imageUrl);
        if (bitmap != null){
            Log.i("memoryCacheUtils","手机内存缓存文件成功");
            listener.getBitmap(bitmap);
            return;
        }

        bitmap = localCacheUtils.getBitmap(imageUrl);
        if (bitmap != null){
            Log.i("localCacheUtils","读取手机本地储存缓存文件成功");
            listener.getBitmap(bitmap);
            return;
        }

        downloadAvatarUtils.download(imageUrl, new DownloadAvatarUtils.DownloadAvatarListener() {
            @Override
            public void done(Bitmap bitmap) {
                listener.getBitmap(bitmap);
                return;
            }
        });
    }

    public interface GetBitmapListener {
        void getBitmap(Bitmap bitmap);
    }
}
