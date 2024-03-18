package eu.fluffici.security;

public class Native {
    static {
        System.loadLibrary("pda");
    }

    static native boolean isMagiskPresentNative();
}
