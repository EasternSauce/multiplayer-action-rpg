package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdatable;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningChain extends Ability {
    AbilityParams params;


    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public Boolean isPositionCalculated() {
        return true;
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
    void onUpdatePosition(AbilityUpdatable game) {

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
    public void onThisCreatureHit(AbilityUpdatable game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {

    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, AbilityUpdatable game) {

    }

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
                .inactiveBody(true)
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
