package com.easternsauce.actionrpg.model.creature;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
public class CreatureStats {
    private Float speed = 10f;
    private Float baseSpeed = 10f;
    private Float life = 100f;
    private Float maxLife = 100f;
    private Float stamina = 100f;
    private Float maxStamina = 100f;
    private Float mana = 100f;
    private Float maxMana = 100f;
    private Float armor = 0f;
    private Float previousTickLife = getLife();
}