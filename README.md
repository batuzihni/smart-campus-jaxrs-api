# 🚀 Smart Campus Sensor & Room Management API

A RESTful Smart Campus API built using **JAX-RS (Jersey)** and **Maven**, developed for the **5COSC022W Client-Server Architectures coursework**.

---

## 🔗 Repository

GitHub:  
https://github.com/batuzihni/smart-campus-jaxrs-api

---

## 🎥 Video Demonstration

YouTube Link:  
👉 https://youtu.be/yKF4yABHng8

---

## 💾 Data Storage

This project uses an **in-memory data store** implemented with thread-safe collections such as `ConcurrentHashMap`.

### Key characteristics
- No external database required
- Safe concurrent access
- Lightweight and simple for coursework deployment

---

## 📌 API Overview

This API manages three core resource types:

- 🏫 **Rooms** — physical campus spaces
- 📡 **Sensors** — smart devices installed inside rooms
- 📊 **Sensor Readings** — historical measurements recorded by sensors

---

## 🌐 Base Path

```text
/api/v1
```

---

## 📁 Core Endpoints

```text
/api/v1/rooms
/api/v1/sensors
/api/v1/sensors/{sensorId}/readings
```

---

## 📂 Project Structure

```text
smart-campus-api/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/batuhan/smartcampus/
│       │       ├── config/
│       │       ├── model/
│       │       ├── resource/
│       │       ├── exception/
│       │       ├── store/
│       │       └── filter/
│       └── webapp/
│           └── WEB-INF/
│               └── web.xml
└── README.md
```

---

## 📊 Data Models

### Room

```java
public class Room {
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds;
}
```

### Sensor

```java
public class Sensor {
    private String id;
    private String type;
    private String status;
    private double currentValue;
    private String roomId;
}
```

### SensorReading

```java
public class SensorReading {
    private String id;
    private long timestamp;
    private double value;
}
```

---

## ⚙️ Build & Run

### Requirements

- Java 8+
- Maven 3.8+
- Apache Tomcat 9
- Postman or `curl` for testing

### Build

```bash
mvn clean package
```

### Run on Tomcat

Deploy the generated WAR file to Apache Tomcat, then access:

```text
http://localhost:8080/smart-campus/api/v1
```

---

## 🧠 API Design

### 🔍 Discovery Endpoint

```http
GET /api/v1
```

Returns API metadata and navigational links.

---

### 🏫 Rooms

#### Endpoints

- `GET /api/v1/rooms`
- `POST /api/v1/rooms`
- `GET /api/v1/rooms/{id}`
- `DELETE /api/v1/rooms/{id}`

#### Behaviour

- `POST /rooms` returns **201 Created**
- `DELETE /rooms/{id}` returns:
  - **204 No Content** on success
  - **409 Conflict** if the room still has assigned sensors

---

### 📡 Sensors

#### Endpoints

- `GET /api/v1/sensors`
- `GET /api/v1/sensors?type=CO2`
- `POST /api/v1/sensors`

#### Behaviour

- Validates that `roomId` exists before creating a sensor
- Adds the created sensor to the parent room automatically
- Supports filtering by sensor type via query parameter

---

### 📊 Sensor Readings (Sub-resource)

```text
/api/v1/sensors/{sensorId}/readings
```

#### Endpoints

- `GET /api/v1/sensors/{sensorId}/readings`
- `POST /api/v1/sensors/{sensorId}/readings`

#### Behaviour

- Stores historical readings for a given sensor
- Updates the parent sensor’s `currentValue` after a successful POST
- Enforces business rules such as maintenance-mode restrictions

---

## ⚠️ Error Handling

All errors are returned as structured JSON rather than raw stack traces or HTML error pages.

### Example Error Response

```json
{
  "timestamp": 1713600000000,
  "status": 409,
  "error": "Conflict",
  "message": "Room cannot be deleted because it still has assigned sensors.",
  "path": "/api/v1/rooms/LIB-301"
}
```

### Custom Exceptions

