# MAI Services

A Spring Boot application for user management with JWT authentication, role-based access control, and permission management.

## Features

- User authentication with JWT (JSON Web Tokens)
- Role-based access control
- Permission-based authorization
- User management (CRUD operations)
- Role management
- Permission management
- Token management (access and refresh tokens)
- PostgreSQL database integration

## Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- PostgreSQL 14 or higher

## Database Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE "mai-servicedb";
```

2. Configure the database connection in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mai-servicedb
    username: postgres
    password: 12345678
    driver-class-name: org.postgresql.Driver
```

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

The application will start on port 8080 with context path `/api`.

## Default Credentials

The application initializes with a default admin user:

- Username: `admin`
- Password: `admin123`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT tokens
- `POST /api/auth/refresh-token` - Refresh access token
- `POST /api/auth/logout` - Logout and invalidate tokens

### Users

- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID (Admin or self)
- `GET /api/users/username/{username}` - Get user by username (Admin or self)
- `PUT /api/users/{id}` - Update user (Admin or self)
- `DELETE /api/users/{id}` - Delete user (Admin only)
- `POST /api/users/{userId}/roles/{roleId}` - Assign role to user (Admin only)
- `DELETE /api/users/{userId}/roles/{roleId}` - Remove role from user (Admin only)
- `PUT /api/users/{userId}/roles` - Update user roles (Admin only)

### Roles

- `GET /api/roles` - Get all roles (Admin only)
- `GET /api/roles/{id}` - Get role by ID (Admin only)
- `GET /api/roles/name/{name}` - Get role by name (Admin only)
- `POST /api/roles` - Create a new role (Admin only)
- `PUT /api/roles/{id}` - Update role (Admin only)
- `DELETE /api/roles/{id}` - Delete role (Admin only)
- `POST /api/roles/{roleId}/permissions/{permissionId}` - Assign permission to role (Admin only)
- `DELETE /api/roles/{roleId}/permissions/{permissionId}` - Remove permission from role (Admin only)
- `PUT /api/roles/{roleId}/permissions` - Update role permissions (Admin only)

### Permissions

- `GET /api/permissions` - Get all permissions (Admin only)
- `GET /api/permissions/{id}` - Get permission by ID (Admin only)
- `GET /api/permissions/name/{name}` - Get permission by name (Admin only)
- `POST /api/permissions` - Create a new permission (Admin only)
- `PUT /api/permissions/{id}` - Update permission (Admin only)
- `DELETE /api/permissions/{id}` - Delete permission (Admin only)

## Security

The application uses Spring Security with JWT for authentication and authorization. Access tokens are valid for 24 hours, and refresh tokens are valid for 7 days.

## License

This project is licensed under the MIT License.
