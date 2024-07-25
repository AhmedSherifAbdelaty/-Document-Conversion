
# Project Title
Document-Conversion

## Introduction
This project allows you to upload, convert, check the status, and download documents asynchronously using RabbitMQ. By checking the status of the requested document, the system becomes more dynamic and easier to maintain, especially when adding more conversion types.

## Architecture
The application consists of two microservices:
1. **doc-conversion**: Responsible for all business logic.
2. **document-conversion-gateway**: Serves as an entry point to doc-conversion and handles rate-limiting.

### Design Principles
Assuming the conversion from Word to any type uses the `DocumentConverter` interface, it is easy to implement and write the logic of any conversion type from WORD in a separate class. This design preserves the Open/Closed Principle and Single Responsibility Principle of SOLID principles. Additionally, it adheres to the Interface Segregation Principle. To add a new conversion type, simply include it in the map, making the code dynamic for multiple conversion types.

## Installation
To set up the application, ensure that you have the following installed:
- Java 21
- RabbitMQ
- Redis
- Postgres

Alternatively, you can use Docker. Run maven install to  **doc-conversion** and **document-conversion-gateway** then  Navigate to the directory containing `docker-compose.yml` and run the following command:
```
docker-compose up
```

## Usage
The application provides the following endpoints which you can find in doc-conversion postman collection:

### Upload Document
**POST** `/api/documents/upload`

Accepts: Multipart file as a `RequestParam`.

### Convert Document
**POST** `/api/documents/convert`

Accepts: JSON body with the following fields:
- `documentId` (long type)
- `targetFormat` (string type)

Example:
```json
{
  "documentId": 1,
  "targetFormat": "pdf"
}
```

### Check Document Status
**GET** `/api/documents/{documentId}/status`

Accepts: `documentId` as a path variable.

### Download Document
**GET** `/api/documents/{documentId}/download`

Accepts: `documentId` as a path variable.

## Future Improvements
- Implement a circuit breaker to ensure the application does not have a single point of failure.
- Run multiple instances and balance the requests via load balancers.
- Add various conversion types (e.g., word to txt) by implementing `DocumentConverter` and defining the required beans in a post-constructed map.
- Store conversion types in a database for better manageability instead of using a map for simplicity.
