package com.mygdx.game.model.action.creature;

import com.mygdx.game.game.interface_.GameActionApplicable;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.action.GameStateAction;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.creature.DropTableEntry;
import com.mygdx.game.model.item.Item;
import com.mygdx.game.model.skill.SkillType;
import com.mygdx.game.model.util.Vector2;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
@Data
public class CreatureHitAction extends GameStateAction {
    CreatureId attackerId;
    CreatureId targetId;
    Ability ability;

    @Override
    public Vector2 actionObjectPos(GameState gameState) {
        return getActionCreaturePos(gameState, targetId);
    }

    @Override
    public void applyToGame(GameActionApplicable game) {
        Creature targetCreature = game.getCreature(targetId);

        if (targetCreature == null) {
            return;
        }

        targetCreature.onBeingHit(ability, game);

        if (targetCreature.getParams().getPreviousTickLife() > 0f && targetCreature.getParams().getLife() <= 0f) {
            targetCreature.getParams().setLife(0f); // just to make sure its dead on client side
            targetCreature.getParams().setIsDead(true);
            targetCreature.getParams().getRespawnTimer().restart();
            targetCreature.getParams().setIsAwaitingRespawn(true);


            spawnDrops(game);
        }
    }

    public void spawnDrops(GameActionApplicable game) {
        Creature creature = game.getCreature(targetId);

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

                    entry.getGrantedSkillWeights()
                         .forEach((skillType, weight) -> totalWeight.set(totalWeight.get() + weight));

                    AtomicReference<Float> randValue =
                            new AtomicReference<>(creature.nextDropRngValue() * totalWeight.get());

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

                Item item = Item.of()
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

        Set<Item> lootPileItems = items.stream()
                                       .map(item -> Item.of()
                                                        .setTemplate(item.getTemplate())
                                                        .setQuantity(item.getQuantity())
                                                        .setQualityModifier(item.getQualityModifier())
                                                        .setGrantedSkills(item.getGrantedSkills())
                                                        .setLootPileId(lootPileId))
                                       .collect(Collectors.toCollection(ConcurrentSkipListSet::new));

        LootPile lootPile =
                LootPile.of(lootPileId, creature.getParams().getAreaId(), creature.getParams().getPos(), lootPileItems);

        game.getLootPiles().put(lootPile.getId(), lootPile);

        game.getLootPileModelsToBeCreated().add(lootPile.getId());


    }
}
