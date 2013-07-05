/*
 * @date 2013-7-5
 * Habzy Huang (habzyhs@gmail.com)
 *
 */

package com.habzy.pillow.wxapi;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import com.habzy.pillow.R;

/**
 * Detailed description
 */
public class WXEntryActivity extends Activity implements OnClickListener, IWXAPIEventHandler {

    public static final String APP_ID = "wx4de4ec7895ae07ca";

    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory()
            .getAbsolutePath();

    private static final String TAG = WXEntryActivity.class.getSimpleName();

    private Button mShareButton;

    private IWXAPI api;

    private Button mOpenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        boolean isRegistered = api.registerApp(APP_ID);
        Log.d(TAG, "is registered:" + isRegistered);

        mShareButton = (Button) findViewById(R.id.share);
        mShareButton.setOnClickListener(this);

        mOpenButton = (Button) findViewById(R.id.open);
        mOpenButton.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
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
                break;
            default:
                break;
        }

    }

    private void sendToWeiXin() {
        // final WXAppExtendObject appdata = new WXAppExtendObject();
        // final String path = SDCARD_ROOT + "/DCIM/Camera/20130705_150018.jpg";
        // appdata.filePath = path;
        // appdata.extInfo = "this is ext info 111";
        //
        // Log.d(TAG, "path:" + path);
        // final WXMediaMessage msg = new WXMediaMessage();
        //
        // Bitmap thumb = BitmapFactory.decodeResource(getResources(),
        // R.drawable.ic_launcher);
        // msg.thumbData = Util.bmpToByteArray(thumb, true);
        // msg.setThumbImage(Util.extractThumbNail(path, 150, 150, true));
        // msg.title = "this is title 1";
        // msg.description = "this is description 1";
        // msg.mediaObject = appdata;

        // WXImageObject imgObj = new WXImageObject();
        // imgObj.setImagePath(path);
        //
        // WXMediaMessage msg = new WXMediaMessage();
        // msg.mediaObject = imgObj;
        //
        // Bitmap bmp = BitmapFactory.decodeFile(path);
        // Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        // bmp.recycle();
        // msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        // Bitmap bmp = BitmapFactory.decodeResource(getResources(),
        // R.drawable.ic_launcher);
        // WXImageObject imgObj = new WXImageObject(bmp);
        //
        // WXMediaMessage msg = new WXMediaMessage();
        // msg.mediaObject = imgObj;
        //
        // Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        // bmp.recycle();
        // msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        //
        // SendMessageToWX.Req req = new SendMessageToWX.Req();
        // req.transaction = buildTransaction("img");
        // req.message = msg;
        // req.scene = SendMessageToWX.Req.WXSceneSession;
        // boolean result = api.sendReq(req);
        // Log.d(TAG, "Is sended:" + result);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        creatTextMsg(req);
        boolean result = api.sendReq(req);
        Log.d(TAG, "Is sended:" + result);
    }

    /**
     * 
     */
    private void creatTextMsg(SendMessageToWX.Req req) {
        String text = "A ha";
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // msg.title = "Will be ignored";
        msg.description = text;

        req.transaction = buildTransaction("text");
        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneSession;
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type
                + System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.tencent.mm.sdk.openapi.IWXAPIEventHandler#onReq(com.tencent.mm.sdk
     * .openapi.BaseReq)
     */
    @Override
    public void onReq(BaseReq req) {
        Log.d(TAG, "onReq");
    }

    /*
     * (non-Javadoc)
     * @see
     * com.tencent.mm.sdk.openapi.IWXAPIEventHandler#onResp(com.tencent.mm.sdk
     * .openapi.BaseResp)
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onResp");
        String result = "";

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "errcode_success";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "errcode_cancel";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "errcode_deny";
                break;
            default:
                result = "errcode_unknown";
                break;
        }

        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

}
