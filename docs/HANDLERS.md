# Notification Channel Service Lambda Handlers

## Public Lambda Handlers

The service exposes two public Lambda handlers for managing notification channels:

### 1. GetNotificationChannelsForUserHandler

Retrieves all notification channels for a user.

**Handler Class**: `GetNotificationChannelsForUserHandler`

**Request Format**:
```json
{
    "pathParameters": {
        "userId": "string"
    }
}
```

**Success Response** (HTTP 200):
```json
{
    "notificationChannels": [
        {
            "userId": "string",
            "channelType": "string",
            "identifier": "string",
            "isActive": boolean,
            "createdAt": "ISO-8601 timestamp",
            "updatedAt": "ISO-8601 timestamp"
        }
    ]
}
```

**Error Response** (HTTP 400):
```json
{
    "message": "Error message"
}
```

### 2. CreateNotificationChannelForUserHandler

Creates a new notification channel for a user.

**Handler Class**: `CreateNotificationChannelForUserHandler`

**Request Format**:
```json
{
    "pathParameters": {
        "userId": "string"
    },
    "body": {
        "channelType": "string",
        "identifier": "string"
    }
}
```

**Success Response** (HTTP 200):
```json
{
    "userId": "string",
    "channelType": "string",
    "identifier": "string",
    "isActive": true
}
```

**Error Response** (HTTP 400):
```json
{
    "message": "Error message"
}
```

## Usage Guidelines

1. **Error Handling**
   - Always check HTTP status code
   - Handle error responses appropriately
   - Log error messages for debugging

2. **Request Validation**
   - Ensure required fields are present
   - Validate field formats
   - Check field length limits

3. **Response Processing**
   - Parse response JSON carefully
   - Handle missing fields gracefully
   - Validate response data

## Examples

### Getting User's Channels

```bash
curl -X GET \
  'https://api.example.com/users/{userId}/notification-channels' \
  -H 'Authorization: Bearer YOUR_TOKEN'
```

### Creating a Channel

```bash
curl -X POST \
  'https://api.example.com/users/{userId}/notification-channels' \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "channelType": "DISCORD",
    "identifier": "123456789"
}'
```

## Error Codes

1. **Invalid Request**
   - Missing required fields
   - Invalid field formats
   - Unsupported channel types

2. **Resource Errors**
   - User not found
   - Channel already exists
   - System errors

## Security

1. **Authentication**
   - Bearer token required
   - Token must be valid
   - Token must have appropriate scope

2. **Authorization**
   - Users can only access their own channels
   - Admin scope required for certain operations
   - All operations are logged

## Performance

1. **Timeouts**
   - Lambda timeout: 30 seconds
   - Recommended client timeout: 10 seconds
   - Retry on 5xx errors

2. **Rate Limits**
   - 1000 requests per minute
   - 429 response when exceeded
   - Implement exponential backoff

## Monitoring

1. **CloudWatch Metrics**
   - Invocation count
   - Error rate
   - Latency

2. **Logging**
   - Request/response pairs
   - Error details
   - Performance data

## Testing

1. **Integration Tests**
```bash
# Get channels
curl -X GET \
  'https://api-test.example.com/users/testUser/notification-channels' \
  -H 'Authorization: Bearer TEST_TOKEN'

# Create channel
curl -X POST \
  'https://api-test.example.com/users/testUser/notification-channels' \
  -H 'Authorization: Bearer TEST_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "channelType": "DISCORD",
    "identifier": "123456789"
}'
```

2. **Error Cases**
```bash
# Missing userId
curl -X GET \
  'https://api-test.example.com/users//notification-channels' \
  -H 'Authorization: Bearer TEST_TOKEN'

# Invalid channel type
curl -X POST \
  'https://api-test.example.com/users/testUser/notification-channels' \
  -H 'Authorization: Bearer TEST_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "channelType": "INVALID",
    "identifier": "123456789"
}'