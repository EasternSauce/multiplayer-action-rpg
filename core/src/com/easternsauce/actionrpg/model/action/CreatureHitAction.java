package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.ability.AbilityState;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.DropTableEntry;
import com.easternsauce.actionrpg.model.enemyrallypoint.EnemyRallyPoint;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.SkillType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class CreatureHitAction extends GameStateAction {
  protected void handleCreatureDeath(Creature targetCreature, Creature attackerCreature, CoreGame game) {
    if (targetCreature.getParams().getStats().getPreviousTickLife() > 0f &&
      targetCreature.getParams().getStats().getLife() <= 0f) {
      onCreatureDeath(targetCreature, attackerCreature, game);
    }
  }

  private void onCreatureDeath(Creature targetCreature, Creature attackerCreature, CoreGame game) {
    targetCreature.getParams().getStats().setLife(0f); // just to make sure its dead on client side
    targetCreature.getParams().setDead(true);
    targetCreature.getParams().getTimeSinceDeathTimer().restart();
    targetCreature.getParams().setAwaitingRespawn(true);
    attackerCreature.onKillEffect();
    targetCreature.onDeath(attackerCreature, game);

    if (targetCreature.getParams().getEnemyRallyPointId() != null) {
      EnemyRallyPoint enemyRallyPoint = game.getGameState()
        .getEnemyRallyPoint(targetCreature.getParams().getEnemyRallyPointId());

      enemyRallyPoint.getRespawnTimer().restart();
    }

    spawnDrops(targetCreature.getId(), game);

    deactivateCreatureAbilities(targetCreature, game);
  }

  private void spawnDrops(CreatureId targetId, CoreGame game) {
    Creature creature = game.getCreature(targetId);

    Set<Item> items = new ConcurrentSkipListSet<>();

    Set<DropTableEntry> dropTable = creature.getParams().getDropTable();

    dropTable.forEach(entry -> {
      if (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) < entry.getDropChance()) {
        AtomicReference<SkillType> randomSkillType = new AtomicReference<>(null);

        if (Math.abs(game.getGameState().getRandomGenerator().nextFloat()) < entry.getGrantedSkillChance()) {
          AtomicReference<Float> totalWeight = new AtomicReference<>((float) 0);

          entry.getGrantedSkillWeights().forEach((skillType, weight) -> totalWeight.set(totalWeight.get() + weight));

          AtomicReference<Float> randValue = new AtomicReference<>(
            Math.abs(game.getGameState().getRandomGenerator().nextFloat()) * totalWeight.get());

          entry.getGrantedSkillWeights().forEach((skillType, weight) -> {
            if (randomSkillType.get() == null && randValue.get() < weight) {
              randomSkillType.set(skillType);
            }
            randValue.updateAndGet(value -> value - weight);
          });

        }

        Map<SkillType, Integer> grantedSkills = new ConcurrentSkipListMap<>();

        if (randomSkillType.get() != null) {
          //                    float randValue = Math.abs(game.getGameState().getRandomGenerator().nextFloat());

          int level = 1;
          //                    if (randValue < 0.5f) { // TODO: temporarily every item drops at lvl1
          //                        level = 1;
          //                    } else if (randValue < 0.8f) {
          //                        level = 2;
          //                    } else {
          //                        level = 3;
          //                    }

          grantedSkills.put(randomSkillType.get(), level);
        }

        float quality;
        if (entry.getTemplate().getQualityNonApplicable()) {
          quality = 1f;
        } else {
          quality = 0.5f + Math.abs(game.getGameState().getRandomGenerator().nextFloat()) / 2f;
        }

        Item item = Item.of().setTemplate(entry.getTemplate()).setQualityModifier(quality)
          .setGrantedSkills(grantedSkills);

        items.add(item);
      }
    });

    if (items.isEmpty()) {
      return;
    }

    LootPileId lootPileId = LootPileId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

    Set<Item> lootPileItems = items.stream().map(
      item -> Item.of().setTemplate(item.getTemplate()).setQuantity(item.getQuantity())
        .setQualityModifier(item.getQualityModifier()).setGrantedSkills(item.getGrantedSkills())
        .setLootPileId(lootPileId)).collect(Collectors.toCollection(ConcurrentSkipListSet::new));

    LootPile lootPile = LootPile.of(lootPileId, creature.getParams().getAreaId(), creature.getParams().getPos(),
      lootPileItems);

    game.getGameState().getLootPiles().put(lootPile.getParams().getId(), lootPile);

    game.getEventProcessor().getLootPileModelsToBeCreated().add(lootPile.getParams().getId());
  }

  private void deactivateCreatureAbilities(Creature targetCreature, CoreGame game) {
    Set<Ability> creatureActiveAbilities = game.getGameState().accessAbilities().getAbilities().values().stream()
      .filter(
        ability -> ability.canBeDeactivated() && ability.getParams().getCreatureId().equals(targetCreature.getId()) &&
          (ability.getParams().getState() == AbilityState.CHANNEL ||
            ability.getParams().getState() == AbilityState.ACTIVE)).collect(Collectors.toSet());

    creatureActiveAbilities.forEach(Ability::deactivate);
  }
}
