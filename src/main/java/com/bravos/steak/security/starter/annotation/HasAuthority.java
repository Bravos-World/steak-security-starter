package com.bravos.steak.security.starter.annotation;

import com.bravos.steak.security.starter.model.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enforce authority-based access control on methods.
 * <p>
 * This annotation checks if the authenticated user has the required permission
 * with the specified scope. The permission is constructed from the resource and action
 * values (e.g., "action.resource").
 * <p>
 * If the user lacks the required authority or scope, a {@link com.bravos.steak.commonutils.exceptions.ForbiddenException}
 * will be thrown.
 *
 * @see com.bravos.steak.security.starter.annotation.aspect.SecurityAspect
 * @see Scope
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasAuthority {

  /**
   * The resource identifier for the permission check.
   * <p>
   * This value, combined with the action, forms the complete permission key.
   *
   * @return the resource name
   */
  String resource();

  /**
   * The action identifier for the permission check.
   * <p>
   * This value, combined with the resource, forms the complete permission key.
   *
   * @return the action name
   */
  String action();

  /**
   * The required scope level for the permission.
   * <p>
   * The scope determines the level of access:
   * <ul>
   *   <li>{@link Scope#OWN} - Access to own resources only</li>
   *   <li>{@link Scope#TENANT} - Access to tenant-level resources</li>
   *   <li>{@link Scope#ALL} - Access to all resources</li>
   *   <li>{@link Scope#NONE} - No access</li>
   * </ul>
   *
   * @return the required scope
   */
  Scope scope();

}
