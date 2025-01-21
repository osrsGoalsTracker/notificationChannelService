package com.osrsGoalTracker.notificationChannel.handler.request;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for creating a new notification channel.
 */
@Data
@NoArgsConstructor
public class CreateNotificationChannelForUserRequest {
    /**
     * The type of notification channel (e.g., DISCORD).
     */
    private String channelType;

    /**
     * The identifier for the channel (e.g., discord channel ID).
     */
    private String identifier;
}