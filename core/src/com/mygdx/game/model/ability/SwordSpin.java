package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class SwordSpin extends Ability {

    AbilityParams params;

    public static SwordSpin of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        SwordSpin ability = SwordSpin.of();
        ability.params =
                abilityParams
                        .width(2.2f)
                        .height(2.2f)
                        .channelTime(0f)
                        .activeTime(5f)
                        .range(2.2f)
                        .textureName("sword")
                        .baseDamage(13f)
                        .isChannelAnimationLooping(false)
                        .isActiveAnimationLooping(false)
                        .rotationShift(0f)
                        .dirVector(abilityParams.dirVector().rotateDeg(90));
        return ability;
    }

    @Override
    public Boolean isPositionUpdated() {
        return true;
    }

    @Override
    public Boolean isRanged() {
        return false;
    }

    @Override
    void onAbilityStarted(AbilityUpdatable game) {

    }

    @Override
    void onDelayedAction(AbilityUpdatable game) {

    }

    @Override
    void onAbilityCompleted(AbilityUpdatable game) {

    }

    @Override
    protected void onUpdatePosition(AbilityUpdatable game) {
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
    void onChannelUpdate(AbilityUpdatable gameState) {
        if (isPositionUpdated()) {
            onUpdatePosition(gameState);
        }

    }

    @Override
    void onActiveUpdate(AbilityUpdatable game) {
        if (isPositionUpdated()) {
            onUpdatePosition(game);
        }
        params().dirVector(params().dirVector().rotateDeg(-7));

        Set<CreatureId> creaturesHitRemove = new HashSet<>();

        params().creaturesAlreadyHit().forEach((creatureId, time) -> {
            if (time < params().stateTimer().time() - 0.4f) {
                creaturesHitRemove.add(creatureId);
            }
        });

        creaturesHitRemove.forEach(creatureId -> params().creaturesAlreadyHit().remove(creatureId));
    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onThisCreatureHit(GameUpdatable game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, GameUpdatable game) {

    }
}
