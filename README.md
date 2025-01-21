# Notification Channel Service

A Java service for managing notification channels for OSRS Goal Tracker users.

## Overview

This service provides AWS Lambda functions for managing notification channels (e.g., Discord, Email) for OSRS Goal Tracker users. It follows a strict layered architecture with well-defined public APIs and internal implementation details.

## Public API

The service exposes two main components:

1. **Lambda Handlers** - AWS Lambda entry points:
   - `GetNotificationChannelsForUserHandler` - Retrieves notification channels for a user
   - `CreateNotificationChannelForUserHandler` - Creates a new notification channel for a user

2. **Service Interface**:
   - `NotificationChannelService` - Interface for notification channel operations

All other components are internal implementation details and not meant for external use. See [Architecture](docs/ARCHITECTURE.md) for details on package visibility rules.

## Documentation

- [Architecture](docs/ARCHITECTURE.md) - Detailed architecture, package visibility rules, and best practices
- [Handlers](docs/HANDLERS.md) - Lambda function specifications and usage
- [Services](docs/SERVICES.md) - Service interface documentation
- [Models](docs/MODELS.md) - Data model documentation

## Requirements

- JDK 21
- Gradle 8.x

## Quick Start

1. Install dependencies:
```bash
./gradlew build
```

2. Run tests:
```bash
./gradlew test
```

3. Build Lambda handlers:
```bash
./gradlew buildAllHandlers
```

Each handler will be built into its own JAR file in `build/libs/`.

## Dependencies

- AWS Lambda Core - Lambda function support
- AWS Lambda Events - Event handling
- AWS DynamoDB - Database operations
- Google Guice - Dependency injection
- Jackson - JSON serialization
- Log4j2 - Logging
- Lombok - Boilerplate reduction
- JUnit 5 - Testing
- Mockito - Mocking for tests

## Infrastructure

The service is deployed using AWS CDK with the following components:

- API Gateway for REST endpoints
- Lambda functions for business logic
- DynamoDB tables for data storage

## Best Practices

1. **Package Visibility**
   - Only use public APIs (handlers and service interface)
   - Do not depend on internal implementation details
   - Treat all other components as implementation details

2. **Error Handling**
   - Handle errors through the public API
   - Use proper HTTP status codes
   - Provide meaningful error messages

3. **Testing**
   - Test through public interfaces
   - Do not test internal implementation details
   - Use mocks for external dependencies 
