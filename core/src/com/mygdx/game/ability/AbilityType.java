package com.mygdx.game.ability;

public enum AbilityType {
    SLASH(22f, 0f, 0.7f, true),
    FIREBALL(0f, 16f, 0.5f, true),
    FIREBALL_EXPLOSION(0f, 0f, 0f, false),
    LIGHTNING_SPARK(0f, 20f, 1.0f, true),
    LIGHTNING_NODE(0f, 0f, 0f, false),
    LIGHTNING_CHAIN(0f, 0f, 0f, false);

    public final float staminaCost;
    public final float manaCost;
    public final float cooldown;

    public final boolean performableByCreature;


    AbilityType(float staminaCost, float manaCost, float cooldown, boolean performableByCreature) {
        this.staminaCost = staminaCost;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.performableByCreature = performableByCreature;
    }


}