# Steak Security Starter

A Spring Boot starter library for streamlined security integration in microservices architectures, providing header-based authentication, authorization, and request context management.

---

## ⚠️ CRITICAL SECURITY NOTICE

**THIS LIBRARY SHOULD ONLY BE USED IN TRUSTED NETWORK ENVIRONMENTS**

### Security Requirements and Assumptions

This security starter is designed for use **ONLY** in the following scenarios:

1. **Behind a Trusted API Gateway**: All requests must pass through a trusted API gateway that validates and sets security headers (`X-Authenticated`, `X-UserId`, `X-TenantId`, `X-Authorities`, etc.)

2. **Within a Private VPC Network**: Services using this starter MUST be deployed in a private VPC network that is NOT directly accessible from the public internet.

3. **No Direct Public Access**: Backend services MUST NOT be exposed to public internet access. All external requests MUST go through the API gateway.

### Why This Matters

⚠️ **This library trusts HTTP headers for authentication and authorization.** Headers can be easily spoofed if services are directly accessible. The security model assumes:

- The API gateway performs actual authentication (JWT validation, OAuth, etc.)
- The API gateway sets security headers after validating credentials
- Backend services are isolated in a private network
- Only the API gateway can reach backend services
- Internal service-to-service calls use the `X-Internal-Secret` for validation

### ❌ DO NOT USE if:
- Your service is directly exposed to the internet
- You don't have a trusted API gateway handling authentication
- Your services are not in a private VPC/network
- You need end-to-end encryption of security context

### ✅ Safe to Use When:
- All services are behind a trusted API gateway (Kong, AWS API Gateway, etc.)
- Services are deployed in a private VPC with no public access
- Network security groups/firewalls prevent direct service access
- You control the entire network infrastructure

---

## Features

- **Header-Based Security**: Leverages security headers set by trusted API gateways
- **Request Context Management**: Thread-safe scoped context for request-specific data
- **Annotation-Driven Authorization**: Simple annotations for authentication and permission checks
- **Internal Service Protection**: Dedicated protection for internal-only endpoints
- **Scope-Based Permissions**: Granular access control with OWN/TENANT/ALL scopes
- **Zero Configuration**: Works out-of-the-box with minimal setup

---

## Prerequisites

- **Java**: 21 or higher (uses ScopedValue API)
- **Spring Boot**: 3.2.x or higher
- **Network Architecture**: Private VPC with API Gateway

---

## Installation

### Add Jitpack Repository

**Gradle (Kotlin DSL)**
```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

**Gradle (Groovy)**
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

### Add Dependency

```kotlin
dependencies {
    implementation("com.github.Bravos-World:steak-security-starter:v1.0.1")
}
```

---

## Quick Start

### 1. Enable Security in Your Application

```java
@SpringBootApplication
@EnableBlockingSecurity
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### 2. Configure Application Properties

```properties
# Required: Secret for internal service-to-service communication
security.internal.secret=your-strong-internal-secret-here
```

### 3. Use Security Annotations

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    // Requires authentication only
    @GetMapping("/profile")
    @RequireAuth
    public UserProfile getProfile() {
        RequestContext ctx = RequestContextHolder.get();
        Long userId = ctx.getUserId();
        return userService.getProfile(userId);
    }
    
    // Requires specific authority with scope
    @PostMapping
    @HasAuthority(resource = "user", action = "create", scope = Scope.TENANT)
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.create(request);
    }
    
    // Internal services only
    @PostMapping("/internal/sync")
    @InternalOnly
    public void syncUsers(@RequestBody SyncRequest request) {
        userService.sync(request);
    }
}
```

---

## Security Headers

The API gateway **MUST** set the following headers:

### Authentication Headers
- `X-Authenticated`: `"true"` or `"false"` - Indicates if request is authenticated
- `X-TraceId`: Unique request identifier for tracing
- `X-DeviceId`: Device identifier (optional)

### Authenticated User Headers (when X-Authenticated is "true")
- `X-UserId`: User identifier (Long)
- `X-TenantId`: Tenant identifier (Long)
- `X-Authorities`: Comma-separated permissions (e.g., `"create.user.tenant,read.user.own"`)

### Internal Service Headers
- `X-Internal-Secret`: Secret token for internal-only endpoints (must match configured value)

---

## Annotations

### `@EnableBlockingSecurity`
Enables the security starter configuration. Add to your main application class.

### `@RequireAuth`
Enforces that the request is authenticated. Throws `UnauthorizeException` if not authenticated.

**Example:**
```java
@GetMapping("/dashboard")
@RequireAuth
public Dashboard getDashboard() {
    // Only authenticated users can access
    return dashboardService.get();
}
```

### `@HasAuthority`
Enforces both authentication and specific permission with scope.

**Parameters:**
- `resource`: Resource name (e.g., "user", "order")
- `action`: Action name (e.g., "create", "read", "update", "delete")
- `scope`: Permission scope (OWN, TENANT, or ALL)

**Example:**
```java
@DeleteMapping("/{id}")
@HasAuthority(resource = "order", action = "delete", scope = Scope.TENANT)
public void deleteOrder(@PathVariable Long id) {
    orderService.delete(id);
}
```

### `@InternalOnly`
Restricts access to internal service-to-service calls only. Requires valid `X-Internal-Secret` header.

**Example:**
```java
@PostMapping("/internal/cache-clear")
@InternalOnly
public void clearCache() {
    cacheService.clearAll();
}
```

---

## Permission Scopes

### Scope Levels

| Scope | Value | Description |
|-------|-------|-------------|
| `NONE` | 0 | No access |
| `OWN` | 1 | Access to own resources only |
| `TENANT` | 2 | Access to tenant-level resources |
| `ALL` | 3 | Access to all resources (system-wide) |

### Authority Format

Authorities are formatted as: `action.resource.scope`

**Examples:**
- `read.user.own` - Can read own user data
- `create.order.tenant` - Can create orders for their tenant
- `delete.user.all` - Can delete any user (admin permission)

---

## Request Context

Access request-specific data anywhere in your code:

```java
import com.bravos.steak.security.starter.context.RequestContext;
import com.bravos.steak.security.starter.context.RequestContextHolder;

