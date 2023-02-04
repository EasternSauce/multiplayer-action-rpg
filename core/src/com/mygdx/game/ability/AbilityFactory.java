package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;

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
                                         MyGdxGame game) {
        if (abilityType == AbilityType.SLASH) {
            Ability ability = Attack.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
            ability.init(game);
            return ability;
        }

        if (abilityType == AbilityType.FIREBALL) {
            Ability ability = Fireball.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
            ability.init(game);
            return ability;
        }

        if (abilityType == AbilityType.FIREBALL_EXPLOSION) {
            Ability ability = FireballExplosion.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
            ability.init(game);
            return ability;
        }

        if (abilityType == AbilityType.LIGHTNING_SPARK) {
            Vector2 creaturePos = game.gameState().creatures().get(creatureId).params().pos();
            Ability ability =
                    LightningSpark.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit, creaturePos);
            ability.init(game);
            return ability;
        }

        if (abilityType == AbilityType.LIGHTNING_NODE) {
            Ability ability = LightningNode.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit);
            ability.init(game);
            return ability;
        }

        if (abilityType == AbilityType.LIGHTNING_CHAIN) {
            Ability ability =
                    LightningChain.of(abilityId, areaId, creatureId, pos, dirVector, creaturesAlreadyHit, chainFromPos);
            ability.init(game);
            return ability;
        }

        throw new RuntimeException("ability type not found");
    }
}


