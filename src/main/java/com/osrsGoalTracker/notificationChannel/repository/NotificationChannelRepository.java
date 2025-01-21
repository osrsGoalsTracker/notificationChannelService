package com.osrsGoalTracker.notificationChannel.repository;

import java.util.List;

import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;

/**
 * Repository interface for managing notification channel persistence
 * operations.
 */
public interface NotificationChannelRepository {
    /**
     * Creates a new notification channel for a user.
     *
     * @param userId      The ID of the user
     * @param channelType The type of notification channel (e.g., DISCORD)
     * @param identifier  The identifier for the channel (e.g., discord channel ID)
     * @param isActive    Whether the notification channel is active
     * @return The created notification channel
     */
    NotificationChannel createNotificationChannel(String userId, String channelType, String identifier,
            boolean isActive);

    /**
     * Retrieves all notification channels for a user.
     *
     * @param userId The ID of the user
     * @return A list of notification channels associated with the user
     */
    List<NotificationChannel> getNotificationChannels(String userId);
}