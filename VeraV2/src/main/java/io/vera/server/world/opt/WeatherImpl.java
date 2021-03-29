package io.vera.server.world.opt;

import io.vera.meta.nbt.Compound;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.packet.PacketOut;
import io.vera.server.packet.play.PlayOutGameState;
import io.vera.server.packet.play.PlayOutLightning;
import io.vera.server.player.RecipientSelector;
import io.vera.server.world.Chunk;
import io.vera.server.world.World;
import io.vera.world.opt.Weather;
import io.vera.world.vector.AbstractVector;
import java.beans.ConstructorProperties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class WeatherImpl implements Weather {
  private static final int MAX_RAND = 216000;
  
  private final World world;
  
  private final AtomicReference<WeatherState> weatherState;
  
  private final AtomicInteger rainTime;
  
  private final AtomicInteger thunderTime;
  
  @ConstructorProperties({"world"})
  public WeatherImpl(World world) {
    this.weatherState = new AtomicReference<>(WeatherState.CLEAR);
    this.rainTime = new AtomicInteger(ThreadLocalRandom.current().nextInt(216000));
    this.thunderTime = new AtomicInteger(ThreadLocalRandom.current().nextInt(216000));
    this.world = world;
  }
  
  private enum WeatherState {
    CLEAR, RAINING, RAINING_THUNDERING;
  }
  
  public void clear() {
    this.weatherState.set(WeatherState.CLEAR);
    RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutGameState(1, 0.0F) });
  }
  
  public void beginRaining() {
    if (this.weatherState.compareAndSet(WeatherState.CLEAR, WeatherState.RAINING))
      RecipientSelector.inWorld(this.world, new PacketOut[] { (PacketOut)new PlayOutGameState(2, 0.0F) }); 
  }
  
  public void beginThunder() {
    this.weatherState.compareAndSet(WeatherState.RAINING, WeatherState.RAINING_THUNDERING);
  }
  
  public void stopThunder() {
    this.weatherState.compareAndSet(WeatherState.RAINING_THUNDERING, WeatherState.RAINING);
  }
  
  public boolean isRaining() {
    WeatherState state = this.weatherState.get();
    return (state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING);
  }
  
  public int getRainTime() {
    return this.rainTime.get();
  }
  
  public void setRainTime(int ticks) {
    if (ticks == -1)
      ticks = ThreadLocalRandom.current().nextInt(216000); 
    this.rainTime.set(ticks);
  }
  
  public boolean isThundering() {
    return (this.weatherState.get() == WeatherState.RAINING_THUNDERING);
  }
  
  public int getThunderTime() {
    return this.thunderTime.get();
  }
  
  public void setThunderTime(int ticks) {
    if (ticks == -1)
      ticks = ThreadLocalRandom.current().nextInt(216000); 
    this.thunderTime.set(ticks);
  }
  
  public boolean isClear() {
    return (this.weatherState.get() == WeatherState.CLEAR);
  }
  
  public int getClearTime() {
    return this.rainTime.get();
  }
  
  public void setClearTime(int ticks) {
    if (ticks == -1)
      ticks = ThreadLocalRandom.current().nextInt(216000); 
    this.rainTime.set(ticks);
  }
  
  public void tick() {
    int prevRain, nextRain, prevThunder, nextThunder;
    WeatherState state = this.weatherState.get();
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    if (state == WeatherState.RAINING_THUNDERING)
      ServerThreadPool.forSpec(PoolSpec.WORLDS).execute(() -> this.world.getChunks().forEach());
    do {
      prevRain = this.rainTime.get();
      if (prevRain == 0) {
        nextRain = rand.nextInt(216000);
      } else {
        nextRain = prevRain - 1;
      } 
    } while (!this.rainTime.compareAndSet(prevRain, nextRain));
    if (prevRain == 0)
      if (state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING) {
        clear();
      } else {
        beginRaining();
      }  
    do {
      prevThunder = this.thunderTime.get();
      if (prevThunder == 0) {
        nextThunder = rand.nextInt(216000);
      } else {
        nextThunder = prevThunder - 1;
      } 
    } while (!this.thunderTime.compareAndSet(prevThunder, nextThunder));
    if (prevThunder == 0)
      if (state == WeatherState.RAINING_THUNDERING) {
        stopThunder();
      } else if (state == WeatherState.RAINING) {
        beginThunder();
      }  
  }
  
  public void read(Compound compound) {
    this.rainTime.set(compound.getInt("rainTime"));
    this.thunderTime.set(compound.getInt("thunderTime"));
    if (compound.getByte("thundering") == 1) {
      this.weatherState.set(WeatherState.RAINING_THUNDERING);
    } else if (compound.getByte("raining") == 1) {
      this.weatherState.set(WeatherState.RAINING);
    } 
  }
  
  public void write(Compound compound) {
    WeatherState state = this.weatherState.get();
    int rainTime = this.rainTime.get();
    compound.putByte("raining", (byte)((state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING) ? 1 : 0));
    compound.putInt("rainTime", rainTime);
    compound.putByte("thundering", (byte)((state == WeatherState.RAINING_THUNDERING) ? 1 : 0));
    compound.putInt("thunderTime", this.thunderTime.get());
    compound.putInt("clearWeatherTime", rainTime);
  }
}
