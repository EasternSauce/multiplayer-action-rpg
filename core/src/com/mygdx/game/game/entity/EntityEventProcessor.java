package com.mygdx.game.game.entity;

import com.mygdx.game.game.CoreGame;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.model.util.TeleportEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class EntityEventProcessor {
    @Getter
    private final List<CreatureId> creatureModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<AbilityId> abilityModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<AbilityId> abilityModelsToBeActivated = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<CreatureId> creatureModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<AbilityId> abilityModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<LootPileId> lootPileModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<LootPileId> lootPileModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<TeleportEvent> teleportEvents = Collections.synchronizedList(new ArrayList<>());

    public void process(GameEntityManager gameEntityManager, CoreGame game) {
        getCreatureModelsToBeCreated().forEach(creatureId -> gameEntityManager.createCreatureEntity(creatureId, game));
        getCreatureModelsToBeCreated().clear();

        getAbilityModelsToBeCreated().forEach(abilityId -> gameEntityManager.createAbilityEntity(abilityId, game));
        getAbilityModelsToBeCreated().clear();

        getAbilityModelsToBeActivated().forEach(abilityId -> gameEntityManager.activateAbility(abilityId, game));
        getAbilityModelsToBeActivated().clear();

        getCreatureModelsToBeRemoved().forEach(creatureId -> gameEntityManager.removeCreatureEntity(creatureId, game));
        getCreatureModelsToBeRemoved().clear();

        getAbilityModelsToBeRemoved().forEach(abilityId -> gameEntityManager.removeAbilityEntity(abilityId, game));
        getAbilityModelsToBeRemoved().clear();

        getLootPileModelsToBeCreated().forEach(lootPileId -> gameEntityManager.createLootPileEntity(lootPileId, game));
        getLootPileModelsToBeCreated().clear();

        getLootPileModelsToBeRemoved().forEach(lootPileId -> gameEntityManager.removeLootPileEntity(lootPileId, game));
        getLootPileModelsToBeRemoved().clear();
    }


}
