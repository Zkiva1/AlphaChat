const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

// Trigger when a new message is added to the "chats" sub-collection using Cloud Functions v2
exports.sendChatNotification = onDocumentCreated(
    "chatrooms/{chatroomId}/chats/{messageId}",
    async (event) => {

        const messageData = event.data.data();

        const senderId = messageData.senderId;
        const messageText = messageData.message;

        // document path parameters are found inside event.params
        const chatroomId = event.params.chatroomId;

        try {
            // 1. Fetch the chatroom document to figure out who the OTHER person is
            const chatroomDoc = await admin.firestore().collection("chatrooms").doc(chatroomId).get();
            const chatroomData = chatroomDoc.data();

            if (!chatroomData || !chatroomData.userIds) {
                console.log("Chatroom data or userIds missing.");
                return null;
            }

            const userIds = chatroomData.userIds;

            // Find the recipient (the ID that is NOT the sender's ID)
            const recipientId = userIds.find(id => id !== senderId);

            if (!recipientId) {
                console.log("Could not find recipient ID in chatroom.");
                return null;
            }

            // 2. Fetch BOTH the sender's profile (for name) and the recipient's profile (for token) simultaneously
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

            // 3. Build and send the notification with embedded custom data
            const payload = {
                notification: {
                    title: senderName || "New Message",
                    body: messageText || "",
                },

                data: {
                    userId: senderId // Key: "userId", Value: the sending user's ID string
                },
                token: token
            };

            await admin.messaging().send(payload);
            console.log("Successfully sent notification.");

        } catch (error) {
            console.error("Error sending notification:", error);
        }
    }
);