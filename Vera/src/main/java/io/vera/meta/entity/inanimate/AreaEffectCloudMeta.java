
package io.vera.meta.entity.inanimate;

import io.vera.meta.entity.EntityMeta;

import java.awt.*;



public interface AreaEffectCloudMeta extends EntityMeta {

    float getRadius();

    void setRadius(float radius);

    Color getColor();

    void setColor(Color color);

    boolean isSinglePoint();

    void setSinglePoint(boolean singlePoint);

    int getParticleID();

    void setParticleID(int id);

    int getParticleParameter1();

    void setParticleParameter1(int par);

    int getParticleParameter2();

    void setParticleParameter2(int par);

}
