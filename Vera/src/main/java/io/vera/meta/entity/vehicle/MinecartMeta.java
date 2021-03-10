
package io.vera.meta.entity.vehicle;

import io.vera.meta.entity.EntityMeta;



public interface MinecartMeta extends EntityMeta {

    int getShakingPower();

    void setShakingPower(int shakingPower);

    int getShakingDirection();

    void setShakingDirection(int shakingDirection);

    float getShakingMultiplier();

    void setShakingMultiplier(boolean shakingMultiplier);

    int getMinecartBlockID();

    void setMinecartBlockID(int blockID);

    int getMinecartBlockData();

    void setMinecartBlockData(int blockData);

    int getMinecartBlockY();

    void setMinecartBlockY(int blockY);

    boolean isShowBlock();

    void setShowBlock(boolean showBlock);

}
