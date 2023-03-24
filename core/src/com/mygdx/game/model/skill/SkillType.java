package com.mygdx.game.model.skill;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum SkillType {
    SWORD_SLASH("Sword Slash"),
    FIREBALL("Fireball"),
    LIGHTNING("Lightning"),
    CROSSBOW_BOLT("Crossbow Bolt"),

    MAGIC_ORB("Magic Orb"),

    SLOW_MAGIC_ORB("Magic Orb"),

    VOLATILE_BUBBLE("Volatile Bubble"),

    SUMMON_GHOSTS("Summon Ghosts"),

    RICOCHET_BALLISTA("Ricochet Ballista"),

    BOOMERANG("Boomerang"),

    SUMMON_SHIELD("Summon Shield"),

    SWORD_SPIN("Sword Spin"),

    TELEPORT("Teleport");

    public String prettyName;

    SkillType(String prettyName) {
        this.prettyName = prettyName;
    }
}
