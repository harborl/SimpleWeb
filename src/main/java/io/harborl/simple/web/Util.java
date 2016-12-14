package io.harborl.simple.web;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class Util {

  private Util() { }
  
  public static final String UTF_8 = "UTF-8";
  
  public static void writeErrorQuitely(OutputStream outputStream, Throwable th) throws IOException {
    StringWriter writer = new StringWriter();
    th.printStackTrace(new PrintWriter(writer));
    String stack = writer.toString();

    writeResponseQuitely(outputStream, stack, 500, "Internal Server Error");
  }
  
  public static void writeResponseQuitely(OutputStream outputStream, String info, 
                                          int code, String reason) throws IOException {
    BufferedWriter reponse = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
    // write response line
    reponse.write("HTTP/1.1 " + code + " " + reason + "\r\n");
    // write headers
    reponse.write("Server: SimpleWeb" + "\r\n");
    reponse.write("Content-Type: text/html" + "\r\n");
    reponse.write("Content-Length: " + info.getBytes(UTF_8).length + "\r\n");
    reponse.write("\r\n");
    // write body
    reponse.write(info);
    reponse.flush();
  }
  
  public static byte[] readBytes(File file) throws IOException {
    FileInputStream fStream = new FileInputStream(file);
    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
    byte[] buf = new byte[1024 * 4]; // 4k -> one block
    
    for (int n; (n = fStream.read(buf)) != -1;) {
      bStream.write(buf, 0, n);
    }

    fStream.close();
    bStream.close();
    return bStream.toByteArray();
  }

  public static void writeBytesQuitely(OutputStream outputStream, byte[] data, String contentType, 
                                       String contentDispository) throws IOException {
    BufferedOutputStream reponse = new BufferedOutputStream(outputStream);
    // write response line
    final String reponseLine = "HTTP/1.1 " + 200 + " " + "OK" + "\r\n";
    reponse.write(reponseLine.getBytes());
    // write headers
    reponse.write(("Server: SimpleWeb" + "\r\n").getBytes());
    reponse.write(("Content-Type: " + contentType + "\r\n").getBytes());
    reponse.write(("Content-Length: " + data.length + "\r\n").getBytes());
    reponse.write(("\r\n").getBytes());
    // write body
    reponse.write(data, 0, data.length);
    reponse.flush();
  }
  
  public static String buildContentDisposition(String fileName) {
    final String encodedFileName = fileName; // TODO: url encode it.
    String contentDisposition = "attachment; filename=\"" + encodedFileName +"\"; filename*=UTF-8''" + encodedFileName;
    return contentDisposition;
  }
}