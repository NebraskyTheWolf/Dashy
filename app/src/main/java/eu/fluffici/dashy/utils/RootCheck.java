package eu.fluffici.dashy.utils;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.system.Os;
import android.util.Log;

import com.topjohnwu.superuser.Shell;

import eu.fluffici.security.IIsolatedService;
import eu.fluffici.security.IsolatedService;

public class RootCheck {
    private IIsolatedService serviceBinder;
    private boolean bServiceBound;
    private static final String TAG = "DetectMagisk";

    public boolean isMagiskPresent() {
        if(bServiceBound){
            try {
                Log.d(TAG, "UID:"+ Os.getuid());
                return serviceBinder.isMagiskPresent();
            } catch (RemoteException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isAlternateRoot() {
        Shell.Result result =  Shell.cmd("setenforce 0").exec();
        return result.isSuccess();
    }

    public void onStart(Context context) {
        Intent intent = new Intent(context, IsolatedService.class);
        /*Binding to an isolated service */
        context.bindService(intent, mIsolatedServiceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection mIsolatedServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serviceBinder = IIsolatedService.Stub.asInterface(iBinder);
            bServiceBound = true;
            Log.d(TAG, "Service bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bServiceBound = false;
            Log.d(TAG, "Service Unbound");
        }
    };
}
