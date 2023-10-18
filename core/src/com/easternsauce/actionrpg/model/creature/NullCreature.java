package com.easternsauce.actionrpg.model.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyParams;
import com.easternsauce.actionrpg.model.id.CreatureId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.Skill;
import com.easternsauce.actionrpg.model.skill.SkillType;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.model.util.WorldDirection;
import com.easternsauce.actionrpg.renderer.animationconfig.CreatureAnimationConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class NullCreature extends Creature {
  private static NullCreature instance;

  public static NullCreature of() {
    if (instance == null) {
      instance = new NullCreature();
    }
    return instance;
  }

  @Override
  public CreatureParams getParams() {
    return CreatureParams.of().setEnemyParams(EnemyParams.of()).setPos(Vector2.of(0, 0));
  }

  @Override
  public void update(float delta, CoreGame game) {

  }

  @Override
  protected void updateEnemyTimers(float delta) {

  }

  @Override
  public void updateTimers(float delta) {

  }

  @Override
  public WorldDirection getFacingDirection(CoreGame game) {
    return WorldDirection.RIGHT;
  }

  @Override
  public Integer getCapability() {
    return 1;
  }

  @Override
  public CreatureAnimationConfig getAnimationConfig() {
    return CreatureAnimationConfig.of();
  }

  @Override
  public void updateAutoControls(CoreGame game) {
  }

  @Override
  public void stopMoving() {
  }

  @Override
  public void moveTowards(Vector2 pos) {
  }

  @Override
  public void takeLifeDamage(float damage, Vector2 contactPoint, CoreGame game) {
  }

  @Override
  public void onDeath(Creature attackerCreature, CoreGame game) {
  }

  @Override
  public CreatureId getId() {
    return null;
  }

  @Override
  public Map<SkillType, Integer> availableSkills() {
    return new ConcurrentSkipListMap<>();
  }

  @Override
  public boolean isMeleeAbilityShielded(Ability ability, CoreGame game) {
    return false;
  }

  @Override
  public void onAbilityPerformed(Ability ability) {
  }

  @Override
  public boolean canPerformSkill(Skill skill, CoreGame game) {
    return false;
  }

  @Override
  public boolean isAlive() {
    return false;
  }

  @Override
  public void onPerformSkill(Skill skill) {
  }

  @Override
  protected void takeStaminaDamage(Float staminaCost) {
  }

  @Override
  protected void takeManaDamage(Float manaCost) {
  }

  @Override
  public void onAfterPerformSkill() {
  }

  @Override
  public float getTimeSinceStarted(CreatureEffect effect, CoreGame game) {
    return 0f;
  }

  @Override
  public void applyEffect(CreatureEffect effect, float duration, CoreGame game) {
  }

  @Override
  public void onKillEffect() {
  }

  @Override
  public Item getCurrentWeapon() {
    return null;
  }

  @Override
  public boolean isStunned(CoreGame game) {
    return false;
  }

  @Override
  public boolean isEffectActive(CreatureEffect effect, CoreGame game) {
    return false;
  }

  @Override
  public void onBeingHit(Ability ability, CoreGame game) {

  }

  @Override
  public boolean isCurrentlyActive(CoreGame game) {
    return false;
  }

  @Override
  public int getNextRandom() {
    return 0;
  }

  @Override
  public boolean isNull() {
    return true;
  }
}
