## Repository

GitHub: https://github.com/yourusername/smart-campus-api

## Video Demonstration

YouTube Link:
https://youtube.com/your-video-link

## API Testing (Postman)

![Postman Test](images/postman.png)

## Data Storage

This project uses an in-memory data store implemented using thread-safe collections (e.g., ConcurrentHashMap). This ensures safe concurrent access without a database.

Smart Campus Sensor & Room Management API

API Overview

This project implements a RESTful Smart Campus API using JAX-RS (Jersey) and Maven. The API manages three core resources:

* Rooms: physical spaces across campus
* Sensors: smart devices deployed inside rooms
* Sensor Readings: historical measurements captured by sensors

The system is designed around RESTful principles:

* resource-oriented URIs
* stateless request handling
* meaningful HTTP status codes
* JSON request/response bodies
* sub-resource nesting for readings
* structured exception mapping
* request/response logging for observability

Base path:

/api/v1

Primary collections:

* /api/v1/rooms
* /api/v1/sensors
* /api/v1/sensors/{sensorId}/readings

⸻

Recommended Project Structure

smart-campus-api/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/smartcampus/
│       │       ├── config/
│       │       │   └── SmartCampusApplication.java
│       │       ├── model/
│       │       │   ├── Room.java
│       │       │   ├── Sensor.java
│       │       │   ├── SensorReading.java
│       │       │   ├── ApiError.java
│       │       │   └── DiscoveryResponse.java
│       │       ├── store/
│       │       │   └── InMemoryStore.java
│       │       ├── resource/
│       │       │   ├── DiscoveryResource.java
│       │       │   ├── SensorRoomResource.java
│       │       │   ├── SensorResource.java
│       │       │   └── SensorReadingResource.java
│       │       ├── exception/
│       │       │   ├── RoomNotEmptyException.java
│       │       │   ├── LinkedResourceNotFoundException.java
│       │       │   ├── SensorUnavailableException.java
│       │       │   ├── ResourceNotFoundException.java
│       │       │   ├── RoomNotEmptyExceptionMapper.java
│       │       │   ├── LinkedResourceNotFoundExceptionMapper.java
│       │       │   ├── SensorUnavailableExceptionMapper.java
│       │       │   ├── ResourceNotFoundExceptionMapper.java
│       │       │   └── GlobalExceptionMapper.java
│       │       └── filter/
│       │           └── ApiLoggingFilter.java
│       └── webapp/
│           └── WEB-INF/
│               └── web.xml
└── README.md

⸻

Data Models

Room

public class Room {
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new ArrayList<>();
}

Sensor

public class Sensor {
    private String id;
    private String type;
    private String status; // ACTIVE, MAINTENANCE, OFFLINE
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

Build and Run Instructions

1. Prerequisites

Install the following:

* Java 8 or above
* Maven 3.8+
* Apache Tomcat 9
* NetBeans or IntelliJ IDEA
* Postman or curl

2. Maven Dependencies

Use Jersey 2.32 for Tomcat 9 compatibility.

<dependencies>
    <dependency>
        <groupId>org.glassfish.jersey.containers</groupId>
        <artifactId>jersey-container-servlet</artifactId>
        <version>2.32</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.jersey.inject</groupId>
        <artifactId>jersey-hk2</artifactId>
        <version>2.32</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.jersey.media</groupId>
        <artifactId>jersey-media-json-jackson</artifactId>
        <version>2.32</version>
    </dependency>
</dependencies>

3. Application Entry Point

package com.example.smartcampus.config;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
}

4. If using web.xml

