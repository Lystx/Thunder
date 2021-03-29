package io.betterbukkit.provider.world;

import io.betterbukkit.provider.item.Substance;

import java.util.List;

public interface Block {

    byte getData();

    Substance getSubstance();

    Position getPosition();

    World getWorld();

    void setSubstance(Substance substance);

    void setData(byte data);

    void breakBlock(long delayPerBreak);

    Block getOtherBlock(int addX, int addY, int addZ);

    List<Block> getBlocksAround(int radius);
}
