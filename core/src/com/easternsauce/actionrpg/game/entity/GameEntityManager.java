package com.easternsauce.actionrpg.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.AreaGate;
import com.easternsauce.actionrpg.model.area.Checkpoint;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.enemy.Enemy;
import com.easternsauce.actionrpg.model.creature.enemy.EnemyTemplate;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.GameEntityPhysics;
import com.easternsauce.actionrpg.physics.body.*;
import com.easternsauce.actionrpg.renderer.AbilityRenderer;
import com.easternsauce.actionrpg.renderer.AreaGateRenderer;
import com.easternsauce.actionrpg.renderer.CheckpointRenderer;
import com.easternsauce.actionrpg.renderer.LootPileRenderer;
import com.easternsauce.actionrpg.renderer.creature.CreatureRenderer;
import com.easternsauce.actionrpg.renderer.game.GameEntityRenderer;
import com.easternsauce.actionrpg.util.OrderedMap;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@SuppressWarnings({"GrazieInspection", "SpellCheckingInspection"})
@NoArgsConstructor(staticName = "of")
public class GameEntityManager {
  @Getter
  private final GameEntityRenderer gameEntityRenderer = GameEntityRenderer.of();
  @Getter
  private final GameEntityPhysics gameEntityPhysics = GameEntityPhysics.of();

  public void createCreatureEntity(EntityId<Creature> creatureId, CoreGame game) {
    Creature creature = game.getCreature(creatureId);

    if (!creature.isEmpty()) {
      if (!gameEntityRenderer.getCreatureRenderers().containsKey(creatureId)) {
        CreatureRenderer creatureRenderer = CreatureRenderer.of(creatureId);
        gameEntityRenderer.getCreatureRenderers().put(creatureId, creatureRenderer);
      }
      if (!gameEntityPhysics.getCreatureBodies().containsKey(creatureId)) {
        CreatureBody creatureBody = CreatureBody.of(creatureId);
        creatureBody.init(creature.getParams().getAreaId(), game);
        gameEntityPhysics.getCreatureBodies().put(creatureId, creatureBody);
      }
    }
  }

  public void createAbilityEntity(EntityId<Ability> abilityId, TextureAtlas atlas, CoreGame game) {
    Ability ability = game.getAbility(abilityId);

    if (ability.usesEntityModel()) {
      if (!ability.getParams().getNoTexture() && !gameEntityRenderer.getAbilityRenderers().containsKey(abilityId)) {
        AbilityRenderer abilityRenderer = AbilityRenderer.of(abilityId);
        abilityRenderer.init(atlas, game);
        gameEntityRenderer.getAbilityRenderers().put(abilityId, abilityRenderer);
      }
      if (!gameEntityPhysics.getAbilityBodies().containsKey(abilityId)) {
        AbilityBody abilityBody = AbilityBody.of(abilityId);
        if (ability.getParams().getState() == AbilityState.ACTIVE) {
          abilityBody.activate(ability.getParams().getSkipCreatingBody(), game);
        }
        gameEntityPhysics.getAbilityBodies().put(abilityId, abilityBody);
      }
    }

  }

  public void activateAbility(EntityId<Ability> abilityId, CoreGame game) {
    Ability ability = game.getAbility(abilityId);

    if (gameEntityPhysics.getAbilityBodies().containsKey(ability.getParams().getId()) &&
      !gameEntityPhysics.getAbilityBodies().get(ability.getParams().getId()).getBodyInitialized()) {
      gameEntityPhysics.getAbilityBodies().get(ability.getParams().getId())
        .activate(ability.getParams().getSkipCreatingBody(), game);
    }
  }

  public void createLootPileEntity(EntityId<LootPile> lootPileId, TextureAtlas atlas, CoreGame game) {
    LootPile lootPile = game.getGameState().getLootPile(lootPileId);

    if (lootPile != null) {
      if (!gameEntityRenderer.getLootPileRenderers().containsKey(lootPileId)) {
        LootPileRenderer lootPileRenderer = LootPileRenderer.of(lootPileId);
        lootPileRenderer.init(atlas, game);
        gameEntityRenderer.getLootPileRenderers().put(lootPileId, lootPileRenderer);
      }
      if (!gameEntityPhysics.getLootPileBodies().containsKey(lootPileId)) {
        LootPileBody lootPileBody = LootPileBody.of(lootPileId);
        lootPileBody.init(game);
        gameEntityPhysics.getLootPileBodies().put(lootPileId, lootPileBody);
      }
    }
  }

  public void removeCreatureEntity(EntityId<Creature> creatureId, @SuppressWarnings("unused") CoreGame game) {
    if (!creatureId.isEmpty()) {
      getGameEntityRenderer().getCreatureRenderers().remove(creatureId);

      if (gameEntityPhysics.getCreatureBodies().containsKey(creatureId)) {
        gameEntityPhysics.getCreatureBodies().get(creatureId).onRemove();
        gameEntityPhysics.getCreatureBodies().remove(creatureId);
      }
    }
  }

