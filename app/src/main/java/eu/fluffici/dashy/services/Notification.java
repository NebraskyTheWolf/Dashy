package eu.fluffici.dashy.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import eu.fluffici.dashy.ui.activities.MainActivity;
import eu.fluffici.dashy.R;

public class Notification extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + Objects.requireNonNull(remoteMessage.getNotification()).getBody());

        sendNotification(remoteMessage.getNotification());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void sendNotification(RemoteMessage.Notification messageBody) {
        NotificationChannel channel = new NotificationChannel("dashy", "dashy", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Dashy notification");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel.getId())
                .setSmallIcon(R.drawable.badges_svg)
                .setContentTitle(messageBody.getTitle())
                .setContentText(messageBody.getBody())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(new Random().nextInt(999999), notificationBuilder.build());
    }
}
