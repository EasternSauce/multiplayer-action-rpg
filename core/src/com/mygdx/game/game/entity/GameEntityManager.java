package com.mygdx.game.game.entity;

import com.mygdx.game.game.interface_.GameUpdatable;
import com.mygdx.game.model.ability.Ability;
import com.mygdx.game.model.ability.AbilityId;
import com.mygdx.game.model.ability.AbilityState;
import com.mygdx.game.model.area.LootPile;
import com.mygdx.game.model.area.LootPileId;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import com.mygdx.game.physics.GamePhysics;
import com.mygdx.game.physics.body.AbilityBody;
import com.mygdx.game.physics.body.CreatureBody;
import com.mygdx.game.physics.body.LootPileBody;
import com.mygdx.game.renderer.AbilityRenderer;
import com.mygdx.game.renderer.LootPileRenderer;
import com.mygdx.game.renderer.creature.CreatureRenderer;
import com.mygdx.game.renderer.game.GameRenderer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
public class GameEntityManager {
    @Getter
    private final GameRenderer gameRenderer = GameRenderer.of();
    @Getter
    private final GamePhysics gamePhysics = GamePhysics.of();

    public void createCreatureEntity(CreatureId creatureId, GameUpdatable game) {
        Creature creature = game.getCreatures().get(creatureId);

        if (creature != null) {
            if (!gameRenderer.getCreatureRenderers().containsKey(creatureId)) {
                CreatureRenderer creatureRenderer = CreatureRenderer.of(creatureId);
                creatureRenderer.init(gameRenderer.getAtlas(), game);
                gameRenderer.getCreatureRenderers().put(creatureId, creatureRenderer);
            }
            if (!gamePhysics.getCreatureBodies().containsKey(creatureId)) {
                CreatureBody creatureBody = CreatureBody.of(creatureId);
                creatureBody.init(game, creature.getParams().getAreaId());
                gamePhysics.getCreatureBodies().put(creatureId, creatureBody);
            }
        }
    }

    public void createAbilityEntity(AbilityId abilityId, GameUpdatable game) {
        Ability ability = game.getAbilities().get(abilityId);

        if (ability != null) {

            if (!gameRenderer.getAbilityRenderers().containsKey(abilityId)) {
                AbilityRenderer abilityRenderer = AbilityRenderer.of(abilityId);
                abilityRenderer.init(gameRenderer.getAtlas(), game);
                gameRenderer.getAbilityRenderers().put(abilityId, abilityRenderer);
            }
            if (!gamePhysics.getAbilityBodies().containsKey(abilityId)) {
                AbilityBody abilityBody = AbilityBody.of(abilityId);
                if (ability.getParams().getState() == AbilityState.ACTIVE) {
                    abilityBody.init(game, ability.getParams().getIsSkipCreatingBody());
                }
                gamePhysics.getAbilityBodies().put(abilityId, abilityBody);
            }
        }

    }

    public void activateAbility(AbilityId abilityId, GameUpdatable game) {
        Ability ability = game.getAbilities().get(abilityId);

        if (ability != null && gamePhysics.getAbilityBodies().containsKey(ability.getParams().getId())) {
            gamePhysics.getAbilityBodies()
                       .get(ability.getParams().getId())
                       .init(game, ability.getParams().getIsSkipCreatingBody());
        }

    }

    public void createLootPileEntity(LootPileId lootPileId, GameUpdatable game) {
        LootPile lootPile = game.getLootPile(lootPileId);

        if (lootPile != null) {
            if (!gameRenderer.getLootPileRenderers().containsKey(lootPileId)) {
                LootPileRenderer lootPileRenderer = LootPileRenderer.of(lootPileId);
                lootPileRenderer.init(gameRenderer.getAtlas(), game);
                gameRenderer.getLootPileRenderers().put(lootPileId, lootPileRenderer);
            }
            if (!gamePhysics.getLootPileBodies().containsKey(lootPileId)) {
                LootPileBody lootPileBody = LootPileBody.of(lootPileId);
                lootPileBody.init(game);
                gamePhysics.getLootPileBodies().put(lootPileId, lootPileBody);
            }
        }
    }

    public void removeCreatureEntity(CreatureId creatureId, GameUpdatable game) {
        if (creatureId != null) {
            game.getCreatures().remove(creatureId);

            getGameRenderer().getCreatureRenderers().remove(creatureId);

            if (gamePhysics.getCreatureBodies().containsKey(creatureId)) {
                gamePhysics.getCreatureBodies().get(creatureId).onRemove();
                gamePhysics.getCreatureBodies().remove(creatureId);
            }
        }
    }

    public void removeAbility(AbilityId abilityId, GameUpdatable game) {

        if (abilityId != null) {

            game.getAbilities().remove(abilityId);

            getGameRenderer().getAbilityRenderers().remove(abilityId);

            if (gamePhysics.getAbilityBodies().containsKey(abilityId)) {
                gamePhysics.getAbilityBodies().get(abilityId).onRemove();
                gamePhysics.getAbilityBodies().remove(abilityId);
            }
        }
    }

    public void removeLootPile(LootPileId lootPileId, GameUpdatable game) {
        if (lootPileId != null) {

            game.getLootPiles().remove(lootPileId);

            getGameRenderer().getLootPileRenderers().remove(lootPileId);

            if (gamePhysics.getLootPileBodies().containsKey(lootPileId)) {
                gamePhysics.getLootPileBodies().get(lootPileId).onRemove();
                gamePhysics.getLootPileBodies().remove(lootPileId);
            }
        }
    }
}
