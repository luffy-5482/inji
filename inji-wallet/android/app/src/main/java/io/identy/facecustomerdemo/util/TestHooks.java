package io.identy.facecustomerdemo.util;

public class TestHooks {
    private static volatile String jsonResponse;
    private static volatile String asHighestSecurityLevelReached;
    private static volatile String matchHighestSecurityLevelReached;
    private static volatile Boolean hasImage;
    private static volatile Boolean hasHRImage;
    private static volatile Boolean hasThumbnail;
    private static volatile Boolean encryptedTemplates;


    public static void setJsonResponseData(
            String response,
            String asLevel,
            String matchLevel,
            boolean image,
            boolean hrImage,
            boolean thumbnail,
            boolean encrypted
    ) {
        jsonResponse = response;
        asHighestSecurityLevelReached = asLevel;
        matchHighestSecurityLevelReached = matchLevel;
        hasImage = image;
        hasHRImage = hrImage;
        hasThumbnail = thumbnail;
        encryptedTemplates = encrypted;
    }

// Created for future use case, if we face any issues with populating the values
    public static void reset() {
        jsonResponse = null;
        asHighestSecurityLevelReached = null;
        matchHighestSecurityLevelReached = null;
        hasImage = null;
        hasHRImage = null;
        hasThumbnail = null;
        encryptedTemplates = null;
    }

    public static String getJsonResponse() { return jsonResponse; }
    public static String getAsHighestSecurityLevel() { return asHighestSecurityLevelReached; }
    public static String getMatchHighestSecurityLevel() { return matchHighestSecurityLevelReached; }
    public static Boolean getHasImage() { return hasImage; }
    public static Boolean getHasHRImage() { return hasHRImage; }
    public static Boolean getHasThumbnail() { return hasThumbnail; }
    public static Boolean getEncryptedTemplates() { return encryptedTemplates; }
}
