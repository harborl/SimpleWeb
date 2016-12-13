package io.harborl.simple.web;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class FileListWebRequestHandler implements WebRequestHandler {
  
  private final Pattern pattern;
  private final String path;
  
  private FileListWebRequestHandler(String path) {
    this.pattern = Pattern.compile("(\\/)");
    this.path = path;
  }
  
  public static FileListWebRequestHandler valueOf(String path) {
    return new FileListWebRequestHandler(path);
  }

  @Override
  public boolean handle(HttpRequest request, HttpResponse response) {
    
    final String reqPath = request.getPath();
    if (pattern.matcher(reqPath).matches()) {
      File folder = new File(path);
      if (folder.isDirectory()) {
        File[] files = folder.listFiles(new FileFilter() {

          @Override
          public boolean accept(File pathname) {
            // excludes the folders to simplify the process
            return !pathname.isDirectory();
          }
          
        });
        
        final String host = request.getHeaders().get("host");
        StringBuilder sb = new StringBuilder();
        {
          sb.append("<!DOCTYPE html>\n");
          sb.append("<body>\n");
          
          sb.append("<ul>\n");
          for (File file : files) {
            sb.append("<li><a href=\"http://" + host + "/"  + file.getName() + "\">" + file.getName() + "</a></li>\n");
          }
          sb.append("</ul>\n");
          
          sb.append("</body>\n");
          sb.append("</html>\n");
        }
        
        Util.writeResponseQuitely(response.getOutStream(), 
                                  sb.toString(), 200, "OK");
        return true;
      } else {
        Util.writeResponseQuitely(response.getOutStream(), 
                                  "Empty Folder", 200, "OK");
        return true;
      }
    }

    return false;
  }
  
  

}
