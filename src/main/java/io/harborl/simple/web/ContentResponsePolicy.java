package io.harborl.simple.web;

import java.io.File;
import java.io.IOException;

public interface ContentResponsePolicy {

  boolean dealWith(File file, HttpResponse response) throws IOException;
}
