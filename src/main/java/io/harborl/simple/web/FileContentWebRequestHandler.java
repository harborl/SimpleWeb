package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileContentWebRequestHandler implements WebRequestHandler {

  private final Pattern pattern;
  private final String workPath;
  private final List<ContentResponsePolicy> policyChain;

  private FileContentWebRequestHandler(String path) {
    this.pattern = Pattern.compile("\\/(.+)");
    this.workPath = path;
    policyChain = new ArrayList<ContentResponsePolicy>();
    {
      policyChain.add(new HtmlTextContentResponsePolicy());
      policyChain.add(new ImageContentResponsePolicy());
      policyChain.add(new ZipContentResponsePolicy());
      policyChain.add(new DefaultContentResponsePolicy());
    }
  }
  
  public static FileContentWebRequestHandler valueOf(String path) {
    return new FileContentWebRequestHandler(path);
  }

  @Override
  public boolean handle(HttpRequest request, HttpResponse response) throws IOException {
    final String reqPath = request.getPath();
    final Matcher matcher = pattern.matcher(reqPath);

    if (matcher.matches()) {
      final String fileName = matcher.group(1);
      final File file = new File(workPath + "/" + fileName);

      if (file.exists() && !file.isDirectory()) {
        for (ContentResponsePolicy policy : policyChain) {
          if (policy.dealWith(file, response)) {
            return true;
          }
        }

        // default content policy should always deal with above situation.
        throw new AssertionError("should never rearch here.");
      } else {
        response.writeText("File doesn't exist\n", 404, "Not Found");
      }
    }
    return false;
  }

}
