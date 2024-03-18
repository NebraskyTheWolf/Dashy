package eu.fluffici.dashy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import eu.fluffici.dashy.entities.PartialUser;

public class Storage {
    public static void setAccessToken(@NonNull Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("BEARER", token);
        editor.apply();
    }

    public static void removeAll(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void setUser(@NonNull Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", username);
        editor.apply();
    }

    public static void setRememberMe(@NonNull Context context, Boolean rememberMe) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remember", rememberMe);
        editor.apply();
    }

    public static void setMessagingToken(@NonNull Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("messagingToken", token);
        editor.apply();
    }

    public static boolean hasMessagingToken(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        return sharedPreferences.contains("messagingToken");
    }

    public static String getMessagingToken(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        return sharedPreferences.getString("messagingToken", null);
    }

    public static PartialUser getUser(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        return new Gson().fromJson(sharedPreferences.getString("user", null), PartialUser.class);
    }

    public static String getAccessToken(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        return sharedPreferences.getString("BEARER", null);
    }

    public static boolean isAuthentified(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        return sharedPreferences.contains("BEARER");
    }

    public static boolean isRememberMe(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PDACrendentials", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("remember", false);
    }

    public static String getAvatar(@NonNull Context context) {
        if (isAuthentified(context)) {
            PartialUser user = getUser(context);
            if (Boolean.parseBoolean(user.getAvatar().toString())) {
                return String.format("https://autumn.fluffici.eu/avatars/%s", user.getAvatarId());
            } else {
                return "https://ui-avatars.com/api/?name=MissingNo&background=0D8ABC&color=fff";
            }
        } else {
            return "https://ui-avatars.com/api/?name=MissingNo&background=0D8ABC&color=fff";
        }
    }
}
