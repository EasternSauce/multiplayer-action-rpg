package com.easternsauce.actionrpg.model.skill;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum SkillType {
    SWORD_SLASH("Sword Slash"),
    FIREBALL("Fireball"),
    LIGHTNING("Lightning"),
    CROSSBOW_SHOT("Crossbow Shot"),
    MOB_CROSSBOW_SHOT("Crossbow Shot (mob only)"),

    MAGIC_ORB("Magic Orb"),

    MOB_MAGIC_ORB("Magic Orb (mob only)"),

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
