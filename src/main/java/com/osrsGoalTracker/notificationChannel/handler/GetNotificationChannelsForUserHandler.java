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
import com.osrsGoalTracker.notificationChannel.handler.response.GetNotificationChannelsForUserResponse;
import com.osrsGoalTracker.notificationChannel.service.NotificationChannelService;
import com.osrsGoalTracker.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Handler for retrieving notification channels for a user.
 */
@Slf4j
public class GetNotificationChannelsForUserHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final NotificationChannelService notificationChannelService;

    /**
     * Constructor for GetNotificationChannelsHandler.
     */
    public GetNotificationChannelsForUserHandler() {
        Injector injector = Guice.createInjector(new NotificationChannelModule());
        this.notificationChannelService = injector.getInstance(NotificationChannelService.class);
    }

    /**
     * Handles the request to get notification channels for a user.
     * 
     * @param input The API Gateway request event.
     * @param context The AWS Lambda context.
     * @return The API Gateway response event.
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        try {
            // Parse request
            String userId = input.getPathParameters().get("userId");

            // Get notification channels
            GetNotificationChannelsForUserResponse response = GetNotificationChannelsForUserResponse.builder()
                    .notificationChannels(notificationChannelService.getNotificationChannels(userId))
                    .build();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(HTTP_OK)
                    .withBody(JsonUtils.toJson(response));

        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage());
        } catch (Exception e) {
            log.error("Error getting notification channels", e);
            return createErrorResponse("Error getting notification channels");
        }
    }

    /**
     * Creates an error response.
     * 
     * @param message The error message.
     * @return The API Gateway response event.
     */
    private APIGatewayProxyResponseEvent createErrorResponse(String message) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(HTTP_BAD_REQUEST)
                .withBody(String.format("{\"message\":\"%s\"}", message));
    }
}