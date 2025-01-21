package com.osrsGoalTracker.notificationChannel.service.impl;

import java.util.List;

import com.google.inject.Inject;
import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;
import com.osrsGoalTracker.notificationChannel.repository.NotificationChannelRepository;
import com.osrsGoalTracker.notificationChannel.service.NotificationChannelService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the NotificationChannelService interface.
 */
@Slf4j
public class NotificationChannelServiceImpl implements NotificationChannelService {
    private final NotificationChannelRepository notificationChannelRepository;

    /**
     * Constructor for NotificationChannelServiceImpl.
     * 
     * @param notificationChannelRepository The notification channel repository.
     */
    @Inject
    public NotificationChannelServiceImpl(NotificationChannelRepository notificationChannelRepository) {
        this.notificationChannelRepository = notificationChannelRepository;
    }

    /**
     * Creates a notification channel for a user.
     * 
     * @param userId      The user ID.
     * @param channelType The type of notification channel.
     * @param identifier  The identifier of the notification channel.
     * @return The created notification channel.
     */
    @Override
    public NotificationChannel createNotificationChannel(String userId, String channelType, String identifier) {
        validateCreateNotificationChannelParams(userId, channelType, identifier);
        log.info("Creating notification channel for user {} of type {}", userId, channelType);
        // Always create notification channels as active
        return notificationChannelRepository.createNotificationChannel(userId, channelType, identifier, true);
    }

    /**
     * Retrieves all notification channels for a user.
     * 
     * @param userId The user ID.
     * @return The list of notification channels.
     */
    @Override
    public List<NotificationChannel> getNotificationChannels(String userId) {
        validateUserId(userId);
        log.info("Getting notification channels for user {}", userId);
        return notificationChannelRepository.getNotificationChannels(userId);
    }

    /**
     * Validates the parameters for creating a notification channel.
     * 
     * @param userId      The user ID.
     * @param channelType The type of notification channel.
     * @param identifier  The identifier of the notification channel.
     */
    private void validateCreateNotificationChannelParams(String userId, String channelType, String identifier) {
        validateUserId(userId);
        if (channelType == null || channelType.trim().isEmpty()) {
            throw new IllegalArgumentException("channelType cannot be null or empty");
        }
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("identifier cannot be null or empty");
        }
    }

    /**
     * Validates the user ID.
     * 
     * @param userId The user ID.
     */
    private void validateUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId cannot be null or empty");
        }
    }
}