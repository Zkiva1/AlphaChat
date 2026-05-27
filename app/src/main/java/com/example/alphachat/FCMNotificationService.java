package com.example.alphachat;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotificationService extends FirebaseMessagingService {

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

    private void showNotification(String title, String body, String sendingUserId) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", sendingUserId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}