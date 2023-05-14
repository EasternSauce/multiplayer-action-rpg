package com.easternsauce.actionrpg.model.action.creature;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.ability.Ability;
import com.easternsauce.actionrpg.model.action.GameStateAction;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.Creature;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.creature.DropTableEntry;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.skill.SkillType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureHitByAbilityAction extends GameStateAction {
    private CreatureId attackerId;
    private CreatureId targetId;
    private Ability ability;

    @Override
    public Entity getEntity(CoreGame game) {
        return game.getGameState().accessCreatures().getCreature(targetId);
    }

    @Override
    public boolean isActionObjectValid(CoreGame game) {
        return true;
    }

    @Override
    public void applyToGame(CoreGame game) {
        Creature targetCreature = game.getGameState().accessCreatures().getCreature(targetId);
        Creature attackerCreature = game.getGameState().accessCreatures().getCreature(attackerId);

        if (targetCreature == null || attackerCreature == null) {
            return;
        }

        targetCreature.onBeingHit(ability, game);

        if (targetCreature.getParams().getPreviousTickLife() > 0f && targetCreature.getParams().getLife() <= 0f) {
            targetCreature.getParams().setLife(0f); // just to make sure its dead on client side
            targetCreature.getParams().setIsDead(true);
            targetCreature.getParams().getRespawnTimer().restart();
            targetCreature.getParams().setIsAwaitingRespawn(true);
            attackerCreature.onKillEffect();

            spawnDrops(game);
        }
    }

    public void spawnDrops(CoreGame game) {
        Creature creature = game.getGameState().accessCreatures().getCreature(targetId);

        Set<Item> items = new ConcurrentSkipListSet<>();

        Set<DropTableEntry> dropTable = creature.getParams().getDropTable();

        if (dropTable == null) {
            return;
        }

        dropTable.forEach(entry -> {
            if (creature.nextDropRngValue() < entry.getDropChance()) {
                Float quality = 0.5f + creature.nextDropRngValue() / 2f;

                AtomicReference<SkillType> weightedSkillType = new AtomicReference<>(null);

                if (creature.nextDropRngValue() < entry.getGrantedSkillChance()) {
                    AtomicReference<Float> totalWeight = new AtomicReference<>((float) 0);

                    entry.getGrantedSkillWeights().forEach((skillType, weight) -> totalWeight.set(totalWeight.get() + weight));

                    AtomicReference<Float> randValue = new AtomicReference<>(creature.nextDropRngValue() * totalWeight.get());

                    entry.getGrantedSkillWeights().forEach((skillType, weight) -> {
                        if (weightedSkillType.get() == null && randValue.get() < weight) {
                            weightedSkillType.set(skillType);
                        }
                        randValue.updateAndGet(value -> value - weight);
                    });

                }

                Map<SkillType, Integer> grantedSkills = new ConcurrentSkipListMap<>();

                if (weightedSkillType.get() != null) {
                    float randValue = creature.nextDropRngValue();

                    int level;
                    if (randValue < 0.5f) {
                        level = 1;
                    }
                    else if (randValue < 0.8f) {
                        level = 2;
                    }
                    else {
                        level = 3;
                    }

                    grantedSkills.put(weightedSkillType.get(), level);
                }

                Item item = Item
                    .of()
                    .setTemplate(entry.getTemplate())
                    .setQualityModifier(quality)
                    .setGrantedSkills(grantedSkills);

                items.add(item);
            }
        });

        if (items.isEmpty()) {
            return;
        }

        LootPileId lootPileId = LootPileId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

        Set<Item> lootPileItems = items
            .stream()
            .map(item -> Item
                .of()
                .setTemplate(item.getTemplate())
                .setQuantity(item.getQuantity())
                .setQualityModifier(item.getQualityModifier())
                .setGrantedSkills(item.getGrantedSkills())
                .setLootPileId(lootPileId))
            .collect(Collectors.toCollection(ConcurrentSkipListSet::new));

        LootPile lootPile = LootPile.of(lootPileId,
                                        creature.getParams().getAreaId(),
                                        creature.getParams().getPos(),
                                        lootPileItems);

        game.getGameState().getLootPiles().put(lootPile.getParams().getId(), lootPile);

        game.getEventProcessor().getLootPileModelsToBeCreated().add(lootPile.getParams().getId());
    }

    public static CreatureHitByAbilityAction of(CreatureId attackerId, CreatureId targetId, Ability ability) {
        CreatureHitByAbilityAction action = CreatureHitByAbilityAction.of();
        action.attackerId = attackerId;
        action.targetId = targetId;
        action.ability = ability;
        return action;
    }
}
