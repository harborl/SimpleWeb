package io.harborl.simple.web;

public enum HttpMethod {
  GET(1, "Get"),
  POST(2, "Post");

  private final int code;
  private final String method;
  private HttpMethod(int code, String method) {
    this.code = code;
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

  public int getCode() {
    return code;
  }

  public String getMethod() {
    return method;
  }
}
