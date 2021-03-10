
package io.vera.server.entity.meta;

import io.vera.meta.entity.living.LivingEntityMeta;
import io.vera.server.net.EntityMetadata;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public class VeraLivingEntityMeta extends VeraEntityMeta implements LivingEntityMeta {

    public VeraLivingEntityMeta(EntityMetadata metadata) {
        super(metadata);
        metadata.add(6, EntityMetadata.EntityMetadataType.BYTE, 0b10);
        metadata.add(7, EntityMetadata.EntityMetadataType.FLOAT, 20.0f);
        metadata.add(8, EntityMetadata.EntityMetadataType.VARINT, 0);
        metadata.add(9, EntityMetadata.EntityMetadataType.BOOLEAN, false);
        metadata.add(10, EntityMetadata.EntityMetadataType.VARINT, 0);
    }

    @Override
    public boolean isHandActive() {
        return this.getMetadata().get(6).asBit(0);
    }

    @Override
    public void setHandActive(boolean active) {
        this.getMetadata().get(6).setBit(0, active);
    }

    @Override
    public boolean isMainHandActive() {
        return !this.getMetadata().get(6).asBit(1);
    }

    @Override
    public void setMainHandActive(boolean mainHand) {
        this.getMetadata().get(6).setBit(1, !mainHand);
    }

    @Override
    public float getHealth() {
        return this.getMetadata().get(7).asFloat();
    }

    @Override
    public void setHealth(float health) {
        this.getMetadata().get(7).set(health);
    }

    @Override
    public int getPotionEffectColor() {
        return this.getMetadata().get(8).asInt();
    }

    @Override
    public void setPotionEffectColor(int potionEffectColor) {
        this.getMetadata().get(8).set(potionEffectColor);
    }

    @Override
    public boolean isPotionEffectAmbient() {
        return this.getMetadata().get(9).asBoolean();
    }

    @Override
    public void setPotionEffectAmbient(boolean ambient) {
        this.getMetadata().get(9).set(ambient);
    }

    @Override
    public int getNumberOfArrowsInEntity() {
        return this.getMetadata().get(10).asInt();
    }

    @Override
    public void setNumberOfArrowsInEntity(int arrows) {
        this.getMetadata().get(10).set(arrows);
    }

}
