package com.mygdx.game.model.ability;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningChain extends Ability {
    AbilityParams params;

    public static LightningChain of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        LightningChain ability = LightningChain.of();
        ability.params = abilityParams
            .setWidth(1f)
            .setHeight(abilityParams.getChainFromPos().distance(abilityParams.getChainToPos()))
            .setChannelTime(0f)
            .setActiveTime(0.4f)
            .setTextureName("lightning_chain")
            .setBaseDamage(0f)
            .setIsActiveAnimationLooping(true)
            .setAttackWithoutMoving(true)
            .setPos(LightningChain.calculatePos(abilityParams.getChainToPos(), abilityParams.getChainFromPos()))
            .setRotationAngle(LightningChain.calculateRotationAngle(abilityParams.getChainToPos(),
                                                                    abilityParams.getChainFromPos()))
            .setIsSkipCreatingBody(true)
            .setRotationShift(90f);

        return ability;
    }

    @Override
    public void init(CoreGame game) {
        getParams().setState(AbilityState.CHANNEL);
        getParams().getStateTimer().restart();

        // overriding like this is bug-inducing, TODO: FIX

    }

    private static Float calculateRotationAngle(Vector2 pos, Vector2 chainFromPos) {
        Vector2 chainDirVector = pos.vectorTowards(chainFromPos);

        return chainDirVector.angleDeg();
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 chainFromPos) {
        Vector2 chainDirVector = pos.vectorTowards(chainFromPos);

        float attackShiftX = chainDirVector.normalized().getX() * chainFromPos.distance(pos) / 2;
        float attackShiftY = chainDirVector.normalized().getY() * chainFromPos.distance(pos) / 2;

        return Vector2.of(pos.getX() + attackShiftX, pos.getY() + attackShiftY);

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

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }
}
