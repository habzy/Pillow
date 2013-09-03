/**
 * Habzy Huang
 * habzyhs@gmail.com
 */

package com.habzy.pillow.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Detailed description
 */
public class ImageTools {

    protected static final String TAG = ImageTools.class.getSimpleName();

    private static final String GRAVATAR = "http://www.gravatar.com/avatar/";

    public void getIconFromGravatar(final String email, final int size,
            final ImageWorkshopListener listener) {

        new Thread() {

            @Override
            public void run() {
                try {
                    Log.d(TAG, "email:" + email);
                    String emailCh = email.trim().toLowerCase();
                    if (TextUtils.isEmpty(emailCh)) {
                        Log.w(TAG, "invalid email");
                        listener.onError();
                        return;
                    }
                    int imageSize = size;
                    if (imageSize < 1 || imageSize > 2048) {
                        imageSize = 200;
                    }
                    String md5 = Util.getMD5(emailCh);
                    String result = GRAVATAR + md5 + ".png?s=" + imageSize + "&d=404";
                    Log.d(TAG, "result:" + result);
                    URL url = new URL(result);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    listener.onImageLoaded(result, myBitmap);
                    Log.d(TAG, "Returned");
                } catch (IOException e) {
                    listener.onError();
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * A Listener for get result of image.
     */
    public interface ImageWorkshopListener {
        /**
         * @param imgPath
         * @param bitmap
         */
        public void onImageLoaded(String imgPath, Bitmap bitmap);

        public void onError();
    }

}
