package com.bravos.steak.security.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enforce authentication on methods.
 * <p>
 * Methods annotated with {@code @RequireAuth} require the request to be authenticated.
 * The authentication status is determined by the X-Authenticated header value.
 * <p>
 * If the request is not authenticated, an
 * {@link com.bravos.steak.commonutils.exceptions.UnauthorizeException} will be thrown.
 *
 * @see com.bravos.steak.security.starter.annotation.aspect.SecurityAspect
 * @see com.bravos.steak.security.starter.filter.SecurityFilter
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
}
