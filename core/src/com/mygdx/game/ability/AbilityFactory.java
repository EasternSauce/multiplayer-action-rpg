package com.mygdx.game.ability;

import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;

import java.util.Set;


public class AbilityFactory {

    public static Ability produceAbility(AbilityType abilityType,
                                         AbilityId abilityId,
                                         AreaId areaId,
                                         CreatureId creatureId,
                                         GameState gameState,
                                         Vector2 dirVector,
                                         Vector2 chainFromPos,
                                         Vector2 pos,
                                         Set<CreatureId> creaturesAlreadyHit) {
        if (abilityType == AbilityType.SLASH) {
            Ability ability = Attack.of(AbilityParams.of(abilityId, areaId, 2f, 2f, 0f, 0.3f, 1.8f, "slash"));
            ability.params().creatureId(creatureId);
            ability.params().damage(22f);
            ability.params().pos(pos);
            ability.params().creaturesAlreadyHit(creaturesAlreadyHit);
            ability.start(dirVector, gameState, abilityType);

            return ability;
        }

        if (abilityType == AbilityType.FIREBALL) {
            Ability ability = Fireball.of(AbilityParams.of(abilityId, areaId, 1.5f, 1.5f, 0f, 30f, null, "fireball"));
            ability.params().creatureId(creatureId);
            ability.params().damage(15f);
            ability.params().isActiveAnimationLooping(true);
            ability.params().pos(pos);
            ability.params().creaturesAlreadyHit(creaturesAlreadyHit);
            ability.start(dirVector, gameState, abilityType);


            return ability;
        }

        if (abilityType == AbilityType.FIREBALL_EXPLOSION) {
            Ability ability =
                    FireballExplosion.of(AbilityParams.of(abilityId, areaId, 9f, 9f, 0f, 0.35f, null, "explosion"));
            ability.params().creatureId(creatureId);
            ability.params().damage(20f);
            ability.params().isActiveAnimationLooping(false);
            ability.params().attackWithoutMoving(true);
            ability.params().pos(pos);
            ability.params().creaturesAlreadyHit(creaturesAlreadyHit);
            ability.start(dirVector, gameState, abilityType);

            return ability;
        }

        if (abilityType == AbilityType.LIGHTNING_SPARK) {
            Ability ability = LightningSpark.of(AbilityParams.of(abilityId, areaId, 3f, 3f, 0f, 0.4f, null,
                                                                 // TODO: move range out of constructor
                                                                 "lightning"));
            ability.params().creatureId(creatureId);
            ability.params().damage(0f);
            ability.params().isActiveAnimationLooping(true);
            ability.params().attackWithoutMoving(true);

            Creature creature = gameState.creatures().get(ability.params().creatureId());

            Vector2 vectorTowards = creature.params().pos().vectorTowards(pos);

            float maxRange = 5f;
            if (vectorTowards.len() > maxRange) {
                ability.params().pos(creature.params().pos().add(vectorTowards.normalized().multiplyBy(maxRange)));
            }
            else {
                ability.params().pos(pos);
            }

            ability.params().creaturesAlreadyHit(creaturesAlreadyHit);
            ability.params().inactiveBody(true);
            ability.start(dirVector, gameState, abilityType); // set pos from mouse pos

            return ability;
        }

        if (abilityType == AbilityType.LIGHTNING_NODE) {
            Ability ability = LightningNode.of(AbilityParams.of(abilityId, areaId, 3f, 3f, 0f, 0.4f, null,
                                                                // TODO: move range out of constructor
                                                                "lightning"));
            ability.params().creatureId(creatureId);
            ability.params().damage(0f);
            ability.params().isActiveAnimationLooping(true);
            ability.params().attackWithoutMoving(true);
            ability.params().pos(pos);
            ability.params().creaturesAlreadyHit(creaturesAlreadyHit);
            ability.params().inactiveBody(true);

            ability.start(dirVector, gameState, abilityType); // set pos from mouse pos

            return ability;
        }

        if (abilityType == AbilityType.LIGHTNING_CHAIN) {
            Ability ability = LightningChain.of(AbilityParams.of(abilityId, areaId, 1f, 3f, 0f, 0.4f, null,
                                                                 // TODO: move range out of constructor
                                                                 "lightning_chain"));
            ability.params().creatureId(creatureId);
            ability.params().damage(0f);
            ability.params().isActiveAnimationLooping(true);
            ability.params().attackWithoutMoving(true);

            ability.params().creaturesAlreadyHit(creaturesAlreadyHit);
            ability.params().inactiveBody(true);

            ability.params().rotationShift(90f);

            Vector2 lightningDirVector = pos.vectorTowards(chainFromPos);

            Float theta = lightningDirVector.angleDeg();

            float attackShiftX = lightningDirVector.normalized().x() * chainFromPos.distance(pos) / 2;
            float attackShiftY = lightningDirVector.normalized().y() * chainFromPos.distance(pos) / 2;

            ability.params().pos(Vector2.of(pos.x() + attackShiftX, pos.y() + attackShiftY));

            ability.params().height(chainFromPos.distance(pos));

            ability.params().rotationAngle(theta);

            ability.start(dirVector, gameState, abilityType); // set pos from mouse pos

            return ability;
        }

        throw new RuntimeException("ability type not found");
    }
}


