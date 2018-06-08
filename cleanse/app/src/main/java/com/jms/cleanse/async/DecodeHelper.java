package com.jms.cleanse.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by WangJun on 2018/5/7.
 */

public class DecodeHelper {

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){

        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if (width > reqWidth || height > reqHeight){

            int halfWidth = width/2;
            int halfHeight = height/2;

            while(halfHeight / inSampleSize > reqHeight|| halfWidth/inSampleSize > reqWidth){
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmap(byte[] data, int reqWidth, int reqHeight){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data,0,data.length,options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data,0,data.length,options);
    }
}
