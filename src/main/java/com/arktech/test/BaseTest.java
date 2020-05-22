package com.arktech.test;

import com.google.common.io.Resources;

import java.io.IOException;

public abstract class BaseTest {

  protected String readFile(String mockResponseFilePath) throws IOException {
    return new String(Resources.toByteArray(Resources.getResource(mockResponseFilePath)));
  }
}
