
package io.vera.server.world.opt;

import io.vera.meta.nbt.Compound;
import io.vera.server.world.World;
import io.vera.world.opt.*;
import lombok.Getter;
import lombok.Setter;
import io.vera.world.other.Vector;
import io.vera.doc.Debug;
import io.vera.server.packet.play.PlayOutDifficulty;
import io.vera.server.player.RecipientSelector;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicMarkableReference;

@Getter
@ThreadSafe
public class WorldOptImpl implements WorldOpts {
    private final World world;

    @Debug("SURVIVAL")
    @Setter
    private volatile GameMode gameMode = GameMode.CREATIVE;
    private final AtomicMarkableReference<Difficulty> difficulty =
            new AtomicMarkableReference<>(Difficulty.NORMAL, false);
    @Setter
    private volatile Vector spawn;
    private final GameRuleMap gameRules = new GameRuleMap();

    public WorldOptImpl(World world, WorldCreateSpec spec) {
        this.world = world;

        if (!spec.isDefault()) {
            this.difficulty.set(spec.getDifficulty(), spec.isDifficultyLocked());
            this.gameMode = spec.getGameMode();
            spec.getGameRules().copyTo(this.gameRules);
            this.spawn = spec.getSpawn() == null ? this.randVector() : spec.getSpawn();
        } else {
            this.spawn = this.randVector();
        }
    }

    @Debug("creative")
    public WorldOptImpl(World world, Compound compound) {
        this.world = world;

        this.gameMode = GameMode.CREATIVE; // GameMode.from(compound.getInt("GameType"));
        this.difficulty.set(Difficulty.from(compound.getByte("Difficulty")),
                compound.getByte("DifficultyLocked") == 1);
        this.spawn = new Vector(compound.getInt("SpawnX"), compound.getInt("SpawnY"), compound.getInt("SpawnZ"));
        Compound rulesCmp = compound.getCompound("GameRules");
        for (String s : rulesCmp.getEntries().keySet()) {
            GameRule<Object> rule = GameRule.from(s);
            this.gameRules.set(rule, rule.parseValue(rulesCmp.getString(s)));
        }
    }


    private Vector randVector() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int x = r.nextInt() % 1000;
        int z = r.nextInt() % 1000;
        return new Vector(x, this.world.getHighestY(x, z) + 1, z);
    }

    @Override
    public Difficulty getDifficulty() {
        return this.difficulty.getReference();
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        Difficulty d0;
        do {
            d0 = this.getDifficulty();
        } while (!this.difficulty.isMarked() &&
                !this.difficulty.compareAndSet(d0, difficulty, false, false));

        if (d0 != difficulty) {
            RecipientSelector.inWorld(this.world, new PlayOutDifficulty(this.world));
        }
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficulty.isMarked();
    }

    @Override
    public void setDifficultyLocked(boolean locked) {
        Difficulty difficulty;
        do {
            difficulty = this.getDifficulty();
        } while (!this.difficulty.attemptMark(difficulty, locked));
    }


    public void write(Compound compound) {
        compound.putInt("GameType", this.gameMode.asInt());
        compound.putByte("Difficulty", this.difficulty.getReference().asByte());
        compound.putByte("DifficultyLocked", (byte) (this.difficulty.isMarked() ? 1 : 0));

        Vector spawn = this.spawn;
        compound.putInt("SpawnX", spawn.getIntX());
        compound.putInt("SpawnY", spawn.getIntY());
        compound.putInt("SpawnZ", spawn.getIntZ());

        Compound rulesCmp = new Compound("GameRules");
        for (String s : GameRule.getKeyStrings()) {
            rulesCmp.putString(s, String.valueOf(this.gameRules.<Object>get(GameRule.from(s))));
        }
        compound.putCompound(rulesCmp);
    }
}