package com.easternsauce.actionrpg.model.ability.teleport;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityParams;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class EnemyTeleportDestination extends TeleportDestinationBase {
    public static EnemyTeleportDestination of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getCreature(abilityParams.getCreatureId());

        Vector2 teleportPos = EnemyTeleportDestination.calculatePos(
            creature.getParams().getPos().add(abilityParams.getDirVector()),
            creature.getParams().getPos(),
            creature.getParams().getAreaId(),
            game
        );

        EnemyTeleportDestination ability = EnemyTeleportDestination.of();
        ability.params = abilityParams
            .setWidth(4.5f)
            .setHeight(4.5f)
            .setChannelTime(0f)
            .setActiveTime(1f)
            .setTextureName("warp")
            .setBaseDamage(0f)
            .setChannelAnimationLooping(false)
            .setActiveAnimationLooping(false)
            .setPos(teleportPos)
            .setChainToPos(teleportPos);

        return ability;
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 creaturePos, AreaId areaId, CoreGame game) {
        Vector2 vectorTowards = creaturePos.vectorTowards(pos);

        float maxRange = 30f;
        Vector2 destinationPos;
        if (vectorTowards.len() > maxRange) {
            destinationPos = creaturePos.add(vectorTowards.normalized().multiplyBy(maxRange));
        } else {
            destinationPos = pos;
        }

        if (!game.isLineBetweenPointsUnobstructedByTerrain(areaId, creaturePos, destinationPos)) {
            return creaturePos;
        }

        return destinationPos;
    }
}
