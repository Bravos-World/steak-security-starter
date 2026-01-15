package com.bravos.steak.security.starter.annotation.aspect;

import com.bravos.steak.commonutils.exceptions.ForbiddenException;
import com.bravos.steak.commonutils.exceptions.UnauthorizeException;
import com.bravos.steak.security.starter.annotation.HasAuthority;
import com.bravos.steak.security.starter.context.RequestContext;
import com.bravos.steak.security.starter.context.RequestContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Aspect for enforcing security annotations on methods.
 * <p>
 * This aspect intercepts methods annotated with security annotations
 * ({@link com.bravos.steak.security.starter.annotation.RequireAuth},
 * {@link com.bravos.steak.security.starter.annotation.HasAuthority},
 * {@link com.bravos.steak.security.starter.annotation.InternalOnly})
 * and performs the necessary security checks before allowing method execution.
 *
 * @see com.bravos.steak.security.starter.annotation.RequireAuth
 * @see com.bravos.steak.security.starter.annotation.HasAuthority
 * @see com.bravos.steak.security.starter.annotation.InternalOnly
 */
@Aspect
public class SecurityAspect {

  /**
   * Enforces authentication requirement on methods annotated with {@code @RequireAuth}.
   * <p>
   * Throws {@link UnauthorizeException} if the request is not authenticated.
   *
   * @param pjp the proceeding join point
   * @return the result of the method execution
   * @throws UnauthorizeException if the request is not authenticated
   * @throws RuntimeException if an error occurs during method execution
   */
  @Around("@annotation(com.bravos.steak.security.starter.annotation.RequireAuth)")
  public Object checkAuth(ProceedingJoinPoint pjp) {
    RequestContext requestContext = RequestContextHolder.get();
    this.checkAuthentication(requestContext);
    try {
      return pjp.proceed();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Enforces authority-based access control on methods annotated with {@code @HasAuthority}.
   * <p>
   * Checks if the authenticated user has the required permission with the specified scope.
   * Throws {@link ForbiddenException} if the user lacks the required authority.
   *
   * @param pjp the proceeding join point
   * @return the result of the method execution
   * @throws UnauthorizeException if the request is not authenticated
   * @throws ForbiddenException if the user lacks the required authority or scope
   * @throws RuntimeException if an error occurs during method execution
   */
  @Around("@annotation(com.bravos.steak.security.starter.annotation.HasAuthority)")
  public Object checkAuthorities(ProceedingJoinPoint pjp) {
    RequestContext requestContext = RequestContextHolder.get();
    this.checkAuthentication(requestContext);
    HasAuthority hasAuthority = ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(HasAuthority.class);
    String findPermission = hasAuthority.action().concat(".").concat(hasAuthority.resource());
    byte findScope = hasAuthority.scope().getValue();
    Byte foundScope = requestContext.getAuthorities().get(findPermission);
    if(foundScope == null || foundScope != findScope) {
      throw new ForbiddenException("Forbidden", "forbidden");
    }
    try {
      return pjp.proceed();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Enforces internal-only access on methods annotated with {@code @InternalOnly}.
   * <p>
   * Throws {@link ForbiddenException} if the request is not identified as internal.
   *
   * @param pjp the proceeding join point
   * @return the result of the method execution
   * @throws ForbiddenException if the request is not internal
   * @throws RuntimeException if an error occurs during method execution
   */
  @Around("@annotation(com.bravos.steak.security.starter.annotation.InternalOnly)")
  public Object checkInternal(ProceedingJoinPoint pjp) {
    RequestContext requestContext = RequestContextHolder.get();
    if(!requestContext.isInternal()) {
      throw new ForbiddenException("Forbidden", "forbidden");
    }
    try {
      return pjp.proceed();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Checks if the request is authenticated.
   *
   * @param requestContext the request context
   * @throws UnauthorizeException if the request is not authenticated
   */
  private void checkAuthentication(RequestContext requestContext) {
    if(!requestContext.isAuthenticated()) {
      throw new UnauthorizeException("Unauthorized", "unauthorized");
    }
  }

}
