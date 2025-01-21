# Project Architecture

## Layered Architecture Rule Set

This rule set defines how to organize and implement a modular layered architecture for a product, including both domain services (e.g., `UserService`, `GoalService`) and orchestration Lambdas for event-driven workflows. It outlines layers, interaction rules, service boundaries, and specific patterns to handle cross-cutting concerns like events and shared resources.

## Rule Set

### Layer Definitions

#### Domain Layer (Service-Specific)

Each domain (e.g., `User`, `Goal`) has its own service responsible for managing the business logic and data access for its entities. It is structured into three sub-layers:

1. **Handler Layer**
   - Contains entry points for APIs (e.g., Lambda handlers or controllers).
   - Handles input (e.g., HTTP requests or events) and delegates logic to the service layer.
   - Only calls the service layer.

2. **Service Layer**
   - Encapsulates business logic for the domain.
   - Uses repository interfaces to fetch or persist data.
   - Only depends on the repository layer or other services via interfaces.

3. **Repository Layer**
   - Abstracts data access logic.
   - Interacts with the underlying data store (e.g., DynamoDB, RDBMS).
   - Provides methods scoped to the domain (e.g., `findUserById`, `saveGoal`).
   - Does not call the service layer.

#### Orchestration Layer

Handles cross-domain interactions and event-driven workflows:

1. **Event Models**
   - Defines domain events (e.g., `GoalCreationEvent`) that represent state changes.
   - Contains all necessary data for downstream processing.
   - Immutable and serializable.
   - Located in `*.orchestration.events` packages.
   - Example:
     ```java
     @Value
     @Builder
     public class GoalCreationEvent {
         String userId;
         String goalId;
         String characterName;
         String targetAttribute;
         String targetType;
         long targetValue;
         long currentValue;
         LocalDate targetDate;
         String notificationChannelType;
         String frequency;
         Instant createdAt;
         Instant updatedAt;
     }
     ```

2. **Event Handlers**
   - Processes domain events.
   - Coordinates interactions between services.
   - Maintains event-driven workflows.
   - Located in `*.orchestration.handlers` packages.
   - Example:
     ```java
     public class GoalCreationEventHandler implements RequestHandler<SQSEvent, Void> {
         private final GoalService goalService;
         private final NotificationService notificationService;

         @Override
         public Void handleRequest(SQSEvent event, Context context) {
             for (SQSEvent.SQSMessage message : event.getRecords()) {
                 GoalCreationEvent goalEvent = parseEvent(message);
                 processGoalCreation(goalEvent);
             }
             return null;
         }

         private void processGoalCreation(GoalCreationEvent event) {
             // 1. Update notification preferences
             notificationService.updateNotificationPreferences(
                 event.getUserId(),
                 event.getGoalId(),
                 event.getNotificationChannelType(),
                 event.getFrequency()
             );

             // 2. Schedule initial progress check
             goalService.scheduleProgressCheck(
                 event.getUserId(),
                 event.getGoalId(),
                 event.getCharacterName(),
                 event.getTargetAttribute()
             );
         }
     }
     ```

3. **Event Publishing**
   - Publishes events to EventBridge.
   - Handles event delivery and retry logic.
   - Ensures event ordering when required.
   - Example:
     ```java
     @Singleton
     public class EventBridgePublisher {
         private final EventBridge eventBridge;
         private final String eventBusName;

         public void publishEvent(Object event) {
             PutEventsRequest request = PutEventsRequest.builder()
                 .entries(PutEventsRequestEntry.builder()
                     .eventBusName(eventBusName)
                     .detail(objectMapper.writeValueAsString(event))
                     .detailType(event.getClass().getSimpleName())
                     .source("com.osrsGoalTracker")
                     .build())
                 .build();

             eventBridge.putEvents(request);
         }
     }
     ```

## Layer Definitions

### 1. Handler Layer (`*.handler`)
- Entry points for AWS Lambda functions
- Handles API Gateway events and responses
- Follows a strict 4-step pattern:
  1. Parse input
  2. Validate input
  3. Run service function
  4. Return output
- Only depends on the service layer
- Located in `*.handler` packages

