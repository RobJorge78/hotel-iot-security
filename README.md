# Hotel IoT Security Monitoring System

A hotel IoT security monitoring system built using Python, MQTT, Java Spring Boot, and PostgreSQL.

The project simulates security events from hotel-connected devices, processes incoming messages, detects suspicious activity, and provides REST API endpoints for viewing security events and alerts.

## Features

- **IoT Device Simulation:** Generates simulated security events from hotel devices.
- **MQTT Messaging:** Publishes device events through an MQTT broker.
- **Event Processing:** A Java Spring Boot service receives and stores security events.
- **Security Alert Detection:** Detects suspicious activity, including repeated failed login attempts and repeated device disconnections.
- **PostgreSQL Storage:** Stores security events and generated alerts.
- **REST API:** Provides endpoints for retrieving events and alerts.
- **Filtering and Pagination:** Supports filtering events by type and alerts by severity, with paginated results.
- **Automated Testing:** Uses JUnit and Mockito to test application startup and controller behavior.
- **Continuous Integration:** Uses GitHub Actions to run Security API tests automatically on pushes and pull requests.

## Technologies Used

| Technology | Purpose |
|---|---|
| Python | Simulates hotel IoT devices |
| MQTT / Eclipse Mosquitto | Transfers security event messages |
| Java 21 | Backend application development |
| Spring Boot | Event processing and REST APIs |
| PostgreSQL | Stores security events and alerts |
| Docker | Runs local MQTT and PostgreSQL services |
| Maven | Builds and tests Java applications |
| JUnit 5 | Automated Java testing |
| Mockito | Mocks repositories in controller tests |
| GitHub Actions | Continuous integration |

## System Architecture

The system follows this flow:

1. The Python simulator generates security events from simulated hotel devices.
2. The simulator publishes events to the MQTT broker.
3. The Event Processor subscribes to the MQTT topic and receives events.
4. The Event Processor saves incoming events to PostgreSQL.
5. Security detection rules identify suspicious activity and generate alerts.
6. The Security API retrieves stored events and alerts from PostgreSQL.
7. API clients can request filtered and paginated results.

## Project Structure

```text
hotel-iot-security/
├── .github/
│   └── workflows/
│       └── security-api-tests.yml
├── event-processor/
├── mosquitto/
│   └── config/
│       └── mosquitto.conf
├── security-api/
├── simulator/
│   └── simulator.py
├── .gitignore
└── README.md
```

## Simulated Security Events

The Python simulator generates the following event types:

| Event Type | Description |
|---|---|
| LOGIN_SUCCESS | A device successfully logs in |
| LOGIN_FAILED | A device fails to log in |
| DEVICE_CONNECTED | A device connects |
| DEVICE_DISCONNECTED | A device disconnects |

Each event includes a device identifier, event type, timestamp, and source.

## Security Alert Detection

The Event Processor applies rules to identify suspicious activity.

Currently implemented scenarios include:

- **Repeated failed logins:** Generates a HIGH-severity security alert.
- **Repeated device disconnections:** Generates a MEDIUM-severity security alert.
- **Duplicate alert suppression:** Helps prevent repeated alerts for the same detected condition.

## REST API Endpoints

The Security API runs locally on port `8081`.

### Retrieve security events

```http
GET /api/events
```

Filter by event type:

```http
GET /api/events?eventType=LOGIN_FAILED
```

Use pagination:

```http
GET /api/events?page=0&size=5
```

Combine filtering and pagination:

```http
GET /api/events?eventType=LOGIN_FAILED&page=0&size=5
```

### Retrieve security alerts

```http
GET /api/alerts
```

Filter by severity:

```http
GET /api/alerts?severity=HIGH
```

Use pagination:

```http
GET /api/alerts?page=0&size=2
```

Combine filtering and pagination:

```http
GET /api/alerts?severity=HIGH&page=0&size=2
```

## Automated Testing

The Security API includes five automated tests covering:

1. Spring Boot application initialization.
2. Filtering security alerts by severity.
3. Retrieving alerts without a severity filter.
4. Filtering security events by event type.
5. Retrieving events without an event type filter.

Controller tests use Mockito to verify repository interactions and pagination settings.

The application startup test uses a dedicated test database in GitHub Actions.

## Continuous Integration

GitHub Actions automatically runs the Security API tests when code is pushed or a pull request is opened or updated.

The CI workflow:

1. Starts a temporary PostgreSQL 17 database.
2. Configures Java 21.
3. Runs the Security API Maven tests.
4. Reports the test results on GitHub.

## Current Scope