<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    <servlet>
        <servlet-name>jersey-servlet</servlet-name>
        <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
        <init-param>
            <param-name>jersey.config.server.provider.packages</param-name>
            <param-value>com.example.smartcampus</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>jersey-servlet</servlet-name>
        <url-pattern>/api/*</url-pattern>
    </servlet-mapping>
</web-app>

5. Run the Project

mvn clean package

Deploy the generated WAR file to Tomcat, then access:

http://localhost:8080/smart-campus/api/v1

⸻

API Design

1. Discovery Endpoint

GET /api/v1

Returns API metadata and resource navigation.

Example response:

{
  "name": "Smart Campus Sensor & Room Management API",
  "version": "v1",
  "maintainer": {
    "name": "Batuhan Amirzehni",
    "email": "w2106852@westminster.ac.uk"
  },
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  },
  "links": {
    "self": "/api/v1",
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}

⸻

2. Rooms

GET /api/v1/rooms

Returns all rooms.

POST /api/v1/rooms

Creates a new room.

Expected best-practice response:

* 201 Created
* Location header pointing to the new room resource

Example request:

{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 40
}

Example response body:

{
  "message": "Room created successfully",
  "room": {
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 40,
    "sensorIds": []
  }
}

GET /api/v1/rooms/{roomId}

Returns one room by ID.

DELETE /api/v1/rooms/{roomId}

Deletes a room only if it has no assigned sensors.

* Returns 204 No Content on success
* Returns 409 Conflict if the room still contains sensors

⸻

3. Sensors

GET /api/v1/sensors

Returns all sensors.

GET /api/v1/sensors?type=CO2

Returns only sensors matching the requested type.

POST /api/v1/sensors

Registers a new sensor.

Validation:

* the referenced roomId must exist
* if not, return 422 Unprocessable Entity (or 400 Bad Request)

Example request:

{
  "id": "CO2-001",
  "type": "CO2",
  "status": "ACTIVE",
  "currentValue": 425.5,
  "roomId": "LIB-301"
}

On successful creation, the sensor ID should also be added to the parent room’s sensorIds list.

⸻

4. Sensor Readings (Sub-Resource)

Sub-resource root

/api/v1/sensors/{sensorId}/readings

GET /api/v1/sensors/{sensorId}/readings

Returns historical readings for the selected sensor.

POST /api/v1/sensors/{sensorId}/readings

Appends a new reading.

Business rules:

* if sensor does not exist: 404 Not Found
* if sensor status is MAINTENANCE: 403 Forbidden
* on successful POST:
    * append the reading to that sensor’s history
    * update the parent sensor’s currentValue

Example request:

{
  "id": "reading-001",
  "timestamp": 1713600000000,
  "value": 431.8
}

Example response:

{
  "message": "Reading recorded successfully",
  "sensorId": "CO2-001",
  "updatedCurrentValue": 431.8
}

⸻

Error Handling Strategy

The API must never expose raw Java stack traces or HTML error pages. All errors should be returned in a structured JSON format.

Standard Error Model

{
  "timestamp": 1713600000000,
  "status": 409,
  "error": "Conflict",
  "message": "Room LIB-301 cannot be deleted because it still has assigned sensors.",
  "path": "/api/v1/rooms/LIB-301"
}

Required Custom Exceptions

* RoomNotEmptyException → 409 Conflict
* LinkedResourceNotFoundException → 422 Unprocessable Entity or 400 Bad Request
* SensorUnavailableException → 403 Forbidden
* ResourceNotFoundException → 404 Not Found
* ExceptionMapper<Throwable> → 500 Internal Server Error

⸻

Logging / Observability

A custom filter should implement both:

* ContainerRequestFilter
* ContainerResponseFilter

It should log:

* HTTP method
* request URI
* final response status code

Example log output:

INFO: --- Incoming Request ---
INFO: Method: POST
INFO: URI: http://localhost:8080/smart-campus/api/v1/sensors
INFO: --- Outgoing Response ---
INFO: Status: 201

⸻

Sample curl Commands

1. Discovery

curl -i http://localhost:8080/smart-campus/api/v1

2. Create a room

curl -i -X POST http://localhost:8080/smart-campus/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id":"LIB-301",
    "name":"Library Quiet Study",
    "capacity":40
  }'

3. Get all rooms

curl -i http://localhost:8080/smart-campus/api/v1/rooms

4. Create a sensor with valid roomId

curl -i -X POST http://localhost:8080/smart-campus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id":"CO2-001",
    "type":"CO2",
    "status":"ACTIVE",
    "currentValue":425.5,
    "roomId":"LIB-301"
  }'

5. Trigger dependency validation error

curl -i -X POST http://localhost:8080/smart-campus/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id":"TEMP-999",
    "type":"Temperature",
    "status":"ACTIVE",
    "currentValue":21.0,
    "roomId":"NO-SUCH-ROOM"
  }'

6. Filter sensors by type

curl -i "http://localhost:8080/smart-campus/api/v1/sensors?type=CO2"

7. Add a reading

curl -i -X POST http://localhost:8080/smart-campus/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{
    "id":"reading-001",
    "timestamp":1713600000000,
    "value":431.8
  }'

8. Get reading history

curl -i http://localhost:8080/smart-campus/api/v1/sensors/CO2-001/readings

9. Trigger 409 conflict by deleting occupied room

curl -i -X DELETE http://localhost:8080/smart-campus/api/v1/rooms/LIB-301

⸻

Conceptual Report Answers

Part 1.1 – JAX-RS Resource Lifecycle

By default, a JAX-RS resource class is request-scoped, which means a new resource instance is typically created for each incoming HTTP request. This default model reduces accidental shared-state bugs because instance fields are not reused across concurrent client requests. In other words, unless a developer explicitly introduces singleton behaviour or stores data in static/global structures, ordinary resource objects are not shared between threads.

This design directly affects in-memory data handling. If the API stores business data inside shared Map or List objects, those collections still become shared application state even when resource instances themselves are request-scoped. Therefore, the safe design is to keep resources stateless and move shared data into a dedicated repository/store layer. That shared layer should use thread-safe structures such as ConcurrentHashMap, synchronized access, or carefully controlled critical sections when updating related objects together. For example, when creating a sensor, the API must both store the sensor and update the parent room’s sensorIds; that combined operation must be treated atomically to avoid race conditions. So, request-scoped resources reduce risk, but they do not remove the need for concurrency control around shared in-memory state.

Part 1.2 – Why Hypermedia / HATEOAS Matters

Hypermedia is considered an advanced REST characteristic because the server does not only return raw data; it also returns navigational links that tell the client what actions are available next. This makes the API more self-descriptive. A client can discover related resources dynamically instead of hardcoding every route from external documentation.

This benefits client developers in several ways. First, it reduces coupling, because clients depend less on memorised endpoint structures. Second, it improves evolvability, because the server can introduce new navigational links without forcing clients to rely entirely on static documentation pages. Third, it improves developer experience by making the API easier to explore and test. Static documentation explains the API once, but hypermedia explains the API at runtime, inside the response itself. For that reason, the discovery endpoint in this coursework should not just return metadata; it should also expose links to core resource collections.

Part 2.1 – Returning IDs Only vs Full Room Objects

Returning only room IDs has a clear bandwidth advantage. The payload is smaller, faster to transfer, and cheaper for clients to process, especially when the number of rooms becomes large. This is useful in list endpoints where the client only needs references and can later fetch detail on demand.

However, returning full room objects improves convenience because the client receives immediately usable information such as room name, capacity, and assigned sensors. This reduces follow-up requests and simplifies UI rendering. The trade-off is that larger responses increase network usage and may send unnecessary data if the client only needs identifiers.

In practice, full objects are often better for moderate-sized collections and richer front ends, while ID-only responses are better for very large lists or highly bandwidth-sensitive clients. For this coursework, returning full room objects is stronger because it demonstrates a more informative API design, but the decision should be justified as a trade-off between payload size and client efficiency.

Part 2.2 – Is DELETE Idempotent?

Yes, DELETE is idempotent in this implementation in the REST sense, because making the same DELETE request multiple times results in the same final server state: the room is no longer present. The first successful DELETE removes the resource. Subsequent identical DELETE requests do not remove it again because it is already gone.

The response code on repeated calls may differ, and that does not break idempotency. For example, the first request may return 204 No Content, while a later identical request may return 404 Not Found. The important point is that the server state after the first request and after the tenth identical request is the same.

A room blocked by the business rule behaves similarly. If it still has assigned sensors, repeated DELETE attempts consistently fail and the room remains undeleted. Again, the resulting state is stable. Therefore, the operation remains idempotent because repeated identical requests do not create additional side effects beyond the first outcome.

Part 3.1 – What Happens if the Client Sends the Wrong Content Type?

The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS that the method accepts only JSON request bodies. If a client sends a payload with an incompatible Content-Type, such as text/plain or application/xml, JAX-RS attempts to match the request to a method that can consume that media type. If none exists, the framework rejects the request before the business logic runs.

The typical technical result is 415 Unsupported Media Type. This is important because it protects the API from ambiguous parsing and guarantees a predictable contract. The server is effectively saying: “your request body may be syntactically valid in some general sense, but it is not in a representation that this endpoint agreed to process.”

So the mismatch is handled at the framework level, not manually inside the method body. That makes the API contract stricter and cleaner, and it also reduces error-prone input handling code.

Part 3.2 – Why Query Parameters Are Better for Filtering

Using @QueryParam for filtering is generally superior because it expresses that the client is still requesting the same collection resource, but with constraints applied. /sensors?type=CO2 still refers to the sensors collection, only filtered by one criterion.

By contrast, a path such as /sensors/type/CO2 makes the filter look like a new nested resource rather than a search constraint. That becomes awkward when more filters are added, for example type, status, room, and pagination. Query strings scale naturally: /sensors?type=CO2&status=ACTIVE&roomId=LIB-301.

Query parameters are therefore more semantic for searching, sorting, pagination, and filtering. They keep the URI model cleaner, reduce route explosion, and clearly separate identity from selection criteria. Paths should identify resources; query parameters should refine collection views.

Part 4.1 – Benefits of the Sub-Resource Locator Pattern

The Sub-Resource Locator pattern improves maintainability by delegating nested resource logic to dedicated classes instead of keeping every route inside one very large controller. In this coursework, readings belong conceptually to a sensor, so /sensors/{sensorId}/readings is naturally represented by a separate SensorReadingResource.

This separation has several architectural benefits. First, it improves cohesion because SensorResource remains focused on sensor registration, listing, and filtering, while SensorReadingResource focuses only on reading history. Second, it reduces class complexity and makes the code easier to test. Third, it scales better when deeper nesting is introduced later, such as /sensors/{sensorId}/readings/{readingId}. Without delegation, one massive controller quickly becomes difficult to navigate, modify, and reason about.

In large APIs, the pattern also supports team development more effectively because related concerns are grouped into smaller, more understandable resource classes. That leads to clearer boundaries and lower maintenance cost.

Part 5.2 – Why 422 Is More Accurate Than 404 Here

HTTP 422 is often more semantically precise because the request itself is structurally valid JSON, but the server cannot process it due to a business-level semantic problem inside that payload. In this scenario, the client successfully sent a well-formed sensor object, but the referenced roomId points to a resource that does not exist.

A plain 404 usually means the requested target URI itself does not exist. Here, the URI /api/v1/sensors is valid and exists; the problem is not the endpoint but the meaning of one field inside the submitted representation. That is why 422 better communicates: “your syntax is correct, but your request data is semantically unacceptable.”

Using 422 gives the client better feedback and allows clearer API contracts. A 400 can still be defended as acceptable, but 422 shows a stronger understanding of HTTP semantics and therefore is the better distinction-level choice.

Part 5.4 – Cybersecurity Risks of Exposing Stack Traces

Exposing internal Java stack traces to external users is dangerous because it leaks technical implementation details that attackers can use for reconnaissance. A stack trace may reveal package names, class names, framework versions, file paths, line numbers, dependency structure, servlet mappings, or database access patterns. Even seemingly harmless details can help an attacker profile the technology stack and identify known weaknesses.

For example, a stack trace may expose that the API uses Jersey, specific Java packages, Tomcat deployment structure, or exact code locations where null handling is weak. An attacker can combine this information with public vulnerability databases to craft more targeted attacks. It may also reveal business logic assumptions or insecure object access paths.

Therefore, production APIs should return only a generic 500 error body to clients while logging the real exception details securely on the server side. This protects internal implementation knowledge while preserving developer visibility for debugging.

Part 5.5 – Why Filters Are Better Than Manual Logger Statements

JAX-RS filters are better for cross-cutting concerns because they centralise behaviour that applies to every request and response. Logging is not part of the business logic of rooms or sensors; it is an infrastructural concern. If Logger.info() calls are inserted manually in every resource method, the code becomes repetitive, inconsistent, and harder to maintain.

Filters solve this by enforcing logging in one place. They guarantee that every request is processed consistently, including future endpoints that may be added later. They also reduce the chance of developers forgetting to log one route or logging different fields in different formats.

This centralisation improves separation of concerns, readability, maintainability, and scalability. Business methods remain focused on business rules, while filters handle platform-level concerns such as logging, authentication, correlation IDs, or timing. That is precisely why filters are the professional design choice for observability.

⸻

Distinction-Level Implementation Checklist

Use this checklist before submission:

* @ApplicationPath("/api/v1") correctly configured
* Discovery endpoint returns valid JSON metadata and links
* POST /rooms returns 201 Created and Location header
* GET /rooms and GET /rooms/{id} fully working
* DELETE /rooms/{id} blocks deletion when sensors still exist
* POST /sensors validates roomId
* GET /sensors?type=... supports filtering
* sub-resource locator implemented for /sensors/{sensorId}/readings
* GET and POST readings fully working
* posting a reading updates parent sensor currentValue
* RoomNotEmptyException mapped to 409
* LinkedResourceNotFoundException mapped to 422 or 400
* SensorUnavailableException mapped to 403
* global ExceptionMapper<Throwable> mapped to 500
* no raw stack trace exposed to clients
* request/response logging filter working
* README includes all required theory answers
* video demonstrates both success and failure scenarios
* video includes you speaking clearly on camera

⸻

Suggested Video Demo Flow (Under 10 Minutes)

1. Show discovery endpoint
2. Create a room and confirm 201 Created + Location
3. Fetch the created room
4. Create a valid sensor linked to that room
5. Attempt to create a sensor with invalid roomId and show 422/400
6. Filter sensors by type
7. Add a reading successfully
8. Show reading history
9. Show that parent sensor currentValue has updated
10. Attempt to post a reading to a MAINTENANCE sensor and show 403
11. Attempt to delete a room with assigned sensors and show 409
12. Show console logs from request/response filter
13. Briefly mention that unexpected runtime errors are handled by the global 500 mapper

⸻

Final Submission Reminder

For full marks, the submission should not only compile and run. It must also demonstrate:

* correct REST semantics
* strong HTTP status code choices
* clean resource hierarchy
* explicit business-rule enforcement
* professional error handling
* observability through logging
* strong theoretical justification in README answers

This combination is what moves a project from merely functional into true distinction-level work.
