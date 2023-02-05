package com.mygdx.game.model.creature;

public enum EnemyType {
    SKELETON("skeleton"),
    ARCHER("undead_adventurer");

    public final String textureName;

    EnemyType(String textureName) {
        this.textureName = textureName;
    }
}
