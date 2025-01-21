package com.osrsGoalTracker.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utility class for JSON operations.
 */
public final class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * Private constructor to prevent instantiation.
     */
    private JsonUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Converts an object to its JSON string representation.
     *
     * @param object The object to convert
     * @return The JSON string representation of the object
     * @throws RuntimeException if there's an error during serialization
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param json  The JSON string to convert
     * @param clazz The class of the object to create
     * @param <T>   The type of the object to create
     * @return The object created from the JSON string
     * @throws RuntimeException if there's an error during deserialization
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to object", e);
        }
    }
}