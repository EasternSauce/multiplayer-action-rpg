package com.easternsauce.actionrpg.model.skill;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum SkillType {
    SWORD_SLASH("Sword Slash"),
    FIREBALL("Fireball"),
    LIGHTNING("Lightning"),
    CROSSBOW_SHOT("Crossbow Shot"),

    MAGIC_ORB("Magic Orb"),

    SLOW_MAGIC_ORB("Magic Orb"),

    VOLATILE_BUBBLE("Volatile Bubble"),

    SUMMON_GHOSTS("Summon Ghosts"),

    RICOCHET_BALLISTA("Ricochet Ballista"),

    BOOMERANG("Boomerang"),

    SUMMON_SHIELD("Summon Shield"),

    SWORD_SPIN("Sword Spin"),

    TELEPORT("Teleport");

    private String prettyName;

    public String getPrettyName() {
        return prettyName;
    }

    SkillType(String prettyName) {
        this.prettyName = prettyName;
    }
}
