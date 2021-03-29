package io.vera.server.entity.meta;

import io.vera.meta.entity.EntityMeta;
import io.vera.server.net.EntityMetadata;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class VeraEntityMeta implements EntityMeta {
  private final EntityMetadata metadata;
  
  public EntityMetadata getMetadata() {
    return this.metadata;
  }
  
  public VeraEntityMeta(EntityMetadata metadata) {
    this.metadata = metadata;
    this.metadata.add(0, EntityMetadata.EntityMetadataType.BYTE, Integer.valueOf(0));
    this.metadata.add(1, EntityMetadata.EntityMetadataType.VARINT, Integer.valueOf(0));
    this.metadata.add(2, EntityMetadata.EntityMetadataType.STRING, "");
    this.metadata.add(3, EntityMetadata.EntityMetadataType.BOOLEAN, Boolean.valueOf(false));
    this.metadata.add(4, EntityMetadata.EntityMetadataType.BOOLEAN, Boolean.valueOf(false));
    this.metadata.add(5, EntityMetadata.EntityMetadataType.BOOLEAN, Boolean.valueOf(false));
  }
  
  public boolean isOnFire() {
    return this.metadata.get(0).asBit(0);
  }
  
  public void setOnFire(boolean onFire) {
    this.metadata.get(0).setBit(0, onFire);
  }
  
  public boolean isCrouched() {
    return this.metadata.get(0).asBit(1);
  }
  
  public void setCrouched(boolean crouched) {
    this.metadata.get(0).setBit(1, crouched);
  }
  
  public boolean isSprinting() {
    return this.metadata.get(0).asBit(3);
  }
  
  public void setSprinting(boolean sprinting) {
    this.metadata.get(0).setBit(3, sprinting);
  }
  
  public boolean isEating() {
    return this.metadata.get(0).asBit(4);
  }
  
  public void setEating(boolean eating) {
    this.metadata.get(0).setBit(4, eating);
  }
  
  public boolean isInvisible() {
    return this.metadata.get(0).asBit(5);
  }
  
  public void setInvisible(boolean invisible) {
    this.metadata.get(0).setBit(5, invisible);
  }
  
  public boolean isGlowing() {
    return this.metadata.get(0).asBit(6);
  }
  
  public void setGlowing(boolean glowing) {
    this.metadata.get(0).setBit(6, glowing);
  }
  
  public boolean isUsingElytra() {
    return this.metadata.get(0).asBit(7);
  }
  
  public void setUsingElytra(boolean usingElytra) {
    this.metadata.get(0).setBit(7, usingElytra);
  }
  
  public int getAir() {
    return this.metadata.get(1).asInt();
  }
  
  public void setAir(int air) {
    this.metadata.get(1).set(Integer.valueOf(air));
  }
  
  public String getCustomName() {
    return this.metadata.get(2).asString();
  }
  
  public void setCustomName(String name) {
    this.metadata.get(2).set(name);
  }
  
  public boolean isCustomNameVisible() {
    return this.metadata.get(3).asBoolean();
  }
  
  public void setCustomNameVisible(boolean visible) {
    this.metadata.get(3).set(Boolean.valueOf(visible));
  }
  
  public boolean isSilent() {
    return this.metadata.get(4).asBoolean();
  }
  
  public void setSilent(boolean silent) {
    this.metadata.get(4).set(Boolean.valueOf(silent));
  }
  
  public boolean isNoGravity() {
    return this.metadata.get(5).asBoolean();
  }
  
  public void setNoGravity(boolean noGravity) {
    this.metadata.get(5).set(Boolean.valueOf(noGravity));
  }
}
