package io.harborl.simple.web;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class HttpResponse {

  private final OutputStream sink;

  private HttpResponse(OutputStream outStream) {
    this.sink = outStream;
  }

  public static HttpResponse newOf(OutputStream outStream) {
    return new HttpResponse(outStream);
  }

  public OutputStream getOutputStream() {
    return sink;
  }

  public void writeError(Throwable th) throws IOException {
    StringWriter writer = new StringWriter();
    th.printStackTrace(new PrintWriter(writer));
    String stack = writer.toString();
    
    StringBuilder sb = new StringBuilder();
    {
      sb.append("<!DOCTYPE html>\n");
      sb.append("<body>\n");
      
      sb.append("<b>Och!</b> Something <em>wrong</em> just happened.<br/>\n " + 
                "For more details, please check the source code of this page.<br/>\n");

      sb.append("<!-- \n");
      sb.append(stack);
      sb.append(" -->\n");
      
      sb.append("</body>\n");
      sb.append("</html>\n");
    }

    writeText(sb.toString(), 500, "Internal Server Error");
  }
  
  public void writeText(String info, int code, String reason) throws IOException {
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sink, Util.UTF_8));

    // write response line
    writer.write("HTTP/1.1 " + code + " " + reason + "\r\n");

    // write headers
    writer.write("Server: SimpleWeb" + "\r\n");
    writer.write("Content-Type: text/html; charset=utf-8" + "\r\n");
    writer.write("Content-Length: " + info.getBytes(Util.UTF_8).length + "\r\n");
    writer.write("\r\n");

    // write body
    writer.write(info);
    writer.flush();
  }

  public void copyFile(File file, String contentType, String contentDispository) throws IOException {
    BufferedOutputStream writer = new BufferedOutputStream(sink);

    // write response line
    writer.write(("HTTP/1.1 " + 200 + " " + "OK" + "\r\n").getBytes());

    // write headers
    writer.write(("Server: SimpleWeb" + "\r\n").getBytes());
    writer.write(("Content-Type: " + contentType + "\r\n").getBytes());
    writer.write(("Content-Length: " + file.length() + "\r\n").getBytes());
    if (contentDispository != null && !contentDispository.isEmpty()) {
      writer.write(("Content-Dispository: " + contentDispository + "\r\n").getBytes());
    }
    writer.write(("\r\n").getBytes());
    writer.flush();

    // write body
    FileInputStream fStream = new FileInputStream(file);
    try {
      byte[] buf = new byte[Util.BUF_SIZE];
      for (int n; (n = fStream.read(buf)) != -1;) {
        sink.write(buf, 0, n);
      }
    } finally {
      fStream.close();
      sink.flush();
    }
  }

}
