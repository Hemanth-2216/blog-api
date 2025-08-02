
# 📝 Blog API - RESTful Backend for Blog Management

This is a **Spring Boot-based RESTful Blog Application API** built as part of a backend development project. The application enables users to **register/login**, **create/manage blog posts**, and **comment** on posts, with proper **authentication and authorization** using JWT.

---

## 📌 Project ID

`SCR845NGFF`

---

## 📆 Project Duration

**25 Days**

---

## 🧠 Objective

To develop a RESTful API that provides full CRUD operations for **posts** and **comments**, integrates secure **user authentication**, and allows only authorized actions based on user roles.

---

## 🚀 Features

- User Registration and Login (JWT Authentication)
- Role-based Access Control (Admin, User)
- Create, Read, Update, Delete (CRUD) for Posts
- Add, View, Update, Delete Comments on Posts
- Pagination and Sorting
- Global Exception Handling
- Swagger API Documentation
- Unit Testing using JUnit and Mockito

---

## 🧰 Tech Stack

| Category        | Technology       |
|----------------|------------------|
| Language        | Java             |
| Framework       | Spring Boot      |
| Security        | Spring Security + JWT |
| Documentation   | Swagger (springdoc-openapi) |
| Database        | MySQL            |
| ORM             | Spring Data JPA  |
| Testing         | JUnit, Mockito   |
| Build Tool      | Maven            |

---

## 📁 Folder Structure

```
src/
└── main/
    └── java/
        └── com.blog.application/
            ├── controller
            ├── service
            ├── dto
            ├── entity
            ├── repository
            ├── security
            └── exception
    └── resources/
        ├── application.properties
        └── data.sql (optional)
```

---

## 🧪 Testing

- `@WebMvcTest` for Controller Layer
- `@SpringBootTest` for Integration Tests
- Mocked services using Mockito

---

## 🗃️ API Endpoints

### 🔐 Authentication
| Method | Endpoint          | Description             |
|--------|-------------------|-------------------------|
| POST   | `/api/auth/register` | Register a new user     |
| POST   | `/api/auth/login`    | Authenticate users and provide tokens |

### 📝 Posts
| Method | Endpoint          | Description          |
|--------|-------------------|----------------------|
| POST   | `/api/posts`         | Create a new post    |
| GET    | `/api/posts`         | Get all posts        |
| GET    | `/api/posts/{id}`    | Get post by ID       |
| PUT    | `/api/posts/{id}`    | Update post by ID    |
| DELETE | `/api/posts/{id}`    | Delete post by ID    |

### 💬 Comments
| Method | Endpoint              | Description               |
|--------|-----------------------|---------------------------|
| POST   | `/api/comments`         | Add a comment              |
| GET    | `/api/comments`         | Get comments by post ID    |
| GET    | `/api/comments/{id}`    | Get a comment by ID        |
| PUT    | `/api/comments/{id}`    | Update comment             |
| DELETE | `/api/comments/{id}`    | Delete comment             |

---

## 🔒 Authentication & Authorization

- JWT Token is issued upon successful login
- Use the token in the `Authorization` header for secured endpoints:

```
Authorization: Bearer <JWT_TOKEN>
```

- Role-based access is enforced:
  - Only **authenticated users** can create, edit, delete posts/comments
  - Admin privileges (optional) can extend capabilities

---

## 📜 Swagger Documentation

- Swagger UI is available at:

```
http://localhost:8080/api/swagger-ui/index.html
```

- It provides:
  - Interactive API exploration
  - Request/response format samples
  - Error response details

---

## 🛠️ Setup Instructions

### ✅ Prerequisites

- Java 17+
- Maven
- MySQL Server

### ⚙️ Configure Database

In `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/blogdb
spring.datasource.username=root
spring.datasource.password=your_password
```

Make sure your MySQL DB `blogdb` is created manually or add:

```properties
spring.jpa.hibernate.ddl-auto=update
```

### ▶️ Run the Project

```bash
mvn spring-boot:run
```

---

## 🧪 Running Tests

```bash
mvn test
```

Test coverage includes:
- User registration/login
- Post CRUD
- Comment handling
- Error scenarios

---

## 🗂️ Deliverables

- ✅ RESTful API for blog posts and comments
- ✅ Role-based JWT authentication
- ✅ Database schema via JPA
- ✅ Swagger API documentation
- ✅ Unit and integration tests
- ✅ GitHub repository with final code

---

## 📎 GitHub Repo

🔗 [Blog API GitHub Repository](https://github.com/Hemanth-2216/blog-api.git)

---

## 🙌 Author

**Uppala Hemanth Kumar**

---

## 📄 License

This project is licensed under the MIT License.
