package io.harborl.simple.web.http;

public enum HttpMethod {
  GET("Get"),
  POST("Post");

  private final String method;
  HttpMethod(String method) {
    this.method = method;
  }

  public static HttpMethod verify(String method) {
    for (HttpMethod m : HttpMethod.values()) {
      if (m.getMethod().equalsIgnoreCase(method)) {
        return m;
      }
    }

    throw new IllegalArgumentException("invalid method - " + method);
  }

  public String getMethod() {
    return method;
  }
}
