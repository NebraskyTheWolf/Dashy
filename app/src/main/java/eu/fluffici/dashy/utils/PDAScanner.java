package eu.fluffici.dashy.utils;

import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import cn.guanmai.scanner.SupporterManager;

@SuppressWarnings("All")
public class PDAScanner {
    public static void startScan(Context context, Intent intent, EventBus mBus, GMCallback callback) {
        SupporterManager sm = new SupporterManager(context, new SupporterManager.IScanListener() {
            @Override
            public void onScannerResultChange(String result) {
                callback.onSuccess(intent, mBus, result);
            }
            @Override
            public void onScannerServiceConnected() {}
            @Override
            public void onScannerServiceDisconnected() {}
            @Override
            public void onScannerInitFail() {
                callback.onError();
            }
        });
    }


    public static interface GMCallback {
        void onSuccess(Intent intent, EventBus mBus, String data);
        void onError();
    }
}
