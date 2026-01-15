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

/**
 * Filter that validates internal service-to-service requests.
 * <p>
 * This filter intercepts requests to URIs starting with "/internal/" and validates
 * that they contain a valid X-Internal-Secret header matching the configured secret.
 * <p>
 * <strong>Security Note:</strong> This provides a basic layer of protection for internal
 * endpoints but should be used in conjunction with network-level security (VPC, security groups).
 * The internal secret should be:
 * <ul>
 *   <li>Stored securely (environment variables, secrets manager)</li>
 *   <li>Strong and unique per environment</li>
 *   <li>Rotated regularly</li>
 *   <li>Never committed to version control</li>
 * </ul>
 * <p>
 * Configuration: Set the property {@code security.internal.secret} in your application properties.
 *
 * @see com.bravos.steak.security.starter.annotation.InternalOnly
 */
public class InternalCheckingFilter extends OncePerRequestFilter {

  @Value("${security.internal.secret}")
  private String internalSecret;

  /**
   * Validates the X-Internal-Secret header for requests to internal endpoints.
   * <p>
   * Returns 403 Forbidden if:
   * <ul>
   *   <li>The request URI starts with "/internal/" AND</li>
   *   <li>The X-Internal-Secret header is missing or incorrect</li>
   * </ul>
   *
   * @param request the HTTP request
   * @param response the HTTP response
   * @param filterChain the filter chain
   * @throws ServletException if a servlet error occurs
   * @throws IOException if an I/O error occurs
   */
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

  /**
   * Denies the request with a 403 Forbidden response.
   *
   * @param httpServletResponse the HTTP response
   * @throws IOException if an I/O error occurs
   */
  private void denyRequest(HttpServletResponse httpServletResponse) throws IOException {
    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
    try(PrintWriter printWriter = httpServletResponse.getWriter()) {
      printWriter.write("Forbidden");
      printWriter.flush();
    }
  }

}
