package io.harborl.simple.web.handler;

import io.harborl.simple.web.http.HttpRequest;
import io.harborl.simple.web.http.HttpResponse;
import io.harborl.simple.web.content.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileRequestHandler implements RequestHandler {

  private final Pattern pattern;
  private final String workPath;
  private final List<ContentPolicy> policyChain;

  private FileRequestHandler(String path) {
    this.pattern = Pattern.compile("/(.+)");
    this.workPath = path;
    policyChain = new ArrayList<>();
    {
      policyChain.add(new TextContentPolicy());
      policyChain.add(new ImageContentPolicy());
      policyChain.add(new DefaultContentPolicy());
    }
  }
  
  public static FileRequestHandler valueOf(String path) {
    return new FileRequestHandler(path);
  }

  @Override
  public boolean handle(HttpRequest request, HttpResponse response) throws IOException {
    final String reqPath = request.getPath();
    final Matcher matcher = pattern.matcher(reqPath);

    if (matcher.matches()) {
      final String fileName = matcher.group(1);
      final File file = new File(workPath + "/" + fileName);

      if (file.exists() && !file.isDirectory()) {
        for (ContentPolicy policy : policyChain) {
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
