
package io.vera.server.player;

import io.vera.meta.entity.living.PlayerMeta;
import io.vera.server.entity.meta.VeraLivingEntityMeta;
import io.vera.server.net.EntityMetadata;

import javax.annotation.concurrent.ThreadSafe;


@ThreadSafe
public class VeraPlayerMeta extends VeraLivingEntityMeta implements PlayerMeta {

    public VeraPlayerMeta(EntityMetadata metadata) {
        super(metadata);
        metadata.add(11, EntityMetadata.EntityMetadataType.FLOAT, 0f);
        metadata.add(12, EntityMetadata.EntityMetadataType.VARINT, 0);
        metadata.add(13, EntityMetadata.EntityMetadataType.BYTE, -1);
        metadata.add(14, EntityMetadata.EntityMetadataType.BYTE, 1);
    }

    @Override
    public float getAdditionalHearts() {
        return this.getMetadata().get(11).asFloat();
    }

    @Override
    public void setAdditionalHearts(float hearts) {
        this.getMetadata().get(11).set(hearts);
    }

    @Override
    public int getScore() {
        return this.getMetadata().get(12).asInt();
    }

    @Override
    public void setScore(int score) {
        this.getMetadata().get(12).set(score);
    }

    @Override
    public byte getSkinFlags() {
        return this.getMetadata().get(13).asByte();
    }

    @Override
    public void setSkinFlags(byte skinFlags) {
        this.getMetadata().get(13).set(skinFlags);
    }

    @Override
    public boolean isCapeEnabled() {
        return this.getMetadata().get(13).asBit(0);
    }

    @Override
    public void setCapeEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(0, enabled);
    }

    @Override
    public boolean isJacketEnabled() {
        return this.getMetadata().get(13).asBit(1);
    }

    @Override
    public void setJacketEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(1, enabled);
    }

    @Override
    public boolean isLeftSleeveEnabled() {
        return this.getMetadata().get(13).asBit(2);
    }

    @Override
    public void setLeftSleeveEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(2, enabled);
    }

    @Override
    public boolean isRightSleeveEnabled() {
        return this.getMetadata().get(13).asBit(3);
    }

    @Override
    public void setRightSleeveEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(3, enabled);
    }

    @Override
    public boolean isLeftLegPantsEnabled() {
        return this.getMetadata().get(13).asBit(4);
    }

    @Override
    public void setLeftLegPantsEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(4, enabled);
    }

    @Override
    public boolean isRightLegPantsEnabled() {
        return this.getMetadata().get(13).asBit(5);
    }

    @Override
    public void setRightLegPantsEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(5, enabled);
    }

    @Override
    public boolean isHatEnabled() {
        return this.getMetadata().get(13).asBit(6);
    }

    @Override
    public void setHatEnabled(boolean enabled) {
        this.getMetadata().get(13).setBit(6, enabled);
    }

    @Override
    public boolean isLeftHandMain() {
        return this.getMetadata().get(14).asByte() == 0;
    }

    @Override
    public void setLeftHandMain(boolean main) {
        this.getMetadata().get(14).set(main ? 0 : 1);
    }

}
