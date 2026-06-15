package com.example.alphachat.model;

import com.google.firebase.Timestamp;

/**
 * Represents a user profile in the Mechinet application.
 *
 * This class stores essential user information including identity, contact details,
 * and professional context (occupation and associated Mechina). It is used throughout
 * the app for authentication and profile management.
 *
 * Firebase Authentication, Cloud Firestore {@code users} collection.
 */
public class UserModel {

    /** The user's email address. Maps to Firestore field {@code email}. */
    private String email;
    /** The user's display name. Maps to Firestore field {@code username}. */
    private String username;
    /** The date and time the user account was created. Maps to Firestore field {@code createdTimestamp}. */
    private Timestamp createdTimestamp;
    /** The unique identifier for the user. Maps to Firestore field {@code userId}. */
    private String userId;
    /** A URL to the user's profile picture. Maps to Firestore field {@code profilePicUrl}. */
    private String profilePicUrl;
    /** The Firebase Cloud Messaging token for notifications. Maps to Firestore field {@code fcmToken}. */
    private String fcmToken;
    /** The user's role (e.g., student, teacher). Maps to Firestore field {@code occupation}. */
    private String occupation;
    /** The Mechina the user is affiliated with. Maps to Firestore field {@code mechina}. */
    private String mechina;

    /**
     * Default constructor required for calls to {@code DataSnapshot.getValue(UserModel.class)}.
     */
    public UserModel() {
    }

    /**
     * Constructs a new {@code UserModel} with the specified profile details.
     *
     * @param email The user's email address.
     * @param username The user's chosen display name.
     * @param createdTimestamp The time of account creation.
     * @param userId The unique Firebase Auth ID.
     * @param profilePicUrl The URL of the profile image.
     * @param occupation The user's professional role.
     * @param mechina The academy the user belongs to.
     */
    public UserModel(String email, String username, Timestamp createdTimestamp, String userId,
                     String profilePicUrl, String occupation, String mechina) {
        this.email = email;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.profilePicUrl = profilePicUrl;
        this.occupation = occupation;
        this.mechina = mechina;
    }

    /**
     * Returns the user's email address.
     *
     * @return The {@code email} string.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's display name.
     *
     * @return The {@code username} string.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's display name.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the timestamp when the user account was created.
     *
     * @return The {@link Timestamp} of creation.
     */
    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Sets the account creation timestamp.
     *
     * @param createdTimestamp The {@link Timestamp} to set.
     */
    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * Returns the unique identifier for the user.
     *
     * @return The {@code userId} string.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param userId The UID to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the URL of the user's profile picture.
     *
     * @return The {@code profilePicUrl} string.
     */
    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    /**
     * Sets the URL for the user's profile picture.
     *
     * @param profilePicUrl The URL string to set.
     */
    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    /**
     * Returns the Firebase Cloud Messaging token.
     *
     * @return The {@code fcmToken} string used for push notifications.
     */
    public String getFcmToken() {
        return fcmToken;
    }

    /**
     * Sets the Firebase Cloud Messaging token.
     *
     * @param fcmToken The token string to set.
     */
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    /**
     * Returns the user's occupation or role.
     *
     * @return The {@code occupation} string.
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * Sets the user's occupation or role.
     *
     * @param occupation The occupation string (e.g., "Student", "Teacher").
     */
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    /**
     * Returns the name of the Mechina the user is affiliated with.
     *
     * @return The {@code mechina} name string.
     */
    public String getMechina() {
        return mechina;
    }

    /**
     * Sets the affiliation Mechina for the user.
     *
     * @param mechina The name of the Mechina.
     */
    public void setMechina(String mechina) {
        this.mechina = mechina;
    }
}
