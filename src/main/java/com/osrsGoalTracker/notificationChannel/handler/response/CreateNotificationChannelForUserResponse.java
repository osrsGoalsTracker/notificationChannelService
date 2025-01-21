package com.osrsGoalTracker.notificationChannel.handler.response;

import lombok.Builder;
import lombok.Value;

/**
 * Response object for notification channel creation operations.
 */
@Value
@Builder
public class CreateNotificationChannelForUserResponse {
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
}