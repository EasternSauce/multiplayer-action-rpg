package com.easternsauce.actionrpg.game.gamestate;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.EntityEventProcessor;
import com.easternsauce.actionrpg.model.GameStateData;
import com.easternsauce.actionrpg.model.id.AbilityId;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.id.AreaGateId;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.id.CheckpointId;
import com.easternsauce.actionrpg.model.id.LootPileId;
import com.easternsauce.actionrpg.model.id.CreatureId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
public class ClientGameState extends GameState {
  @Getter
  @Setter
  private CreatureId thisClientPlayerId;

  @Override
  public Set<CreatureId> getCreaturesToUpdate(CoreGame game) {
    return accessCreatures().getCreaturesToUpdateForPlayerCreatureId(getThisClientPlayerId(), game);
  }

  @Override
  public void scheduleServerSideAction(GameStateAction action) {
    // do nothing on client side
  }

  @Override
  public AreaId getCurrentAreaId() {
    if (!accessCreatures().getCreatures().containsKey(getThisClientPlayerId())) {
      return getDefaultAreaId();
    }

    return accessCreatures().getCreature(getThisClientPlayerId()).getParams().getAreaId();
  }

  public void createEventsFromReceivedGameStateData(GameStateData newGameStateData, EntityEventProcessor eventProcessor) {
    GameStateData oldGameStateData = dataHolder.getData();

    Set<CreatureId> oldCreatureIds = oldGameStateData.getCreatures().keySet();
    Set<CreatureId> newCreatureIds = newGameStateData.getCreatures().keySet();
    Set<AbilityId> oldAbilityIds = oldGameStateData.getAbilities().keySet();
    Set<AbilityId> newAbilityIds = newGameStateData.getAbilities().keySet();
    Set<LootPileId> oldLootPileIds = oldGameStateData.getLootPiles().keySet();
    Set<LootPileId> newLootPileIds = newGameStateData.getLootPiles().keySet();
    Set<AreaGateId> oldAreaGateIds = oldGameStateData.getAreaGates().keySet();
    Set<AreaGateId> newAreaGateIds = newGameStateData.getAreaGates().keySet();
    Set<CheckpointId> oldCheckpointIds = oldGameStateData.getCheckpoints().keySet();
    Set<CheckpointId> newCheckpointIds = newGameStateData.getCheckpoints().keySet();

    Set<CreatureId> creaturesAddedSinceLastUpdate = new HashSet<>(newCreatureIds);
    creaturesAddedSinceLastUpdate.removeAll(oldCreatureIds);

    Set<CreatureId> creaturesRemovedSinceLastUpdate = new HashSet<>(oldCreatureIds);
    creaturesRemovedSinceLastUpdate.removeAll(newCreatureIds);

    Set<AbilityId> abilitiesAddedSinceLastUpdate = new HashSet<>(newAbilityIds);
    abilitiesAddedSinceLastUpdate.removeAll(oldAbilityIds);

    Set<AbilityId> abilitiesRemovedSinceLastUpdate = new HashSet<>(oldAbilityIds);
    abilitiesRemovedSinceLastUpdate.removeAll(newAbilityIds);

    Set<LootPileId> lootPilesAddedSinceLastUpdate = new HashSet<>(newLootPileIds);
    lootPilesAddedSinceLastUpdate.removeAll(oldLootPileIds);

    Set<LootPileId> lootPilesRemovedSinceLastUpdate = new HashSet<>(oldLootPileIds);
    lootPilesRemovedSinceLastUpdate.removeAll(newLootPileIds);

    Set<AreaGateId> areaGatesAddedSinceLastUpdate = new HashSet<>(newAreaGateIds);
    areaGatesAddedSinceLastUpdate.removeAll(oldAreaGateIds);

    Set<AreaGateId> areaGatesRemovedSinceLastUpdate = new HashSet<>(oldAreaGateIds);
    areaGatesRemovedSinceLastUpdate.removeAll(newAreaGateIds);

    Set<CheckpointId> checkpointsAddedSinceLastUpdate = new HashSet<>(newCheckpointIds);
    checkpointsAddedSinceLastUpdate.removeAll(oldCheckpointIds);

    Set<CheckpointId> checkpointsRemovedSinceLastUpdate = new HashSet<>(oldCheckpointIds);
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
