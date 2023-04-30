package com.mygdx.game.game.gamestate;

import com.mygdx.game.model.GameStateData;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.model.util.Vector2;
import com.mygdx.game.util.RandomHelper;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class GameState {
    protected GameStateData gameStateData = GameStateData.of();

    public CreatureId getAliveCreatureIdClosestTo(Vector2 pos, float maxRange, Set<CreatureId> excluded) {
        CreatureId minCreatureId = null;
        float minDistance = Float.MAX_VALUE;
        for (CreatureId creatureId : getCreaturesToUpdate()) {
            Creature creature = gameStateData.getCreatures().get(creatureId);
            float distance = pos.distance(creature.getParams().getPos());
            if (creature.isAlive() && distance < minDistance && distance < maxRange && !excluded.contains(creatureId)) {
                minDistance = distance;
                minCreatureId = creatureId;
            }
        }
        return minCreatureId;
    }

    public abstract Set<CreatureId> getCreaturesToUpdate();

    public Ability getAbility(AbilityId abilityId) {
        if (abilityId == null || !gameStateData.getAbilities().containsKey(abilityId)) {
            return null;
        }
        return gameStateData.getAbilities().get(abilityId);
    }

    public Ability getAbilityBySkillType(CreatureId creatureId, SkillType skillType) {
        Optional<Ability> first = gameStateData
                .getAbilities()
                .values()
                .stream()
                .filter(ability -> ability.getParams()
                        .getCreatureId()
                        .equals(creatureId) &&
                        ability.getParams().getSkillType() ==
                                skillType)
                .findFirst();

        return first.orElse(null);
    }

    public Map<CreatureId, Creature> getRemovedCreatures() {
        return gameStateData.getRemovedCreatures();
    }

    public Map<CreatureId, Creature> getCreatures() {
        return gameStateData.getCreatures();
    }


    public Vector2 getCreaturePos(CreatureId creatureId) {
        if (!gameStateData.getCreatures().containsKey(creatureId)) {
            return null;
        }
        return gameStateData.getCreatures().get(creatureId).getParams().getPos();
    }

    public Creature getCreature(CreatureId creatureId) {
        if (creatureId == null || !gameStateData.getCreatures().containsKey(creatureId)) {
            return null;
        }
        return gameStateData.getCreatures().get(creatureId);
    }

    public Map<AbilityId, Ability> getAbilities() {
        return gameStateData.getAbilities();
    }

    public AreaId getDefaultAreaId() {
        return gameStateData.getDefaultAreaId();
    }

    public void initPlayerParams(CreatureId playerId) {
        gameStateData.getPlayerParams().put(playerId, PlayerParams.of());
    }

    public PlayerParams getPlayerParams(CreatureId creatureId) {
        if (creatureId != null) {
            return gameStateData.getPlayerParams().get(creatureId);
        }
        return null;
    }

    public Set<AreaGate> getAreaGates() {
        return gameStateData.getAreaGates();
    }

    public LootPile getLootPile(LootPileId lootPileId) {
        return gameStateData.getLootPiles().get(lootPileId);
    }

    public Map<LootPileId, LootPile> getLootPiles() {
        return gameStateData.getLootPiles();
    }

    public Float getTime() {
        return gameStateData.getGeneralTimer().getTime();
    }


    public Float nextRandomValue() {
        float result = RandomHelper.seededRandomFloat(gameStateData.getLastRandomValue());

        gameStateData.setLastRandomValue(result);

        return result;
    }

    public void updateGeneralTimer(float delta) {
        gameStateData.getGeneralTimer().update(delta);
    }

    public void setAreaGates(Set<AreaGate> areaGates) {
        gameStateData.setAreaGates(areaGates);
    }
}
