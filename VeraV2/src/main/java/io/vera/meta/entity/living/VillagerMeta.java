package io.vera.meta.entity.living;

public interface VillagerMeta extends AgeableMeta {
  VillagerProfession getProfession();
  
  void setProfession(VillagerProfession paramVillagerProfession);
}
