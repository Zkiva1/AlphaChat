package com.example.alphachat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "";
        String body = "";
        String sendingUserId = "";

        // 1. Extract custom data payload (The sender's ID)
        if (remoteMessage.getData().size() > 0) {
            sendingUserId = remoteMessage.getData().get("userId");
        }

        // 2. Extract the visual notification payload
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // 3. Display the actual popup notification
        showNotification(title, body, sendingUserId);
    }

    private void showNotification(String title, String body, String sendingUserId) {
        // This ID must exactly match the one in index.js
        String channelId = "chat_messages_channel";

        // Set up the click action to open ChatActivity and pass the sender's ID
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", sendingUserId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.chat_icon) // Ensure this icon exists in your drawables
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // For Android 7.1 and lower
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Required for Android 8.0 (Oreo) and above to force the heads-up popup
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Notifications for incoming direct chat messages");
            channel.enableLights(true);
            channel.enableVibration(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Display the notification using a unique timestamp ID
        if (notificationManager != null) {
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }
}