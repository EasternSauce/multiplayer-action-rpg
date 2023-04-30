package com.mygdx.game.game.gamestate;

import com.mygdx.game.game.entity.EntityEventProcessor;
import com.mygdx.game.model.GameStateData;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.CreatureId;
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
    public Set<CreatureId> getCreaturesToUpdate() {
        return getCreaturesToUpdateForPlayerCreatureId(getThisClientPlayerId());
    }

    public void createEventsFromReceivedGameStateData(GameStateData receivedGameStateData, EntityEventProcessor eventProcessor) {
        GameStateData oldGameStateData = gameStateData;
        //noinspection UnnecessaryLocalVariable
        GameStateData newGameStateData = receivedGameStateData;

        Set<CreatureId> oldCreatureIds = oldGameStateData.getCreatures().keySet();
        Set<CreatureId> newCreatureIds = newGameStateData.getCreatures().keySet();
        Set<AbilityId> oldAbilityIds = oldGameStateData.getAbilities().keySet();
        Set<AbilityId> newAbilityIds = newGameStateData.getAbilities().keySet();
        Set<LootPileId> oldLootPileIds = oldGameStateData.getLootPiles().keySet();
        Set<LootPileId> newLootPileIds = newGameStateData.getLootPiles().keySet();

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

        eventProcessor.getCreatureModelsToBeCreated().addAll(creaturesAddedSinceLastUpdate);
        eventProcessor.getCreatureModelsToBeRemoved().addAll(creaturesRemovedSinceLastUpdate);
        eventProcessor.getAbilityModelsToBeCreated().addAll(abilitiesAddedSinceLastUpdate);
        eventProcessor.getAbilityModelsToBeRemoved().addAll(abilitiesRemovedSinceLastUpdate);
        eventProcessor.getLootPileModelsToBeCreated().addAll(lootPilesAddedSinceLastUpdate);
        eventProcessor.getLootPileModelsToBeRemoved().addAll(lootPilesRemovedSinceLastUpdate);
    }

    public void setNewGameState(GameStateData receivedGameStateData) {
        gameStateData = receivedGameStateData;
    }
}
