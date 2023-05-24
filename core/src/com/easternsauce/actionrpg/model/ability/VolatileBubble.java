package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class VolatileBubble extends Projectile {

    AbilityParams params;

    public static VolatileBubble of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        VolatileBubble ability = VolatileBubble.of();
        ability.params = abilityParams
            .setWidth(1.5f)
            .setHeight(1.5f)
            .setChannelTime(0f)
            .setActiveTime(30f)
            .setTextureName("bubble")
            .setBaseDamage(32f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(true)
            .setRotationShift(0f)
            .setDelayedActionTime(0.001f)
            .setSpeed(10f);

        return ability;
    }

    @Override
    public Boolean isRanged() {
        return true;
    }

    @Override
    public void updatePosition(CoreGame game) {

    }

    @Override
    public void onAbilityStarted(CoreGame game) {

    }

    @Override
    public void onDelayedAction(CoreGame game) {

    }

    @Override
    protected void onAbilityCompleted(CoreGame game) {
        float baseAngle = getParams().getDirVector().angleDeg();

        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this, AbilityType.ICE_SPEAR, getParams().getPos(), params.getDirVector(), null, null, game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.ICE_SPEAR,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle + 45f),
                                 null,
                                 null,
                                 game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.ICE_SPEAR,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle + 90f),
                                 null,
                                 null,
                                 game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.ICE_SPEAR,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle + 135f),
                                 null,
                                 null,
                                 game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.ICE_SPEAR,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle + 180f),
                                 null,
                                 null,
                                 game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.ICE_SPEAR,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle + 225f),
                                 null,
                                 null,
                                 game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.ICE_SPEAR,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle + 270f),
                                 null,
                                 null,
                                 game);
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.ICE_SPEAR,
                                 getParams().getPos(),
                                 params.getDirVector().withSetDegAngle(baseAngle + 315f),
                                 null,
                                 null,
                                 game);
    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {
        deactivate();
    }

    @Override
    public void onThisCreatureHit(CoreGame game) {

    }

    @Override
    public void onTerrainHit(Vector2 abilityPos, Vector2 tilePos) {
        deactivate();
    }

    @Override
    public void onOtherAbilityHit(AbilityId otherAbilityId, CoreGame game) {

    }

    @Override
    protected boolean isWeaponAttack() {
        return false;
    }
}
