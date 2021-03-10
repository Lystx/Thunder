
package io.vera.meta.entity.living.monster;



public interface WitherMeta extends MonsterMeta {

    int getFirstHeadTarget();

    void setFirstHeadTarget(int target);

    int getSecondHeadTarget();

    void setSecondHeadTarget(int target);

    int getThirdHeadTarget();

    void setThirdHeadTarget(int target);

    int getInvulnerableTime();

    void setInvulnerableTime(int invulnerableTime);

}
