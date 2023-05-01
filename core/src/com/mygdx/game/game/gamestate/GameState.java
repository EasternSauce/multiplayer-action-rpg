package com.mygdx.game.game.gamestate;

import com.mygdx.game.Constants;
import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.GameStateData;
import com.mygdx.game.model.ability.*;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.action.ability.AbilityActivateAction;
import com.mygdx.game.model.action.ability.AbilityAddAction;
import com.mygdx.game.model.action.ability.SkillTryPerformAction;
import com.mygdx.game.model.action.creature.CreatureHitAction;
import com.mygdx.game.model.action.creature.CreatureMovingVectorSetAction;
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

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    public Set<CreatureId> getCreaturesToUpdateForPlayerCreatureId(CreatureId playerCreatureId) {
        Creature player = gameStateData.getCreatures().get(playerCreatureId);

        if (player == null) {
            return new HashSet<>();
        }

        return gameStateData.getCreatures().keySet().stream().filter(creatureId -> {
            Creature creature = gameStateData.getCreatures().get(creatureId);
            if (creature != null) {
                return player.getParams().getAreaId().equals(creature.getParams().getAreaId()) && creature.getParams().getPos().distance(player.getParams().getPos()) <
                        Constants.ClientGameUpdateRange;
            }

            return false;

        }).collect(Collectors.toSet());
    }

    public void spawnAbility(AbilityType abilityType, AbilityParams abilityParams, CoreGame game) {
        Creature creature = getCreature(abilityParams.getCreatureId());

        if (creature != null) {
            Ability ability = AbilityFactory.produceAbility(abilityType, abilityParams, game);

            AbilityAddAction action = AbilityAddAction.of(ability);

            scheduleServerSideAction(action);
        }
    }

    public abstract void scheduleServerSideAction(GameStateAction action);

    public void chainAbility(Ability chainFromAbility, AbilityType abilityType, Vector2 chainToPos, Vector2 dirVector, CoreGame game) {
        AbilityId abilityId = AbilityId.of("Ability_" + (int) (Math.random() * 10000000));

        Map<CreatureId, Float> creaturesAlreadyHit =
                new ConcurrentSkipListMap<>(chainFromAbility.getParams().getCreaturesAlreadyHit());

        Vector2 chainFromPos = chainFromAbility.getParams().getPos();

        AbilityParams abilityParams = AbilityParams.of()
                .setId(abilityId)
                .setAreaId(chainFromAbility.getParams().getAreaId())
                .setCreatureId(chainFromAbility.getParams().getCreatureId())
                .setCreaturesAlreadyHit(creaturesAlreadyHit)
                .setChainFromPos(chainFromPos)
                .setChainToPos(chainToPos)
                .setDirVector(dirVector)
                .setSkillType(chainFromAbility.getParams().getSkillType());

        spawnAbility(abilityType, abilityParams, game);
    }

    public abstract CreatureId getThisClientPlayerId();

    public void setCreatureMovingVector(CreatureId creatureId, Vector2 dirVector) {
        CreatureMovingVectorSetAction action = CreatureMovingVectorSetAction.of(creatureId, dirVector);

        scheduleServerSideAction(action);
    }

    public void activateAbility(Ability ability) {
        AbilityActivateAction action = AbilityActivateAction.of(ability);

        scheduleServerSideAction(action);
    }

    public void forEachAliveCreature(Consumer<Creature> creatureAction) {
        getCreatures().values().stream().filter(Creature::isAlive).forEach(creatureAction);
    }

    public void forEachDeadCreature(Consumer<Creature> creatureAction) {
        getCreatures().values().stream().filter(creature -> !creature.isAlive()).forEach(creatureAction);
    }

    public void onAbilityHitsCreature(CreatureId attackerId, CreatureId targetId, Ability ability) {
        ability.onCreatureHit();
        ability.getParams().getCreaturesAlreadyHit().put(targetId, ability.getParams().getStateTimer().getTime());

        CreatureHitAction action = CreatureHitAction.of(attackerId, targetId, ability);

        scheduleServerSideAction(action);
    }

    public void handleAttackTarget(CreatureId attackingCreatureId, Vector2 vectorTowardsTarget, SkillType skillType) {
        Creature attackingCreature = getCreatures().get(attackingCreatureId);

        SkillTryPerformAction action = SkillTryPerformAction.of(attackingCreatureId,
                skillType,
                attackingCreature.getParams().getPos(),
                vectorTowardsTarget);

        scheduleServerSideAction(action);
    }

    public Set<AbilityId> getAbilitiesWithinRange(Creature player) {
        return getAbilities().keySet().stream().filter(abilityId -> {
            Ability ability = getAbilities().get(abilityId);
            if (ability != null) {
                return ability.getParams().getPos().distance(player.getParams().getPos()) <
                        Constants.ClientGameUpdateRange;
            }
            return false;
        }).collect(Collectors.toSet());
    }

    public abstract AreaId getCurrentAreaId();
}
