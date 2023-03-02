package com.mygdx.game.model.ability;

import com.mygdx.game.game.AbilityUpdateable;
import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.game.MyGdxGame;
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
    void onAbilityStarted(AbilityUpdateable game) {

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
    public void onTerrainHit() {

    }


    public static LightningChain of(AbilityParams abilityParams, @SuppressWarnings("unused") MyGdxGame game) {
        LightningChain ability = LightningChain.of();
        ability.params = abilityParams
                .width(1f)
                .height(abilityParams.chainFromPos()
                                     .distance(abilityParams.chainToPos()))
                .channelTime(0f)
                .activeTime(0.4f)
                .textureName("lightning_chain")
                .damage(0f)
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
