# User Management System

A robust User Management System built with Spring Boot and Spring Security, supporting user registration, authentication, role-based access control, account management, and two-factor authentication (2FA).

---

## Features

- **User Registration & Login** (with JWT authentication)
- **Role-based Access Control** (Admin, User)
- **Account Management** (lock/unlock, enable/disable, expire accounts)
- **Password Management** (reset, update, forgot password)
- **Two-Factor Authentication (2FA)**
- **Audit Logging**
- **CSRF Protection**
- **RESTful API Endpoints**
- **Spring Security Integration**
- **Email Notifications** (for password reset, etc.)

---

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA (Hibernate)
- H2/MySQL/PostgreSQL (configurable)
- JWT (JSON Web Tokens)
- Lombok
- Maven

---

## Architecture Overview

```mermaid
graph TD
    A["Client (Frontend)"] -->|REST API| B["Spring Boot Application"]
    B --> C["Controllers"]
    C --> D["Services"]
    D --> E["Repositories"]
    E --> F["Database"]
    B --> G["Security Layer (JWT, 2FA, RBAC)"]
    B --> H["Audit Logging"]
```

---

## Setup & Installation

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd User-Management-System
   ```

2. **Configure Database**
   - Edit `src/main/resources/application.properties` for your DB settings.

3. **Build and Run**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **API is available at:** `http://localhost:8080/api/`

---

## Configuration

- **application.properties**: Main configuration file for DB, JWT, email, etc.
- **Profiles**: Use `application-dev.properties` or `application-prod.properties` for environment-specific settings.

---

## API Endpoints

### Auth

| Method | Endpoint                | Description                |
|--------|-------------------------|----------------------------|
| POST   | `/api/auth/public/signin`  | User login (JWT)           |
| POST   | `/api/auth/public/signup`  | User registration          |
| GET    | `/api/auth/user`           | Get current user info      |
| GET    | `/api/auth/username`       | Get current username       |

### Admin

| Method | Endpoint                        | Description                        |
|--------|---------------------------------|------------------------------------|
| GET    | `/api/admin/users`              | List all users                     |
| PUT    | `/api/admin/update/role`        | Update user role                   |
| PUT    | `/api/admin/update/lock/status` | Lock/unlock user account           |
| GET    | `/api/admin/roles`              | List all roles                     |

### User Management

| Method | Endpoint                        | Description                        |
|--------|---------------------------------|------------------------------------|
| PUT    | `/api/admin/update/expiry/status` | Expire/unexpire user account     |
| PUT    | `/api/admin/update/enabled/status` | Enable/disable user account     |
| PUT    | `/api/admin/update/credentials/expiry/status` | Expire/unexpire credentials |
| PUT    | `/api/admin/update/password`     | Update user password               |

### 2FA

| Method | Endpoint                        | Description                        |
|--------|---------------------------------|------------------------------------|
| POST   | `/api/auth/enable-2fa`          | Enable 2FA for user                |
| POST   | `/api/auth/disable-2fa`         | Disable 2FA for user               |
| POST   | `/api/auth/verify-2fa`          | Verify 2FA code                    |

### Password Reset

| Method | Endpoint                        | Description                        |
|--------|---------------------------------|------------------------------------|
| POST   | `/api/auth/forgot-password`     | Request password reset             |
| POST   | `/api/auth/reset-password`      | Reset password with token          |

---

## Security

- **Spring Security**: All endpoints are secured by default.
- **JWT Authentication**: Stateless authentication for API endpoints.
- **Role-based Access**: Admin endpoints require `ROLE_ADMIN`.
- **2FA**: Optional for users, enforced via TOTP.
- **CSRF Protection**: Enabled for state-changing operations.
- **Password Hashing**: BCrypt algorithm.

---

## Contribution Guide

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Create a new Pull Request

---

## License

This project is licensed under the MIT License.

---

## Contact

For questions or support, please open an issue or contact the maintainer.
