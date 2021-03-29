package io.vera.server.entity.meta;

import io.vera.meta.entity.living.LivingEntityMeta;
import io.vera.server.net.EntityMetadata;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class VeraLivingEntityMeta extends VeraEntityMeta implements LivingEntityMeta {
  public VeraLivingEntityMeta(EntityMetadata metadata) {
    super(metadata);
    metadata.add(6, EntityMetadata.EntityMetadataType.BYTE, Integer.valueOf(2));
    metadata.add(7, EntityMetadata.EntityMetadataType.FLOAT, Float.valueOf(20.0F));
    metadata.add(8, EntityMetadata.EntityMetadataType.VARINT, Integer.valueOf(0));
    metadata.add(9, EntityMetadata.EntityMetadataType.BOOLEAN, Boolean.valueOf(false));
    metadata.add(10, EntityMetadata.EntityMetadataType.VARINT, Integer.valueOf(0));
  }
  
  public boolean isHandActive() {
    return getMetadata().get(6).asBit(0);
  }
  
  public void setHandActive(boolean active) {
    getMetadata().get(6).setBit(0, active);
  }
  
  public boolean isMainHandActive() {
    return !getMetadata().get(6).asBit(1);
  }
  
  public void setMainHandActive(boolean mainHand) {
    getMetadata().get(6).setBit(1, !mainHand);
  }
  
  public float getHealth() {
    return getMetadata().get(7).asFloat();
  }
  
  public void setHealth(float health) {
    getMetadata().get(7).set(Float.valueOf(health));
  }
  
  public int getPotionEffectColor() {
    return getMetadata().get(8).asInt();
  }
  
  public void setPotionEffectColor(int potionEffectColor) {
    getMetadata().get(8).set(Integer.valueOf(potionEffectColor));
  }
  
  public boolean isPotionEffectAmbient() {
    return getMetadata().get(9).asBoolean();
  }
  
  public void setPotionEffectAmbient(boolean ambient) {
    getMetadata().get(9).set(Boolean.valueOf(ambient));
  }
  
  public int getNumberOfArrowsInEntity() {
    return getMetadata().get(10).asInt();
  }
  
  public void setNumberOfArrowsInEntity(int arrows) {
    getMetadata().get(10).set(Integer.valueOf(arrows));
  }
}