This project is a local development and demonstration system using simulated hotel IoT events. It is not intended to represent a production-deployed hotel security platform.


## Local Setup

This project runs locally using Docker, Java 21, Maven, Python, and PostgreSQL.

### Prerequisites

Install the following tools:

- Docker Desktop
- Java 21
- Python 3
- Git

The Java services include Maven Wrapper scripts, so a separate Maven installation is not required.

### 1. Clone the Repository

```powershell
git clone https://github.com/RobJorge78/hotel-iot-security.git
cd hotel-iot-security
```

### 2. Start the MQTT Broker

The MQTT broker uses Eclipse Mosquitto 2 and listens on port 1883.

```powershell
docker run -d --name hotel-mqtt `
  -p 127.0.0.1:1883:1883 `
  -v "${PWD}/mosquitto/config:/mosquitto/config:ro" `
  eclipse-mosquitto:2
```

The broker uses the configuration in `mosquitto/config/mosquitto.conf`.

**Security note:** Anonymous MQTT connections are enabled for local development only. Production deployments should use authentication, TLS encryption, and network access controls.

### 3. Start PostgreSQL

Create a persistent Docker volume:

```powershell
docker volume create hotel-postgres-data
```

Create the PostgreSQL container:

```powershell
docker run -d --name hotel-postgres `
  -p 127.0.0.1:5432:5432 `
  -e POSTGRES_DB=hotelsecurity `
  -e POSTGRES_USER=hoteluser `
  -e POSTGRES_PASSWORD=YOUR_PASSWORD `
  -v hotel-postgres-data:/var/lib/postgresql/data `
  postgres:17
```

Replace `YOUR_PASSWORD` with a password of your choice.

The Docker volume preserves database data between container restarts.

**Note:** These commands create new containers. If the containers already exist, start them instead:

```powershell
docker start hotel-mqtt hotel-postgres
```

### 4. Configure the Database Password

Both Java services connect to PostgreSQL using the `DB_PASSWORD` environment variable.

In PowerShell, set it to the password you chose when creating the PostgreSQL container:

```powershell
$env:DB_PASSWORD = "YOUR_PASSWORD"
```

Replace `YOUR_PASSWORD` with your actual local database password.

Set this variable in each PowerShell terminal used to run a Java service.

Do not commit passwords or other credentials to GitHub.

### 5. Start the Event Processor

Open a new PowerShell terminal in the project root.

Set `DB_PASSWORD` as described above, then run:

```powershell
cd event-processor
.\mvnw.cmd spring-boot:run
```

The event processor runs on port `8080`.

It subscribes to the MQTT topic `hotel/security/events`, stores incoming security events in PostgreSQL, and generates alerts when suspicious patterns are detected.

Keep this terminal running.

### 6. Start the Security API

Open another PowerShell terminal in the project root.

Set `DB_PASSWORD` as described above, then run:

```powershell
cd security-api
.\mvnw.cmd spring-boot:run
```

The Security API runs on port `8081`.

Once started, you can access these endpoints in your browser:

- `http://localhost:8081/api/events`
- `http://localhost:8081/api/alerts`

Both endpoints support pagination. Events can be filtered by event type, and alerts can be filtered by severity.

### 7. Start the Python Simulator

Open another PowerShell terminal in the project root.

Install the MQTT Python library:

```powershell
python -m pip install paho-mqtt
```

Start the simulator:

```powershell
cd simulator
python simulator.py
```

The simulator publishes security events to the MQTT topic:

`hotel/security/events`

Supported simulated events include:

- `LOGIN_SUCCESS`
- `LOGIN_FAILED`
- `DEVICE_CONNECTED`
- `DEVICE_DISCONNECTED`

The event processor receives these events and stores them in PostgreSQL.

When suspicious patterns are detected, the event processor generates security alerts.

### 8. View Security Events and Alerts

With the Docker containers, Java services, and Python simulator running, open the following URLs in your browser:

**All security events:**

`http://localhost:8081/api/events`

**All security alerts:**

`http://localhost:8081/api/alerts`

**Failed login events:**

`http://localhost:8081/api/events?eventType=LOGIN_FAILED`

**High-severity alerts:**

`http://localhost:8081/api/alerts?severity=HIGH`

**Paginated events:**

`http://localhost:8081/api/events?page=0&size=10`

The API returns JSON responses.

### 9. Stop the Application

Stop the Python simulator and Java services by pressing `Ctrl + C` in their respective terminals.

Stop the Docker containers using:

```powershell
docker stop hotel-postgres hotel-mqtt
```

The PostgreSQL Docker volume preserves stored security events and alerts between restarts.
