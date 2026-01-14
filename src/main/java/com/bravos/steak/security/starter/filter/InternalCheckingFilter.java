package com.bravos.steak.security.starter.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class InternalCheckingFilter extends OncePerRequestFilter {

  @Value("${security.internal.secret}")
  private String internalSecret;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
    String uri = request.getRequestURI();
    if(uri.startsWith("/internal/")) {
      String internalSecret = request.getHeader("X-Internal-Secret");
      if(internalSecret == null || !internalSecret.equals(this.internalSecret)) {
        denyRequest(response);
        return;
      }
      filterChain.doFilter(request, response);
    }
  }

  private void denyRequest(HttpServletResponse httpServletResponse) throws IOException {
    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    try(PrintWriter printWriter = httpServletResponse.getWriter()) {
      printWriter.write("Forbidden");
      printWriter.flush();
    }
  }

}
