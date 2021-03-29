package io.vera.world.gen;

import io.vera.inventory.Substance;

public interface GeneratorContext {
  long nextLong();
  
  long nextLong(long paramLong);
  
  int nextInt();
  
  int nextInt(int paramInt);
  
  long seed();
  
  int maxHeight(int paramInt1, int paramInt2);
  
  void set(int paramInt1, int paramInt2, int paramInt3, Substance paramSubstance, byte paramByte);
  
  void set(int paramInt1, int paramInt2, int paramInt3, Substance paramSubstance);
  
  void set(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte paramByte);
  
  void run(Runnable paramRunnable);
}
