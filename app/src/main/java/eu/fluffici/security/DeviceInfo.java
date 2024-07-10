package eu.fluffici.security;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;
import cn.guanmai.scanner.SupporterManager;

@SuppressWarnings("All")
public class DeviceInfo {
    private final Context context;

    public DeviceInfo(Context context) {
        this.context = context;
    }

    public boolean isPDADevice() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        SupporterManager sm = new SupporterManager(context, new SupporterManager.IScanListener() {
            @Override
            public void onScannerResultChange(String result) {}
            @Override
            public void onScannerServiceConnected() {
                atomicBoolean.set(true);
            }
            @Override
            public void onScannerServiceDisconnected() {}
            @Override
            public void onScannerInitFail() {
                atomicBoolean.set(false);
            }
        });

        return atomicBoolean.get();
    }

    @NonNull
    public String GetDeviceId() {
        return "35" +
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10;
    }
}
