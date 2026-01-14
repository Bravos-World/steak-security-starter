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

public class SecurityFilter extends OncePerRequestFilter {

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
