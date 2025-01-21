package com.osrsGoalTracker.notificationChannel.repository.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;
import com.osrsGoalTracker.notificationChannel.repository.NotificationChannelRepository;
import com.osrsGoalTracker.shared.dao.util.SortKeyUtil;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

/**
 * Implementation of the NotificationChannelRepository interface.
 */
@Slf4j
public class NotificationChannelRepositoryImpl implements NotificationChannelRepository {
    private static final String PK = "pk";
    private static final String SK = "sk";
    private static final String USER_PREFIX = "USER#";
    private static final String USER_ID = "userId";
    private static final String CHANNEL_TYPE = "channelType";
    private static final String IDENTIFIER = "identifier";
    private static final String IS_ACTIVE = "isActive";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String TABLE_NAME = System.getenv("NOTIFICATION_CHANNEL_TABLE_NAME");

    private final DynamoDbClient dynamoDbClient;

    /**
     * Constructor for NotificationChannelRepositoryImpl.
     *
     * @param dynamoDbClient The AWS DynamoDB client
     */
    @Inject
    public NotificationChannelRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    private void validateCreateNotificationChannelInput(String userId, String channelType, String identifier,
            Boolean isActive) {
        if (userId == null || userId.trim().isEmpty()) {
            log.warn("Attempted to create notification channel with null or empty user ID");
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }
        if (channelType == null || channelType.trim().isEmpty()) {
            log.warn("Attempted to create notification channel with null channel type");
            throw new IllegalArgumentException("Channel type cannot be null");
        }
        if (identifier == null || identifier.trim().isEmpty()) {
            log.warn("Attempted to create notification channel with null or empty identifier");
            throw new IllegalArgumentException("Identifier cannot be null or empty");
        }
        if (isActive == null) {
            log.warn("Attempted to create notification channel with null isActive");
            throw new IllegalArgumentException("isActive cannot be null");
        }
    }

    private Map<String, AttributeValue> createNewChannelItem(String userId, String channelType, String identifier,
            Boolean isActive, Instant timestamp) {
        Map<String, AttributeValue> item = new LinkedHashMap<>();
        item.put(PK, AttributeValue.builder().s(USER_PREFIX + userId).build());
        item.put(SK, AttributeValue.builder()
                .s(SortKeyUtil.getNotificationChannelSortKey(channelType)).build());
        item.put(CHANNEL_TYPE, AttributeValue.builder().s(channelType).build());
        item.put(IDENTIFIER, AttributeValue.builder().s(identifier).build());
        item.put(IS_ACTIVE, AttributeValue.builder().bool(isActive).build());
        item.put(CREATED_AT, AttributeValue.builder().s(timestamp.toString()).build());
        item.put(UPDATED_AT, AttributeValue.builder().s(timestamp.toString()).build());
        item.put(USER_ID, AttributeValue.builder().s(userId).build());
        return item;
    }


    /**
     * Creates a notification channel for a user.
     * 
     * @param userId The user ID.
     * @param channelType The type of notification channel.
     * @param identifier The identifier of the notification channel.
     * @param isActive Whether the notification channel is active.
     * @return The created notification channel.
     */
    @Override
    public NotificationChannel createNotificationChannel(String userId, String channelType, String identifier,
            boolean isActive) {
        log.info("Creating notification channel for user {} of type {}", userId, channelType);

        log.debug("Attempting to create notification channel for user: {}", userId);

        validateCreateNotificationChannelInput(userId, channelType, identifier, isActive);

        Instant now = Instant.now();

        Map<String, AttributeValue> item = createNewChannelItem(userId, channelType, identifier, isActive, now);

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        log.debug("Putting new notification channel item in DynamoDB for user {}", userId);
        dynamoDbClient.putItem(putItemRequest);
        log.info("Successfully created notification channel for user {}", userId);

        return NotificationChannel.builder()
                .userId(userId)
                .channelType(channelType)
                .identifier(identifier)
                .isActive(isActive)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Retrieves all notification channels for a user.
     * 
     * @param userId The user ID.
     * @return The list of notification channels.
     */
    @Override
    public List<NotificationChannel> getNotificationChannels(String userId) {
        log.info("Getting notification channels for user {}", userId);

        if (userId == null || userId.trim().isEmpty()) {
            log.warn("Attempted to get notification channels with null or empty user ID");
            throw new IllegalArgumentException("UserId cannot be null or empty");
        }

        Map<String, AttributeValue> expressionAttributeValues = new LinkedHashMap<>();
        expressionAttributeValues.put(":pk", AttributeValue.builder().s(USER_PREFIX + userId).build());
        expressionAttributeValues.put(":sk_prefix", AttributeValue.builder().s("NOTIFICATION#").build());

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("pk = :pk AND begins_with(sk, :sk_prefix)")
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        QueryResponse response = dynamoDbClient.query(queryRequest);
        List<NotificationChannel> channels = new ArrayList<>();

        for (Map<String, AttributeValue> item : response.items()) {
            channels.add(NotificationChannel.builder()
                    .userId(item.get(USER_ID).s())
                    .channelType(item.get(CHANNEL_TYPE).s())
                    .identifier(item.get(IDENTIFIER).s())
                    .isActive(item.get(IS_ACTIVE).bool())
                    .createdAt(Instant.parse(item.get(CREATED_AT).s()))
                    .updatedAt(Instant.parse(item.get(UPDATED_AT).s()))
                    .build());
        }

        log.debug("Retrieved {} notification channels for user {}", channels.size(), userId);
        return channels;
    }
}