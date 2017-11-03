package io.harborl.simple.web.handler;

import io.harborl.simple.web.http.HttpRequest;
import io.harborl.simple.web.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FolderRequestHandler implements RequestHandler {
  
  private final Pattern pattern;
  private final String workPath;
  
  private FolderRequestHandler(String path) {
    this.pattern = Pattern.compile("(.*?)/");
    this.workPath = path;
  }

  public static FolderRequestHandler valueOf(String path) {
    return new FolderRequestHandler(path);
  }

  @Override
  public boolean handle(HttpRequest request, HttpResponse response) throws IOException {
    final String reqPath = request.getPath();
    final Matcher matcher = pattern.matcher(reqPath);

    if (matcher.matches()) {
      final String relativePath = matcher.group(1);
      final File folder = new File(workPath + relativePath);

      if (folder.exists() && folder.isDirectory()) {
        final File[] files = folder.listFiles();

        final StringBuilder sb = new StringBuilder();
        {
          sb.append("<!DOCTYPE html>\n");
          sb.append("<body>\n");

          if (files != null && files.length > 0) {
            sb.append("<ul>\n");
            for (File file : files) {
              if (file.isDirectory()) {
                sb.append("<li><a href=\"").append(relativePath).append("/").append(file.getName()).append("/\">")
                        .append("+ ").append(file.getName()).append("</a></li>\n");
              } else {
                sb.append("<li><a href=\"").append(relativePath).append("/").append(file.getName()).append("\">")
                        .append("- ").append(file.getName()).append("</a></li>\n");
              }
            }
            sb.append("</ul>\n");
          } else {
            sb.append("Empty Folder!");
          }

          sb.append("</body>\n");
          sb.append("</html>\n");
        }

        response.writeText(sb.toString(), 200, "OK");
        return true;
      } else {
        response.writeText("Invalid Folder\n", 400, "Bad Request");
        return true;
      }
    }

    return false;
  }
  
  

}
