package com.osrsGoalTracker.notificationChannel.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.osrsGoalTracker.notificationChannel.repository.NotificationChannelRepository;
import com.osrsGoalTracker.notificationChannel.repository.impl.NotificationChannelRepositoryImpl;
import com.osrsGoalTracker.notificationChannel.service.NotificationChannelService;
import com.osrsGoalTracker.notificationChannel.service.impl.NotificationChannelServiceImpl;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;

/**
 * Guice module for notification channel dependencies.
 */
public class NotificationChannelModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NotificationChannelService.class).to(NotificationChannelServiceImpl.class);
        bind(NotificationChannelRepository.class).to(NotificationChannelRepositoryImpl.class);
    }

    @Provides
    @Singleton
    DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();
    }
}
