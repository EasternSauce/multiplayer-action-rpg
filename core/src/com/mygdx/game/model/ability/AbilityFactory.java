package com.mygdx.game.model.ability;


public class AbilityFactory {

    public static Ability produceAbility(AbilityType abilityType,
                                         AbilityInitialParams abilityInitialParams) {
        if (abilityType == AbilityType.SLASH) {
            return Slash.of(abilityInitialParams);

        }

        if (abilityType == AbilityType.FIREBALL) {
            return Fireball.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.FIREBALL_EXPLOSION) {
            return FireballExplosion.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.LIGHTNING_SPARK) {
            return LightningSpark.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.LIGHTNING_NODE) {
            return LightningNode.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.LIGHTNING_CHAIN) {
            return LightningChain.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.CROSSBOW_BOLT) {
            return CrossbowBolt.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.MAGIC_ORB) {
            return MagicOrb.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.VOLATILE_BUBBLE) {
            return VolatileBubble.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.ICE_SPEAR) {
            return IceSpear.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.PLAYFUL_GHOST) {
            return PlayfulGhost.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.SUMMON_GHOSTS) {
            return SummonGhosts.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.RICOCHET_SHOT) {
            return RicochetShot.of(abilityInitialParams);
        }

        if (abilityType == AbilityType.RICOCHET_BULLET) {
            return RicochetBullet.of(abilityInitialParams);
        }

        throw new RuntimeException("ability type not found: " + abilityType);
    }

}


