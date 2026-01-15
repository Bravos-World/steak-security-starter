package com.bravos.steak.security.starter.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

/**
 * Default implementation of {@link RequestContext}.
 * <p>
 * This class holds request-specific security context populated by
 * {@link com.bravos.steak.security.starter.filter.SecurityFilter} from HTTP headers.
 * <p>
 * Instances are stored in a {@link ScopedValue} and should not be manually instantiated
 * or modified outside of the security filters.
 *
 * @see RequestContext
 * @see RequestContextHolder
 * @see com.bravos.steak.security.starter.filter.SecurityFilter
 */
@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class CustomRequestContext implements RequestContext {

  /**
   * Indicates whether the request is authenticated.
   */
  private boolean authenticated = false;

  /**
   * Indicates whether the request is an internal service-to-service call.
   */
  private boolean isInternal = false;

  /**
   * Unique identifier for request tracing across services.
   */
  private String traceId;

  /**
   * The authenticated user's identifier.
   */
  private Long userId;

  /**
   * The authenticated user's tenant/organization identifier.
   */
  private Long tenantId;

  /**
   * The device identifier from which the request originated.
   */
  private String deviceId;

  /**
   * Map of permission to scope level.
   * Key format: "action.resource" (e.g., "create.user")
   * Value: Scope level as byte (0-3)
   */
  private Map<String, Byte> authorities;

}
