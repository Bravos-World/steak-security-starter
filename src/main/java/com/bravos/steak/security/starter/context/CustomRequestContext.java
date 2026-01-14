package com.bravos.steak.security.starter.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class CustomRequestContext implements RequestContext {

  private boolean authenticated = false;

  private boolean isInternal = false;

  private String traceId;

  private Long userId;

  private Long tenantId;

  private String deviceId;

  private Map<String, Byte> authorities;

}
