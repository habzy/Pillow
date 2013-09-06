
package com.habzy.pillow;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

public class ShowFromWXActivity extends Activity {

    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory()
            .getAbsolutePath();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_from_wx);
        initView();
    }

    private void initView() {

        final String title = getIntent().getStringExtra("showmsg_title");
        final String message = getIntent().getStringExtra("showmsg_message");
        final byte[] thumbData = getIntent().getByteArrayExtra("showmsg_thumb_data");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        if (thumbData != null && thumbData.length > 0) {
            ImageView thumbIv = new ImageView(this);
            thumbIv.setImageBitmap(BitmapFactory.decodeByteArray(thumbData, 0, thumbData.length));
            builder.setView(thumbIv);
        }

        builder.show();
    }
}
