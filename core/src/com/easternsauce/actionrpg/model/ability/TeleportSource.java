package com.easternsauce.actionrpg.model.ability;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.effect.CreatureEffect;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class TeleportSource extends Ability {
    AbilityParams params;

    public static TeleportSource of(AbilityParams abilityParams, @SuppressWarnings("unused") CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(abilityParams.getCreatureId());

        TeleportSource ability = TeleportSource.of();
        ability.params = abilityParams
            .setWidth(4.5f)
            .setHeight(4.5f)
            .setChannelTime(0f)
            .setActiveTime(3f)
            .setTextureName("warp")
            .setBaseDamage(0f)
            .setIsChannelAnimationLooping(false)
            .setIsActiveAnimationLooping(false)
            .setRotationShift(0f)
            .setPos(creature.getParams().getPos())
            .setDelayedActionTime(0.3f);

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
        Creature creature = game.getGameState().accessCreatures().getCreature(getParams().getCreatureId());
        creature.applyEffect(CreatureEffect.SELF_STUN, 0.5f, game);
    }

    @Override
    public void onDelayedAction(CoreGame game) {
        game
            .getGameState()
            .accessAbilities()
            .chainAnotherAbility(this,
                                 AbilityType.TELEPORT_DESTINATION,
                                 getParams().getPos(),
                                 getParams().getDirVector(),
                                 null,
                                 null,
                                 game);
    }

    @Override
    protected void onAbilityCompleted(CoreGame game) {

    }

    @Override
    public void onChannelUpdate(CoreGame game) {

    }

    @Override
    protected void onActiveUpdate(float delta, CoreGame game) {

    }

    @Override
    public void onCreatureHit(CreatureId creatureId, CoreGame game) {

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

    @Override
    public boolean isCanBeDeactivated() {
        return true;
    }

    @Override
    public boolean isCanStun() {
        return false;
    }
}
