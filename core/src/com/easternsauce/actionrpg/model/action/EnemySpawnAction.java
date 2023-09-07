package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Enemy;
import com.easternsauce.actionrpg.model.creature.EnemyTemplate;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPointId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemySpawnAction extends GameStateAction {
  private CreatureId creatureId;
  private AreaId areaId;
  private EnemyRallyPointId enemyRallyPointId;
  private EnemyTemplate enemyTemplate;

  public void applyToGame(CoreGame game) {
    Creature enemy = createNewEnemy(game);

    game.getAllCreatures().put(creatureId, enemy);

    game.getEventProcessor().getCreatureModelsToBeCreated().add(creatureId);
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return game.getCreature(creatureId);
  }

  @SuppressWarnings("PointlessArithmeticExpression")
  private Creature createNewEnemy(CoreGame game) {
    EnemyRallyPoint enemyRallyPoint = game.getGameState().getEnemyRallyPoint(enemyRallyPointId);

    Vector2 rallyPointPos = enemyRallyPoint.getRallyPointInfo().getPos();

    Vector2 pos = Vector2.of(rallyPointPos.getX() + 1f * game.getGameState().getRandomGenerator().nextFloat(), rallyPointPos.getY() + 1f * game.getGameState().getRandomGenerator().nextFloat());

    int rngSeed = game.getGameState().getRandomGenerator().nextInt();

    return Enemy.of(creatureId, areaId, pos, enemyTemplate, enemyRallyPointId, rngSeed);
  }
}
