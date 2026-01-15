package com.bravos.steak.security.starter.annotation;

import com.bravos.steak.security.starter.configuration.BlockingSecurityConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables the Steak Security Starter in a Spring Boot application.
 * <p>
 * This annotation imports the {@link BlockingSecurityConfiguration} which sets up:
 * <ul>
 *   <li>Security filters for extracting context from HTTP headers</li>
 *   <li>Internal request validation</li>
 *   <li>Aspect-based annotation enforcement</li>
 *   <li>Spring Security configuration (stateless, permissive)</li>
 * </ul>
 * <p>
 * <strong>⚠️ CRITICAL SECURITY WARNING:</strong>
 * <br>
 * This security model trusts HTTP headers for authentication and authorization.
 * <strong>ONLY use in environments where:</strong>
 * <ul>
 *   <li>Services are behind a trusted API gateway that validates and sets security headers</li>
 *   <li>Services are deployed in a private VPC network with no direct public access</li>
 *   <li>Network security controls prevent bypassing the API gateway</li>
 * </ul>
 * <p>
 * <strong>Usage Example:</strong>
 * <pre>{@code
 * @SpringBootApplication
 * @EnableBlockingSecurity
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * }</pre>
 * <p>
 * <strong>Required Configuration:</strong>
 * <pre>
 * # application.properties
 * security.internal.secret=your-strong-secret-here
 * </pre>
 *
 * @see BlockingSecurityConfiguration
 * @see RequireAuth
 * @see HasAuthority
 * @see InternalOnly
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(BlockingSecurityConfiguration.class)
public @interface EnableBlockingSecurity {

}
