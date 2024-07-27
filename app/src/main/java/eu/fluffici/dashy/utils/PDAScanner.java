package eu.fluffici.dashy.utils;

import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.guanmai.scanner.SupporterManager;
import eu.fluffici.dashy.events.module.OrderScannerEvent;

@SuppressWarnings("All")
public class PDAScanner {
    private SupporterManager mSupporterManager;

    public void startScan(Context context, Intent intent, EventBus mBus) {
        // Prevent dual instance in the same context.
        if (mSupporterManager == null) {
            mSupporterManager = new SupporterManager(context, new SupporterManager.IScanListener() {
                @Override
                public void onScannerResultChange(String data) {
                    if (intent.hasExtra("isVoucher"))
                        mBus.post(new OrderScannerEvent(data, "VOUCHER"));
                    if (intent.hasExtra("isVoucherInfo"))
                        mBus.post(new OrderScannerEvent(data, "VOUCHER_INFO"));
                    if (intent.hasExtra("isOrder"))
                        mBus.post(new OrderScannerEvent(data, "ORDER"));
                    if (intent.hasExtra("isProduct"))
                        mBus.post(new OrderScannerEvent(data, "PRODUCT"));
                }
                @Override
                public void onScannerServiceConnected() {}
                @Override
                public void onScannerServiceDisconnected() {}
                @Override
                public void onScannerInitFail() {}
            });
        }

        // Rewrite instance config for each scans.
        mSupporterManager.singleScan(true);
        mSupporterManager.scannerEnable(true);
        mSupporterManager.continuousScan(true);
    }

    public void onDestroy() {
        if (mSupporterManager != null)
            mSupporterManager.recycle();
    }
}
