
package io.vera.meta.entity.living.monster;



public interface CreeperMeta extends MonsterMeta {

    int getCreeperState();

    void setCreeperState(int state);

    boolean isCharged();

    void setCharged(boolean charged);

    boolean isIgnited();

    void setIgnited(boolean ignited);

}
