package eu.fluffici.security;

import android.os.Build;

import androidx.annotation.NonNull;

@SuppressWarnings("All")
public class DeviceInfo {
    public boolean isPDADevice() {
        return Build.MODEL.equals("PDA") || Build.MODEL.equals("Android SDK built for x86");
    }

    @NonNull
    public String GetDeviceId() {
        return "35" +
                Build.BOARD.length()        % 10 + Build.BRAND.length()          % 10 +
                Build.CPU_ABI.length()      % 10 + Build.DEVICE.length()         % 10 +
                Build.DISPLAY.length()      % 10 + Build.HOST.length()           % 10 +
                Build.ID.length()           % 10 + Build.MANUFACTURER.length()   % 10 +
                Build.MODEL.length()        % 10 + Build.PRODUCT.length()        % 10 +
                Build.TAGS.length()         % 10 + Build.TYPE.length()           % 10 +
                Build.USER.length()         % 10;
    }
}
