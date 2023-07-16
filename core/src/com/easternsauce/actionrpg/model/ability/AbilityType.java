package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.BiFunction;

@NoArgsConstructor
public enum AbilityType {
    SWORD_SLASH(SwordSlash::of),
    MOB_SWORD_SLASH(MobSwordSlash::of),
    BOSS_SWORD_SLASH(BossSwordSlash::of),
    FIREBALL(Fireball::of),
    FIREBALL_EXPLOSION(FireballExplosion::of),
    LIGHTNING_SPARK(LightningSpark::of),
    LIGHTNING_NODE(LightningNode::of),
    LIGHTNING_CHAIN(LightningChain::of),

    CROSSBOW_BOLT(CrossbowBolt::of),
    CROSSBOW_SHOT(CrossbowShot::of),
    MOB_CROSSBOW_SHOT(MobCrossbowShot::of),

    MAGIC_ORB(MagicOrb::of),
    MOB_MAGIC_ORB(MobMagicOrb::of),

    VOLATILE_BUBBLE(VolatileBubble::of),
    MOB_VOLATILE_BUBBLE(MobVolatileBubble::of),

    ICE_SPEAR(IceSpear::of),

    SUMMON_GHOSTS(SummonGhosts::of),

    PLAYFUL_GHOST(PlayfulGhost::of),

    RICOCHET_BALLISTA(RicochetBallista::of),

    RICOCHET_BULLET(RicochetBullet::of),

    BOOMERANG(Boomerang::of),

    SHIELD_GUARD(ShieldGuard::of),

    SWORD_SPIN(SwordSpin::of),

    BOSS_SWORD_SPIN(BossSwordSpin::of),

    TELEPORT_SOURCE(TeleportSource::of),

    TELEPORT_DESTINATION(TeleportDestination::of),

    MOB_TELEPORT_SOURCE(MobTeleportSource::of),

    MOB_TELEPORT_DESTINATION(MobTeleportDestination::of),

    MAGE_TELEPORT_COMBO(MageTeleportCombo::of),

    POISONOUS_MIXTURE(PoisonousMixture::of),
    POISONOUS_CLOUD(PoisonousCloud::of),
    EXPANDING_POISONOUS_CLOUD(ExpandingPoisonousCloud::of),

    PUNCH(Punch::of),

    RING_OF_FIRE(RingOfFire::of),
    DASH(Dash::of),
    ICE_SPEAR_RAMPAGE(IceSpearRampage::of);

    @Getter
    private BiFunction<AbilityParams, CoreGame, Ability> factoryMapping;

    AbilityType(BiFunction<AbilityParams, CoreGame, Ability> factoryMapping) {
        this.factoryMapping = factoryMapping;
    }
}