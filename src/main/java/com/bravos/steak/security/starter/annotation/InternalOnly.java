package com.bravos.steak.security.starter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to restrict method access to internal requests only.
 * <p>
 * Methods annotated with {@code @InternalOnly} can only be invoked when the request
 * is identified as internal (typically requests starting with "/internal/" and
 * containing a valid X-Internal-Secret header).
 * <p>
 * If a non-internal request attempts to invoke the method, a
 * {@link com.bravos.steak.commonutils.exceptions.ForbiddenException} will be thrown.
 *
 * @see com.bravos.steak.security.starter.annotation.aspect.SecurityAspect
 * @see com.bravos.steak.security.starter.filter.InternalCheckingFilter
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InternalOnly {
}
