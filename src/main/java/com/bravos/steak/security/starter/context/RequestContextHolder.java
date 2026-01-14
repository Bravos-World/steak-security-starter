package com.bravos.steak.security.starter.context;

/**
 * Utility class for managing the {@link RequestContext} in a scoped manner.
 * <p>
 * Provides methods to get the current {@link RequestContext}.
 */

public final class RequestContextHolder {

  public static final ScopedValue<RequestContext> REQUEST_CONTEXT = ScopedValue.newInstance();

  public static RequestContext get() {
    return REQUEST_CONTEXT.get();
  }

}
