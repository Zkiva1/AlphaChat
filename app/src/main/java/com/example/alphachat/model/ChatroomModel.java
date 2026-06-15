package com.example.alphachat.model;

import com.google.firebase.Timestamp;

import java.util.List;

/**
 * Represents a chat room entity in the Mechinet application.
 *
 * This class tracks the participants of a conversation and metadata about the
 * most recent message sent within the room. It is used to manage and display
 * chat lists.
 *
 * Cloud Firestore {@code chatrooms} collection.
 */
public class ChatroomModel{

    /** The unique identifier for the chat room. Maps to Firestore field {@code chatroomId}. */
    String chatroomId;
    /** A list of UIDs of users participating in the chat. Maps to Firestore field {@code userIds}. */
    List<String> userIds;
    /** The timestamp of the last message sent. Maps to Firestore field {@code lastMessageTimestamp}. */
    com.google.firebase.Timestamp lastMessageTimestamp;
    /** The UID of the user who sent the last message. Maps to Firestore field {@code lastMessageSenderId}. */
    String lastMessageSenderId;
    /** The text content of the last message. Maps to Firestore field {@code lastMessage}. */
    String lastMessage;

    /**
     * Default constructor required for calls to {@code DataSnapshot.getValue(ChatroomModel.class)}.
     */
    public ChatroomModel() {
    }

    /**
     * Constructs a new {@code ChatroomModel} with the specified metadata.
     *
     * @param chatroomId The unique ID for the chat room.
     * @param userIds The list of participants' user IDs.
     * @param lastMessageTimestamp The timestamp of the most recent message.
     * @param lastMessageSenderId The UID of the last message sender.
     */
    public ChatroomModel(String chatroomId, List<String> userIds, com.google.firebase.Timestamp lastMessageTimestamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    /**
     * Returns the unique identifier for the chat room.
     *
     * @return The {@code chatroomId} string.
     */
    public String getChatroomId() {
        return chatroomId;
    }

    /**
     * Sets the unique identifier for the chat room.
     *
     * @param chatroomId The ID string to set.
     */
    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    /**
     * Returns the list of user IDs participating in this chat room.
     *
     * @return A {@link List} of UID strings.
     */
    public List<String> getUserIds() {
        return userIds;
    }

    /**
     * Sets the list of user IDs for the chat room participants.
     *
     * @param userIds The {@link List} of UIDs to set.
     */
    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    /**
     * Returns the timestamp of the last message sent in this chat room.
     *
     * @return The {@link Timestamp} of the last message.
     */
    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    /**
     * Sets the timestamp of the last message sent in this chat room.
     *
     * @param lastMessageTimestamp The {@link Timestamp} to set.
     */
    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    /**
     * Returns the UID of the user who sent the last message.
     *
     * @return The {@code lastMessageSenderId} string.
     */
    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    /**
     * Sets the UID of the user who sent the last message.
     *
     * @param lastMessageSenderId The sender's UID string to set.
     */
    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    /**
     * Returns the text content of the last message sent.
     *
     * @return The {@code lastMessage} string.
     */
    public String getLastMessage() {
        return lastMessage;
    }

    /**
     * Sets the text content of the last message sent.
     *
     * @param lastMessage The message text to set.
     */
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
