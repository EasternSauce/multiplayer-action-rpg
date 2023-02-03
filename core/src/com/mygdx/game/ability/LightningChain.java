package com.mygdx.game.ability;

import com.mygdx.game.game.MyGdxGame;
import com.mygdx.game.model.GameState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "of")
@Data
@EqualsAndHashCode(callSuper = true)
public class LightningChain extends Ability {
    AbilityParams params;

    public static LightningChain of(AbilityParams params) {
        LightningChain ability = LightningChain.of();
        ability.params = params;
        return ability;
    }

    @Override
    void onAbilityStarted(MyGdxGame game) {

    }

    @Override
    void onDelayedAction(MyGdxGame game) {

    }

    @Override
    void onAbilityCompleted(MyGdxGame game) {

    }

    @Override
    void updatePosition(GameState gameState) {

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