### 2. Service Layer (`*.service`)
- Contains core business logic
- Defines interfaces and implementations
- Uses repository interfaces for data access
- Manages transactions and orchestration
- Located in `*.service` packages
- Implementations in `*.service.impl`

### 3. Repository Layer (`*.repository`)
- Abstracts data persistence operations
- Provides DynamoDB implementations
- Handles data mapping and queries
- Located in `*.repository` packages
- Implementations in `*.repository.impl`

### 4. Model Layer (`*.model`)
- Domain models using Lombok
- Request/Response DTOs
- Uses `@Value` and `@Builder` for immutability
- Located in `*.model` packages

### 5. External Layer (`*.external`)
- Handles third-party integrations (e.g., OSRS Hiscores)
- Abstracts external API calls
- Located in `*.external` packages
- Implementations in `*.external.impl`

### 6. Dependency Injection Layer (`*.di`)
- Google Guice modules for each domain
- Binds interfaces to implementations
- Located in `*.di` packages

## Domain Modules

### User Domain (`com.osrsGoalTracker.user`)
- User account management
- Authentication and authorization
- Profile management

### Hiscore Domain (`com.osrsGoalTracker.hiscore`)
- OSRS hiscores integration
- Skill and activity tracking
- Historical data management

## Layer Interaction Rules

1. **Handler Layer**
   - Can only call Service layer
   - Must follow 4-step pattern
   - Must handle API Gateway events

2. **Service Layer**
   - Can call Repository layer
   - Can call other Services
   - Can call External layer
   - Manages transactions

3. **Repository Layer**
   - Can only access data store
   - No service layer calls
   - No cross-repository calls

4. **External Layer**
   - Can only call external APIs
   - Must handle retries and failures
   - Must provide clean interfaces

## Package Structure Example (User Domain)

```
com.osrsGoalTracker.user/
├── di/
│   └── UserModule.java
├── handler/
│   ├── CreateUserHandler.java
│   ├── GetUserHandler.java
│   ├── request/
│   │   └── CreateUserRequest.java
│   └── response/
│       └── GetUserResponse.java
├── model/
│   └── User.java
├── repository/
│   ├── UserRepository.java
│   └── impl/
│       └── UserRepositoryImpl.java
└── service/
    ├── UserService.java
    └── impl/
        └── UserServiceImpl.java
```

## Development Guidelines

### 1. Handler Implementation
```java
public class CreateUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject

    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        CreateUserRequest request = parseInput(input);
        String userId = input.getPathParameters().get("userId");
        
        validateRequest(request);
        }
        // 3. Run service function
        User user = userService.createUser(request);
        User user = userService.getUser(userId);
        // 4. Return output
        // 4. Format response
        return ApiGatewayResponse.builder()
            .setStatusCode(200)
            .setObjectBody(user)
            .build();
    }
}
```
### 2. Model Implementation
```java
@Value
@Builder
public class User {
    String userId;
    String email;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```

### 3. Service Implementation
Example:
```java
@Singleton
    private final UserRepository userRepository;

public class UserServiceImpl implements UserService {
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    
    public User createUser(CreateUserRequest request) {
        // Business logic here
        return userRepository.save(user);
    }
}
```
### 4. Repository Implementation
Example:
```java
@Singleton
    private final DynamoDbClient dynamoDb;

public class UserRepositoryImpl implements UserRepository {
    public UserRepositoryImpl(DynamoDbClient dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    
    @Override
        // DynamoDB operations here
        return user;
    }
}
```
## Testing Strategy
Each layer should have comprehensive unit tests:

   - Test input parsing
   - Test validation
   - Mock service layer
   - Test input validation
   - Mock service layer

2. **Service Tests**
   - Test error conditions
   - Test error cases
   - Test transactions
   - Mock repositories

3. **Repository Tests**
   - Use DynamoDB Local
   - Test queries
   - Test data operations

4. **Integration Tests**
   - Test full flow
   - Use test containers
   - Test error scenarios

## Build and Deployment

Each Lambda handler is built into its own JAR file using Gradle tasks:

```bash
# Build individual handlers
./gradlew buildGetUserHandler

# Build all handlers
./gradlew buildAllHandlers
}
``` 

### Event-Driven Architecture

