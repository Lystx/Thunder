
package io.vera.meta.entity.living.monster;



public interface BlazeMeta extends MonsterMeta {

    @Override
    boolean isOnFire();

    @Override
    void setOnFire(boolean onFire);

}
