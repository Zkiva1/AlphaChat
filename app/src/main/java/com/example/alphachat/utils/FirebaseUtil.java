package com.example.alphachat.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Utility class for Firebase service operations.
 *
 * This class centralizes access to Firebase Authentication, Cloud Firestore, and
 * Firebase Storage. It provides helper methods for user identity, data references,
 * and common database paths.
 */
public class FirebaseUtil {

    /**
     * Returns the unique ID of the currently authenticated user.
     *
     * @return The UID string from {@link FirebaseAuth}, or {@code null} if no user is logged in.
     */
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }


    /**
     * Checks if a user is currently logged into the application.
     *
     * @return {@code true} if a user is authenticated, {@code false} otherwise.
     */
    public static boolean isLoggedIn() {
        if (currentUserId() != null) {
            return true;
        }
        return false;
    }

    /**
     * Returns a reference to the current user's document in Firestore.
     *
     * @return A {@link DocumentReference} pointing to the user's data in the {@code users}
     *         collection.
     */
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }


    /**
     * Signs the current user out of Firebase Authentication.
     */
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Returns a reference to a specific chat room document.
     *
     * @param chatroomId The unique ID of the chat room.
     * @return A {@link DocumentReference} for the room in the {@code chatrooms} collection.
     */
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    /**
     * Generates a deterministic chat room ID based on two user IDs.
     *
     * The IDs are sorted lexicographically to ensure that the same room ID is
     * generated regardless of which user initiates the chat.
     *
     * @param user1 The first user's UID.
     * @param user2 The second user's UID.
     * @return A composite string ID for the chat room.
     */
    public static String getChatroomId(String user1, String user2) {
        if(user1.hashCode()<user2.hashCode()) {
            return user1+"_"+user2;
        }else {
            return user2+"_"+user1;
        }
    }

    /**
     * Returns a reference to the messages sub-collection within a chat room.
     *
     * @param chatroomId The unique ID of the chat room.
     * @return A {@link CollectionReference} for the {@code chats} sub-collection.
     */
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    /**
     * Returns a reference to the top-level chat rooms collection.
     *
     * @return A {@link CollectionReference} for the {@code chatrooms} collection.
     */
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    /**
     * Retrieves a reference to the document of the other participant in a two-person chat.
     *
     * @param userIds A list containing exactly two user IDs.
     * @return A {@link DocumentReference} for the user who is NOT the current user.
     */
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        }else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    /**
     * Formats a Firebase {@link Timestamp} into a readable "HH:mm" string.
     *
     * @param timestamp The Firestore timestamp to format.
     * @return A formatted time string.
     */
    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    /**
     * Returns a reference to the current user's profile picture in Firebase Storage.
     *
     * @return A {@link StorageReference} pointing to the image in the {@code profile_pic} path.
     */
    public static StorageReference getCurrentProfilePicRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    /**
     * Returns a reference to the top-level users collection in Firestore.
     *
     * @return A {@link CollectionReference} for the {@code users} collection.
     */
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

}
