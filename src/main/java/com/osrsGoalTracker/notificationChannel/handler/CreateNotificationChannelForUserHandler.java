package com.osrsGoalTracker.notificationChannel.handler;

import static com.osrsGoalTracker.utils.Constants.HTTP_BAD_REQUEST;
import static com.osrsGoalTracker.utils.Constants.HTTP_OK;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrsGoalTracker.notificationChannel.di.NotificationChannelModule;
import com.osrsGoalTracker.notificationChannel.handler.request.CreateNotificationChannelForUserRequest;
import com.osrsGoalTracker.notificationChannel.handler.response.CreateNotificationChannelForUserResponse;
import com.osrsGoalTracker.notificationChannel.model.NotificationChannel;
import com.osrsGoalTracker.notificationChannel.service.NotificationChannelService;
import com.osrsGoalTracker.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Handler for creating a notification channel.
 */
@Slf4j
public class CreateNotificationChannelForUserHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final NotificationChannelService notificationChannelService;

    /**
     * Constructor for CreateNotificationChannelHandler.
     */
    public CreateNotificationChannelForUserHandler() {
        Injector injector = Guice.createInjector(new NotificationChannelModule());
        this.notificationChannelService = injector.getInstance(NotificationChannelService.class);
    }

    /**
     * Handles the request to create a notification channel.
     * 
     * @param input   The API Gateway request event.
     * @param context The AWS Lambda context.
     * @return The API Gateway response event.
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            // Parse request
            String userId = input.getPathParameters().get("userId");
            CreateNotificationChannelForUserRequest request = JsonUtils.fromJson(input.getBody(),
                    CreateNotificationChannelForUserRequest.class);

            // Create notification channel
            NotificationChannel notificationChannel = notificationChannelService.createNotificationChannel(
                    userId,
                    request.getChannelType(),
                    request.getIdentifier());

            // Build response
            CreateNotificationChannelForUserResponse response = CreateNotificationChannelForUserResponse.builder()
                    .userId(notificationChannel.getUserId())
                    .channelType(notificationChannel.getChannelType())
                    .identifier(notificationChannel.getIdentifier())
                    .isActive(notificationChannel.isActive())
                    .build();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HTTP_OK)
                    .withBody(JsonUtils.toJson(response));

        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating notification channel", e);
            return createErrorResponse("Error creating notification channel");
        }
    }

    private APIGatewayProxyResponseEvent createErrorResponse(String message) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(HTTP_BAD_REQUEST)
                .withBody(String.format("{\"message\":\"%s\"}", message));
    }
}