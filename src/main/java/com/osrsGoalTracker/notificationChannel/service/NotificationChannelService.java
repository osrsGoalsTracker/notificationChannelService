package com.osrsGoalTracker.notificationChannel.service;

import java.util.List;

import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;

/**
 * Service interface for managing notification channel operations.
 */
public interface NotificationChannelService {
    /**
     * Creates a new notification channel for a user.
     *
     * @param userId      The ID of the user
     * @param channelType The type of notification channel (e.g., DISCORD)
     * @param identifier  The identifier for the channel (e.g., discord channel ID)
     * @return The created notification channel
     * @throws IllegalArgumentException if any of the parameters are invalid
     */
    NotificationChannel createNotificationChannel(String userId, String channelType, String identifier);

    /**
     * Retrieves all notification channels for a user.
     *
     * @param userId The ID of the user
     * @return A list of notification channels associated with the user
     * @throws IllegalArgumentException if userId is null or empty
     */
    List<NotificationChannel> getNotificationChannels(String userId);
}