public class MyService {
    
    public void doSomething() {
        RequestContext ctx = RequestContextHolder.get();
        
        // Check authentication
        boolean isAuth = ctx.isAuthenticated();
        
        // Get user info
        Long userId = ctx.getUserId();
        Long tenantId = ctx.getTenantId();
        String deviceId = ctx.getDeviceId();
        String traceId = ctx.getTraceId();
        
        // Check internal call
        boolean isInternal = ctx.isInternal();
        
        // Get authorities
        Map<String, Byte> authorities = ctx.getAuthorities();
        Byte scope = authorities.get("read.user");
    }
}
```

---

## Architecture Overview

```
┌─────────────────┐
│  Public Client  │
└────────┬────────┘
         │ HTTPS
         ▼
┌─────────────────────────────────────┐
│       API Gateway (Trusted)         │
│  - JWT Validation                   │
│  - Sets X-Authenticated             │
│  - Sets X-UserId, X-TenantId       │
│  - Sets X-Authorities               │
└────────┬────────────────────────────┘
         │ Private Network
         ▼
┌─────────────────────────────────────┐
│  Backend Service (Private VPC)      │
│  ┌───────────────────────────────┐  │
│  │  InternalCheckingFilter       │  │
│  │  SecurityFilter               │  │
│  │  @RequireAuth / @HasAuthority │  │
│  │  Your Business Logic          │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

---

## Best Practices

### 1. Network Security
- Deploy services in private subnets
- Use security groups to restrict traffic
- Only allow API gateway to access backend services
- Use VPN or AWS PrivateLink for internal access

### 2. Secret Management
- Store `security.internal.secret` in environment variables or secrets manager
- Rotate internal secrets regularly
- Use strong, unique secrets per environment

### 3. API Gateway Configuration
- Validate JWT tokens at the gateway
- Set security headers only after successful authentication
- Sanitize/remove security headers from incoming requests
- Log authentication failures

### 4. Monitoring
- Use `X-TraceId` for distributed tracing
- Monitor for unauthorized access attempts
- Alert on missing security headers
- Track internal endpoint access

---

## Common Pitfalls

❌ **Don't** expose services with this library directly to the internet  
✅ **Do** ensure all traffic goes through a trusted API gateway

❌ **Don't** trust user-provided security headers  
✅ **Do** let the API gateway set security headers after validation

❌ **Don't** use the same internal secret across environments  
✅ **Do** use unique secrets per environment

❌ **Don't** hard-code the internal secret  
✅ **Do** use environment variables or secret managers

---

## Troubleshooting

### `UnauthorizeException` thrown
- Verify `X-Authenticated` header is set to `"true"`
- Check API gateway authentication logic
- Ensure headers are forwarded correctly

### `ForbiddenException` thrown
- Verify user has required authority in `X-Authorities` header
- Check scope matches the required scope
- Ensure authority format is `action.resource.scope`

### Internal endpoints return 403
- Verify `X-Internal-Secret` header is present
- Ensure header value matches `security.internal.secret` property
- Check endpoint path starts with `/internal/`

---

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Add tests for new features
4. Submit a pull request

---

## License

This project is licensed under the MIT License.

---

## Support

For issues, questions, or feature requests, please open an issue on GitHub.

