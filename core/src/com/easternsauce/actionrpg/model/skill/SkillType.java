package com.easternsauce.actionrpg.model.skill;

import com.easternsauce.actionrpg.model.ability.AbilityType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum SkillType {
    SWORD_SLASH("Sword Slash", true, AbilityType.SWORD_SLASH, 0.6f, 20f, 0f),
    MOB_SWORD_SLASH("Sword Slash (mob only)", true, AbilityType.SWORD_SLASH, 0.6f, 20f, 0f),
    FIREBALL("Fireball", true, AbilityType.FIREBALL, 1.5f, 30f, 20f),
    LIGHTNING("Lightning", true, AbilityType.LIGHTNING_SPARK, 2f, 20f, 26f),
    CROSSBOW_SHOT("Crossbow Shot", true, AbilityType.CROSSBOW_SHOT, 2f, 40f, 0f),
    MOB_CROSSBOW_SHOT("Crossbow Shot (mob only)", true, AbilityType.CROSSBOW_SHOT, 2f, 40f, 0f),

    MAGIC_ORB("Magic Orb", true, AbilityType.MAGIC_ORB, 0.8f, 15f, 10f),

    MOB_MAGIC_ORB("Magic Orb (mob only)", true, AbilityType.MAGIC_ORB, 1.3f, 15f, 10f),

    VOLATILE_BUBBLE("Volatile Bubble", true, AbilityType.VOLATILE_BUBBLE, 1.3f, 15f, 22f),

    SUMMON_GHOSTS("Summon Ghosts", true, AbilityType.SUMMON_GHOSTS, 1.3f, 15f, 20f),

    RICOCHET_BALLISTA("Ricochet Ballista", true, AbilityType.RICOCHET_BALLISTA, 1.3f, 15f, 20f),

    BOOMERANG("Boomerang", true, AbilityType.BOOMERANG, 6f, 30f, 0f),

    SUMMON_GUARD("Shield Guard", false, AbilityType.SHIELD_GUARD, 6f, 25f, 0f),

    SWORD_SPIN("Sword Spin", true, AbilityType.SWORD_SPIN, 4f, 30f, 0f),

    BOSS_SWORD_SPIN("Sword Spin (boss only)", true, AbilityType.SWORD_SPIN, 4f, 30f, 0f),

    TELEPORT("Teleport", false, AbilityType.TELEPORT_SOURCE, 2f, 10f, 35f),

    POISONOUS_MIXTURE("Poisonous Mixture", false, AbilityType.POISONOUS_MIXTURE, 2f, 10f, 35f),

    PUNCH("Punch", true, AbilityType.PUNCH, 0.4f, 14f, 0f),

    RING_OF_FIRE("Ring of Fire", true, AbilityType.RING_OF_FIRE, 1f, 10f, 13f),

    DASH("Dash", false, AbilityType.DASH, 0.45f, 12f, 0f);

    @Getter
    private String prettyName;
    @Getter
    private Boolean isDamaging;
    @Getter
    private AbilityType startingAbilityType;
    @Getter
    Float cooldown;
    @Getter
    Float staminaCost;
    @Getter
    Float manaCost;

    SkillType(String prettyName, Boolean isDamaging, AbilityType startingAbilityType, Float cooldown, Float staminaCost,
              Float manaCost) {
        this.prettyName = prettyName;
        this.isDamaging = isDamaging;
        this.startingAbilityType = startingAbilityType;
        this.cooldown = cooldown;
        this.staminaCost = staminaCost;
        this.manaCost = manaCost;
    }
}
