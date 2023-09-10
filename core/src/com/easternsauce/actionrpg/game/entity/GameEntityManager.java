package com.easternsauce.actionrpg.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.area.*;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.Enemy;
import com.easternsauce.actionrpg.model.creature.EnemyTemplate;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import com.easternsauce.actionrpg.model.util.Vector2;
import com.easternsauce.actionrpg.physics.GameEntityPhysics;
import com.easternsauce.actionrpg.physics.body.AbilityBody;
import com.easternsauce.actionrpg.physics.body.AreaGateBody;
import com.easternsauce.actionrpg.physics.body.CreatureBody;
import com.easternsauce.actionrpg.physics.body.LootPileBody;
import com.easternsauce.actionrpg.renderer.AbilityRenderer;
import com.easternsauce.actionrpg.renderer.AreaGateRenderer;
import com.easternsauce.actionrpg.renderer.LootPileRenderer;
import com.easternsauce.actionrpg.renderer.creature.CreatureRenderer;
import com.easternsauce.actionrpg.renderer.game.GameEntityRenderer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@SuppressWarnings({"GrazieInspection", "SpellCheckingInspection"})
@NoArgsConstructor(staticName = "of")
public class GameEntityManager {
  @Getter
  private final GameEntityRenderer gameEntityRenderer = GameEntityRenderer.of();
  @Getter
  private final GameEntityPhysics gameEntityPhysics = GameEntityPhysics.of();

  public void createCreatureEntity(CreatureId creatureId, CoreGame game) {
    if (creatureId != null) {
      Creature creature = game.getCreature(creatureId);

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

  public void createAbilityEntity(AbilityId abilityId, TextureAtlas atlas, CoreGame game) {
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

  public void activateAbility(AbilityId abilityId, CoreGame game) {
    Ability ability = game.getAbility(abilityId);

    if (gameEntityPhysics.getAbilityBodies().containsKey(ability.getParams().getId()) &&
      !gameEntityPhysics.getAbilityBodies().get(ability.getParams().getId()).getBodyInitialized()) {
      gameEntityPhysics.getAbilityBodies().get(ability.getParams().getId())
        .activate(ability.getParams().getSkipCreatingBody(), game);
    }
  }

  public void createLootPileEntity(LootPileId lootPileId, TextureAtlas atlas, CoreGame game) {
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

  public void removeCreatureEntity(CreatureId creatureId, @SuppressWarnings("unused") CoreGame game) {
    if (creatureId != null) {
      getGameEntityRenderer().getCreatureRenderers().remove(creatureId);

      if (gameEntityPhysics.getCreatureBodies().containsKey(creatureId)) {
        gameEntityPhysics.getCreatureBodies().get(creatureId).onRemove();
        gameEntityPhysics.getCreatureBodies().remove(creatureId);
      }
    }
  }

  public void removeAbilityEntity(AbilityId abilityId, CoreGame game) {
    if (abilityId != null) {
      game.getAbilities().remove(abilityId);

      getGameEntityRenderer().getAbilityRenderers().remove(abilityId);

      if (gameEntityPhysics.getAbilityBodies().containsKey(abilityId)) {
        gameEntityPhysics.getAbilityBodies().get(abilityId).onRemove();
        gameEntityPhysics.getAbilityBodies().remove(abilityId);
      }
    }
  }

  public void removeLootPileEntity(LootPileId lootPileId, CoreGame game) {
    if (lootPileId != null) {

      game.getGameState().getLootPiles().remove(lootPileId);

      getGameEntityRenderer().getLootPileRenderers().remove(lootPileId);

      if (gameEntityPhysics.getLootPileBodies().containsKey(lootPileId)) {
        gameEntityPhysics.getLootPileBodies().get(lootPileId).onRemove();
        gameEntityPhysics.getLootPileBodies().remove(lootPileId);
      }
    }
  }

  public void spawnEnemy(CreatureId creatureId, AreaId areaId, Vector2 pos, EnemyTemplate enemyTemplate, int rngSeed, CoreGame game) {
    Enemy enemy = Enemy.of(creatureId, areaId, pos, enemyTemplate, null, rngSeed);

    game.getAllCreatures().put(creatureId, enemy);

    game.getEventProcessor().getCreatureModelsToBeCreated().add(creatureId);
  }

  public void updateCreatures(float delta, CoreGame game) {
    Set<CreatureId> creaturesToUpdate = game.getGameState().getCreaturesToUpdate(game);

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
    Set<AbilityId> abilitiesToUpdate = game.getAbilitiesToUpdate();

    abilitiesToUpdate.forEach(abilityId -> {
      if (abilityId != null && game.getAbilities().containsKey(abilityId) &&
        game.getAbility(abilityId) != null) {
        game.getAbility(abilityId).update(delta, game);
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

    if (teleportEvent.getCreatureId() != null && !teleportEvent.getUsedGate() && teleportEvent.getToAreaId().getValue()
      .equals(game.getCreature(teleportEvent.getCreatureId()).getParams().getAreaId().getValue()) &&
      teleportEvent.getToAreaId().getValue()
        .equals(game.getCreatureBodies().get(teleportEvent.getCreatureId()).getAreaId().getValue())) {
      getGameEntityPhysics().getCreatureBodies().get(teleportEvent.getCreatureId())
        .forceSetTransform(teleportEvent.getPos());
    } else {
      if (teleportEvent.getCreatureId() != null) {
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

  public void createAreaGateEntity(AreaGateId areaGateId, TextureAtlas atlas, CoreGame game) {
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

  public void removeAreaGateEntity(AreaGateId areaGateId, CoreGame game) {
    if (areaGateId != null) {

      game.getGameState().getAreaGates().remove(areaGateId);

      getGameEntityRenderer().getAreaGateRenderers().remove(areaGateId);

      if (gameEntityPhysics.getAreaGateBodies().containsKey(areaGateId)) {
        gameEntityPhysics.getAreaGateBodies().get(areaGateId).onRemove();
        gameEntityPhysics.getAreaGateBodies().remove(areaGateId);
      }
    }
  }
}
