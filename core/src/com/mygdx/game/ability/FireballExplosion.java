package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
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
public class FireballExplosion extends Ability {
    AbilityParams params;


    @Override
    public AbilityType type() {
        return AbilityType.FIREBALL_EXPLOSION;
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
    void onUpdatePosition(GameState gameState) {

    }

    @Override
    void onChannelUpdate(GameState gameState) {

    }

    @Override
    void onActiveUpdate(GameState gameState) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }

    public static FireballExplosion of(AbilityId abilityId,
                                       AreaId areaId,
                                       CreatureId creatureId,
                                       Vector2 pos,
                                       Vector2 dirVector,
                                       Set<CreatureId> creaturesAlreadyHit) {
        FireballExplosion ability = FireballExplosion.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(9f)
                                      .height(9f)
                                      .channelTime(0f)
                                      .activeTime(0.35f)
                                      .textureName("explosion")
                                      .creatureId(creatureId)
                                      .damage(28f)
                                      .pos(pos)
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .manaCost(22f)
                                      .staminaCost(0f)
                                      .cooldown(0f)
                                      .performableByCreature(false)
                                      .dirVector(dirVector)
                                      .cooldown(0f)
                                      .isChannelAnimationLooping(false)
                                      .isActiveAnimationLooping(false)
                                      .attackWithoutMoving(true)
                                      .creaturesAlreadyHit(new HashSet<>())
                                      .rotationShift(0f);

        return ability;
    }

}
