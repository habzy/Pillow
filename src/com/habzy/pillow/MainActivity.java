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
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class MainActivity extends Activity implements OnClickListener {

    public static final String APP_ID = "wx4de4ec7895ae07ca";

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mShareButton;

    private Button mOpenButton;

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);

        mShareButton = (Button) findViewById(R.id.share);
        mShareButton.setOnClickListener(this);

        mOpenButton = (Button) findViewById(R.id.open);
        mOpenButton.setOnClickListener(this);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                Log.d(TAG, "Click share button");
                sendToWeiXin();
                break;
            case R.id.open:
                Log.d(TAG, "Click open button: opened:" + api.openWXApp());
                sendToWeiXin();
                break;
            default:
                break;
        }

    }

    private void sendToWeiXin() {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        creatTextMsg(req);
        boolean result = api.sendReq(req);
        Log.d(TAG, "Is sended:" + result);
    }

    /**
     * Create Message type is text.
     */
    private void creatTextMsg(SendMessageToWX.Req req) {
        String text = "Test from Pillow";
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // msg.title = "Will be ignored";
        msg.description = text;

        req.transaction = buildTransaction("text");
        req.message = msg;
        // Default is WXSceneSession.
        // req.scene = SendMessageToWX.Req.WXSceneSession;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type
                + System.currentTimeMillis();
    }

}
