package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileListWebRequestHandler implements WebRequestHandler {
  
  private final Pattern pattern;
  private final String workPath;
  
  private FileListWebRequestHandler(String path) {
    this.pattern = Pattern.compile("(.*?)\\/");
    this.workPath = path;
  }

  public static FileListWebRequestHandler valueOf(String path) {
    return new FileListWebRequestHandler(path);
  }

  @Override
  public boolean handle(HttpRequest request, HttpResponse response) throws IOException {
    final String reqPath = request.getPath();
    final Matcher matcher = pattern.matcher(reqPath);

    if (matcher.matches()) {
      final String relativePath = matcher.group(1);
      final File folder = new File(workPath + relativePath);

      if (folder.isDirectory()) {
        final File[] files = folder.listFiles();
        final String host = request.getHeaders().get("host");

        final StringBuilder sb = new StringBuilder();
        {
          sb.append("<!DOCTYPE html>\n");
          sb.append("<body>\n");
          
          sb.append("<ul>\n");
          for (File file : files) {
            if (file.isDirectory()) {
              sb.append("<li><a href=\"http://" + host  + relativePath + "/" + file.getName() + "/\">" + "+ " + file.getName() + "</a></li>\n");
            } else {
              sb.append("<li><a href=\"http://" + host  + relativePath + "/" + file.getName() + "\">" + "- " + file.getName() + "</a></li>\n");
            }
          }
          sb.append("</ul>\n");
          
          sb.append("</body>\n");
          sb.append("</html>\n");
        }

        response.writeText(sb.toString(), 200, "OK");
        return true;
      } else {
        response.writeText("Empty Folder\n", 200, "OK");
        return true;
      }
    }

    return false;
  }
  
  

}
