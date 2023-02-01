package com.mygdx.game.ability;

import com.mygdx.game.model.GameState;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.util.Vector2;

import java.util.HashMap;
import java.util.Map;


public class AbilityFactory {

    public static Map<String, Float> manaCosts = new HashMap<>();
    public static Map<String, Float> staminaCosts = new HashMap<>();

    static {
        manaCosts.put("fireball", 15f);
        staminaCosts.put("slash", 22f);
    }

    public static Ability produceAbility(String abilityType,
                                         AbilityId abilityId,
                                         AreaId areaId,
                                         CreatureId creatureId,
                                         GameState gameState,
                                         Vector2 dirVector) {
        if (abilityType.equals("slash")) {
            Ability ability = Attack.of(AbilityParams.of(abilityId, areaId, 2f, 2f, 0f, 0.3f, 1.8f, "slash"));
            ability.params().creatureId(creatureId);
            ability.params().damage(22f);
            ability.params().staminaCost(staminaCosts.getOrDefault("slash", 0f));
            ability.params().manaCost(manaCosts.getOrDefault("slash", 0f));
            ability.start(dirVector, gameState);

            return ability;
        }

        if (abilityType.equals("fireball")) {
            Ability ability = Fireball.of(AbilityParams.of(abilityId, areaId, 1.5f, 1.5f, 0f, 30f, null, "fireball"));
            ability.params().creatureId(creatureId);
            ability.params().damage(15f);
            ability.params().staminaCost(staminaCosts.getOrDefault("fireball", 0f));
            ability.params().manaCost(manaCosts.getOrDefault("fireball", 0f));
            ability.params().isActiveAnimationLooping(true);
            ability.start(dirVector, gameState);

            return ability;
        }

        if (abilityType.equals("fireball_explosion")) {
            Ability ability = FireballExplosion.of(AbilityParams.of(abilityId,
                                                                    areaId,
                                                                    7f,
                                                                    7f,
                                                                    0f,
                                                                    0.35f,
                                                                    null,
                                                                    "explosion"));
            ability.params().creatureId(creatureId);
            ability.params().damage(20f);
            ability.params().staminaCost(staminaCosts.getOrDefault("fireball_explosion", 0f));
            ability.params().manaCost(manaCosts.getOrDefault("fireball_explosion", 0f));
            ability.params().isActiveAnimationLooping(false);
            ability.params().attackWithoutMoving(true);
            ability.start(dirVector, gameState);

            return ability;
        }

        throw new RuntimeException("ability type not found");
    }
}
