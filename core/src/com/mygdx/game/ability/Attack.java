package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
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
    void onAbilityStarted(MyGdxGame game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {

    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    protected void onUpdatePosition(GameState gameState) {
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

        Creature creature = gameState.creatures().get(params().creatureId());

        if (creature != null) {
            Vector2 creaturePos = creature.params().pos();

            float attackRectX = attackShiftX + creaturePos.x();
            float attackRectY = attackShiftY + creaturePos.y();

            params().pos(Vector2.of(attackRectX, attackRectY));
            params().rotationAngle(theta);
        }
    }

    @Override
    void onChannelUpdate(GameState gameState) {
        if (isPositionManipulated()) {
            onUpdatePosition(gameState);
        }

    }

    @Override
    void onActiveUpdate(GameState gameState) {
        if (isPositionManipulated()) {
            onUpdatePosition(gameState);
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
                                      .manaCost(22f)
                                      .staminaCost(0f)
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
