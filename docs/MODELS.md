# Data Models

## Overview

All models in the service follow these principles:
- Immutable using Lombok `@Value`
- Builder pattern using Lombok `@Builder`
- Request objects use `@Data` with `@NoArgsConstructor`
- Clear separation between domain models and DTOs
- Validation using Jakarta Validation annotations

## Domain Models

### Notification Channel Domain

```java
@Value
@Builder
public class NotificationChannel {
    String userId;
    String channelId;
    String channelType;
    String identifier;
    boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```

### Notification Channel Endpoints

```java
@Data
@NoArgsConstructor
public class CreateNotificationChannelForUserRequest {
    @NotBlank(message = "Channel type is required")
    private String channelType;

    @NotBlank(message = "Identifier is required")
    private String identifier;
    // Note: Notification channels are active by default upon creation
}

@Value
@Builder
public class GetNotificationChannelsForUserResponse {
    List<NotificationChannel> notificationChannels;
}
```

## Database Models

### DynamoDB Entities

```java
@DynamoDBTable(tableName = "NotificationChannels")
public class NotificationChannelEntity {
    @DynamoDBHashKey
    private String userId;
    
    @DynamoDBRangeKey
    private String channelId;
    
    @DynamoDBAttribute
    private String channelType;
    
    @DynamoDBAttribute
    private String identifier;
    
    @DynamoDBAttribute
    private boolean active;
    
    @DynamoDBAttribute
    private String createdAt;
    
    @DynamoDBAttribute
    private String updatedAt;
}
```

## Model Conversion

Each domain should provide mapper methods for converting between different model representations:

```java
public class UserMapper {
    public static User fromEntity(UserEntity entity) {
        return User.builder()
            .userId(entity.getUserId())
            .email(entity.getEmail())
            .createdAt(parseDateTime(entity.getCreatedAt()))
            .updatedAt(parseDateTime(entity.getUpdatedAt()))
            .build();
    }
    
    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setUserId(user.getUserId());
        entity.setEmail(user.getEmail());
        entity.setCreatedAt(formatDateTime(user.getCreatedAt()));
        entity.setUpdatedAt(formatDateTime(user.getUpdatedAt()));
        return entity;
    }
}
``` 