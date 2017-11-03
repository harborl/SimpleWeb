package io.harborl.simple.web.content;

import io.harborl.simple.web.http.HttpResponse;

import java.io.File;
import java.io.IOException;

public interface ContentPolicy {

  boolean dealWith(File file, HttpResponse response) throws IOException;
}
