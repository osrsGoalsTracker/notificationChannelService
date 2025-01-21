package com.osrsGoalTracker.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

class JsonUtilsTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestObject {
        private String stringField;
        private int intField;
        private boolean booleanField;
        private Instant instantField;
    }

    @Test
    void toJson_Success() {
        // Arrange
        TestObject testObject = new TestObject("test", 42, true, Instant.parse("2024-01-01T00:00:00Z"));

        // Act
        String json = JsonUtils.toJson(testObject);

        // Assert
        assertNotNull(json);
        TestObject result = JsonUtils.fromJson(json, TestObject.class);
        assertEquals(testObject, result);
    }

    @Test
    void toJson_NullObject() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.toJson(null));
        assertTrue(exception.getCause() instanceof JsonProcessingException);
    }

    @Test
    void fromJson_Success() {
        // Arrange
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        String json = String.format(
                "{\"stringField\":\"test\",\"intField\":42,\"booleanField\":true,\"instantField\":\"%s\"}",
                now.toString());

        // Act
        TestObject result = JsonUtils.fromJson(json, TestObject.class);

        // Assert
        assertEquals("test", result.getStringField());
        assertEquals(42, result.getIntField());
        assertEquals(true, result.isBooleanField());
        assertEquals(now, result.getInstantField());
    }

    @Test
    void fromJson_InvalidJson() {
        // Arrange
        String invalidJson = "invalid json";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.fromJson(invalidJson, TestObject.class));
        assertTrue(exception.getCause() instanceof JsonProcessingException);
    }

    @Test
    void fromJson_NullJson() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.fromJson(null, TestObject.class));
        assertTrue(exception.getCause() instanceof JsonProcessingException);
    }

    @Test
    void fromJson_NullClass() {
        // Arrange
        String json = "{\"stringField\":\"test\"}";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> JsonUtils.fromJson(json, null));
        assertTrue(exception.getCause() instanceof JsonProcessingException);
    }
}