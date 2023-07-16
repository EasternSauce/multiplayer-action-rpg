package com.easternsauce.actionrpg.model.creature;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum EnemyType {
    SKELETON("skeleton"),
    ARCHER("undead_adventurer"),

    MAGE("black_mage"),
    MINOS("minos"),
    SERPENT("serpent");

    public String textureName;

    EnemyType(String textureName) {
        this.textureName = textureName;
    }
}
