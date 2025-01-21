package com.osrsGoalTracker.notificationChannel.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.osrsGoalTracker.notificationChannel.handler.response.GetNotificationChannelsForUserResponse;
import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;
import com.osrsGoalTracker.notificationChannel.service.NotificationChannelService;
import com.osrsGoalTracker.utils.JsonUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GetNotificationChannelsForUserHandlerTest {

    @Mock
    private NotificationChannelService notificationChannelService;

    @Mock
    private Context context;

    private GetNotificationChannelsForUserHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GetNotificationChannelsForUserHandler(notificationChannelService);
    }

    @Test
    void handleRequest_Success() {
        // Arrange
        String userId = "testUser";
        Instant now = Instant.now();
        List<NotificationChannel> mockChannels = Arrays.asList(
                NotificationChannel.builder()
                        .userId(userId)
                        .channelType("DISCORD")
                        .identifier("123456789")
                        .isActive(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build());

        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", userId);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters);

        when(notificationChannelService.getNotificationChannels(userId)).thenReturn(mockChannels);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertEquals(200, response.getStatusCode());

        GetNotificationChannelsForUserResponse expectedResponse = GetNotificationChannelsForUserResponse.builder()
                .notificationChannels(mockChannels)
                .build();
        assertEquals(JsonUtils.toJson(expectedResponse), response.getBody());
    }

    @Test
    void handleRequest_InvalidUserId() {
        // Arrange
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "invalid-user");

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters);

        when(notificationChannelService.getNotificationChannels(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Invalid user ID\"}", response.getBody());
    }

    @Test
    void handleRequest_NullPathParameters() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Error getting notification channels\"}", response.getBody());
    }

    @Test
    void handleRequest_UnexpectedError() {
        // Arrange
        String userId = "testUser";
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", userId);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters);

        when(notificationChannelService.getNotificationChannels(userId))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Error getting notification channels\"}", response.getBody());
    }
}