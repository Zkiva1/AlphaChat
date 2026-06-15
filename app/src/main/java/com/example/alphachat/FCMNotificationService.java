package com.example.alphachat;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Service for handling Firebase Cloud Messaging (FCM) notifications.
 *
 * This service listens for incoming messages from FCM while the app is in the
 * foreground or background. It extracts notification data and initiates the
 * process to display a system notification.
 *
 * Firebase Cloud Messaging.
 */
public class FCMNotificationService extends FirebaseMessagingService {

    /**
     * Called when a new message is received from FCM.
     *
     * Extracts the notification title, body, and custom data (e.g., sender UID)
     * and triggers {@link #showNotification(String, String, String)}.
     *
     * @param remoteMessage The {@link RemoteMessage} received from Firebase.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String sendingUserId = remoteMessage.getData().get("userId");
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body, sendingUserId);
        }
    }

    /**
     * Configures an intent to launch {@link ChatActivity} when a notification is clicked.
     *
     * @param title The title text for the notification.
     * @param body The body text for the notification.
     * @param sendingUserId The UID of the user who sent the message.
     */
    private void showNotification(String title, String body, String sendingUserId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", sendingUserId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }


    /**
     * Called when a new FCM token is generated for the device.
     *
     * @param token The new registration token string.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}