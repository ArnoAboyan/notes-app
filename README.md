# Notes App

RESTful API application for managing notes, built with Spring Boot and MongoDB.

## Description

Notes App is a test assignment that provides a REST API for creating, managing, and analyzing notes. The application allows users to store notes with titles, text, and tags, get word usage statistics in notes, and filter and view notes with pagination support.

## Technology Stack

- **Java** 21
- **Spring Boot** 3.5.11
- **MongoDB** 7
- **Maven** for dependency management
- **Docker** and **Docker Compose** for containerization
- **Lombok** for reducing boilerplate code
- **MapStruct** for object mapping
- **SpringDoc OpenAPI** for API documentation (Swagger UI)
- **Spring Data MongoDB** for database operations
- **Spring Validation** for data validation

## Features

- ✅ Create notes with title, text, and tags
- ✅ Update and delete notes
- ✅ Get paginated list of notes
- ✅ Filter notes by tags
- ✅ Get word frequency statistics for a note
- ✅ Validate required fields (title and text)
- ✅ Support for tags: `BUSINESS`, `PERSONAL`, `IMPORTANT`
- ✅ Automatic sorting of notes by creation date (newest first)

## Requirements

- **Docker** and **Docker Compose** to run the application

## Quick Start

### Running with Docker Compose

1. Clone the repository:
```bash
git clone <repository-url>
cd notes-app
```

2. Create a `.env` file in the project root (optional, for custom settings):
```env
MONGO_DATABASE=notes_db
MONGO_USERNAME=root
MONGO_PASSWORD=secret
```

3. Start the application:
```bash
docker-compose up -d
```

4. The application will be available at: `http://localhost:8080`
5. Swagger UI is available at: `http://localhost:8080/swagger-ui/index.html`
6. API documentation (JSON): `http://localhost:8080/api-docs`

**Note:** The application uses a pre-built Docker image `yanabo/notes-app:latest`. MongoDB starts automatically via Docker Compose.

## API Endpoints

### Create Note
```http
POST /api/notes
Content-Type: application/json

{
  "title": "My first note",
  "text": "This is my note text",
  "tags": ["PERSONAL", "IMPORTANT"]
}
```

**Response:** `201 Created` with full note data

### Get Notes List
```http
GET /api/notes?page=0&size=10&tags=PERSONAL,BUSINESS
```

**Parameters:**
- `page` (optional, default 0) - page number
- `size` (optional, default 10) - page size
- `tags` (optional) - filter by tags (comma-separated)

**Response:** `200 OK` with paginated list of notes (title and creation date only)

### Get Note by ID
```http
GET /api/notes/{id}
```

**Response:** `200 OK` with full note data

### Update Note (Full)
```http
PUT /api/notes/{id}
Content-Type: application/json

{
  "title": "Updated title",
  "text": "Updated text",
  "tags": ["BUSINESS"]
}
```

**Response:** `200 OK` with updated note data

### Partially Update Note
```http
PATCH /api/notes/{id}
Content-Type: application/json

{
  "title": "Only title updated"
}
```

**Response:** `200 OK` with updated note data

### Delete Note
```http
DELETE /api/notes/{id}
```

**Response:** `204 No Content`

### Get Word Statistics
```http
GET /api/notes/{id}/stats
```

**Response:** `200 OK` with word frequency map sorted in descending order:
```json
{
  "note": 2,
  "is": 1,
  "just": 1,
  "a": 1
}
```

## Project Structure

```
notes-app/
├── src/
│   ├── main/
│   │   ├── java/com/qoqtest/notes/
│   │   │   ├── config/          # Configuration (OpenAPI)
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entity/           # MongoDB entities
│   │   │   ├── handler/          # Exception handlers
│   │   │   ├── repository/       # MongoDB repositories
│   │   │   ├── service/          # Business logic
│   │   │   └── NotesAppApplication.java
│   │   └── resources/
│   │       └── application.yml   # Application configuration
│   └── test/                     # Tests
├── docker-compose.yaml           # Docker Compose configuration
├── Dockerfile                    # Docker image configuration
├── pom.xml                       # Maven configuration
└── README.md                     # This file
```

## Logging

Application logs are saved in the `logs/` directory:
- Current log: `logs/notes-app.log`
- Archived logs: `logs/archived/notes-app.{date}.{index}.log.gz`

Logging settings:
- Maximum file size: 10MB
- Retention period: 7 days
- Maximum total size: 1GB

## Configuration

Main settings are located in `src/main/resources/application.yml`:

- **MongoDB**: configured via environment variables or default values
- **Application port**: 8080
- **Swagger UI**: available at `swagger-ui/index.html`
- **API Docs**: available at `/api-docs`

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATA_MONGODB_HOST` | MongoDB host | `localhost` |
| `SPRING_DATA_MONGODB_PORT` | MongoDB port | `27017` |
| `SPRING_DATA_MONGODB_DATABASE` | Database name | `notes_db` |
| `SPRING_DATA_MONGODB_USERNAME` | MongoDB username | `root` |
| `SPRING_DATA_MONGODB_PASSWORD` | MongoDB password | `secret` |

## Validation

- **Title** (`title`) - required field, cannot be empty
- **Text** (`text`) - required field, cannot be empty
- **Tags** (`tags`) - optional field, allowed values: `BUSINESS`, `PERSONAL`, `IMPORTANT`

## Error Handling

The application uses a global exception handler (`GlobalExceptionHandler`) that returns structured error responses in the following format:

**Validation error example:**
```json
{
  "timestamp": "2026-02-20T10:30:00Z",
  "status": 400,
  "error": "Validation Error",
  "message": "One or more fields are invalid",
  "path": "/api/notes",
  "validationErrors": {
    "title": "must not be blank",
    "text": "must not be blank"
  }
}
```

**Not Found error example:**
```json
{
  "timestamp": "2026-02-20T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Note with id 123 not found",
  "path": "/api/notes/123"
}
```

**Internal Server Error example:**
```json
{
  "timestamp": "2026-02-20T10:30:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/notes"
}
```

## Viewing API Documentation

After starting the application via Docker Compose, open your browser and navigate to:
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/api-docs

## License

This project was created as part of a test assignment.

## Author

Developed as a test assignment to demonstrate skills in Spring Boot and MongoDB.
