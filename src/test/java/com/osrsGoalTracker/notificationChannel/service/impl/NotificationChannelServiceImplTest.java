package com.osrsGoalTracker.notificationChannel.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;
import com.osrsGoalTracker.notificationChannel.repository.NotificationChannelRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class NotificationChannelServiceImplTest {

    @Mock
    private NotificationChannelRepository notificationChannelRepository;

    private NotificationChannelServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new NotificationChannelServiceImpl(notificationChannelRepository);
    }

    @Test
    void createNotificationChannel_Success() {
        // Arrange
        String userId = "testUser";
        String channelType = "DISCORD";
        String identifier = "123456789";
        Instant now = Instant.now();

        NotificationChannel expectedChannel = NotificationChannel.builder()
                .userId(userId)
                .channelType(channelType)
                .identifier(identifier)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(notificationChannelRepository.createNotificationChannel(
                eq(userId), eq(channelType), eq(identifier), eq(true)))
                .thenReturn(expectedChannel);

        // Act
        NotificationChannel result = service.createNotificationChannel(userId, channelType, identifier);

        // Assert
        assertEquals(expectedChannel, result);
    }

    @Test
    void createNotificationChannel_NullUserId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createNotificationChannel(null, "DISCORD", "123456789"));
        assertEquals("userId cannot be null or empty", exception.getMessage());
    }

    @Test
    void createNotificationChannel_EmptyUserId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createNotificationChannel("  ", "DISCORD", "123456789"));
        assertEquals("userId cannot be null or empty", exception.getMessage());
    }

    @Test
    void createNotificationChannel_NullChannelType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createNotificationChannel("testUser", null, "123456789"));
        assertEquals("channelType cannot be null or empty", exception.getMessage());
    }

    @Test
    void createNotificationChannel_EmptyChannelType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createNotificationChannel("testUser", "  ", "123456789"));
        assertEquals("channelType cannot be null or empty", exception.getMessage());
    }

    @Test
    void createNotificationChannel_NullIdentifier() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createNotificationChannel("testUser", "DISCORD", null));
        assertEquals("identifier cannot be null or empty", exception.getMessage());
    }

    @Test
    void createNotificationChannel_EmptyIdentifier() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createNotificationChannel("testUser", "DISCORD", "  "));
        assertEquals("identifier cannot be null or empty", exception.getMessage());
    }

    @Test
    void getNotificationChannels_Success() {
        // Arrange
        String userId = "testUser";
        Instant now = Instant.now();

        List<NotificationChannel> expectedChannels = Arrays.asList(
                NotificationChannel.builder()
                        .userId(userId)
                        .channelType("DISCORD")
                        .identifier("123456789")
                        .isActive(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                NotificationChannel.builder()
                        .userId(userId)
                        .channelType("EMAIL")
                        .identifier("test@example.com")
                        .isActive(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build());

        when(notificationChannelRepository.getNotificationChannels(userId))
                .thenReturn(expectedChannels);

        // Act
        List<NotificationChannel> result = service.getNotificationChannels(userId);

        // Assert
        assertEquals(expectedChannels, result);
    }

    @Test
    void getNotificationChannels_NullUserId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getNotificationChannels(null));
        assertEquals("userId cannot be null or empty", exception.getMessage());
    }

    @Test
    void getNotificationChannels_EmptyUserId() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getNotificationChannels("  "));
        assertEquals("userId cannot be null or empty", exception.getMessage());
    }
}