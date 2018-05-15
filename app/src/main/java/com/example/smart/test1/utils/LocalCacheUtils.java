package com.example.smart.test1.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5;

/**
 * 手机本地储存缓存
 */

public class LocalCacheUtils {
    private MemoryCacheUtils memoryCacheUtils;
    public LocalCacheUtils(MemoryCacheUtils memoryCacheUtils){
        this.memoryCacheUtils = memoryCacheUtils;
    }

    public Bitmap getBitmap(String imageUrl) {
        String fileName = MD5Util.encrypt(imageUrl);
        File file = new File(Environment.getExternalStorageDirectory()+"/headIcon",fileName);
        try {
            if (file.exists()){
                FileInputStream fis = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
                memoryCacheUtils.putBitmap(imageUrl,bitmap);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LocalCache","读取本地储存缓存失败");
        }
        return null;
    }

/**
 * 保存一个bitmap对象到手机本地存储中
 */
public void putBitmap(String imageUrl,Bitmap bitmap){
    String fileName = MD5Util.encrypt(imageUrl);
    File file = new File(Environment.getExternalStorageDirectory() + "/headIcon", fileName);
    try{
        File parentFile = file.getParentFile();//获取图片路径
        if (!file.exists()){//如果该文件路径不存在
            parentFile.mkdirs();//创建
        }
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);//压缩图片
        fos.flush();
        fos.close();;
        Log.i("LocalCache","保存本地储存缓存成功");

    } catch (Exception e) {
        e.printStackTrace();
        Log.e("LocalCache","保存本地储存缓存失败");
    }
}

 }