#### Goal Creation Flow
1. A goal creation event is published to EventBridge
2. The `CreateGoalFromGoalCreationEventHandler` Lambda is triggered
3. The handler:
   - Parses the `GoalCreationEvent`
   - Extracts the goal configuration and current progress
   - Validates the input
   - Creates the goal via the service layer
4. The goal is persisted with its initial progress value

Example Event:
```json
{
    "userId": "123",
    "goalId": "456",
    "characterName": "PlayerOne",
    "metricName": "WOODCUTTING",
    "targetType": "xp",
    "targetValue": 1000000,
    "currentValue": 500000,
    "targetDate": "2024-12-31T23:59:59Z",
    "notificationChannelType": "DISCORD",
    "frequency": "DAILY",
    "createdAt": "2024-01-12T00:00:00Z",
    "updatedAt": "2024-01-12T00:00:00Z"
}
``` 

# Notification Channel Service Architecture

## Overview

The Notification Channel Service follows a strict layered architecture pattern with well-defined boundaries and visibility rules. This document outlines the architectural principles, package structure, and visibility rules that govern the service.

## Package Visibility Rules

### Public APIs
Only the following components are intended for external use:

1. **Lambda Handlers**
   - `GetNotificationChannelsForUserHandler`
   - `CreateNotificationChannelForUserHandler`
   These are the entry points for AWS Lambda functions and are publicly accessible.

2. **Service Interface**
   - `NotificationChannelService`
   The service interface is public to allow for integration with other services.

### Internal Components
All other components are internal implementation details and are not meant for external use:

1. **Repository Layer**
   - Package-private implementation
   - Only accessible within the service package
   - Handles data persistence details

2. **Models**
   - Internal data structures
   - Used for data transfer between layers
   - Not exposed outside the service

3. **Utilities**
   - Package-private helper classes
   - Internal implementation details
   - Not accessible outside their respective packages

## Package Structure

```
com.osrsGoalTracker.notificationChannel/
├── handler/                  # Public Lambda handlers
│   ├── request/             # Internal request models
│   └── response/            # Internal response models
├── service/                 # Public service interface
│   └── impl/               # Package-private implementation
├── repository/             # Package-private repository interface
│   └── impl/              # Package-private implementation
├── model/                  # Internal models
└── di/                     # Package-private dependency injection
```

## Layer Responsibilities

### Handler Layer (Public)
- Entry point for AWS Lambda functions
- Request/response transformation
- Error handling and logging
- Delegates to service layer

### Service Layer (Public Interface, Package-Private Implementation)
- Business logic implementation
- Transaction management
- Input validation
- Orchestration of repository operations

### Repository Layer (Package-Private)
- Data persistence operations
- DynamoDB interactions
- Data mapping and transformation

### Models (Internal)
- Data structures for internal use
- Request/response objects
- Domain objects

## Dependency Rules

1. Handlers may only depend on:
   - Service interfaces
   - Request/response models

2. Services may only depend on:
   - Repository interfaces
   - Domain models

3. Repositories may only depend on:
   - Domain models
   - Data store SDKs

## Testing Strategy

1. **Public Components**
   - Handlers and service interfaces must have comprehensive public API tests
   - Focus on contract testing and integration scenarios

2. **Internal Components**
   - Package-private components are tested through their public interfaces
   - Implementation details are verified via internal tests

## Best Practices

1. **Visibility**
   - Use the minimum required visibility for all components
   - Make classes package-private by default
   - Only expose what is absolutely necessary

2. **Dependencies**
   - Depend on interfaces, not implementations
   - Use dependency injection for loose coupling
   - Keep implementation details hidden

3. **Testing**
   - Test through public interfaces
   - Use mocks for dependencies
   - Focus on behavior, not implementation

## Error Handling

1. **Public Layer**
   - Well-defined error responses
   - Proper HTTP status codes
   - Meaningful error messages

2. **Internal Layer**
   - Detailed logging
   - Exception translation
   - Proper error propagation

## Monitoring and Observability

1. **Public Endpoints**
   - Request/response metrics
   - Latency tracking
   - Error rates

2. **Internal Operations**
   - Detailed logging
   - Performance metrics
   - Resource utilization

## Security

1. **Public APIs**
   - Authentication
   - Authorization
   - Input validation

2. **Internal Components**
   - Secure configuration
   - Data encryption
   - Access control 