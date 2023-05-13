package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;

import java.util.function.BiFunction;

public class AbilityFactory {

    public static BiFunction<AbilityParams, CoreGame, Ability> getAbilityByType(AbilityType abilityType) {
        switch (abilityType) {
            case SLASH:
                return SwordSlash::of;
            case FIREBALL:
                return Fireball::of;
            case FIREBALL_EXPLOSION:
                return FireballExplosion::of;
            case LIGHTNING_SPARK:
                return LightningSpark::of;
            case LIGHTNING_NODE:
                return LightningNode::of;
            case LIGHTNING_CHAIN:
                return LightningChain::of;
            case CROSSBOW_BOLT:
                return CrossbowBolt::of;
            case CROSSBOW_SHOT:
                return CrossbowShot::of;
            case MAGIC_ORB:
                return MagicOrb::of;
            case VOLATILE_BUBBLE:
                return VolatileBubble::of;
            case ICE_SPEAR:
                return IceSpear::of;
            case PLAYFUL_GHOST:
                return PlayfulGhost::of;
            case SUMMON_GHOSTS:
                return SummonGhosts::of;
            case RICOCHET_BALLISTA:
                return RicochetBallista::of;
            case RICOCHET_BULLET:
                return RicochetBullet::of;
            case BOOMERANG:
                return Boomerang::of;
            case SUMMON_SHIELD:
                return SummonShield::of;
            case SWORD_SPIN:
                return SwordSpin::of;
            case TELEPORT:
                return TeleportSource::of;
            case TELEPORT_DESTINATION:
                return TeleportDestination::of;
            default:
                throw new RuntimeException("ability type not found: " + abilityType);
        }

    }

    public static Ability produceAbility(AbilityType abilityType, AbilityParams abilityParams, CoreGame game) {

        return getAbilityByType(abilityType).apply(abilityParams, game);
    }

}


