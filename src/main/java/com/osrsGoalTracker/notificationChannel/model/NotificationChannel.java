package com.osrsGoalTracker.notificationChannel.model;

import java.time.Instant;

import lombok.Builder;
import lombok.Value;

/**
 * Model representing a notification channel for a user.
 */
@Value
@Builder
public class NotificationChannel {
    /**
     * The ID of the user who owns this notification channel.
     */
    private final String userId;

    /**
     * The type of notification channel (e.g., DISCORD).
     */
    private final String channelType;

    /**
     * The identifier for the channel (e.g., discord channel ID).
     */
    private final String identifier;

    /**
     * Whether the notification channel is active.
     */
    private final boolean isActive;

    /**
     * The timestamp when the notification channel was created.
     */
    private final Instant createdAt;

    /**
     * The timestamp when the notification channel was last updated.
     */
    private final Instant updatedAt;
}
