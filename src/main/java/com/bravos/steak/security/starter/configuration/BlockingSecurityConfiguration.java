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
 * Configuration class for setting up security filters and security chain.
 * <p>
 * This class defines beans for {@link com.bravos.steak.security.starter.filter.SecurityFilter}
 * and {@link com.bravos.steak.security.starter.filter.InternalCheckingFilter},
 * and configures the {@link org.springframework.security.web.SecurityFilterChain}.
 */

@Configuration
public class BlockingSecurityConfiguration {

  @Bean
  public SecurityFilter blockingSecurityFilter() {
    return new SecurityFilter();
  }

  @Bean
  public InternalCheckingFilter internalCheckingFilter() {
    return new InternalCheckingFilter();
  }

  @Bean
  public SecurityAspect securityAspect() {
    return new SecurityAspect();
  }

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