  public void removeAbilityEntity(EntityId<Ability> abilityId, CoreGame game) {
    if (!abilityId.isEmpty()) {
      game.getAbilities().remove(abilityId);

      getGameEntityRenderer().getAbilityRenderers().remove(abilityId);

      if (gameEntityPhysics.getAbilityBodies().containsKey(abilityId)) {
        gameEntityPhysics.getAbilityBodies().get(abilityId).onRemove();
        gameEntityPhysics.getAbilityBodies().remove(abilityId);
      }
    }
  }

  public void removeLootPileEntity(EntityId<LootPile> lootPileId, CoreGame game) {
    if (!lootPileId.isEmpty()) {

      game.getGameState().getLootPiles().remove(lootPileId);

      getGameEntityRenderer().getLootPileRenderers().remove(lootPileId);

      if (gameEntityPhysics.getLootPileBodies().containsKey(lootPileId)) {
        gameEntityPhysics.getLootPileBodies().get(lootPileId).onRemove();
        gameEntityPhysics.getLootPileBodies().remove(lootPileId);
      }
    }
  }

  public void spawnEnemy(EntityId<Creature> creatureId, EntityId<Area> areaId, Vector2 pos, EnemyTemplate enemyTemplate, int rngSeed, CoreGame game) {
    Enemy enemy = Enemy.of(creatureId, areaId, pos, enemyTemplate, null, rngSeed);

    game.getAllCreatures().put(creatureId, enemy);

    game.getEventProcessor().getCreatureModelsToBeCreated().add(creatureId);
  }

  public void updateCreatures(float delta, CoreGame game) {
    Set<EntityId<Creature>> creaturesToUpdate = game.getGameState().getCreaturesToUpdate(game);

    creaturesToUpdate.forEach(creatureId -> {
      if (getGameEntityPhysics().getCreatureBodies().containsKey(creatureId)) {
        getGameEntityPhysics().getCreatureBodies().get(creatureId).update(game);
      }
    });

    // set gamestate position based on b2body position
    creaturesToUpdate.forEach(creatureId -> {
      if (game.getActiveCreatures().containsKey(creatureId) &&
        getGameEntityPhysics().getCreatureBodies().containsKey(creatureId)) {

        game.getCreature(creatureId).getParams()
          .setPos(getGameEntityPhysics().getCreatureBodies().get(creatureId).getBodyPos());

      }
    });

    // if creature is to be updated, then body should be active, otherwise it should be inactive
    getGameEntityPhysics().getCreatureBodies().forEach(
      (key, value) -> getGameEntityPhysics().getCreatureBodies().get(key).setActive(creaturesToUpdate.contains(key)));

    creaturesToUpdate.forEach(creatureId -> {
      if (game.getActiveCreatures().containsKey(creatureId) &&
        getGameEntityRenderer().getCreatureRenderers().containsKey(creatureId)) {
        getGameEntityRenderer().getCreatureRenderers().get(creatureId).update(game);
      }
    });

    creaturesToUpdate.forEach(creatureId -> {
      if (game.getActiveCreatures().containsKey(creatureId)) {
        game.getCreature(creatureId).update(delta, game);
      }
    });

  }

  public void updateEnemyRallyPoints(float delta, CoreGame game) {
    game.getGameState().getEnemyRallyPoints()
      .forEach((enemyRallyPointId, enemyRallyPoint) -> enemyRallyPoint.update(delta, game));
  }

  public void updateAbilities(float delta, CoreGame game) {
    Set<EntityId<Ability>> abilitiesToUpdate = new ConcurrentSkipListSet<>(game.getAbilitiesToUpdate());

    abilitiesToUpdate.forEach(abilityId -> {
      if (game.getAbilities().containsKey(abilityId) && !game.getAbility(abilityId).isEmpty()) {
        Ability ability = game.getAbility(abilityId);

        ability.onUpdateState(delta, game);
      }
    });

    Map<EntityId<Ability>, Ability> abilities = new OrderedMap<>(game.getAbilities());

    abilities.forEach((abilityId, ability) -> {
      if (abilities.containsKey(abilityId) && !game.getAbility(abilityId).isEmpty()) {
        ability.update(delta, game);
      }
    });

    abilitiesToUpdate.forEach(abilityId -> {
      if (getGameEntityPhysics().getAbilityBodies().containsKey(abilityId)) {
        getGameEntityPhysics().getAbilityBodies().get(abilityId).update(game);
      }
    });

    abilitiesToUpdate.forEach(abilityId -> {
      if (getGameEntityPhysics().getAbilityBodies().containsKey(abilityId)) {
        Ability ability = game.getAbility(abilityId);
        if (!ability.isPositionChangedOnUpdate() && !ability.getParams().getSkipCreatingBody() &&
          getGameEntityPhysics().getAbilityBodies().get(abilityId).getBodyInitialized()) {
          ability.getParams().setPos(getGameEntityPhysics().getAbilityBodies().get(abilityId).getBodyPos());
        }
      }
    });

    abilitiesToUpdate.forEach(abilityId -> {
      if (getGameEntityRenderer().getAbilityRenderers().containsKey(abilityId)) {
        getGameEntityRenderer().getAbilityRenderers().get(abilityId).update(game);
      }
    });

  }

