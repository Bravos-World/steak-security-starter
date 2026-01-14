package com.bravos.steak.security.starter.context;

import java.util.Map;

/**
 * Interface representing the context of a request.
 * <p>
 * Provides methods to access request-specific data such as authentication status,
 * trace ID, user ID, tenant ID, device ID, and authorities.
 */

public interface RequestContext {

  boolean isAuthenticated();

  boolean isInternal();

  String getTraceId();

  Long getUserId();

  Long getTenantId();

  String getDeviceId();

  Map<String, Byte> getAuthorities();

}
