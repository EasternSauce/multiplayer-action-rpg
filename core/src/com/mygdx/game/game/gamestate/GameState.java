package com.mygdx.game.game.gamestate;

import com.mygdx.game.model.GameStateData;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.AreaGate;
import com.mygdx.game.model.area.AreaId;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.PlayerParams;
import com.mygdx.game.util.RandomHelper;

import java.util.Map;
import java.util.Set;

public abstract class GameState {
    protected GameStateData data = GameStateData.of();

    protected GameStateAbilityAccessor abilityAccessor = GameStateAbilityAccessor.of(this);
    protected GameStateCreatureAccessor creatureAccessor = GameStateCreatureAccessor.of(this);

    public GameStateAbilityAccessor accessAbilities() {
        return abilityAccessor;
    }

    public GameStateCreatureAccessor accessCreatures() {
        return creatureAccessor;
    }

    public void initPlayerParams(CreatureId playerId) {
        data.getPlayerParams().put(playerId, PlayerParams.of());
    }

    public PlayerParams getPlayerParams(CreatureId creatureId) {
        if (creatureId != null) {
            return data.getPlayerParams().get(creatureId);
        }
        return null;
    }

    public Set<AreaGate> getAreaGates() {
        return data.getAreaGates();
    }

    public LootPile getLootPile(LootPileId lootPileId) {
        return data.getLootPiles().get(lootPileId);
    }

    public Map<LootPileId, LootPile> getLootPiles() {
        return data.getLootPiles();
    }

    public Float nextRandomValue() {
        float result = RandomHelper.seededRandomFloat(data.getLastRandomValue());

        data.setLastRandomValue(result);

        return result;
    }

    public Float getTime() {
        return data.getGeneralTimer().getTime();
    }

    public void updateGeneralTimer(float delta) {
        data.getGeneralTimer().update(delta);
    }

    public void setAreaGates(Set<AreaGate> areaGates) {
        data.setAreaGates(areaGates);
    }

    public AreaId getDefaultAreaId() {
        return data.getDefaultAreaId();
    }

    public abstract Set<CreatureId> getCreaturesToUpdate();

    public abstract void scheduleServerSideAction(GameStateAction action);

    public abstract CreatureId getThisClientPlayerId();

    public abstract AreaId getCurrentAreaId();
}
