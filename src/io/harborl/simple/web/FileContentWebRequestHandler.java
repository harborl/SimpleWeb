package io.harborl.simple.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileContentWebRequestHandler implements WebRequestHandler {
  
  private final Pattern pattern;
  private final String path;
  private final List<ContentResponsePolicy> policies;

  private FileContentWebRequestHandler(String path) {
    this.pattern = Pattern.compile("\\/(.+)");
    this.path = path;
    policies = new ArrayList<ContentResponsePolicy>();
    {
      policies.add(new DefaultContentResponsePolicy());
    }
  }
  
  public static FileContentWebRequestHandler valueOf(String path) {
    return new FileContentWebRequestHandler(path);
  }

  @Override
  public boolean handle(HttpRequest request, HttpResponse response) {
    final String reqPath = request.getPath();
    Matcher matcher = pattern.matcher(reqPath);
    if (matcher.matches()) {
      String fileName = matcher.group(1);
      File file = new File(path + "/" + fileName);
      if (!file.isDirectory()) {
        for (ContentResponsePolicy policy : policies) {
          if (policy.dealWith(file, response)) {
            return true;
          }
        }
      }
    }

    Util.writeResponseQuitely(response.getOutStream(), 
                              "File doesn't exist", 400, "Not Found");
    return false;
  }

}
