# 📘 Blog API - Spring Boot RESTful Application

A secure, feature-rich blog backend built with **Java 17**, **Spring Boot 3**, and **JWT authentication**. Users can register, log in, and manage blog posts and comments with role-based access.

---

## 🚀 Features

- ✅ User Registration and Login with JWT
- ✅ Create, Read, Update, Delete Posts
- ✅ Create, Read, Update, Delete Comments
- ✅ Pagination & Search on Posts
- ✅ Role-Based Access (USER by default)
- ✅ SpringDoc Swagger UI integration
- ✅ Global Exception Handling & Validation

---

## 🛠️ Tech Stack

| Layer        | Technology                     |
|--------------|--------------------------------|
| Language     | Java 17                        |
| Framework    | Spring Boot 3.1.4              |
| Security     | Spring Security + JWT (jjwt)   |
| Database     | MySQL                          |
| Docs         | Swagger UI via SpringDoc       |
| Build Tool   | Maven                          |

---

## 📦 API Endpoints

### 🔐 Authentication

| Method | Endpoint       | Description          |
|--------|----------------|----------------------|
| POST   | `/auth/register` | Register a new user |
| POST   | `/auth/login`    | Login and get JWT   |

### 📝 Posts

| Method | Endpoint        | Description              |
|--------|------------------|--------------------------|
| GET    | `/posts`         | Get all posts (paged)    |
| GET    | `/posts/{id}`    | Get post by ID           |
| POST   | `/posts`         | Create post (auth)       |
| PUT    | `/posts/{id}`    | Update post (auth)       |
| DELETE | `/posts/{id}`    | Delete post (auth)       |
| GET    | `/posts/search`  | Search posts by keyword  |

### 💬 Comments

| Method | Endpoint             | Description                  |
|--------|-----------------------|------------------------------|
| GET    | `/comments?postId=x`  | Get comments for a post      |
| GET    | `/comments/{id}`      | Get comment by ID            |
| POST   | `/comments`           | Add a comment (auth)         |
| PUT    | `/comments/{id}`      | Update comment (auth)        |
| DELETE | `/comments/{id}`      | Delete comment (auth)        |

---

## 🔐 JWT Authentication

- All write operations (POST/PUT/DELETE) require a **JWT** token in the header:
```
Authorization: Bearer <your_token>
```

---

## 📘 API Documentation (Swagger)

- Swagger UI: [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
- API Docs JSON: [http://localhost:8080/api/api-docs](http://localhost:8080/api/api-docs)

---

## 🧑‍💻 Getting Started

### ✅ Prerequisites

- Java 17+
- Maven
- MySQL running locally

### 📂 Clone & Run

```bash
git clone https://github.com/your-username/blog-api.git
cd blog-api
```

### ⚙️ Configure Database

Edit `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/blogdb
spring.datasource.username=root
spring.datasource.password=root
```

### 🚀 Start Application

```bash
./mvnw spring-boot:run
```

---

## 🗃️ Sample Credentials

| Username | Password | Role  |
|----------|----------|-------|
| `admin`  | `admin`  | ADMIN |
| `user1`  | `user1`  | USER  |

---

## 📜 License

This project is part of the assignment `SCR845NGFF` and is licensed for academic use.

---

## ✉️ Contact

For queries, contact [contact@blogapi.com](mailto:contact@blogapi.com)