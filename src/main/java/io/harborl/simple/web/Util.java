package io.harborl.simple.web;

import java.io.UnsupportedEncodingException;

public final class Util {

  private Util() { }

  public static final String UTF_8 = "UTF-8";
  public static final int BUF_SIZE = 0x1000; // a page block -> 4k

  public static String buildContentDisposition(String fileName) {
    String encodedFileName = urlEncodeWithFullback(fileName);
    return "attachment; filename=\"" + encodedFileName +"\"; filename*=UTF-8''" + encodedFileName;
  }

  private static String urlEncodeWithFullback(String src) {
    String target;
    try {
      target = java.net.URLEncoder.encode(src, UTF_8);
    } catch (UnsupportedEncodingException fullback) {
      target = src;
    }
    return target;
  }

  public static String urlDecodeWithFullback(String src) {
    String target;
    try {
      target = java.net.URLDecoder.decode(src, "UTF-8");
    } catch (Exception fullback) {
      target = src;
    }
    return target;
  }
}
