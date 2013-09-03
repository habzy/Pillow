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
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.ShowMessageFromWX.Req;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.habzy.pillow.R;
import com.habzy.pillow.ShowFromWXActivity;


/**
 * Detailed description
 */
public class WXEntryActivity extends Activity implements OnClickListener, IWXAPIEventHandler {

    public static final String APP_ID = "wx4de4ec7895ae07ca";

    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory()
            .getAbsolutePath();

    private static final String TAG = WXEntryActivity.class.getSimpleName();

    private TextView mTextConten;

    private IWXAPI api;

    private Button mConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_from_wx);
        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
        boolean isRegistered = api.registerApp(APP_ID);
        Log.d(TAG, "is registered:" + isRegistered);

        mTextConten = (TextView) findViewById(R.id.content);

        mConfirmButton = (Button) findViewById(R.id.confirm);
        mConfirmButton.setOnClickListener(this);

        Intent intent = getIntent();
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
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
            case R.id.confirm:
                Log.d(TAG, "Click confirm button");
                // sendToWeiXin();
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
        // req.scene = SendMessageToWX.Req.WXSceneSession;
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
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    /**
     * @param req
     */
    private void goToShowMsg(Req req) {
        // TODO Auto-generated method stub
        Log.d(TAG, "goToShowMsg");
        
        WXMediaMessage wxMsg = req.message;
        WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;
        
        StringBuffer msg = new StringBuffer();
        msg.append("description: ");
        msg.append(wxMsg.description);
        msg.append("\n");
        msg.append("extInfo: ");
        msg.append(obj.extInfo);
        msg.append("\n");
        msg.append("filePath: ");
        msg.append(obj.filePath);
        
        Log.d(TAG, "description:" + wxMsg.description);
        Log.d(TAG, "extInfo:" + obj.extInfo);
        Log.d(TAG, "filePath:" + obj.filePath);
        
        Intent intent = new Intent(this, ShowFromWXActivity.class);
        intent.putExtra("showmsg_title", wxMsg.title);
        intent.putExtra("showmsg_message", msg.toString());
        intent.putExtra("showmsg_thumb_data", wxMsg.thumbData);
        startActivity(intent);
        finish();
    }

    /**
     * 
     */
    private void goToGetMsg() {
        // TODO Auto-generated method stub
        Log.d(TAG, "goToGetMsg");
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
