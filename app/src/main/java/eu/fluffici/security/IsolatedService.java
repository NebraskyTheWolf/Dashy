package eu.fluffici.security;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.system.Os;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class IsolatedService extends Service{

    private static final String[] blackListedMountPaths = { "magisk", "core/mirror", "core/img"};
    private static final String TAG = "DetectMagisk-Isolated";
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IIsolatedService.Stub mBinder = new IIsolatedService.Stub(){
        public boolean isMagiskPresent(){

            Log.d(TAG, "Isolated UID:"+ Os.getuid());

            boolean isMagiskPresent = false;

            File file = new File("/proc/self/mounts");

            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String str;
                int count =0;
                while((str = reader.readLine()) != null && (count==0)){
                    for(String path:blackListedMountPaths){
                        if(str.contains(path)){
                            Log.d(TAG, "Blacklisted Path found "+ path);
                            count++;
                            break;
                        }
                    }
                }
                reader.close();
                fis.close();
                Log.d(TAG, "Count of detected paths "+ count);
                if(count > 0){
                    Log.d(TAG, "Found magisk in at least 1 mount path ");
                    isMagiskPresent = true;
                }else {
                    /**
                     * The following code provides an additional security layer in the Java-native interface.
                     * When Java calls are maliciously intercepted (hooked), this section of code acts as a secondary check.
                     * It does so by searching for any blacklisted paths that appear in the /proc maps file.
                     * This safety scan also extends to checking for unauthorized usage of superuser (su) files from the native libraries.
                     *
                     * It is important to note that native functions can also be corrupted (hooked).
                     * However, intercepting functions at the native level is more challenging if the code is properly obfuscated.
                     * Additionally, system calls employed in lieu of standard libc calls provide an extra layer of protection.
                     */
                    isMagiskPresent = Native.isMagiskPresentNative();
                    Log.d(TAG, "Found Magisk in Native " + isMagiskPresent);
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
            return isMagiskPresent;
        }
    };
}