/**
 * Habzy Huang
 * habzyhs@gmail.com
 */

package com.habzy.pillow;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;

import com.android.vending.billing.IInAppBillingService;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends Activity implements OnClickListener {

    public static final String APP_ID = "wx4de4ec7895ae07ca";

    private static final String APP_SPECIFIC = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkbGhaVegh5bNOBVzyF7rX/i9FTtPsNxrl/qXZuZHqgBVLtbOmnt9XsT8UB698AtxxQCr10jjFhbzGxhfOSGl5MyZKXBAhjRHAyf2NyoKDNA2IH7yfVxxL9QSIOSE6IAjfVcrBaVcnSai2n8G3uAQGxVQgVmYyGSLtzAPf9kpQLrMTBk+6IF3WYFdu6RqnzIoHbB9vGQTq8O0KuKP6zjgbNzcntJvJda6KzMV0nj4PUozUUonZW0DU5ZxM2t1fjsB97U7eRfMKZisTuHrUiINfM3ZDL5UBtVZnQiigKxPclVm7auHHoU7Jg/NCRj7UCe3NAxlpHxtEGMWPtf0rMnDBwIDAQAB";

    private static final String TAG = MainActivity.class.getSimpleName();

    protected static final int UPDATE_BUY = 0;

    private Button mBuyButton;

    private Button mShareButton;

    private Button mOpenButton;

    private IWXAPI api;

    private IInAppBillingService mService;

    private HashMap<String, String> mHashMap = new HashMap<String, String>();

    private String mPillow_0001Name = "pillow_0001";

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            new Thread() {
                public void run() {
                    ArrayList<String> skuList = new ArrayList<String>();
                    skuList.add(mPillow_0001Name);
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                    if (null != mService) {
                        try {
                            Bundle skuDetails = mService.getSkuDetails(3, getPackageName(),
                                    "inapp", querySkus);

                            int response = skuDetails.getInt("RESPONSE_CODE");
                            Log.d(TAG, "response:" + response);
                            if (response == 0) {
                                ArrayList<String> responseList = skuDetails
                                        .getStringArrayList("DETAILS_LIST");

                                for (String thisResponse : responseList) {
                                    JSONObject object;
                                    try {
                                        object = new JSONObject(thisResponse);
                                        String sku = object.getString("productId");
                                        String price = object.getString("price");
                                        Log.d(TAG, "sku:" + sku + ";price:" + price);
                                        if (sku.equals(mPillow_0001Name)) {
                                            mHashMap.put(mPillow_0001Name, price);
                                            mHandler.obtainMessage(UPDATE_BUY, price)
                                                    .sendToTarget();
                                        }

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }

                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
            }.start();
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_BUY:
                    mBuyButton.setText("Use " + (String) msg.obj + " To buy");
                    mBuyButton.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
                mServiceConn, Context.BIND_AUTO_CREATE);

        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);

        mBuyButton = (Button) findViewById(R.id.buy);
        mBuyButton.setOnClickListener(this);

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
            case R.id.buy:
                Log.d(TAG, "Click buy button");
                buyView();
                break;
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

    /**
     * Buy a item
     */
    private void buyView() {
        new Thread() {
            public void run() {
                if (null != mService && mHashMap.containsKey(mPillow_0001Name)) {
                    try {
                        long time = System.currentTimeMillis();
                        Random r = new Random();
                        String payload = time + "p" + r.nextInt(1000) + r.nextInt(1000);
                        Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                                mPillow_0001Name, "inapp", payload);
                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                        try {
                            startIntentSenderForResult(pendingIntent.getIntentSender(), 1001,
                                    new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                    Integer.valueOf(0));
                        } catch (SendIntentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            };
        }.start();
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
        String text = "Text from Pillow";
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Log.d(TAG, "You have bought the " + sku + ". Excellent choice, adventurer!");
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }

}
