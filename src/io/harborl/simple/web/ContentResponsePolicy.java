package io.harborl.simple.web;

import java.io.File;

public interface ContentResponsePolicy {

  boolean dealWith(File file, HttpResponse response);
}
