package com.easternsauce.actionrpg.model.creature.enemy.autocontrols;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.Enemy;
import com.easternsauce.actionrpg.model.id.EntityId;

public abstract class EnemyRetriever {
  public Creature getEnemy(CoreGame game) {
    Creature creature = game.getCreature(getEnemyId());

    if (!(creature instanceof Enemy)) throw new RuntimeException("enemy retriever called on non-enemy object");

    return creature;
  }

  protected abstract EntityId<Creature> getEnemyId();
}
