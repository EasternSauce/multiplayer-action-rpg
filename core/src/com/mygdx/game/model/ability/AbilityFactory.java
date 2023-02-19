package com.mygdx.game.model.ability;

import com.mygdx.game.game.CreaturePosRetrievable;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.Vector2;

import java.util.Set;


public class AbilityFactory {

    public static Ability produceAbility(AbilityType abilityType,
                                         AbilityId abilityId,
                                         AreaId areaId,
                                         CreatureId creatureId,
                                         Vector2 dirVector,
                                         Vector2 chainFromPos,
                                         Vector2 pos,
                                         Set<CreatureId> creaturesAlreadyHit,
                                         CreaturePosRetrievable game) {
        if (abilityType == AbilityType.SLASH) {
            return Attack.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
        }

        if (abilityType == AbilityType.FIREBALL) {
            return Fireball.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
        }

        if (abilityType == AbilityType.FIREBALL_EXPLOSION) {
            return FireballExplosion.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
        }

        if (abilityType == AbilityType.LIGHTNING_SPARK) {
            Vector2 creaturePos = game.getCreaturePos(creatureId);
            return LightningSpark.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit, creaturePos);
        }

        if (abilityType == AbilityType.LIGHTNING_NODE) {
            return LightningNode.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
        }

        if (abilityType == AbilityType.LIGHTNING_CHAIN) {
            return LightningChain.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit, chainFromPos);
        }

        if (abilityType == AbilityType.CROSSBOW_BOLT) {
            Vector2 creaturePos = game.getCreaturePos(creatureId);
            return CrossbowBolt.of(abilityId, areaId, creatureId, creaturePos, dirVector, creaturesAlreadyHit);
        }

        if (abilityType == AbilityType.MAGIC_ORB) {
            return MagicOrb.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
        }

        throw new RuntimeException("ability type not found");
    }
}


