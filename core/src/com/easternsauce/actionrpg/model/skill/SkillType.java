package com.easternsauce.actionrpg.model.skill;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum SkillType {
    SWORD_SLASH("Sword Slash", true),
    FIREBALL("Fireball", true),
    LIGHTNING("Lightning", true),
    CROSSBOW_SHOT("Crossbow Shot", true),
    MOB_CROSSBOW_SHOT("Crossbow Shot (mob only)", true),

    MAGIC_ORB("Magic Orb", true),

    MOB_MAGIC_ORB("Magic Orb (mob only)", true),

    VOLATILE_BUBBLE("Volatile Bubble", true),

    SUMMON_GHOSTS("Summon Ghosts", true),

    RICOCHET_BALLISTA("Ricochet Ballista", true),

    BOOMERANG("Boomerang", true),

    SUMMON_SHIELD("Summon Shield", false),

    SWORD_SPIN("Sword Spin", true),

    TELEPORT("Teleport", false);

    @Getter
    private String prettyName;
    @Getter
    private Boolean isDamaging;

    SkillType(String prettyName, Boolean isDamaging) {
        this.prettyName = prettyName;
        this.isDamaging = isDamaging;
    }
}
