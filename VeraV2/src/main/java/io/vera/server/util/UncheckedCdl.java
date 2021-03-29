package io.vera.server.util;

import java.util.concurrent.CountDownLatch;

public class UncheckedCdl extends CountDownLatch {
  public UncheckedCdl(int count) {
    super(count);
  }
  
  public void await() {
    try {
      super.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } 
  }
}
