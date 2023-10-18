package com.easternsauce.actionrpg.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class EntityEventProcessor {
  @Getter
  private final List<EntityId<Creature>> creatureModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
  @Getter
  private final List<EntityId<Ability>> abilityModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
  @Getter
  private final List<EntityId<LootPile>> lootPileModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
  @Getter
  private final List<EntityId<AreaGate>> areaGateModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
  @Getter
  private final List<EntityId<Checkpoint>> checkpointModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());

  @Getter
  private final List<EntityId<Creature>> creatureModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
  @Getter
  private final List<EntityId<Ability>> abilityModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
  @Getter
  private final List<EntityId<LootPile>> lootPileModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
  @Getter
  private final List<EntityId<AreaGate>> areaGateModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
  @Getter
  private final List<EntityId<Checkpoint>> checkpointModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());

  @Getter
  private final List<EntityId<Ability>> abilityModelsToBeActivated = Collections.synchronizedList(new ArrayList<>());

  @Getter
  private final List<TeleportEvent> teleportEvents = Collections.synchronizedList(new ArrayList<>());

  public void process(GameEntityManager gameEntityManager, TextureAtlas atlas, CoreGame game) {
    getCreatureModelsToBeRemoved().forEach(creatureId -> gameEntityManager.removeCreatureEntity(creatureId, game));
    getCreatureModelsToBeRemoved().clear();

    getAbilityModelsToBeRemoved().forEach(abilityId -> gameEntityManager.removeAbilityEntity(abilityId, game));
    getAbilityModelsToBeRemoved().clear();

    getLootPileModelsToBeRemoved().forEach(lootPileId -> gameEntityManager.removeLootPileEntity(lootPileId, game));
    getLootPileModelsToBeRemoved().clear();

    getAreaGateModelsToBeRemoved().forEach(areaGateId -> gameEntityManager.removeAreaGateEntity(areaGateId, game));
    getAreaGateModelsToBeRemoved().clear();

    getCheckpointModelsToBeRemoved().forEach(
      checkpointId -> gameEntityManager.removeCheckpointEntity(checkpointId, game));
    getCheckpointModelsToBeRemoved().clear();

    getCreatureModelsToBeCreated().forEach(creatureId -> gameEntityManager.createCreatureEntity(creatureId, game));
    getCreatureModelsToBeCreated().clear();

    getAbilityModelsToBeCreated().forEach(abilityId -> gameEntityManager.createAbilityEntity(abilityId, atlas, game));
    getAbilityModelsToBeCreated().clear();

    getLootPileModelsToBeCreated().forEach(
      lootPileId -> gameEntityManager.createLootPileEntity(lootPileId, atlas, game));
    getLootPileModelsToBeCreated().clear();

    getAreaGateModelsToBeCreated().forEach(
      areaGateId -> gameEntityManager.createAreaGateEntity(areaGateId, atlas, game));
    getAreaGateModelsToBeCreated().clear();

    getCheckpointModelsToBeCreated().forEach(
      checkpointId -> gameEntityManager.createCheckpointEntity(checkpointId, atlas, game));
    getCheckpointModelsToBeCreated().clear();

    getAbilityModelsToBeActivated().forEach(abilityId -> gameEntityManager.activateAbility(abilityId, game));
    getAbilityModelsToBeActivated().clear();
  }

}
