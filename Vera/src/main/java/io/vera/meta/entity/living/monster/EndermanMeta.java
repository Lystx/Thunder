
package io.vera.meta.entity.living.monster;



public interface EndermanMeta extends MonsterMeta {

    int getCarriedBlockID();

    void setCarriedBlockID(int blockID);

    int getCarriedBlockData();

    void setCarriedBlockData(int blockData);

    boolean isScreaming();

    void setScreaming(boolean screaming);

}
