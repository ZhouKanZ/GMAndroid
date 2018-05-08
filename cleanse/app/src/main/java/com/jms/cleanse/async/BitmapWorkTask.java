package com.jms.cleanse.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by WangJun on 2018/5/7.
 */

public class BitmapWorkTask extends AsyncTask<byte[],Integer,Bitmap> {

    private final WeakReference<ImageView> imageWeakReference;

    public BitmapWorkTask(ImageView imageView) {
        imageWeakReference = new WeakReference(imageView);
    }

    @Override
    protected Bitmap doInBackground(byte[]... bytes) {

        return DecodeHelper.decodeSampledBitmap(bytes[0],100,100);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageWeakReference != null && bitmap != null){
            ImageView imageView = imageWeakReference.get();
            if (imageView != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
