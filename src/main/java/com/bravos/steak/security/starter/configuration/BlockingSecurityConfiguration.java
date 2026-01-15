package com.bravos.steak.security.starter.configuration;

import com.bravos.steak.security.starter.annotation.aspect.SecurityAspect;
import com.bravos.steak.security.starter.filter.InternalCheckingFilter;
import com.bravos.steak.security.starter.filter.SecurityFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Auto-configuration for the security starter library.
 * <p>
 * This configuration sets up the security infrastructure including filters and aspects
 * for header-based authentication and authorization. It is enabled by using the
 * {@link com.bravos.steak.security.starter.annotation.EnableBlockingSecurity} annotation.
 * <p>
 * <strong>Key Components Configured:</strong>
 * <ul>
 *   <li>{@link SecurityFilter} - Extracts security context from HTTP headers</li>
 *   <li>{@link InternalCheckingFilter} - Validates internal service requests</li>
 *   <li>{@link SecurityAspect} - Enforces security annotations on methods</li>
 *   <li>{@link SecurityFilterChain} - Configures Spring Security (stateless, permits all)</li>
 * </ul>
 * <p>
 * <strong>Security Configuration:</strong>
 * <ul>
 *   <li>Stateless session management (no sessions created)</li>
 *   <li>CSRF protection disabled (designed for API use)</li>
 *   <li>Form login and logout disabled</li>
 *   <li>All requests permitted at Spring Security level (authorization via annotations)</li>
 * </ul>
 * <p>
 * The SecurityFilterChain bean is only created if one doesn't already exist, allowing
 * applications to provide their own custom security configuration if needed.
 *
 * @see com.bravos.steak.security.starter.annotation.EnableBlockingSecurity
 * @see SecurityFilter
 * @see InternalCheckingFilter
 * @see SecurityAspect
 */
@Configuration
public class BlockingSecurityConfiguration {

  /**
   * Creates the {@link SecurityFilter} bean that extracts security context from headers.
   *
   * @return the security filter instance
   */
  @Bean
  public SecurityFilter blockingSecurityFilter() {
    return new SecurityFilter();
  }

  /**
   * Creates the {@link InternalCheckingFilter} bean that validates internal requests.
   *
   * @return the internal checking filter instance
   */
  @Bean
  public InternalCheckingFilter internalCheckingFilter() {
    return new InternalCheckingFilter();
  }

  /**
   * Creates the {@link SecurityAspect} bean that enforces security annotations.
   *
   * @return the security aspect instance
   */
  @Bean
  public SecurityAspect securityAspect() {
    return new SecurityAspect();
  }

  /**
   * Configures the Spring Security filter chain.
   * <p>
   * This bean is only created if:
   * <ul>
   *   <li>No SecurityFilterChain bean already exists (allows custom configuration)</li>
   *   <li>SecurityFilter and InternalCheckingFilter beans are present</li>
   *   <li>HttpSecurity bean is available</li>
 * </ul>
   * <p>
   * The configuration:
   * <ul>
   *   <li>Permits all requests (authorization handled by annotations)</li>
   *   <li>Disables form login, logout, and CSRF</li>
   *   <li>Sets session management to STATELESS</li>
   *   <li>Adds InternalCheckingFilter before SecurityFilter</li>
   *   <li>Adds SecurityFilter before UsernamePasswordAuthenticationFilter</li>
   * </ul>
   *
   * @param http the HttpSecurity to configure
   * @param securityFilter the security filter
   * @param internalCheckingFilter the internal checking filter
   * @return the configured security filter chain
   */
  @Bean
  @ConditionalOnMissingBean(SecurityFilterChain.class)
  @ConditionalOnBean({SecurityFilter.class, InternalCheckingFilter.class, HttpSecurity.class})
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                 SecurityFilter securityFilter,
                                                 InternalCheckingFilter internalCheckingFilter) {
    http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(internalCheckingFilter, SecurityFilter.class)
        .csrf(AbstractHttpConfigurer::disable);
    return http.build();
  }

}
