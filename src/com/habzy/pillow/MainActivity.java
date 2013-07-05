/**
 * Habzy Huang
 * habzyhs@gmail.com
 */

package com.habzy.pillow;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends Activity implements OnClickListener {

    private static final String APP_ID = "wx4de4ec7895ae07ca";

    private static final String TAG = MainActivity.class.getSimpleName();
    
    private Button mShareButton;

    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
        
        mShareButton = (Button)findViewById(R.id.share);
        mShareButton.setOnClickListener(this);
    }
    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                Log.d(TAG, "Click share button");
                break;

            default:
                break;
        }
        
    }

}
