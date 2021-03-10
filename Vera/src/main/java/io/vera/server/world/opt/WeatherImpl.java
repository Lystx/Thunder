
package io.vera.server.world.opt;

import io.vera.meta.nbt.Compound;
import io.vera.server.world.World;
import io.vera.world.opt.Weather;
import lombok.RequiredArgsConstructor;
import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.packet.play.PlayOutGameState;
import io.vera.server.packet.play.PlayOutLightning;
import io.vera.server.player.RecipientSelector;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class WeatherImpl implements Weather {

    private static final int MAX_RAND = 20 * 60 * 60 * 3;

    private final World world;

    private final AtomicReference<WeatherState> weatherState = new AtomicReference<>(WeatherState.CLEAR);

    private final AtomicInteger rainTime = new AtomicInteger(ThreadLocalRandom.current().nextInt(MAX_RAND));

    private final AtomicInteger thunderTime = new AtomicInteger(ThreadLocalRandom.current().nextInt(MAX_RAND));


    private enum WeatherState {
        CLEAR, RAINING, RAINING_THUNDERING
    }

    @Override
    public void clear() {
        this.weatherState.set(WeatherState.CLEAR);
        RecipientSelector.inWorld(this.world, new PlayOutGameState(1, 0));
    }

    @Override
    public void beginRaining() {
        if (this.weatherState.compareAndSet(WeatherState.CLEAR, WeatherState.RAINING)) {
            RecipientSelector.inWorld(this.world, new PlayOutGameState(2, 0));
        }
    }

    @Override
    public void beginThunder() {
        this.weatherState.compareAndSet(WeatherState.RAINING, WeatherState.RAINING_THUNDERING);
    }

    @Override
    public void stopThunder() {
        this.weatherState.compareAndSet(WeatherState.RAINING_THUNDERING, WeatherState.RAINING);
    }

    @Override
    public boolean isRaining() {
        WeatherState state = this.weatherState.get();
        return state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING;
    }

    @Override
    public int getRainTime() {
        return this.rainTime.get();
    }

    @Override
    public void setRainTime(int ticks) {
        if (ticks == RANDOM_TIME) {
            ticks = ThreadLocalRandom.current().nextInt(MAX_RAND);
        }
        this.rainTime.set(ticks);
    }

    @Override
    public boolean isThundering() {
        return this.weatherState.get() == WeatherState.RAINING_THUNDERING;
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime.get();
    }

    @Override
    public void setThunderTime(int ticks) {
        if (ticks == RANDOM_TIME) {
            ticks = ThreadLocalRandom.current().nextInt(MAX_RAND);
        }
        this.thunderTime.set(ticks);
    }

    @Override
    public boolean isClear() {
        return this.weatherState.get() == WeatherState.CLEAR;
    }

    @Override
    public int getClearTime() {
        return this.rainTime.get();
    }

    @Override
    public void setClearTime(int ticks) {
        if (ticks == RANDOM_TIME) {
            ticks = ThreadLocalRandom.current().nextInt(MAX_RAND);
        }
        this.rainTime.set(ticks);
    }

    public void tick() {
        WeatherState state = this.weatherState.get();
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        if (state == WeatherState.RAINING_THUNDERING) {
            ServerThreadPool.forSpec(PoolSpec.WORLDS).execute(() -> this.world.getChunks().forEach(c -> {
                ThreadLocalRandom cur = ThreadLocalRandom.current();
                if (cur.nextInt(100_000) == 69) {
                    int randX = cur.nextInt(16);
                    int randZ = cur.nextInt(16);
                    PlayOutLightning lightning = new PlayOutLightning(c.getBlockAt(randX, c.getHighestY(randX, randZ), randZ).getPosition());
                    RecipientSelector.whoCanSee(c, null, lightning);
                }
            }));
        }

        int prevRain;
        int nextRain;
        do {
            prevRain = this.rainTime.get();

            if (prevRain == 0) {
                nextRain = rand.nextInt(MAX_RAND);
            } else {
                nextRain = prevRain - 1;
            }
        }
        while (!this.rainTime.compareAndSet(prevRain, nextRain));

        if (prevRain == 0) {
            if (state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING) {
                this.clear();
            } else {
                this.beginRaining();
            }
        }

        int prevThunder;
        int nextThunder;
        do {
            prevThunder = this.thunderTime.get();

            if (prevThunder == 0) {
                nextThunder = rand.nextInt(MAX_RAND);
            } else {
                nextThunder = prevThunder - 1;
            }
        }
        while (!this.thunderTime.compareAndSet(prevThunder, nextThunder));

        if (prevThunder == 0) {
            if (state == WeatherState.RAINING_THUNDERING) {
                this.stopThunder();
            } else if (state == WeatherState.RAINING) {
                this.beginThunder();
            }
        }
    }

    public void read(Compound compound) {
        this.rainTime.set(compound.getInt("rainTime"));
        this.thunderTime.set(compound.getInt("thunderTime"));

        if (compound.getByte("thundering") == 1) {
            this.weatherState.set(WeatherState.RAINING_THUNDERING);
        } else {
            if (compound.getByte("raining") == 1) {
                this.weatherState.set(WeatherState.RAINING);
            }
        }
    }

    public void write(Compound compound) {
        WeatherState state = this.weatherState.get();
        int rainTime = this.rainTime.get();
        compound.putByte("raining", (byte) (state == WeatherState.RAINING || state == WeatherState.RAINING_THUNDERING ? 1 : 0));
        compound.putInt("rainTime", rainTime);
        compound.putByte("thundering", (byte) (state == WeatherState.RAINING_THUNDERING ? 1 : 0));
        compound.putInt("thunderTime", this.thunderTime.get());
        compound.putInt("clearWeatherTime", rainTime);
    }
}