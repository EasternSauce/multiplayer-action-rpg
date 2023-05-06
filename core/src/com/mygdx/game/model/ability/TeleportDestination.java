package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.TeleportEvent;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class TeleportDestination extends Ability {
    AbilityParams params;

    public static TeleportDestination of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(abilityParams.getCreatureId());

        Vector2 teleportPos =
                TeleportDestination.calculatePos(creature.getParams().getPos().add(abilityParams.getDirVector()),
                                                 creature.getParams().getPos(),
                                                 creature.getParams().getAreaId(),
                                                 game);

        TeleportDestination ability = TeleportDestination.of();
        ability.params = abilityParams.setWidth(4.5f)
                                      .setHeight(4.5f)
                                      .setChannelTime(0f)
                                      .setActiveTime(0.5f)
                                      .setTextureName("blast")
                                      .setBaseDamage(0f)
                                      .setIsChannelAnimationLooping(false)
                                      .setIsActiveAnimationLooping(false)
                                      .setRotationShift(0f)
                                      .setPos(teleportPos)
                                      .setChainToPos(teleportPos);


        return ability;
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 creaturePos, AreaId areaId, CoreGame game) {
        Vector2 vectorTowards = creaturePos.vectorTowards(pos);

        float maxRange = 14f;
        Vector2 destinationPos;
        if (vectorTowards.len() > maxRange) {
            destinationPos = creaturePos.add(vectorTowards.normalized().multiplyBy(maxRange));
        }
        else {
            destinationPos = pos;
        }

        if (!game.isLineOfSight(areaId, creaturePos, destinationPos)) {
            return creaturePos;
        }

        return destinationPos;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(CoreGame game) {

    }

    @Override
    void onAbilityStarted(CoreGame game) {
        game.addTeleportEvent(TeleportEvent.of(getParams().getCreatureId(),
                                               getParams().getPos(),
                                               getParams().getAreaId(),
                                               getParams().getAreaId()));
    }

    @Override
    void onDelayedAction(CoreGame game) {

    }

    @Override
    void onAbilityCompleted(CoreGame game) {

    }


    @Override
    void onChannelUpdate(CoreGame game) {

    }

    @Override
    void onActiveUpdate(CoreGame game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }
}
