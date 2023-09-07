package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.bite.Bite;
import com.easternsauce.actionrpg.model.ability.boomerang.Boomerang;
import com.easternsauce.actionrpg.model.ability.crossbowbolt.CrossbowBolt;
import com.easternsauce.actionrpg.model.ability.crossbowbolt.CrossbowBoltControl;
import com.easternsauce.actionrpg.model.ability.crossbowbolt.EnemyCrossbowBoltControl;
import com.easternsauce.actionrpg.model.ability.dash.Dash;
import com.easternsauce.actionrpg.model.ability.emeraldspin.EmeraldSpin;
import com.easternsauce.actionrpg.model.ability.emeraldspin.EmeraldSpinControl;
import com.easternsauce.actionrpg.model.ability.explosion.Explosion;
import com.easternsauce.actionrpg.model.ability.fireball.Fireball;
import com.easternsauce.actionrpg.model.ability.icespear.IceSpear;
import com.easternsauce.actionrpg.model.ability.icespear.IceSpearRampage;
import com.easternsauce.actionrpg.model.ability.lightning.LightningChain;
import com.easternsauce.actionrpg.model.ability.lightning.LightningNode;
import com.easternsauce.actionrpg.model.ability.lightning.LightningSpark;
import com.easternsauce.actionrpg.model.ability.mageteleportcombo.MageTeleportCombo;
import com.easternsauce.actionrpg.model.ability.magicorb.EnemyMagicOrb;
import com.easternsauce.actionrpg.model.ability.magicorb.MagicOrb;
import com.easternsauce.actionrpg.model.ability.meteor.Meteor;
import com.easternsauce.actionrpg.model.ability.meteor.MeteorTarget;
import com.easternsauce.actionrpg.model.ability.playfulghost.PlayfulGhost;
import com.easternsauce.actionrpg.model.ability.playfulghost.PlayfulGhostControl;
import com.easternsauce.actionrpg.model.ability.poisonbite.PoisonBite;
import com.easternsauce.actionrpg.model.ability.poisonmixture.*;
import com.easternsauce.actionrpg.model.ability.punch.Punch;
import com.easternsauce.actionrpg.model.ability.ricochetbullet.RicochetBullet;
import com.easternsauce.actionrpg.model.ability.ricochetbullet.RicochetBulletControl;
import com.easternsauce.actionrpg.model.ability.ringoffire.EnemyRingOfFire;
import com.easternsauce.actionrpg.model.ability.ringoffire.RingOfFire;
import com.easternsauce.actionrpg.model.ability.shieldguard.ShieldGuard;
import com.easternsauce.actionrpg.model.ability.swordslash.BossEnemySwordSlash;
import com.easternsauce.actionrpg.model.ability.swordslash.EnemySwordSlash;
import com.easternsauce.actionrpg.model.ability.swordslash.SwordSlash;
import com.easternsauce.actionrpg.model.ability.swordspin.BossEnemySwordSpin;
import com.easternsauce.actionrpg.model.ability.swordspin.SwordSpin;
import com.easternsauce.actionrpg.model.ability.teleport.EnemyTeleportDestination;
import com.easternsauce.actionrpg.model.ability.teleport.EnemyTeleportSource;
import com.easternsauce.actionrpg.model.ability.teleport.TeleportDestination;
import com.easternsauce.actionrpg.model.ability.teleport.TeleportSource;
import com.easternsauce.actionrpg.model.ability.tunneldig.TunnelDig;
import com.easternsauce.actionrpg.model.ability.tunneldig.TunnelDigExplosion;
import com.easternsauce.actionrpg.model.ability.tunneldig.TunnelDigSplash;
import com.easternsauce.actionrpg.model.ability.volatilebubble.EnemyVolatileBubble;
import com.easternsauce.actionrpg.model.ability.volatilebubble.VolatileBubble;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.BiFunction;

@NoArgsConstructor
public enum AbilityType {
  SWORD_SLASH(SwordSlash::of), EMEMY_SWORD_SLASH(EnemySwordSlash::of), BOSS_ENEMY_SWORD_SLASH(
    BossEnemySwordSlash::of), FIREBALL(Fireball::of), FIREBALL_EXPLOSION(Explosion::of), LIGHTNING_SPARK(
    LightningSpark::of), LIGHTNING_NODE(LightningNode::of), LIGHTNING_CHAIN(LightningChain::of),

  CROSSBOW_BOLT(CrossbowBolt::of), CROSSBOW_SHOT(CrossbowBoltControl::of), ENEMY_CROSSBOW_SHOT(
    EnemyCrossbowBoltControl::of),

  MAGIC_ORB(MagicOrb::of), ENEMY_MAGIC_ORB(EnemyMagicOrb::of),

  VOLATILE_BUBBLE(VolatileBubble::of), ENEMY_VOLATILE_BUBBLE(EnemyVolatileBubble::of),

  ICE_SPEAR(IceSpear::of),

  SUMMON_GHOSTS(PlayfulGhostControl::of),

  PLAYFUL_GHOST(PlayfulGhost::of),

  RICOCHET_BALLISTA(RicochetBulletControl::of),

  RICOCHET_BULLET(RicochetBullet::of),

  BOOMERANG(Boomerang::of),

  SHIELD_GUARD(ShieldGuard::of),

  SWORD_SPIN(SwordSpin::of),

  BOSS_ENEMY_SWORD_SPIN(BossEnemySwordSpin::of),

  TELEPORT_SOURCE(TeleportSource::of),

  TELEPORT_DESTINATION(TeleportDestination::of),

  ENEMY_TELEPORT_SOURCE(EnemyTeleportSource::of),

  ENEMY_TELEPORT_DESTINATION(EnemyTeleportDestination::of),

  MAGE_TELEPORT_COMBO(MageTeleportCombo::of),

  POISONOUS_MIXTURE(PoisonousMixture::of), POISONOUS_CLOUD(PoisonousCloud::of), POISONOUS_CLOUD_CONTROL(
    PoisonousCloudControl::of),

  ENEMY_POISONOUS_MIXTURE(EnemyPoisonousMixture::of), ENEMY_POISONOUS_CLOUD(
    EnemyPoisonousCloud::of), ENEMY_POISONOUS_CLOUD_CONTROL(EnemyPoisonousCloudControl::of),

  PUNCH(Punch::of),

  RING_OF_FIRE(RingOfFire::of), ENEMY_RING_OF_FIRE(EnemyRingOfFire::of), DASH(Dash::of), ICE_SPEAR_RAMPAGE(
    IceSpearRampage::of), TUNNEL_DIG(TunnelDig::of), DIG_TUNNEL_SPLASH(TunnelDigSplash::of), DIG_TUNNEL_EXPLOSION(
    TunnelDigExplosion::of),

  BITE(Bite::of), POISON_BITE(PoisonBite::of),

  EMERALD_SPIN(EmeraldSpin::of), EMERALD_SPIN_CONTROL(EmeraldSpinControl::of),

  METEOR(Meteor::of), METEOR_TARGET(MeteorTarget::of);

  @Getter
  private BiFunction<AbilityParams, CoreGame, Ability> factoryMapping;

  AbilityType(BiFunction<AbilityParams, CoreGame, Ability> factoryMapping) {
    this.factoryMapping = factoryMapping;
  }
}