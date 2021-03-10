
package io.vera.meta.entity.living.monster;



public interface GuardianMeta extends MonsterMeta {

    boolean isRetractingSpikes();

    void setRetractingSpikes(boolean retractingSpikes);

    boolean isElderly();

    void setElderly(boolean elderly);

    int getTargetEntityID();

    void setTargetEntityID();

}
