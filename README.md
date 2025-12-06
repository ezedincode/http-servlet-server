# Custom HTTP Server

A lightweight, multi-threaded HTTP server implementation in Java, designed to handle HTTP requests with a custom dispatcher system.

## Features

- **Multi-threaded**: Uses a thread pool to handle multiple concurrent connections.
- **Custom Parsing**: Implements custom logic for parsing HTTP requests and generating responses.
- **Dispatcher System**: Routes requests to appropriate handlers based on the request path and method.
- **Robustness**: Gracefully handles empty connections and health checks.
- **JSON Support**: Automatically maps JSON request bodies to method parameters.


## Installation

To use this library, you need to add the JitPack repository and the dependency to your `pom.xml`.

### Add the Repository

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

### Add the Dependency

```xml
<dependency>
	<groupId>com.github.ezedincode</groupId>
	<artifactId>http-servlet-server</artifactId>
	<version>-44d2d43ffa-1</version>
</dependency>
```

## Usage

To use the HTTP server, instantiate the `HttpServletServer` class with your base package for controllers and the desired port.

```java
import com.ezedin.Httpserver.HttpServletServer;

public class App {
    public static void main(String[] args) throws IOException {
        // Initialize server on port 8080 scanning "com.example.app" for handlers
        HttpServletServer server = new HttpServletServer("com.example.app", 8080);
        server.start();
    }
}
```

## Components

- **HttpServletServer**: The main entry point that initializes the server socket and thread pool.
- **httpServerConnection**: Handles individual client connections on separate threads.
- **dispatcher**: Scans the base package and dispatches requests to the appropriate methods.
- **httpRequest / httpResponse**: Models for HTTP data.
- **httpDataUtil**: Utility for parsing HTTP requests.

## Requirements


## Creating Controllers

To create a controller, annotate your class with `@customController` and your methods with `@customGet` or `@customPost`.

```java
@customController
public class MyController {

    @customGet("/hello")
    public String sayHello() {
        return "Hello World";
    }

    @customPost("/data")
    public String processData() {
        return "Data Processed";
    }

    @customPost("/student/register")
    public String create(@customRequestBody("name") String name, @customRequestBody("age") int age){
        return "Registered: " + name;
    }
}
```
