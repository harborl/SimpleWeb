package io.harborl.simple.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequest {
  
  private final Map<String, String> headers;
  private final InputStream inStream;
  
  // Status line
  private final String path;
  private final HttpMethod method;
  private final String httpVersion;
  
  private HttpRequest(InputStream inStream) throws IOException {
    this.inStream = inStream;
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
    String statusLine = reader.readLine().trim();
    
    String[] tokens = statusLine.split("\\s+");
    if (tokens.length < 3) {
      throw new RuntimeException("status line format wrong. - " + statusLine);
    }

    method = HttpMethod.verify(tokens[0]);
    path = Util.urlDecodeWithFullback(tokens[1]);
    httpVersion = tokens[2];

    headers = new HashMap<String, String>();
    
    for (String line = null; (line = reader.readLine()) != null; ) {
      line = line.trim();
      
      // Reach the end of headers
      if (line.isEmpty()) break;
      
      final int splitPos = line.indexOf(':');
      if (splitPos != -1) {
        final String key = line.substring(0, splitPos).trim().toLowerCase();
        final String value = line.substring(splitPos + 1).trim().toLowerCase();

        headers.put(key, value);
      }

    }

    if (!headers.containsKey("host")) {
      throw new RuntimeException("Don't contain host header. - " + statusLine);
    }
  }

  @Override
  public String toString() {
    return "HttpRequest [path=" + path + ", method=" + method + ", httpVersion=" + httpVersion + "]";
  }

  public static HttpRequest newOf(InputStream inStream) throws IOException {
    return new HttpRequest(inStream);
  }

  public String getPath() {
    return path;
  }

  public Map<String, String> getHeaders() {
    return Collections.unmodifiableMap(headers);
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public InputStream getInStream() {
    return inStream;
  }

}
