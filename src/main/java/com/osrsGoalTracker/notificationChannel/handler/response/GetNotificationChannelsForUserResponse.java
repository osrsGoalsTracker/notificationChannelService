package com.osrsGoalTracker.notificationChannel.handler.response;

import java.util.List;

import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;

import lombok.Builder;
import lombok.Value;

/**
 * Response object for retrieving notification channels.
 */
@Value
@Builder
public class GetNotificationChannelsForUserResponse {
    /**
     * The list of notification channels.
     */
    private final List<NotificationChannel> notificationChannels;
}