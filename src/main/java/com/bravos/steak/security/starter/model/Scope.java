package com.bravos.steak.security.starter.model;

import lombok.Getter;

/**
 * Enum representing permission scope levels for authorization.
 * <p>
 * Scopes define the breadth of access a user has for a particular permission.
 * Higher scope values generally grant broader access.
 *
 * @see com.bravos.steak.security.starter.annotation.HasAuthority
 */
public enum Scope {

  /**
   * Own scope - Access limited to the user's own resources.
   * <p>
   * Example: A user can only view/edit their own profile.
   */
  OWN((byte) 1),

  /**
   * Tenant scope - Access to resources within the user's tenant/organization.
   * <p>
   * Example: A manager can view all users in their organization.
   */
  TENANT((byte) 2),

  /**
   * All scope - System-wide access to all resources.
   * <p>
   * Example: A system administrator can view/edit any user in any tenant.
   */
  ALL((byte) 3),

  /**
   * No scope - No access granted.
   * <p>
   * Used as a default when permission parsing fails or access should be denied.
   */
  NONE((byte) 0)
  ;

  /**
   * The numeric value representing the scope level.
   */
  @Getter
  private final byte value;

  Scope(byte i) {
    this.value = i;
  }

}
