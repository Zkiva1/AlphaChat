package com.example.alphachat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 1. Read your custom data payload
        String sendingUserId = "";
        if (remoteMessage.getData().size() > 0) {
            sendingUserId = remoteMessage.getData().get("userId");
            // This is your "userId" key!
        }

        // 2. Pass it down to showNotification so clicking it knows who sent it
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body, sendingUserId);
        }
    }

    private void showNotification(String title, String body, String sendingUserId) {
        Intent intent = new Intent(this, ChatActivity.class);

        // Pass the sending user's ID into ChatActivity so you can open the right chatroom!
        intent.putExtra("userId", sendingUserId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // ... rest of your PendingIntent and NotificationCompat code stays exactly the same ...
    }

    private void showNotification(String title, String body) {
        // Clicking the notification opens ChatActivity
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        String channelId = "chat_messages_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android Oreo (API 26) and above requires a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // Uses your app icon as default notification icon
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // If the token refreshes while the app is running, update it in your utility
        // com.example.alphachat.utils.FirebaseUtil.currentUserDetails().update("fcmToken", token);
    }
}