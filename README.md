🚀 Smart Campus Sensor & Room Management API

A RESTful Smart Campus API built using JAX-RS (Jersey) and Maven, designed for the 5COSC022W Client-Server Architectures coursework.

⸻

🔗 Repository

GitHub:
https://github.com/batuzihni/smart-campus-jaxrs-api

⸻

🎥 Video Demonstration

YouTube Link:
👉 (Add your video link here)

⸻

🧪 API Testing (Postman)

(Optional: Add screenshot if needed)
images/postman.png

⸻

💾 Data Storage

This project uses an in-memory data store implemented with:

* ConcurrentHashMap
* Thread-safe collections

✔ No database required
✔ Safe concurrent access
✔ Fast and lightweight

⸻

📌 API Overview

This API manages:

* 🏫 Rooms – Physical campus spaces
* 📡 Sensors – Devices inside rooms
* 📊 Sensor Readings – Historical measurements

⸻

🌐 Base Path

/api/v1

⸻

📂 Core Endpoints

/api/v1/rooms
/api/v1/sensors
/api/v1/sensors/{sensorId}/readings

⸻

🏗️ Project Structure
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

⸻

📊 Data Models

Room

public class Room {
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds;
}

Sensor

public class Sensor {
    private String id;
    private String type;
    private String status;
    private double currentValue;
    private String roomId;
}

SensorReading

public class SensorReading {
    private String id;
    private long timestamp;
    private double value;
}

⸻

⚙️ Build & Run

Requirements

* Java 8+
* Maven 3.8+
* Apache Tomcat 9

⸻

Build

mvn clean package

⸻

Run (Tomcat)

Deploy WAR file to Tomcat:

http://localhost:8080/smart-campus/api/v1

⸻

🧠 API Design

🔍 Discovery Endpoint

GET /api/v1

Returns API metadata and navigation links.

⸻

🏫 Rooms

* GET /rooms
* POST /rooms → 201 Created
* GET /rooms/{id}
* DELETE /rooms/{id} → 204 / 409

⸻

📡 Sensors

* GET /sensors
* GET /sensors?type=CO2
* POST /sensors

✔ Validates roomId
✔ Updates parent room automatically

⸻

📊 Sensor Readings (Sub-resource)

/sensors/{sensorId}/readings

* GET
* POST

✔ Updates sensor currentValue
✔ Enforces business rules

⸻

⚠️ Error Handling

All errors return structured JSON:

{
  "timestamp": 1713600000000,
  "status": 409,
  "error": "Conflict",
  "message": "Room cannot be deleted",
  "path": "/api/v1/rooms/LIB-301"
}

⸻

Custom Exceptions

Exception	Status
RoomNotEmptyException	409
LinkedResourceNotFoundException	422
SensorUnavailableException	403
ResourceNotFoundException	404
GlobalExceptionMapper	500

⸻

📈 Logging / Observability

Custom JAX-RS filter logs:

* HTTP Method
* Request URI
* Response Status

Example:

Incoming: POST /sensors
Outgoing: 201

⸻

🔧 Sample cURL Commands

Create Room

curl -X POST http://localhost:8080/smart-campus/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"LIB-301","name":"Library","capacity":40}'

Get Rooms

curl http://localhost:8080/smart-campus/api/v1/rooms

Create Sensor

curl -X POST http://localhost:8080/smart-campus/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":400,"roomId":"LIB-301"}'

⸻

🧠 Conceptual Answers

JAX-RS Lifecycle

Resources are request-scoped, reducing shared-state issues.

⸻

HATEOAS

Improves API discoverability and reduces client coupling.

⸻

DELETE Idempotency

DELETE is idempotent because repeated calls do not change final state.

⸻

Query Parameters

Better for filtering:

/sensors?type=CO2

⸻

Sub-Resource Locator

Improves modularity and scalability.

⸻

HTTP 422 vs 404

422 = semantic error in payload
404 = endpoint not found

⸻

Security

Stack traces are hidden to prevent information leakage.

⸻

Filters vs Logging

Filters centralize cross-cutting concerns like logging.

⸻

🏆 Distinction Checklist

✔ RESTful design
✔ Correct HTTP status codes
✔ Sub-resource implementation
✔ Validation & business rules
✔ Structured error handling
✔ Logging filter
✔ Clean architecture
✔ Strong theoretical justification

⸻

🎬 Suggested Demo Flow

1. Discovery endpoint
2. Create room
3. Create sensor
4. Validation error (422)
5. Filter sensors
6. Add reading
7. Show updated value
8. Trigger 403
9. Trigger 409
10. Show logs

⸻

📌 Final Notes

This project demonstrates:

* REST API design principles
* Clean architecture
* Error handling best practices
* Real-world backend patterns

⸻
