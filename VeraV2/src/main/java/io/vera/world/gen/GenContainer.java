package io.vera.world.gen;

import java.util.concurrent.Executor;
import javax.annotation.concurrent.Immutable;

@Immutable
public interface GenContainer extends Executor {
  public static final GenContainer DEFAULT = c -> {
      throw new RuntimeException();
    };
  
  public static final GenContainer ARBITRARY = c -> {
      throw new RuntimeException();
    };
}
