package com.example.smart.test1.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * Created by Smart on 2018-04-21.
 */

public class DownloadAvatarUtils {
    private MemoryCacheUtils memoryCacheUtils;
    private LocalCacheUtils localCacheUtils;
    public DownloadAvatarUtils(MemoryCacheUtils memoryCacheUtils,LocalCacheUtils localCacheUtils){
        this.memoryCacheUtils =memoryCacheUtils;
        this.localCacheUtils = localCacheUtils;
    }

    public void download(final String headIcon, final DownloadAvatarListener listener) {
        String headiconName = MD5Util.encrypt(headIcon) + ".png";
        BmobFile bmobfile = new BmobFile(headiconName, "", headIcon);
        File saveFile = new File(Environment.getExternalStorageDirectory() + "/headIcon", bmobfile.getFilename());
        bmobfile.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                Log.i("bmob", "头像下载中");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    Log.i("bmob", "下载成功,保存路径:" + savePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(savePath);
                    if (bitmap == null) {
                        return;
                    }
                    //压缩图片
                    int byteCount = bitmap.getByteCount() / 1024;
                    Log.i("wechat", "压缩前图片的大小" + (bitmap.getByteCount() / 1024) + "KB宽度为"
                            + bitmap.getWidth() + "高度为" + bitmap.getHeight());
                    if (byteCount > 128) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, 180, 180, true);
                        Log.i("wechat", "压缩后图片的大小" + (bitmap.getByteCount() / 1024) + "KB宽度为"
                                + bitmap.getWidth() + "高度为" + bitmap.getHeight());
                    }

                    //资源获取成功后，加入到一级和二级缓存中
                    //1、缓存到手机缓存中
                    memoryCacheUtils.putBitmap(headIcon, bitmap);
                    //2、缓存到手机本地存储中
                    localCacheUtils.putBitmap(headIcon, bitmap);
                    //设置头像
                    listener.done(bitmap);
                } else {
                    Log.i("bmob", "下载失败：" + e.getErrorCode() + "," + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                // Log.i("bmob", "下载进度：" + value + "," + newworkSpeed);
            }

        });


    }

    public interface DownloadAvatarListener {
        void done(Bitmap bitmap);
    }
}
