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

@Aspect
public class SecurityAspect {

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

  private void checkAuthentication(RequestContext requestContext) {
    if(!requestContext.isAuthenticated()) {
      throw new UnauthorizeException("Unauthorized", "unauthorized");
    }
  }

}