| Exception | HTTP Status |
|---|---:|
| `RoomNotEmptyException` | 409 |
| `LinkedResourceNotFoundException` | 422 |
| `SensorUnavailableException` | 403 |
| `ResourceNotFoundException` | 404 |
| `GlobalExceptionMapper` | 500 |

---

## 📈 Logging / Observability

A custom JAX-RS filter is used to log:

- HTTP method
- Request URI
- Final response status code

### Example Log Output

```text
--- Incoming Request ---
Method: POST
URI: http://localhost:8080/smart-campus/api/v1/sensors

--- Outgoing Response ---
Status: 201
```

---

## 🔧 Sample cURL Commands

### Create a Room

```bash
curl -X POST http://localhost:8080/smart-campus/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id":"LIB-301",
    "name":"Library Quiet Study",
    "capacity":40
  }'
```

### Get All Rooms

```bash
curl http://localhost:8080/smart-campus/api/v1/rooms
```

### Create a Sensor

```bash
curl -X POST http://localhost:8080/smart-campus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id":"CO2-001",
    "type":"CO2",
    "status":"ACTIVE",
    "currentValue":400,
    "roomId":"LIB-301"
  }'
```

### Filter Sensors by Type

```bash
curl "http://localhost:8080/smart-campus/api/v1/sensors?type=CO2"
```

### Add a Reading

```bash
curl -X POST http://localhost:8080/smart-campus/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{
    "id":"reading-001",
    "timestamp":1713600000000,
    "value":431.8
  }'
```

---

## 🧠 Conceptual Answers

### JAX-RS Lifecycle

JAX-RS resources are typically request-scoped, meaning a new resource instance is created per request. This reduces accidental shared-state problems, but shared in-memory collections must still be managed carefully to avoid race conditions.

### HATEOAS

Hypermedia improves discoverability by allowing the API to expose available links and next actions directly in responses, reducing client-side hardcoding and tight coupling.

### DELETE Idempotency

DELETE is idempotent because sending the same DELETE request multiple times should leave the server in the same final state, even if later responses differ.

### Query Parameters for Filtering

Using query parameters such as:

```text
/sensors?type=CO2
```

is preferable because it keeps the URI focused on the same collection while applying filtering criteria cleanly.

### Sub-Resource Locator Pattern

Using a dedicated sub-resource for readings improves modularity, readability, and maintainability by separating sensor logic from sensor-reading logic.

### HTTP 422 vs 404

- **422 Unprocessable Entity** is appropriate when the request body is valid JSON but semantically invalid
- **404 Not Found** is appropriate when the requested resource URI itself does not exist

### Security

Internal Java stack traces should never be exposed to clients because they reveal implementation details that may help attackers.

### Filters vs Manual Logging

Filters are better than manually placing log statements inside each method because they centralise cross-cutting concerns and keep resource methods focused on business logic.

---

## 🏆 Distinction Checklist

- RESTful design principles applied
- Correct and meaningful HTTP status codes
- Sub-resource implementation for readings
- Business rule validation
- Structured JSON error handling
- Logging filter for observability
- Clean package organisation
- Theoretical justification included in README
- Demonstration includes both successful and failure scenarios

---

## 🎬 Suggested Demo Flow

1. Show the discovery endpoint
2. Create a room
3. Retrieve the room
4. Create a valid sensor
5. Trigger a validation error with an invalid `roomId`
6. Filter sensors by type
7. Add a reading successfully
8. Show reading history
9. Show updated `currentValue`
10. Trigger a `403 Forbidden` with a maintenance sensor
11. Trigger a `409 Conflict` by deleting an occupied room
12. Show request/response logs

---

## 📌 Final Notes

This project demonstrates:

- REST API design principles
- Resource-oriented architecture
- Nested resource design
- Business rule enforcement
- Structured exception mapping
- Logging and observability
- Clean separation of concerns

It was developed as a practical implementation of RESTful service design using JAX-RS and Maven for the Client-Server Architectures module.
