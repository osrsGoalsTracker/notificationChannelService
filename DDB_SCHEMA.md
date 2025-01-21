
# DynamoDB Schema for Goal Tracking and Notification System

## Overview

This schema is designed to support a goal tracking and notification platform for RuneScape characters. The system allows users to:
- Create and manage RuneScape-related goals.
- Track progress towards goals over time.
- Configure notification channels for goal updates.
- Support efficient queries for metadata, progress tracking, and notifications.

### Key Features
1. **Flexible Data Organization**: Supports storing metadata, goals, progress, and notifications using structured keys.
2. **Efficient Access Patterns**: Enables querying for user-specific data, goals, and progress.
3. **Scalable Design**: Accommodates daily progress updates for multiple goals while adhering to DynamoDB’s scalability limits.
4. **Low Maintenance**: Designed with minimal data duplication and optional TTL for data retention.

---

## DynamoDB Schema

### Primary Table: Goals Table
- **Partition Key (PK):** `USER#<user_id>`
- **Sort Key (SK):** Encodes various entity types and their metadata/data using structured prefixes.

---

### Sort Key Structure and Examples

#### 2. **Notification Channels**
   - **Sort Key:** `NOTIFICATION#<channel_type>`
   - **Purpose:** This is the metadata for the notification channels for the user. It is used to store information about the notification channels for the user such as the channel type (e.g. SMS, Discord, etc.), identifier (e.g. phone number, discord channel id, etc.), createdAt, and isActive.
   - **Example Item:**
     ```json
     {
       "PK": "USER#12345",
       "SK": "NOTIFICATION#SMS",
       "channelType": "SMS",
       "identifier": "+1234567890",
       "createdAt": "2025-01-01T00:00:00Z",
       "updatedAt": "2025-01-01T00:00:00Z",
       "isActive": true
     }
     ```

---

### Indexes

#### Primary Index
- **PK:** `USER#<user_id>`
- **SK:** Encodes metadata, notification channels, goals, and progress.

#### Secondary Index
- **PK:** `email`
- **SK:** `METADATA`
- **Purpose:** This is the secondary index for the user. It is used to quickly query for a user by their email.
