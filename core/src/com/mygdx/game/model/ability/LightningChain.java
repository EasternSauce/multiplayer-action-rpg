package com.mygdx.game.model.ability;

import com.mygdx.game.game.interface_.AbilityUpdatable;
import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningChain extends Ability {
    AbilityParams params;

    public static LightningChain of(AbilityParams abilityParams, @SuppressWarnings("unused") AbilityUpdatable game) {
        LightningChain ability = LightningChain.of();
        ability.params = abilityParams
                .width(1f)
                .height(abilityParams.chainFromPos()
                                     .distance(abilityParams.chainToPos()))
                .channelTime(0f)
                .activeTime(0.4f)
                .textureName("lightning_chain")
                .baseDamage(0f)
                .isActiveAnimationLooping(true)
                .attackWithoutMoving(true)
                .pos(LightningChain.calculatePos(abilityParams.chainToPos(),
                                                 abilityParams.chainFromPos()))
                .rotationAngle(LightningChain.calculateRotationAngle(abilityParams.chainToPos(),
                                                                     abilityParams.chainFromPos()))
                .isSkipCreatingBody(true)
                .rotationShift(90f);

        return ability;
    }

    @Override
    public void init(GameActionApplicable game) {

        params().state(AbilityState.CHANNEL);
        params().stateTimer().restart();

        Creature creature = game.getCreature(params().creatureId());

        if (creature != null) {
            if (params().chainToPos() != null) {
                params().pos(params().chainToPos());
            }
            else {
                params().pos(creature.params().pos());
            }
        }

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

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(AbilityUpdatable game) {

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
    void onChannelUpdate(AbilityUpdatable game) {

    }

    @Override
    void onActiveUpdate(AbilityUpdatable game) {

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
