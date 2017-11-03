package io.harborl.simple.web.content;

import io.harborl.simple.web.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextContentPolicy implements ContentPolicy {

  private final Pattern pattern = Pattern.compile("(.+?)\\.(html|txt|htm|log|css|js|json)", Pattern.CASE_INSENSITIVE);

  private final static Map<String, String> TYPE_MAP;
  static {
    Map<String, String> typeMap = new HashMap<>();
    typeMap.put("txt", "text/plain");
    typeMap.put("html", "text/html");
    typeMap.put("htm", "text/html");
    typeMap.put("log", "text/plain");
    typeMap.put("css", "text/css");
    typeMap.put("js", "application/javascript");
    typeMap.put("json", "application/json");
    TYPE_MAP = Collections.unmodifiableMap(typeMap);
  }

  @Override
  public boolean dealWith(File file, HttpResponse response) throws IOException {

    final String fileName = file.getName();
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.matches()) {
      String contentType = TYPE_MAP.get(matcher.group(2).toLowerCase()) + "; charset=utf-8";
      response.copyFile(file, contentType, null);
      return true;
    }

    return false;
  }
}
