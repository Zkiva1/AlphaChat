package com.example.alphachat.model;

import com.google.firebase.Timestamp;

/**
 * Represents a single chat message entity in the Mechinet application.
 *
 * This class encapsulates the content, origin, and timing of a message sent
 * within a chat room. It is used for displaying message bubbles in the chat UI.
 *
 * Cloud Firestore {@code chatrooms/messages} sub-collection.
 */
public class ChatMessageModel{

    /** The text content of the message. Maps to Firestore field {@code message}. */
    private String message;
    /** The UID of the user who sent the message. Maps to Firestore field {@code senderId}. */
    private String senderId;
    /** The timestamp when the message was sent. Maps to Firestore field {@code timestamp}. */
    private Timestamp timestamp;

    /**
     * Default constructor required for calls to {@code DataSnapshot.getValue(ChatMessageModel.class)}.
     */
    public ChatMessageModel() {
    }

    /**
     * Constructs a new {@code ChatMessageModel} with the specified content and metadata.
     *
     * @param message The text content of the message.
     * @param senderId The UID of the sender.
     * @param timestamp The time the message was sent.
     */
    public ChatMessageModel(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    /**
     * Returns the text content of the message.
     *
     * @return The {@code message} string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the text content of the message.
     *
     * @param message The message text to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the UID of the sender.
     *
     * @return The {@code senderId} string.
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Sets the UID of the sender.
     *
     * @param senderId The sender's UID string to set.
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * Returns the timestamp of when the message was sent.
     *
     * @return The {@link Timestamp} of the message.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp for the message.
     *
     * @param timestamp The {@link Timestamp} to set.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
