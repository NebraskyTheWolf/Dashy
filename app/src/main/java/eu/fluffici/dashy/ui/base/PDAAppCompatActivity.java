package eu.fluffici.dashy.ui.base;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.os.Bundle;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.internal.GeneratedComponent;
import eu.fluffici.dashy.ui.activities.auth.LoginActivity;
import eu.fluffici.dashy.ui.activities.common.ErrorScreen;
import eu.fluffici.dashy.ui.activities.modules.impl.scanner.ScannerActivity;
import eu.fluffici.dashy.utils.Storage;
import eu.fluffici.security.DeviceInfo;

public class PDAAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Storage.getAccessToken(getApplicationContext()) == null && Storage.isAuthentified(getApplicationContext())) {
            Storage.removeAll(getApplicationContext());
        }

        DeviceInfo deviceInfo = new DeviceInfo();

        if (!Storage.isAuthentified(getApplicationContext()) || System.getProperty("X-Bearer-token") == null && deviceInfo.isPDADevice()) {
            Intent i = new Intent(getApplicationContext(), ErrorScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("title", "Unable to authenticate");
            i.putExtra("description", "It seems that your device skipped the authentication.");
            return;
        }

        if (!Storage.isAuthentified(getApplicationContext()) && !deviceInfo.isPDADevice()) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(i.getFlags()
                    | FLAG_ACTIVITY_CLEAR_TOP
                    | FLAG_ACTIVITY_NEW_TASK
                    | FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (Storage.isOrderFocusMode) {
            Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
            intent.getExtras().putBoolean("isOrder", true);
            this.startActivity(intent);
        }
    }
}
