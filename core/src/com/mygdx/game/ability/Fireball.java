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
public class Fireball extends Projectile {
    AbilityParams params;


    @Override
    public void onCreatureHit() {
        deactivate();
        // stop moving, then start explode ability
    }

    @Override
    public void onTerrainHit() {
        deactivate();
        // stop moving, then start explode ability
    }

    @Override
    protected void onActiveUpdate(CreaturePosRetrievable game) {
        //projectile speeds up over time
        if (params().speed() != null) {
            params().velocity(params().dirVector().normalized().multiplyBy(params().speed()));
        }
        params().rotationAngle(params().dirVector().angleDeg());

        if (params().stateTimer().time() < 2f) {
            params().speed(5f + (params().stateTimer().time() / 2f) * 40f);
        }
        else {
            params().speed(45f);
        }
    }

    @Override
    public AbilityType type() {
        return AbilityType.FIREBALL;
    }

    @Override
    void onAbilityStarted(CreatureAbilityUpdateable game) {

    }

    @Override
    void onDelayedAction(CreatureAbilityChainable game) {

    }

    @Override
    protected void onAbilityCompleted(CreatureAbilityChainable game) {
        game.chainAbility(this, AbilityType.FIREBALL_EXPLOSION, null, null);
    }

    @Override
    void onUpdatePosition(CreaturePosRetrievable game) {

    }

    public static Fireball of(AbilityId abilityId,
                              AreaId areaId,
                              CreatureId creatureId,
                              Vector2 pos,
                              Vector2 dirVector,
                              Set<CreatureId> creaturesAlreadyHit) {
        Fireball ability = Fireball.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(1.5f)
                                      .height(1.5f)
                                      .channelTime(0f)
                                      .activeTime(30f)
                                      .textureName("fireball")
                                      .creatureId(creatureId)
                                      .damage(40f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .manaCost(22f)
                                      .staminaCost(0f)
                                      .cooldown(0.35f)
                                      .performableByCreature(true)
                                      .dirVector(dirVector)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(true)
                                      .creaturesAlreadyHit(new HashSet<>())
                                      .rotationShift(0f)
                                      .delayedActionTime(0.001f);


        return ability;
    }
}
