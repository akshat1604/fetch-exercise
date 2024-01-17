# HTTP Endpoint Health Checker

## Overview

This program checks the health of a set of HTTP endpoints by reading a configuration file in YAML format. It tests the health of specified endpoints every 15 seconds and logs the cumulative availability percentage for each domain.

## Usage

To run the program, use the following command:

```java -jar healthcheck-0.0.1-SNAPSHOT.jar PATH_TO_YML```

The jar file mentioned above exists inside ```target``` folder.

Replace `PATH_TO_YML` with the actual path to your YAML configuration file. Ensure you run this command from the folder where the JAR file is located.

The yml file I used for testing resides in ```src\main\resources``` with the name ```test.yml```
## Approach


The program utilizes Java (Spring Boot) and follows these steps:

1. **YAML to POJO Mapping:** The provided YAML is mapped into Plain Old Java Objects (POJOs) using the Jackson utility in Java.

2. **HTTP Endpoint Testing:** The program iterates over the parsed POJOs and initiates HTTP requests to the specified endpoints every 15 seconds using the ```@Scheduled``` annotation.

3. **Logging:** After testing all endpoints, the program logs the cumulative availability percentage for each URL domain to the console.

## YAML Format

Ensure your YAML file follows the correct format:

```yaml
- request1:
    headers:
        user-agent: fetch-synthetic-monitor
    method: GET
    name: fetch index page
    url: https://fetch.com/
- request2:
    body: '{"foo":"bar"}'
    headers:
        content-type: application/json
        user-agent: fetch-synthetic-monitor
    method: POST
    name: fetch some fake post endpoint
    url: https://fetch.com/some/post/endpoint
- request3:
    name: fetch rewards index page
    url: https://www.fetchrewards.com/
- request4:
    headers:
        user-agent: fetch-synthetic-monitor
    method: GET
    name: fetch careers page
    url: https://fetch.com/careers
```
