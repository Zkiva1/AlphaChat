const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

// Trigger when a new message is added to the "chats" sub-collection using Cloud Functions v2
exports.sendChatNotification = onDocumentCreated(
    "chatrooms/{chatroomId}/chats/{messageId}",
    async (event) => {

        const messageData = event.data.data();
        if (!messageData) {
            console.log("No message data found.");
            return null;
        }

        const senderId = messageData.senderId;
        const messageText = messageData.message;
        const chatroomId = event.params.chatroomId;

        try {
            // 1. Fetch the chatroom document to find the recipient
            const chatroomDoc = await admin.firestore().collection("chatrooms").doc(chatroomId).get();
            const chatroomData = chatroomDoc.data();

            if (!chatroomData || !chatroomData.userIds) {
                console.log("Chatroom data or userIds missing.");
                return null;
            }

            const userIds = chatroomData.userIds;
            const recipientId = userIds.find(id => id !== senderId);

            if (!recipientId) {
                console.log("Could not find recipient ID in chatroom.");
                return null;
            }

            // 2. Fetch sender profile (for name) and recipient profile (for token) simultaneously
            const [senderDoc, recipientDoc] = await Promise.all([
                admin.firestore().collection("users").doc(senderId).get(),
                admin.firestore().collection("users").doc(recipientId).get()
            ]);

            if (!senderDoc.exists || !recipientDoc.exists) {
                console.log("Sender or recipient document does not exist in Firestore.");
                return null;
            }

            const senderName = senderDoc.data().username;
            const recipientData = recipientDoc.data();
            const token = recipientData.fcmToken;

            if (!token) {
                console.log(`User ${recipientId} does not have an FCM token.`);
                return null;
            }

            // 3. Build and send the notification with High Priority and Custom Data
            const payload = {
                notification: {
                    title: senderName || "New Message",
                    body: messageText || "",
                },
                data: {
                    userId: senderId // Custom data caught by the Android app
                },
                android: {
                    priority: "high",
                    notification: {
                        channelId: "chat_messages_channel" // Triggers the popup channel
                    }
                },
                token: token
            };

            const response = await admin.messaging().send(payload);
            console.log("Successfully sent notification:", response);
            return null;

        } catch (error) {
            console.error("Error sending notification:", error);
            return null;
        }
    }
);