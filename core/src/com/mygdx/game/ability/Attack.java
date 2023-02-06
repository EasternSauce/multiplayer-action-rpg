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

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class Attack extends Ability {

    AbilityParams params;

    @Override
    public Boolean isPositionManipulated() {
        return true;
    }

    @Override
    public AbilityType type() {
        return AbilityType.SLASH;
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
    protected void onUpdatePosition(CreaturePosRetrievable game) {
        Vector2 dirVector;
        if (params().dirVector().len() <= 0) {
            dirVector = Vector2.of(1, 0);
        }
        else {
            dirVector = params().dirVector();
        }

        Float theta = dirVector.angleDeg();

        float attackShiftX = dirVector.normalized().x() * params().range();
        float attackShiftY = dirVector.normalized().y() * params().range();

        Vector2 pos = game.getCreaturePos(params().creatureId());

        if (pos != null) {
            float attackRectX = attackShiftX + pos.x();
            float attackRectY = attackShiftY + pos.y();

            params().pos(Vector2.of(attackRectX, attackRectY));
            params().rotationAngle(theta);
        }
    }

    @Override
    void onChannelUpdate(CreaturePosRetrievable gameState) {
        if (isPositionManipulated()) {
            onUpdatePosition(gameState);
        }

    }

    @Override
    void onActiveUpdate(CreaturePosRetrievable game) {
        if (isPositionManipulated()) {
            onUpdatePosition(game);
        }

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }

    public static Attack of(AbilityId abilityId,
                            AreaId areaId,
                            CreatureId creatureId,
                            Vector2 pos,
                            Vector2 dirVector,
                            Set<CreatureId> creaturesAlreadyHit) {
        Attack ability = Attack.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(2f)
                                      .height(2f)
                                      .channelTime(0f)
                                      .activeTime(0.3f)
                                      .range(1.8f)
                                      .textureName("slash")
                                      .creatureId(creatureId)
                                      .damage(22f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .manaCost(0f)
                                      .staminaCost(22f)
                                      .cooldown(0.7f)
                                      .performableByCreature(true)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(false)
                                      .creaturesAlreadyHit(new HashSet<>())
                                      .rotationShift(0f);
        return ability;
    }
}
