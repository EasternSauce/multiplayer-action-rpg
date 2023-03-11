package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.game.data.TeleportInfo;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class TeleportDestination extends Ability {
    AbilityParams params;

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    void onAbilityStarted(MyGdxGame game) {
        game.creaturesToTeleport()
            .add(TeleportInfo.of(params().creatureId(), params().pos(), params().areaId(), params().areaId()));
    }

    @Override
    void onDelayedAction(MyGdxGame game) {

    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    void onChannelUpdate(CreaturePosRetrievable game) {

    }

    @Override
    void onActiveUpdate(AbilityUpdateable game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(MyGdxGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos, MyGdxGame game) {

    }

    @Override
    public void onAbilityHit(AbilityId otherAbilityId, MyGdxGame game) {

    }

    public static TeleportDestination of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        Creature creature = game.getCreature(abilityParams.creatureId());

        Vector2 teleportPos = TeleportDestination.calculatePos(creature.params()
                                                                       .pos()
                                                                       .add(abilityParams.dirVector()),
                                                               creature.params().pos(),
                                                               creature.params().areaId(),
                                                               game);

        TeleportDestination ability = TeleportDestination.of();
        ability.params =
                abilityParams.width(4.5f)
                             .height(4.5f)
                             .channelTime(0f)
                             .activeTime(0.5f)
                             .textureName("blast")
                             .baseDamage(0f)
                             .isChannelAnimationLooping(false)
                             .isActiveAnimationLooping(false)
                             .rotationShift(0f)
                             .pos(teleportPos)
                             .chainToPos(teleportPos);


        return ability;
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 creaturePos, AreaId areaId, MyGdxGame game) {
        Vector2 vectorTowards = creaturePos.vectorTowards(pos);

        float maxRange = 14f;
        Vector2 destinationPos;
        if (vectorTowards.len() > maxRange) {
            destinationPos = creaturePos.add(vectorTowards.normalized().multiplyBy(maxRange));
        }
        else {
            destinationPos = pos;
        }

        if (!game.physics().physicsWorlds().get(areaId).isLineOfSight(creaturePos, destinationPos)) {
            return creaturePos;
        }

        return destinationPos;
    }
}
