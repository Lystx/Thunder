package io.vera.world.opt;

import io.vera.world.other.Vector;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface WorldOpts {
    GameMode getGameMode();

    void setGameMode(GameMode paramGameMode);

    Difficulty getDifficulty();

    void setDifficulty(Difficulty paramDifficulty);

    boolean isDifficultyLocked();

    void setDifficultyLocked(boolean paramBoolean);

    Vector getSpawn();

    void setSpawn(Vector paramVector);

    GameRuleMap getGameRules();
}
