# User Management Microservice Documentation

## Overview

This User Management Microservice allows you to manage user data through CRUD (Create, Read, Update, Delete) operations. The service is built using Java Spring Boot and connects to an Oracle Cloud Database.

## Technologies Used

- **Java**: Programming language for building the microservice.
- **Spring Boot**: Framework for creating stand-alone, production-grade Spring applications.
- **Oracle Cloud Database**: Managed database service for storing user data.
- **Maven**: Build automation tool used for dependency management.

## Features

- **Create User**: Add a new user to the database.
- **Read User**: Retrieve user details by ID or fetch all users.
- **Update User**: Modify existing user information.
- **Delete User**: Remove a user from the database.

## API Endpoints

### 1. Create User

- **Endpoint**: `POST /api/users`

  #### Request Body (Default)

  ```json
  {
    "username": "new_user",
    "email": "new_user@example.com",
    "password": "new_user_pass_123"
  }
  ```

  #### Request Body (With Role)

  ```json
  {
    "username": "new_user",
    "email": "new_user@example.com",
    "password": "new_user_pass_123",
    "role": "ADMIN"
  }
  ```

- **Response**:
  - **201 Created**: Returns the created user object.
  - **400 Bad Request**: If the request body is invalid.

### 2. Read User

- **Endpoint**: `GET /api/users/{id}`
- **Response**:

  - **200 OK**: Returns the user object.
  - **404 Not Found**: If the user does not exist.

- **Endpoint**: `GET /api/users`
- **Response**:
  - **200 OK**: Returns a list of all users.

### 3. Update User

- **Endpoint**: `PUT /api/users/{id}`

  #### Request Body (Default)

  ```json
  {
    "username": "updated_user",
    "email": "updated_user@example.com",
    "password": "updated_user_pass_123"
  }
  ```

  #### Request Body (With Role)

  ```json
  {
    "username": "updated_user",
    "email": "updated_user@example.com",
    "password": "updated_user_pass_123",
    "role": "ADMIN"
  }
  ```

- **Response**:
  - **200 OK**: Returns the updated user object.
  - **404 Not Found**: If the user does not exist.
  - **400 Bad Request**: If the request body is invalid.

### 4. Delete User

- **Endpoint**: `DELETE /api/users/{id}`
- **Response**:
  - **204 No Content**: If the user is successfully deleted.
  - **404 Not Found**: If the user does not exist.

## Database Schema

### User Table

| Column   | Type          | Description                     |
| -------- | ------------- | ------------------------------- |
| id       | NUMBER(10)    | Unique identifier (Primary Key) |
| username | VARCHAR2(50)  | User's username                 |
| email    | VARCHAR2(100) | User's email (Unique)           |
| password | VARCHAR2(100) | User's password                 |
| role     | VARCHAR2(50)  | User's role                     |

## Configuration

### Application Properties

In the `.env` file, configure:

```properties
DBNAME=<DBNAME>
WALLET_PATH=<WALLET_PATH>
DATASOURCE_USERNAME=<USERNAME>
DATASOURCE_PASSWORD=<PASSWORD>
```

## Running the Application

1. Build the application:

   ```bash
   mvn clean install
   ```

2. Run the application:

   ```bash
   mvn spring-boot:run
   ```

3. Access the API at `http://localhost:8080/api/users`.

## Testing

You can use tools like Postman or curl to test the API endpoints. Ensure that your Oracle Cloud Database is running and accessible.

## Conclusion

This User Management Microservice provides a simple and effective way to handle user-related operations. Modify and expand upon this service as necessary to suit your application's needs. For any issues or contributions, please refer to the repository's issue tracker.
