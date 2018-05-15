package com.example.smart.test1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by Smart on 2018-03-12.
 */

public class  MemoryCacheUtils {
    private LruCache<String,Bitmap> lruCache;
    public MemoryCacheUtils() {
        //获取最大内存
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        lruCache = new LruCache<String,Bitmap>(cacheSize){
          protected  int sizeOf(String key,Bitmap value){
              return value.getRowBytes()*value.getHeight();
          }
        };
    }
    /**
     * 保存bitmap对象到手机缓存中
     *
     */
    public void putBitmap(String imageUrl,Bitmap bitmap){
        lruCache.put(imageUrl,bitmap);
        Log.i("MemoryCache","保存到内存缓存成功");
    }

    public Bitmap getBitmap(String imageUrl) {
        Bitmap bitmap = lruCache.get(imageUrl);
        return bitmap;
    }
}
