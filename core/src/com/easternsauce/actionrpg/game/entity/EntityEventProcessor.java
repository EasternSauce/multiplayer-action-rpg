package com.easternsauce.actionrpg.game.entity;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.easternsauce.actionrpg.game.CoreGame;
import com.easternsauce.actionrpg.model.ability.AbilityId;
import com.easternsauce.actionrpg.model.area.AreaGateId;
import com.easternsauce.actionrpg.model.area.AreaId;
import com.easternsauce.actionrpg.model.area.LootPileId;
import com.easternsauce.actionrpg.model.creature.CreatureId;
import com.easternsauce.actionrpg.model.util.TeleportEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(staticName = "of")
public class EntityEventProcessor {
    @Getter
    private final List<AreaId> areasToBeCreated = Collections.synchronizedList(new ArrayList<>());

    @Getter
    private final List<CreatureId> creatureModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<AbilityId> abilityModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<LootPileId> lootPileModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<AreaGateId> areaGateModelsToBeCreated = Collections.synchronizedList(new ArrayList<>());

    @Getter
    private final List<AbilityId> abilityModelsToBeActivated = Collections.synchronizedList(new ArrayList<>());

    @Getter
    private final List<CreatureId> creatureModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<AbilityId> abilityModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<LootPileId> lootPileModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final List<AreaGateId> areaGateModelsToBeRemoved = Collections.synchronizedList(new ArrayList<>());

    @Getter
    private final List<TeleportEvent> teleportEvents = Collections.synchronizedList(new ArrayList<>());

    public void process(GameEntityManager gameEntityManager, TextureAtlas atlas, CoreGame game) {
        getCreatureModelsToBeCreated().forEach(creatureId -> gameEntityManager.createCreatureEntity(
            creatureId,
            game
        ));
        getCreatureModelsToBeCreated().clear();

        getAbilityModelsToBeCreated().forEach(abilityId -> gameEntityManager.createAbilityEntity(
            abilityId,
            atlas,
            game
        ));
        getAbilityModelsToBeCreated().clear();

        getLootPileModelsToBeCreated().forEach(lootPileId -> gameEntityManager.createLootPileEntity(
            lootPileId,
            atlas,
            game
        ));
        getLootPileModelsToBeCreated().clear();

        getAreaGateModelsToBeCreated().forEach(areaGateId -> gameEntityManager.createAreaGateEntity(
            areaGateId,
            atlas,
            game
        ));
        getAreaGateModelsToBeCreated().clear();

        getAbilityModelsToBeActivated().forEach(abilityId -> gameEntityManager.activateAbility(
            abilityId,
            game
        ));
        getAbilityModelsToBeActivated().clear();

        getCreatureModelsToBeRemoved().forEach(creatureId -> gameEntityManager.removeCreatureEntity(
            creatureId,
            game
        ));
        getCreatureModelsToBeRemoved().clear();

        getAbilityModelsToBeRemoved().forEach(abilityId -> gameEntityManager.removeAbilityEntity(
            abilityId,
            game
        ));
        getAbilityModelsToBeRemoved().clear();

        getLootPileModelsToBeRemoved().forEach(lootPileId -> gameEntityManager.removeLootPileEntity(
            lootPileId,
            game
        ));
        getLootPileModelsToBeRemoved().clear();

        getAreaGateModelsToBeRemoved().forEach(areaGateId -> gameEntityManager.removeAreaGateEntity(
            areaGateId,
            game
        ));
        getAreaGateModelsToBeRemoved().clear();

    }

}
