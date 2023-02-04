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
public class LightningSpark extends Ability {

    AbilityParams params;


    @Override
    void onAbilityStarted(MyGdxGame game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {
        // find closest enemy, and if they are within distance, and havent been hit yet, then start node over them
        Set<CreatureId> excluded = new HashSet<>(params().creaturesAlreadyHit());
        excluded.add(params().creatureId());

        CreatureId creatureId = game.aliveCreatureClosestTo(params().pos(), 10f, excluded);


        if (creatureId != null && game.gameState().creatures().containsKey(creatureId)) {
            Creature chainToCreature = game.gameState().creatures().get(creatureId);
            chainToCreature.handleBeingAttacked(25f, params().creatureId());

            game.chainAbility(this, AbilityType.LIGHTNING_CHAIN, chainToCreature.params().pos(), null);

            game.chainAbility(this, AbilityType.LIGHTNING_NODE, chainToCreature.params().pos(), creatureId);
        }
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

    public static LightningSpark of(AbilityId abilityId,
                                    AreaId areaId,
                                    CreatureId creatureId,
                                    Vector2 pos,
                                    Vector2 dirVector,
                                    Set<CreatureId> creaturesAlreadyHit,
                                    Vector2 creaturePos) {
        LightningSpark ability = LightningSpark.of();
        ability.params = AbilityParams.of()
                                      .id(abilityId)
                                      .areaId(areaId)
                                      .width(3f)
                                      .height(3f)
                                      .channelTime(0f)
                                      .activeTime(0.4f)
                                      .textureName("lightning")
                                      .creatureId(creatureId)
                                      .damage(0f)
                                      .isActiveAnimationLooping(true)
                                      .attackWithoutMoving(true)
                                      .pos(LightningSpark.calculatePos(pos, creaturePos))
                                      .creaturesAlreadyHit(creaturesAlreadyHit)
                                      .manaCost(20f)
                                      .staminaCost(0f)
                                      .cooldown(1.0f)
                                      .performableByCreature(true)
                                      .inactiveBody(true)
                                      .dirVector(dirVector);
        return ability;
    }

    private static Vector2 calculatePos(Vector2 pos, Vector2 creaturePos) {
        Vector2 vectorTowards = creaturePos.vectorTowards(pos);

        float maxRange = 5f;
        if (vectorTowards.len() > maxRange) {
            return creaturePos.add(vectorTowards.normalized().multiplyBy(maxRange));
        }
        return pos;
    }

}
