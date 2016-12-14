package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;
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
      policies.add(new HtmlTextContentResponsePolicy());
      policies.add(new ImageContentResponsePolicy());
      policies.add(new ZipContentResponsePolicy());
      policies.add(new DefaultContentResponsePolicy());
    }
  }
  
  public static FileContentWebRequestHandler valueOf(String path) {
    return new FileContentWebRequestHandler(path);
  }

  @Override
  public boolean handle(HttpRequest request, HttpResponse response) throws IOException {
    final String reqPath = request.getPath();
    Matcher matcher = pattern.matcher(reqPath);
    if (matcher.matches()) {
      String fileName = matcher.group(1);
      File file = new File(path + "/" + fileName);
      if (file.exists() && !file.isDirectory()) {
        for (ContentResponsePolicy policy : policies) {
          if (policy.dealWith(file, response)) {
            return true;
          }
        }

        // default content policy should always deal with it.
        throw new AssertionError("should never rearch here.");
      } else {
        Util.writeResponseQuitely(response.getOutStream(), 
                                  "File doesn't exist\n", 404, "Not Found");
      }
    }
    return false;
  }

}
