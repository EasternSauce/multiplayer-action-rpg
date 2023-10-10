package com.easternsauce.actionrpg.model.creature.enemy;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum EnemyType {
  SKELETON("skeleton"), ARCHER("undead_adventurer"),

  MAGE("black_mage"), MINOS("minos"), SERPENT("serpent"), SLUDGE("sludge"), WOLF("wolf"), RAT("rat"), SPIDER(
    "spider"), BABY_SPIDER("baby_spider"), TAURUS("taurus");

  public String textureName;

  EnemyType(String textureName) {
    this.textureName = textureName;
  }
}
