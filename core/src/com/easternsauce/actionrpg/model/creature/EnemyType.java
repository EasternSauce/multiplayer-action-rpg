package com.easternsauce.actionrpg.model.creature;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum EnemyType {
    SKELETON("skeleton"),
    ARCHER("undead_adventurer"),

    MAGE("black_mage"),
    MINOS("minos"),
    SERPENT("serpent"),
    SLUDGE("sludge"),
    WOLF("wolf"),
    RAT("rat");

    public String textureName;

    EnemyType(String textureName) {
        this.textureName = textureName;
    }
}
