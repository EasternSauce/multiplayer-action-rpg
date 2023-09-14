package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.renderer.animationconfig.AbilityAnimationConfig;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@NoArgsConstructor(staticName = "of")
public class NullAbility extends Ability {
  @Override
  public Boolean isPositionChangedOnUpdate() {
    return false;
  }

  @Override
  public Boolean isRanged() {
    return false;
  }

  @Override
  public void update(Float delta, CoreGame game) {

  }

  @Override
  public AbilityParams getParams() {
    return AbilityParams.of().setChannelTime(0f).setActiveTime(0f).setWidth(0f).setHeight(0f).setPos(Vector2.of(0f, 0f))
      .setState(AbilityState.INACTIVE);
  }

  @Override
  protected void onChannelUpdate(CoreGame game) {

  }

  @Override
  public void onStarted(CoreGame game) {

  }

  @Override
  protected void onActiveUpdate(float delta, CoreGame game) {

  }

  @Override
  public void onDelayedAction(CoreGame game) {

  }

  @Override
  protected void onCompleted(CoreGame game) {

  }

  @Override
  public void updateTimers(float delta) {

  }

  @Override
  public void init(CoreGame game) {

  }

  @Override
  public AbilityAnimationConfig animationConfig() {
    return AbilityAnimationConfig.of();
  }

  @Override
  public void deactivate() {

  }

  @Override
  public void onCreatureHit(CreatureId creatureId, CoreGame game) {

  }

  @Override
  public void onSelfCreatureHit(CoreGame game) {

  }

  @Override
  public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

  }

  @Override
  public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

  }

  @Override
  public Float getDamage(CoreGame game) {
    return 0f;
  }

  @Override
  protected boolean isWeaponAttack() {
    return false;
  }

  @Override
  public Float getLevelScaling(CoreGame game) {
    return 1.0f;
  }

  @Override
  public Map<Integer, Float> levelScalings() {
    return new ConcurrentSkipListMap<>();
  }

  @Override
  public Integer getSkillLevel(CoreGame game) {
    return 1;
  }

  @Override
  public Float getStunDuration() {
    return 0f;
  }

  @Override
  public boolean usesEntityModel() {
    return false;
  }

  @Override
  public boolean canBeDeactivated() {
    return true;
  }

  @Override
  public boolean isDamagingSkillNotAllowedWhenActive() {
    return false;
  }

  @Override
  public boolean canStun() {
    return false;
  }

  @Override
  public boolean isAbleToChainAfterCreatureDeath() {
    return false;
  }

  @Override
  public boolean isBlockable() {
    return true;
  }

  @Override
  public int maximumCreatureHitCount(CreatureId creatureId, CoreGame game) {
    return 0;
  }
}
