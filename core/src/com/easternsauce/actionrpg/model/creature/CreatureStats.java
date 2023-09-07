package com.easternsauce.actionrpg.model.creature;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureStats {
  @NonNull
  private Float speed = 10f;
  @NonNull
  private Float baseSpeed = 10f;
  @NonNull
  private Float life = 100f;
  @NonNull
  private Float maxLife = 100f;
  @NonNull
  private Float stamina = 100f;
  @NonNull
  private Float maxStamina = 100f;
  @NonNull
  private Float mana = 100f;
  @NonNull
  private Float maxMana = 100f;
  @NonNull
  private Float armor = 0f;
  @NonNull
  private Float previousTickLife = getLife();
}