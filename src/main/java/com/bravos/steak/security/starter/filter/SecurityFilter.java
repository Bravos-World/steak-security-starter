package com.bravos.steak.security.starter.filter;

import com.bravos.steak.security.starter.context.CustomRequestContext;
import com.bravos.steak.security.starter.context.RequestContext;
import com.bravos.steak.security.starter.context.RequestContextHolder;
import com.bravos.steak.security.starter.model.Scope;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter that extracts security context from HTTP headers and populates the request context.
 * <p>
 * <strong>SECURITY WARNING:</strong> This filter trusts HTTP headers for authentication and authorization.
 * It MUST only be used in environments where:
 * <ul>
 *   <li>Services are behind a trusted API gateway that validates and sets headers</li>
 *   <li>Services are deployed in a private VPC network</li>
 *   <li>Direct public access to services is prevented</li>
 * </ul>
 * <p>
 * The filter processes the following headers:
 * <ul>
 *   <li><strong>X-TraceId</strong>: Request tracing identifier</li>
 *   <li><strong>X-Authenticated</strong>: Authentication status ("true" or "false")</li>
 *   <li><strong>X-DeviceId</strong>: Device identifier</li>
 *   <li><strong>X-UserId</strong>: User identifier (when authenticated)</li>
 *   <li><strong>X-TenantId</strong>: Tenant identifier (when authenticated)</li>
 *   <li><strong>X-Authorities</strong>: Comma-separated permissions (when authenticated)</li>
 * </ul>
 * <p>
 * Internal requests (URI starting with "/internal/") are handled separately and require
 * the X-Internal-Secret header to be validated by {@link InternalCheckingFilter}.
 *
 * @see RequestContext
 * @see RequestContextHolder
 * @see InternalCheckingFilter
 */
public class SecurityFilter extends OncePerRequestFilter {

  /**
   * Processes the request and populates the request context from security headers.
   * <p>
   * The context is stored in a {@link ScopedValue} for thread-safe access throughout
   * the request lifecycle.
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain
   */
  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) {
    final CustomRequestContext requestContext = new CustomRequestContext();
    final String traceId = request.getHeader("X-TraceId");

    requestContext.setTraceId(traceId);

    if(request.getRequestURI().startsWith("/internal/")) {
      requestContext.setInternal(true);
      next(filterChain, request, response, requestContext);
      return;
    }

    final String authenticated = request.getHeader("X-Authenticated");
    final String deviceId = request.getHeader("X-DeviceId");

    requestContext.setAuthenticated(authenticated.equalsIgnoreCase("true"));
    requestContext.setDeviceId(deviceId);

    if(!requestContext.isAuthenticated()) {
      next(filterChain, request, response, requestContext);
      return;
    }

    final String userIdHeader = request.getHeader("X-UserId");
    final String tenantIdHeader = request.getHeader("X-TenantId");
    final String authoritiesHeader = request.getHeader("X-Authorities");
    final String[] authorities = authoritiesHeader.split(",");

    requestContext.setUserId(Long.valueOf(userIdHeader));
    requestContext.setTenantId(Long.valueOf(tenantIdHeader));
    requestContext.setAuthorities(buildAuthoritiesMap(authorities));

    next(filterChain, request, response, requestContext);
  }

  /**
   * Builds an authorities map from the X-Authorities header value.
   * <p>
   * Expected format: "action.resource.scope" (e.g., "create.user.tenant")
   *
   * @param authorities array of authority strings
   * @return immutable map of permission to scope value
   */
  private Map<String, Byte> buildAuthoritiesMap(String[] authorities) {
    Map<String, Byte> authoritiesMap = new HashMap<>();
    for(String authority: authorities) {
      String[] parts = authority.split("\\.");
      String resourceAction = parts[0].concat(".").concat(parts[1]);
      try {
        authoritiesMap.put(resourceAction, Scope.valueOf(parts[2].toLowerCase()).getValue());
      } catch (IllegalArgumentException e) {
        authoritiesMap.put(resourceAction, Scope.NONE.getValue());
      }
    }
    return Collections.unmodifiableMap(authoritiesMap);
  }

  /**
   * Proceeds with the filter chain within a scoped value context.
   *
   * @param filterChain the filter chain
   * @param request the HTTP request
   * @param response the HTTP response
   * @param requestContext the populated request context
   */
  private void next(FilterChain filterChain,
                    HttpServletRequest request,
                    HttpServletResponse response,
                    RequestContext requestContext) {
    ScopedValue.where(RequestContextHolder.REQUEST_CONTEXT, requestContext)
        .run(() -> {
          try {
            filterChain.doFilter(request, response);
          } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
          }
        });
  }

}
