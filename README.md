# Steak Security Starter

Steak Security Starter is a Spring Boot starter library designed to simplify the integration of security features into your application. It provides pre-configured security filters and utilities to manage request contexts.

## Features
- **Blocking Security Configuration**: Predefined security filters for internal and external requests.
- **Request Context Management**: Scoped context for managing request-specific data.
- **Customizable Authorization Rules**: Extend and configure security as per your application's needs.

## Getting Started

### Prerequisites
- Java 25 or higher
- Spring Boot 4.x

### Use jitpack.io

```Gradle (Kotlin DSL)
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

```Gradle (Groovy)
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

### Installation
Add the following dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Bravos-World:steak-security-starter:v1.0.0")
}
```

### Usage

1. Annotate your Spring Boot application with `@EnableBlockingSecurity`:

```java
@SpringBootApplication
@EnableBlockingSecurity
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

2. Configure properties in `application.properties`:

```properties
security.internal.secret=your-internal-secret
```

3. Define additional security rules by extending `BlockingSecurityConfiguration` if needed.

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request.

## License
This project is licensed under the MIT License.

