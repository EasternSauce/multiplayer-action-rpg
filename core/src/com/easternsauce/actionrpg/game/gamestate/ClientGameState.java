package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.EntityEventProcessor;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullCreatureId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
public class ClientGameState extends GameState {
  @Getter
  @Setter
  private EntityId<Creature> thisClientPlayerId = NullCreatureId.of();

  @Override
  public Set<EntityId<Creature>> getCreaturesToUpdate(CoreGame game) {
    return accessCreatures().getCreaturesToUpdateForPlayerCreatureId(getThisClientPlayerId(), game);
  }

  @Override
  public void scheduleServerSideAction(GameStateAction action) {
    // do nothing on client side
  }

  @Override
  public EntityId<Area> getCurrentAreaId() {
    if (!accessCreatures().getCreatures().containsKey(getThisClientPlayerId())) {
      return getDefaultAreaId();
    }

    return accessCreatures().getCreature(getThisClientPlayerId()).getParams().getAreaId();
  }

  public void createEventsFromReceivedGameStateData(GameStateData newGameStateData, EntityEventProcessor eventProcessor) {
    GameStateData oldGameStateData = dataHolder.getData();

    Set<EntityId<Creature>> oldCreatureIds = oldGameStateData.getCreatures().keySet();
    Set<EntityId<Creature>> newCreatureIds = newGameStateData.getCreatures().keySet();
    Set<EntityId<Ability>> oldAbilityIds = oldGameStateData.getAbilities().keySet();
    Set<EntityId<Ability>> newAbilityIds = newGameStateData.getAbilities().keySet();
    Set<EntityId<LootPile>> oldLootPileIds = oldGameStateData.getLootPiles().keySet();
    Set<EntityId<LootPile>> newLootPileIds = newGameStateData.getLootPiles().keySet();
    Set<EntityId<AreaGate>> oldAreaGateIds = oldGameStateData.getAreaGates().keySet();
    Set<EntityId<AreaGate>> newAreaGateIds = newGameStateData.getAreaGates().keySet();
    Set<EntityId<Checkpoint>> oldCheckpointIds = oldGameStateData.getCheckpoints().keySet();
    Set<EntityId<Checkpoint>> newCheckpointIds = newGameStateData.getCheckpoints().keySet();

    Set<EntityId<Creature>> creaturesAddedSinceLastUpdate = new HashSet<>(newCreatureIds);
    creaturesAddedSinceLastUpdate.removeAll(oldCreatureIds);

    Set<EntityId<Creature>> creaturesRemovedSinceLastUpdate = new HashSet<>(oldCreatureIds);
    creaturesRemovedSinceLastUpdate.removeAll(newCreatureIds);

    Set<EntityId<Ability>> abilitiesAddedSinceLastUpdate = new HashSet<>(newAbilityIds);
    abilitiesAddedSinceLastUpdate.removeAll(oldAbilityIds);

    Set<EntityId<Ability>> abilitiesRemovedSinceLastUpdate = new HashSet<>(oldAbilityIds);
    abilitiesRemovedSinceLastUpdate.removeAll(newAbilityIds);

    Set<EntityId<LootPile>> lootPilesAddedSinceLastUpdate = new HashSet<>(newLootPileIds);
    lootPilesAddedSinceLastUpdate.removeAll(oldLootPileIds);

    Set<EntityId<LootPile>> lootPilesRemovedSinceLastUpdate = new HashSet<>(oldLootPileIds);
    lootPilesRemovedSinceLastUpdate.removeAll(newLootPileIds);

    Set<EntityId<AreaGate>> areaGatesAddedSinceLastUpdate = new HashSet<>(newAreaGateIds);
    areaGatesAddedSinceLastUpdate.removeAll(oldAreaGateIds);

    Set<EntityId<AreaGate>> areaGatesRemovedSinceLastUpdate = new HashSet<>(oldAreaGateIds);
    areaGatesRemovedSinceLastUpdate.removeAll(newAreaGateIds);

    Set<EntityId<Checkpoint>> checkpointsAddedSinceLastUpdate = new HashSet<>(newCheckpointIds);
    checkpointsAddedSinceLastUpdate.removeAll(oldCheckpointIds);

    Set<EntityId<Checkpoint>> checkpointsRemovedSinceLastUpdate = new HashSet<>(oldCheckpointIds);
    checkpointsRemovedSinceLastUpdate.removeAll(newCheckpointIds);

    eventProcessor.getCreatureModelsToBeCreated().addAll(creaturesAddedSinceLastUpdate);
    eventProcessor.getCreatureModelsToBeRemoved().addAll(creaturesRemovedSinceLastUpdate);
    eventProcessor.getAbilityModelsToBeCreated().addAll(abilitiesAddedSinceLastUpdate);
    eventProcessor.getAbilityModelsToBeRemoved().addAll(abilitiesRemovedSinceLastUpdate);
    eventProcessor.getLootPileModelsToBeCreated().addAll(lootPilesAddedSinceLastUpdate);
    eventProcessor.getLootPileModelsToBeRemoved().addAll(lootPilesRemovedSinceLastUpdate);
    eventProcessor.getAreaGateModelsToBeCreated().addAll(areaGatesAddedSinceLastUpdate);
    eventProcessor.getAreaGateModelsToBeRemoved().addAll(areaGatesRemovedSinceLastUpdate);
    eventProcessor.getCheckpointModelsToBeCreated().addAll(checkpointsAddedSinceLastUpdate);
    eventProcessor.getCheckpointModelsToBeRemoved().addAll(checkpointsRemovedSinceLastUpdate);
  }

  public void setNewGameState(GameStateData receivedGameStateData) {
    dataHolder.setData(receivedGameStateData);
  }

}
