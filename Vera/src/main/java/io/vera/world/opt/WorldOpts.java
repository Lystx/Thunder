
package io.vera.world.opt;

import io.vera.world.other.Vector;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface WorldOpts {

    GameMode getGameMode();

    void setGameMode(GameMode mode);

    Difficulty getDifficulty();

    void setDifficulty(Difficulty difficulty);


    boolean isDifficultyLocked();

    void setDifficultyLocked(boolean locked);

    Vector getSpawn();

    void setSpawn(Vector vector);

    GameRuleMap getGameRules();

}