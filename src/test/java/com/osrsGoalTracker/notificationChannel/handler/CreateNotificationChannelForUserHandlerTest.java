package com.osrsGoalTracker.notificationChannel.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.osrsGoalTracker.notificationChannel.handler.request.CreateNotificationChannelForUserRequest;
import com.osrsGoalTracker.notificationChannel.handler.response.CreateNotificationChannelForUserResponse;
import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;
import com.osrsGoalTracker.notificationChannel.service.NotificationChannelService;
import com.osrsGoalTracker.utils.JsonUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CreateNotificationChannelForUserHandlerTest {

    @Mock
    private NotificationChannelService notificationChannelService;

    @Mock
    private Context context;

    private CreateNotificationChannelForUserHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new CreateNotificationChannelForUserHandler(notificationChannelService);
    }

    @Test
    void handleRequest_Success() {
        // Arrange
        String userId = "testUser";
        String channelType = "DISCORD";
        String identifier = "123456789";
        Instant now = Instant.now();

        CreateNotificationChannelForUserRequest request = new CreateNotificationChannelForUserRequest();
        request.setChannelType(channelType);
        request.setIdentifier(identifier);

        NotificationChannel mockChannel = NotificationChannel.builder()
                .userId(userId)
                .channelType(channelType)
                .identifier(identifier)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", userId);

        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody(JsonUtils.toJson(request));

        when(notificationChannelService.createNotificationChannel(
                eq(userId), eq(channelType), eq(identifier)))
                .thenReturn(mockChannel);

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(input, context);

        // Assert
        assertEquals(200, response.getStatusCode());

        CreateNotificationChannelForUserResponse expectedResponse = CreateNotificationChannelForUserResponse.builder()
                .userId(userId)
                .channelType(channelType)
                .identifier(identifier)
                .isActive(true)
                .build();
        assertEquals(JsonUtils.toJson(expectedResponse), response.getBody());
    }

    @Test
    void handleRequest_InvalidRequest() {
        // Arrange
        String userId = "testUser";
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", userId);

        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody("invalid json");

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(input, context);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Error creating notification channel\"}", response.getBody());
    }

    @Test
    void handleRequest_ServiceError() {
        // Arrange
        String userId = "testUser";
        String channelType = "DISCORD";
        String identifier = "123456789";

        CreateNotificationChannelForUserRequest request = new CreateNotificationChannelForUserRequest();
        request.setChannelType(channelType);
        request.setIdentifier(identifier);

        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", userId);

        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody(JsonUtils.toJson(request));

        when(notificationChannelService.createNotificationChannel(
                anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid channel type"));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(input, context);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Invalid channel type\"}", response.getBody());
    }

    @Test
    void handleRequest_NullPathParameters() {
        // Arrange
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Error creating notification channel\"}", response.getBody());
    }

    @Test
    void handleRequest_UnexpectedError() {
        // Arrange
        String userId = "testUser";
        String channelType = "DISCORD";
        String identifier = "123456789";

        CreateNotificationChannelForUserRequest request = new CreateNotificationChannelForUserRequest();
        request.setChannelType(channelType);
        request.setIdentifier(identifier);

        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", userId);

        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent()
                .withPathParameters(pathParameters)
                .withBody(JsonUtils.toJson(request));

        when(notificationChannelService.createNotificationChannel(
                anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        APIGatewayProxyResponseEvent response = handler.handleRequest(input, context);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Error creating notification channel\"}", response.getBody());
    }
}