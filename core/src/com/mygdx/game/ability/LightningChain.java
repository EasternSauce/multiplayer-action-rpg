package com.mygdx.game.ability;

import com.mygdx.game.game.CreatureAbilityChainable;
import com.mygdx.game.game.CreatureAbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningChain extends Ability {
    AbilityParams params;


    @Override
    public AbilityType type() {
        return AbilityType.LIGHTNING_CHAIN;
    }

    @Override
    void onAbilityStarted(CreatureAbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(CreatureAbilityChainable game) {

    }

    @Override
    void onAbilityCompleted(CreatureAbilityChainable game) {

    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    @Override
    void onChannelUpdate(CreaturePosRetrievable game) {

    }

    @Override
    void onActiveUpdate(CreaturePosRetrievable game) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }

    public static LightningChain of(AbilityId abilityId,
                                    AreaId areaId,
                                    CreatureId creatureId,
                                    Vector2 pos,
                                    Vector2 dirVector,
                                    Set<CreatureId> creaturesAlreadyHit,
                                    Vector2 chainFromPos) {
        LightningChain ability = LightningChain.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1f)
                                      .height(chainFromPos.distance(pos))
                                      .channelTime(0f)
                                      .activeTime(0.4f)
                                      .textureName("lightning_chain")
                                      .creatureId(creatureId)
                                      .damage(0f)
                                      .isActiveAnimationLooping(true)
                                      .attackWithoutMoving(true)
                                      .pos(LightningChain.calculatePos(pos, chainFromPos))
                                      .rotationAngle(LightningChain.calculateRotationAngle(pos, chainFromPos))
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .manaCost(0f)
                                      .staminaCost(0f)
                                      .cooldown(0f)
                                      .performableByCreature(false)
                                      .inactiveBody(true)
                                      .dirVector(dirVector)
                                      .rotationShift(90f);

        return ability;
    }

    private static Float calculateRotationAngle(Vector2 pos, Vector2 chainFromPos) {
        Vector2 chainDirVector = pos.vectorTowards(chainFromPos);

        return chainDirVector.angleDeg();
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 chainFromPos) {
        Vector2 chainDirVector = pos.vectorTowards(chainFromPos);

        float attackShiftX = chainDirVector.normalized().x() * chainFromPos.distance(pos) / 2;
        float attackShiftY = chainDirVector.normalized().y() * chainFromPos.distance(pos) / 2;

        return Vector2.of(pos.x() + attackShiftX, pos.y() + attackShiftY);

    }
}
