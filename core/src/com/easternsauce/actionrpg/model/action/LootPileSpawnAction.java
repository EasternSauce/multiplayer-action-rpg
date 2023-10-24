package com.easternsauce.actionrpg.model.action;

import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.game.entity.Entity;
import com.easternsauce.actionrpg.model.area.Area;
import com.easternsauce.actionrpg.model.area.LootPile;
import com.easternsauce.actionrpg.model.id.EntityId;
import com.easternsauce.actionrpg.model.id.NullAreaId;
import com.easternsauce.actionrpg.model.item.Item;
import com.easternsauce.actionrpg.model.util.Vector2;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@NoArgsConstructor(staticName = "of")
@EqualsAndHashCode(callSuper = true)
public class LootPileSpawnAction extends GameStateAction {
  private EntityId<Area> areaId = NullAreaId.of();

  private Vector2 pos;
  private Set<Item> items;

  public static LootPileSpawnAction of(EntityId<Area> areaId, Vector2 pos, Set<Item> items) {
    LootPileSpawnAction action = LootPileSpawnAction.of();
    action.areaId = areaId;
    action.pos = pos;
    action.items = items;
    return action;
  }

  @Override
  public void applyToGame(CoreGame game) {
    EntityId<LootPile> lootPileId = EntityId.of("LootPile_" + (int) (Math.random() * 10000000)); // TODO: use seeded rng

    Set<Item> lootPileItems = items.stream().map(
      item -> Item.of().setTemplate(item.getTemplate()).setQuantity(item.getQuantity())
        .setQualityModifier(item.getQualityModifier()).setGrantedSkills(item.getGrantedSkills())
        .setLootPileId(lootPileId)).collect(Collectors.toCollection(ConcurrentSkipListSet::new));

    LootPile lootPile = LootPile.of(lootPileId, areaId, pos, lootPileItems);

    game.getGameState().getLootPiles().put(lootPile.getParams().getId(), lootPile);

    game.getEventProcessor().getLootPileModelsToBeCreated().add(lootPile.getParams().getId());
  }

  @Override
  protected Vector2 getOverridePos() {
    return pos;
  }

  @Override
  public Entity getEntity(CoreGame game) {
    return null;
  }

  @Override
  protected EntityId<Area> getOverrideAreaId() {
    return areaId;
  }
}
