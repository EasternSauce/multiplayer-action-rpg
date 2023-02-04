package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import com.mygdx.game.model.creature.Creature;
import com.mygdx.game.model.creature.CreatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningSpark extends Ability {

    AbilityParams params;

    public static LightningSpark of(AbilityParams params) {
        LightningSpark ability = LightningSpark.of();
        ability.params = params;
        return ability;
    }

    @Override
    void onAbilityStarted(MyGdxGame game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {
        // find closest enemy, and if they are within distance, and havent been hit yet, then start node over them
        Set<CreatureId> excluded = new HashSet<>(params().creaturesAlreadyHit());
        excluded.add(params().creatureId());

        CreatureId creatureId = game.aliveCreatureClosestTo(params().pos(), 10f, excluded);


        if (creatureId != null && game.gameState().creatures().containsKey(creatureId)) {
            Creature chainToCreature = game.gameState().creatures().get(creatureId);
            chainToCreature.handleBeingAttacked(25f, params().creatureId());

            game.chainAbility(this, AbilityType.LIGHTNING_CHAIN, chainToCreature.params().pos(), null);

            game.chainAbility(this, AbilityType.LIGHTNING_NODE, chainToCreature.params().pos(), creatureId);
        }
    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    void onUpdatePosition(GameState gameState) {

    }

    @Override
    void onChannelUpdate(GameState gameState) {

    }

    @Override
    void onActiveUpdate(GameState gameState) {

    }

    @Override
    public void onCreatureHit() {

    }

    @Override
    public void onTerrainHit() {

    }


}
