package com.bravos.steak.security.starter.annotation;

import com.bravos.steak.security.starter.configuration.BlockingSecurityConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable blocking security configuration in a Spring application.
 * <p>
 * This annotation imports the {@link com.bravos.steak.security.starter.configuration.BlockingSecurityConfiguration}
 * to set up security filters and configurations.
 */
@Target(ElementType.MODULE)
@Retention(RetentionPolicy.RUNTIME)
@Import(BlockingSecurityConfiguration.class)
public @interface EnableBlockingSecurity {

}
