package com.mygdx.game.model.ability;


import com.mygdx.game.game.MyGdxGame;

public class AbilityFactory {

    public static Ability produceAbility(AbilityType abilityType,
                                         AbilityParams abilityParams, MyGdxGame game) {
        if (abilityType == AbilityType.SLASH) {
            return Slash.of(abilityParams, game);
        }

        if (abilityType == AbilityType.FIREBALL) {
            return Fireball.of(abilityParams, game);
        }

        if (abilityType == AbilityType.FIREBALL_EXPLOSION) {
            return FireballExplosion.of(abilityParams, game);
        }

        if (abilityType == AbilityType.LIGHTNING_SPARK) {
            return LightningSpark.of(abilityParams, game);
        }

        if (abilityType == AbilityType.LIGHTNING_NODE) {
            return LightningNode.of(abilityParams, game);
        }

        if (abilityType == AbilityType.LIGHTNING_CHAIN) {
            return LightningChain.of(abilityParams, game);
        }

        if (abilityType == AbilityType.CROSSBOW_BOLT) {
            return CrossbowBolt.of(abilityParams, game);
        }

        if (abilityType == AbilityType.MAGIC_ORB) {
            return MagicOrb.of(abilityParams, game);
        }

        if (abilityType == AbilityType.VOLATILE_BUBBLE) {
            return VolatileBubble.of(abilityParams, game);
        }

        if (abilityType == AbilityType.ICE_SPEAR) {
            return IceSpear.of(abilityParams, game);
        }

        if (abilityType == AbilityType.PLAYFUL_GHOST) {
            return PlayfulGhost.of(abilityParams, game);
        }

        if (abilityType == AbilityType.SUMMON_GHOSTS) {
            return SummonGhosts.of(abilityParams, game);
        }

        if (abilityType == AbilityType.RICOCHET_SHOT) {
            return RicochetShot.of(abilityParams, game);
        }

        if (abilityType == AbilityType.RICOCHET_BULLET) {
            return RicochetBullet.of(abilityParams, game);
        }

        throw new RuntimeException("ability type not found: " + abilityType);
    }

}


