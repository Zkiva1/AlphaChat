package com.example.alphachat.model;

import java.io.Serializable;

/**
 * Represents a Mechina (pre-military academy) entity.
 *
 * This model class holds information about a specific academy, including its name, location,
 * target audience, and web presence. It is used for displaying academy details and within
 * Firestore collections.
 *
 * Firebase Cloud Firestore {@code mechinot} collection.
 */
public class Mechina implements Serializable {

    /** The name of the Mechina. Maps to Firestore field {@code name}. */
    private String name;
    /** The geographic region where the Mechina is located. Maps to Firestore field {@code region}. */
    private String region;
    /** The gender orientation of the Mechina. Maps to Firestore field {@code gender}. */
    private String gender;
    /** The religious or secular type of the Mechina. Maps to Firestore field {@code type}. */
    private String type;
    /** A URL link to the Mechina's official website. Maps to Firestore field {@code link}. */
    private String link;
    /** A URL to an image representing the Mechina. Maps to Firestore field {@code image}. */
    private String image;

    /**
     * Default constructor required for calls to {@code DataSnapshot.getValue(Mechina.class)}.
     */
    public Mechina() {
    }

    /**
     * Constructs a new Mechina with the specified details.
     *
     * @param name The name of the academy.
     * @param region The location region.
     * @param gender The gender audience (e.g., "Mixed", "Boys", "Girls").
     * @param type The type of academy (e.g., "Secular", "Religious").
     * @param link The official website URL.
     * @param image The representative image URL.
     */
    public Mechina(String name, String region, String gender, String type, String link, String image) {
        this.name = name;
        this.region = region;
        this.gender = gender;
        this.type = type;
        this.link = link;
        this.image = image;
    }

    /**
     * Returns the name of the Mechina.
     *
     * @return The {@code name} string.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Mechina.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the region where the Mechina is located.
     *
     * @return The {@code region} string.
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the region of the Mechina.
     *
     * @param region The region to set.
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Returns the gender orientation of the Mechina.
     *
     * @return The {@code gender} string.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender orientation of the Mechina.
     *
     * @param gender The gender category to set.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Returns the religious or secular type of the Mechina.
     *
     * @return The {@code type} string.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the Mechina.
     *
     * @param type The type category to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the official website URL of the Mechina.
     *
     * @return The {@code link} URL string.
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the official website URL of the Mechina.
     *
     * @param link The URL string to set.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Returns the representative image URL of the Mechina.
     *
     * @return The {@code image} URL string.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the representative image URL of the Mechina.
     *
     * @param image The image URL string to set.
     */
    public void setImage(String image) {
        this.image = image;
    }
}
