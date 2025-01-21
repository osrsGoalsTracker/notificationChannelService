package com.osrsGoalTracker.notificationChannel.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;
import com.osrsGoalTracker.shared.dao.util.SortKeyUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

class NotificationChannelRepositoryImplTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    @Captor
    private ArgumentCaptor<PutItemRequest> putItemRequestCaptor;

    @Captor
    private ArgumentCaptor<QueryRequest> queryRequestCaptor;

    private NotificationChannelRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new NotificationChannelRepositoryImpl(dynamoDbClient);
    }

    @Test
    void createNotificationChannel_Success() {
        // Arrange
        String userId = "testUser";
        String channelType = "DISCORD";
        String identifier = "123456789";
        boolean isActive = true;

        // Act
        NotificationChannel result = repository.createNotificationChannel(userId, channelType, identifier, isActive);

        // Assert
        verify(dynamoDbClient).putItem(putItemRequestCaptor.capture());
        PutItemRequest capturedRequest = putItemRequestCaptor.getValue();

        assertEquals("USER#" + userId, capturedRequest.item().get("pk").s());
        assertEquals(SortKeyUtil.getNotificationChannelSortKey(channelType), capturedRequest.item().get("sk").s());
        assertEquals(channelType, capturedRequest.item().get("channelType").s());
        assertEquals(identifier, capturedRequest.item().get("identifier").s());
        assertEquals(isActive, capturedRequest.item().get("isActive").bool());
        assertNotNull(capturedRequest.item().get("createdAt").s());
        assertNotNull(capturedRequest.item().get("updatedAt").s());
        assertEquals(userId, capturedRequest.item().get("userId").s());

        // Verify returned object
        assertEquals(userId, result.getUserId());
        assertEquals(channelType, result.getChannelType());
        assertEquals(identifier, result.getIdentifier());
        assertEquals(isActive, result.isActive());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void createNotificationChannel_NullUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.createNotificationChannel(null, "DISCORD", "123456789", true));
    }

    @Test
    void createNotificationChannel_EmptyUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.createNotificationChannel("  ", "DISCORD", "123456789", true));
    }

    @Test
    void createNotificationChannel_NullChannelType() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.createNotificationChannel("testUser", null, "123456789", true));
    }

    @Test
    void createNotificationChannel_EmptyChannelType() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.createNotificationChannel("testUser", "  ", "123456789", true));
    }

    @Test
    void createNotificationChannel_NullIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.createNotificationChannel("testUser", "DISCORD", null, true));
    }

    @Test
    void createNotificationChannel_EmptyIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.createNotificationChannel("testUser", "DISCORD", "  ", true));
    }

    @Test
    void getNotificationChannels_Success() {
        // Arrange
        String userId = "testUser";
        Instant now = Instant.now();

        // Create mock DynamoDB response
        Map<String, AttributeValue> item1 = new LinkedHashMap<>();
        item1.put("userId", AttributeValue.builder().s(userId).build());
        item1.put("channelType", AttributeValue.builder().s("DISCORD").build());
        item1.put("identifier", AttributeValue.builder().s("123456789").build());
        item1.put("isActive", AttributeValue.builder().bool(true).build());
        item1.put("createdAt", AttributeValue.builder().s(now.toString()).build());
        item1.put("updatedAt", AttributeValue.builder().s(now.toString()).build());

        Map<String, AttributeValue> item2 = new LinkedHashMap<>();
        item2.put("userId", AttributeValue.builder().s(userId).build());
        item2.put("channelType", AttributeValue.builder().s("EMAIL").build());
        item2.put("identifier", AttributeValue.builder().s("test@example.com").build());
        item2.put("isActive", AttributeValue.builder().bool(true).build());
        item2.put("createdAt", AttributeValue.builder().s(now.toString()).build());
        item2.put("updatedAt", AttributeValue.builder().s(now.toString()).build());

        List<Map<String, AttributeValue>> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        QueryResponse queryResponse = QueryResponse.builder()
                .items(items)
                .build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

        // Act
        List<NotificationChannel> result = repository.getNotificationChannels(userId);

        // Assert
        verify(dynamoDbClient).query(queryRequestCaptor.capture());
        QueryRequest capturedRequest = queryRequestCaptor.getValue();

        // Verify query parameters
        assertEquals("USER#" + userId, capturedRequest.expressionAttributeValues().get(":pk").s());
        assertEquals("NOTIFICATION#", capturedRequest.expressionAttributeValues().get(":sk_prefix").s());

        // Verify results
        assertEquals(2, result.size());

        NotificationChannel channel1 = result.get(0);
        assertEquals(userId, channel1.getUserId());
        assertEquals("DISCORD", channel1.getChannelType());
        assertEquals("123456789", channel1.getIdentifier());
        assertEquals(true, channel1.isActive());
        assertEquals(now, channel1.getCreatedAt());
        assertEquals(now, channel1.getUpdatedAt());

        NotificationChannel channel2 = result.get(1);
        assertEquals(userId, channel2.getUserId());
        assertEquals("EMAIL", channel2.getChannelType());
        assertEquals("test@example.com", channel2.getIdentifier());
        assertEquals(true, channel2.isActive());
        assertEquals(now, channel2.getCreatedAt());
        assertEquals(now, channel2.getUpdatedAt());
    }

    @Test
    void getNotificationChannels_NullUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.getNotificationChannels(null));
    }

    @Test
    void getNotificationChannels_EmptyUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.getNotificationChannels("  "));
    }

    @Test
    void getNotificationChannels_NoResults() {
        // Arrange
        String userId = "testUser";
        QueryResponse queryResponse = QueryResponse.builder()
                .items(new ArrayList<>())
                .build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

        // Act
        List<NotificationChannel> result = repository.getNotificationChannels(userId);

        // Assert
        assertEquals(0, result.size());
    }
}