package com.easternsauce.actionrpg.model.enemyrallypoint;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.action.EnemySpawnAction;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.EnemyTemplate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor(staticName = "of")
@AllArgsConstructor(staticName = "of")
@Data
public class EnemyRallyPoint {
    EnemyRallyPointId id;
    AreaId areaId;
    EnemyRallyPointInfo rallyPointInfo;

    public void update(CoreGame game) {
        long enemiesAliveCount = game
            .getGameState()
            .accessCreatures()
            .getCreatures()
            .values()
            .stream()
            .filter(creature -> creature.getParams().getEnemyRallyPointId() != null &&
                creature.getParams().getEnemyRallyPointId().getValue().equals(id.getValue()) &&
                creature.isAlive())
            .count();

        AtomicReference<EnemyTemplate> randomEnemyTemplate = new AtomicReference<>(null);

        AtomicReference<Float> totalWeight = new AtomicReference<>((float) 0);

        rallyPointInfo.getEnemyTemplateWeights().forEach((enemyTemplate, weight) -> totalWeight.set(totalWeight.get() +
            weight));

        AtomicReference<Float> randValue = new AtomicReference<>(Math.abs(game
            .getGameState()
            .getRandomGenerator()
            .nextFloat()) * totalWeight.get());

        rallyPointInfo.getEnemyTemplateWeights().forEach((enemyTemplate, weight) -> {
            if (randomEnemyTemplate.get() == null && randValue.get() < weight) {
                randomEnemyTemplate.set(enemyTemplate);
            }
            randValue.updateAndGet(value -> value - weight);
        });

        System.out.println(rallyPointInfo.getEnemiesTotal() + " " + enemiesAliveCount);
        if (rallyPointInfo.getEnemiesTotal() - enemiesAliveCount > 0) {
            System.out.println("spawning enemy");
            CreatureId enemyId = CreatureId.of("Enemy_" + (int) (Math.random() * 10000000));

            EnemySpawnAction action = EnemySpawnAction.of(enemyId, areaId, id, randomEnemyTemplate.get());

            game.getGameState().scheduleServerSideAction(action);
        }
    }
}
