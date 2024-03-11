# Social Network

This is a social networking application developed in Java, JavaFX, and PostgreSQL. It provides functionalities for user management, messaging, friendship management, and more.

## Features
### User Management
- Add, remove, update, and display user details.
- Manage user authentication and authorization.

### Messaging
- Send, receive, and delete messages between users.
- View message history and conversations.

### Friendship Management
- Send and accept friend requests.
- View and manage friends and friend requests.

### Community Management
- Determine communities within the network based on friendship connections.
- Identify the most sociable community.

### User Interface
- Provides a user-friendly interface for interacting with the application.
- Supports operations like adding users, removing users, adding friendships, and more.

## Entity Model
1. **User**: Represents a user of the social network.
2. **Message**: Represents a message sent between users.
3. **Friendship**: Represents a connection between two users.
4. **FriendRequest**: Represents a friend request between two users.

## Repository Structure
- **com.example.laboratorjavafx.controller: Contains JavaFX controllers for user interface interaction.
- **com.example.laboratorjavafx.domain: Defines domain entities like User, Message, etc.
- **com.example.laboratorjavafx.repository: Contains repository interfaces and implementations for data access.
- **com.example.laboratorjavafx.service: Contains service classes for business logic implementation.
- **com.example.laboratorjavafx.repository.database: Provides database-specific implementations of repositories.
- **com.example.laboratorjavafx.Paging: Contains classes related to pagination.
- **com.example.laboratorjavafx.ui**: Provides the user interface for interaction with the application.

## Running the Application
1. Clone the repository to your local machine.
2. Set up the required database schema and tables in PostgreSQL.
3. Ensure you have Java, JavaFX, and PostgreSQL installed on your system.
4. Build and run the application using your preferred Java IDE.

## Usage
1. Launch the application.
2. Use the provided menu options to perform various actions such as adding users, managing friendships, sending messages, etc.
3. Follow the prompts in the user interface to execute desired functionalities.

## Screenshots
![SingIn](https://github.com/caba12345/Retea-De-Socializare/assets/131769398/6bdef04d-7877-4b34-b577-9b39598ea0e0)
![SignUp](https://github.com/caba12345/Retea-De-Socializare/assets/131769398/9b10ddcb-c86f-4071-bd35-96dec8e4ec94)
![Friends](https://github.com/caba12345/Retea-De-Socializare/assets/131769398/5c5c444c-0ccb-43b1-805a-79fed8046c75)
![Conversation](https://github.com/caba12345/Retea-De-Socializare/assets/131769398/785b6ec3-daae-4c83-be98-b1548a191f1c)