  public void teleportCreature(TeleportEvent teleportEvent, CoreGame game) {
    Creature creature = game.getCreature(teleportEvent.getCreatureId());

    creature.getParams().getMovementParams().setDashing(false);

    if (!teleportEvent.getCreatureId().isEmpty() && !teleportEvent.getUsedGate() && teleportEvent.getToAreaId().getValue()
      .equals(game.getCreature(teleportEvent.getCreatureId()).getParams().getAreaId().getValue()) &&
      teleportEvent.getToAreaId().getValue()
        .equals(game.getCreatureBodies().get(teleportEvent.getCreatureId()).getAreaId().getValue())) {
      getGameEntityPhysics().getCreatureBodies().get(teleportEvent.getCreatureId())
        .forceSetTransform(teleportEvent.getPos());
    } else {
      if (!teleportEvent.getCreatureId().isEmpty()) {
        creature.getParams().setAreaId(teleportEvent.getToAreaId());

        creature.getParams().setPos(teleportEvent.getPos());
        creature.getParams().getMovementParams().setMovementCommandTargetPos(teleportEvent.getPos());

        if (getGameEntityPhysics().getCreatureBodies().containsKey(teleportEvent.getCreatureId())) {
          getGameEntityPhysics().getCreatureBodies().get(teleportEvent.getCreatureId())
            .moveBodyToNewArea(teleportEvent.getToAreaId(), game);
        } else {
          CreatureBody creatureBody = CreatureBody.of(teleportEvent.getCreatureId());
          creatureBody.init(teleportEvent.getToAreaId(), game);
          getGameEntityPhysics().getCreatureBodies().put(teleportEvent.getCreatureId(), creatureBody);
        }

        if (teleportEvent.getUsedGate()) {
          creature.getParams().getMovementParams().setStillInsideGateAfterTeleport(true);
          creature.getParams().getMovementParams().getGateTeleportCooldownTimer().restart();
        }
      }
    }
  }

  public void createAreaGateEntity(EntityId<AreaGate> areaGateId, TextureAtlas atlas, CoreGame game) {
    AreaGate areaGate = game.getGameState().getAreaGate(areaGateId);

    if (areaGate != null) {
      if (!gameEntityRenderer.getAreaGateRenderers().containsKey(areaGateId)) {
        AreaGateRenderer areaGateRenderer = AreaGateRenderer.of(areaGateId);
        areaGateRenderer.init(atlas, game);
        gameEntityRenderer.getAreaGateRenderers().put(areaGateId, areaGateRenderer);
      }
      if (!gameEntityPhysics.getAreaGateBodies().containsKey(areaGateId)) {
        AreaGateBody areaGateBody = AreaGateBody.of(areaGateId);
        areaGateBody.init(game);
        gameEntityPhysics.getAreaGateBodies().put(areaGateId, areaGateBody);
      }
    }
  }

  public void removeAreaGateEntity(EntityId<AreaGate> areaGateId, CoreGame game) {
    if (!areaGateId.isEmpty()) {
      game.getGameState().getAreaGates().remove(areaGateId);

      getGameEntityRenderer().getAreaGateRenderers().remove(areaGateId);

      if (gameEntityPhysics.getAreaGateBodies().containsKey(areaGateId)) {
        gameEntityPhysics.getAreaGateBodies().get(areaGateId).onRemove();
        gameEntityPhysics.getAreaGateBodies().remove(areaGateId);
      }
    }
  }

  public void createCheckpointEntity(EntityId<Checkpoint> checkpointId, TextureAtlas atlas, CoreGame game) {
    Checkpoint checkpoint = game.getGameState().getCheckpoint(checkpointId);

    if (checkpoint != null) {
      if (!gameEntityRenderer.getCheckpointRenderers().containsKey(checkpointId)) {
        CheckpointRenderer checkpointRenderer = CheckpointRenderer.of(checkpointId);
        checkpointRenderer.init(atlas, game);
        gameEntityRenderer.getCheckpointRenderers().put(checkpointId, checkpointRenderer);
      }
      if (!gameEntityPhysics.getCheckpointBodies().containsKey(checkpointId)) {
        CheckpointBody checkpointBody = CheckpointBody.of(checkpointId);
        checkpointBody.init(game);
        gameEntityPhysics.getCheckpointBodies().put(checkpointId, checkpointBody);
      }
    }
  }

  public void removeCheckpointEntity(EntityId<Checkpoint> checkpointId, CoreGame game) {
    if (!checkpointId.isEmpty()) {
      game.getGameState().getCheckpoints().remove(checkpointId);

      getGameEntityRenderer().getCheckpointRenderers().remove(checkpointId);

      if (gameEntityPhysics.getCheckpointBodies().containsKey(checkpointId)) {
        gameEntityPhysics.getCheckpointBodies().get(checkpointId).onRemove();
        gameEntityPhysics.getCheckpointBodies().remove(checkpointId);
      }
    }
  }
}
