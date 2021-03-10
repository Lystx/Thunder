
package io.vera.world.opt;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public interface Weather {

    int RANDOM_TIME = -1;

    void clear();

    void beginRaining();

    void beginThunder();

    void stopThunder();

    boolean isRaining();

    int getRainTime();

    void setRainTime(int ticks);

    boolean isThundering();

    int getThunderTime();

    void setThunderTime(int ticks);

    boolean isClear();

    int getClearTime();

    void setClearTime(int ticks);
}