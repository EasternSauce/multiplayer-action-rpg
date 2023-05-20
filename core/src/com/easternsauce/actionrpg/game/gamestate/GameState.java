package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.gamestate.accesor.AbilityAccessor;
import com.easternsauce.actionrpg.game.gamestate.accesor.CreatureAccessor;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.area.AreaGateConnection;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.PlayerConfig;
import com.easternsauce.actionrpg.util.RandomHelper;

import java.util.Map;
import java.util.Set;

public abstract class GameState {
    protected final GameStateDataHolder dataHolder = GameStateDataHolder.of(GameStateData.of());

    protected AbilityAccessor abilityAccessor = AbilityAccessor.of(this, dataHolder);
    protected CreatureAccessor creatureAccessor = CreatureAccessor.of(this, dataHolder);

    private GameStateData getData() {
        return dataHolder.getData();
    }

    public AbilityAccessor accessAbilities() {
        return abilityAccessor;
    }

    public CreatureAccessor accessCreatures() {
        return creatureAccessor;
    }

    public void initPlayerParams(CreatureId playerId) {
        getData().getPlayerConfig().put(playerId, PlayerConfig.of());
    }

    public PlayerConfig getPlayerConfig(CreatureId creatureId) {
        if (creatureId != null) {
            return getData().getPlayerConfig().get(creatureId);
        }
        return null;
    }

    public Set<AreaGateConnection> getAreaGateConnections() {
        return getData().getAreaGateConnections();
    }

    public LootPile getLootPile(LootPileId lootPileId) {
        return getData().getLootPiles().get(lootPileId);
    }

    public Map<LootPileId, LootPile> getLootPiles() {
        return getData().getLootPiles();
    }

    public Float nextRandomValue() {
        float result = RandomHelper.seededRandomFloat(getData().getLastRandomValue());

        getData().setLastRandomValue(result);

        return result;
    }

    public void handleExpiredAbilities(CoreGame game) {
        accessAbilities()
            .getAbilities()
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().getParams().getState() == AbilityState.INACTIVE)
            .forEach(entry -> game.getEventProcessor().getAbilityModelsToBeRemoved().add(entry.getKey()));
    }

    public Float getTime() {
        return getData().getGeneralTimer().getTime();
    }

    public void updateGeneralTimer(float delta) {
        getData().getGeneralTimer().update(delta);
    }

    public void setAreaGates(Set<AreaGateConnection> areaGateConnections) {
        getData().setAreaGateConnections(areaGateConnections);
    }

    public AreaId getDefaultAreaId() {
        return getData().getDefaultAreaId();
    }

    public abstract Set<CreatureId> getCreaturesToUpdate();

    public abstract void scheduleServerSideAction(GameStateAction action);

    public abstract CreatureId getThisClientPlayerId();

    public abstract AreaId getCurrentAreaId();
}
